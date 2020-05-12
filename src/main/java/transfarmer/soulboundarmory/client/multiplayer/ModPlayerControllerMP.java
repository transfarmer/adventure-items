package transfarmer.soulboundarmory.client.multiplayer;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.PlayerEntitySP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@Environment(CLIENT)
public class ModPlayerControllerMP extends PlayerControllerMP {
    public static final ModPlayerControllerMP modController = new ModPlayerControllerMP(CLIENT);

    private final Minecraft mc;
    private final NetHandlerPlayClient netHandler;
    private BlockPos currentBlock = new BlockPos(-1, -1, -1);
    private ItemStack currentItemHittingBlock = ItemStack.EMPTY;
    private float curBlockDamageMP;
    private float stepSoundTickCounter;
    private int blockHitDelay;
    private boolean isHittingBlock;
    private GameType currentGameType = GameType.SURVIVAL;
    private int currentPlayerItem;

    public ModPlayerControllerMP(final Minecraft mc) {
        super(mc, mc.getConnection());
        this.mc = mc;
        this.netHandler = mc.getConnection();
    }

    public static void clickBlockCreative(Minecraft mcIn, ModPlayerControllerMP playerController, BlockPos pos, EnumFacing facing) {
        if (!mcIn.world.extinguishFire(mcIn.player, pos, facing)) {
            playerController.onPlayerDestroyBlock(pos);
        }
    }

    public void setPlayerCapabilities(PlayerEntity player) {
        this.currentGameType.configurePlayerCapabilities(player.capabilities);
    }

    public boolean isSpectator() {
        return this.currentGameType == GameType.SPECTATOR;
    }

    public void setGameType(GameType type) {
        this.currentGameType = type;
        this.currentGameType.configurePlayerCapabilities(this.mc.player.capabilities);
    }

    public void flipPlayer(PlayerEntity playerIn) {
        playerIn.rotationYaw = -180.0F;
    }

    public boolean shouldDrawHUD() {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    public boolean onPlayerDestroyBlock(BlockPos pos) {
        if (this.currentGameType.hasLimitedInteractions()) {
            if (this.currentGameType == GameType.SPECTATOR) {
                return false;
            }

            if (!this.mc.player.isAllowEdit()) {
                final ItemStack itemstack = this.mc.player.getOffHandStack();

                if (itemstack.isEmpty()) {
                    return false;
                }

                if (!itemstack.canDestroy(this.mc.world.getBlockState(pos).getBlock())) {
                    return false;
                }
            }
        }

        final ItemStack stack = mc.player.getOffHandStack();
        if (!stack.isEmpty() && stack.getItem().onBlockStartBreak(stack, pos, mc.player)) {
            return false;
        }

        if (this.currentGameType.isCreative() && !stack.isEmpty() && !stack.getItem().canDestroyBlockInCreative(mc.world, pos, stack, mc.player)) {
            return false;
        } else {
            World world = this.mc.world;
            IBlockState iblockstate = world.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !this.mc.player.canUseCommandBlock()) {
                return false;
            } else if (iblockstate.getMaterial() == Material.AIR) {
                return false;
            } else {
                world.playEvent(2001, pos, Block.getStateId(iblockstate));

                this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());

                if (!this.currentGameType.isCreative()) {
                    ItemStack itemstack1 = this.mc.player.getOffHandStack();
                    ItemStack copyBeforeUse = itemstack1.copy();

                    if (!itemstack1.isEmpty()) {
                        itemstack1.onBlockDestroyed(world, iblockstate, pos, this.mc.player);

                        if (itemstack1.isEmpty()) {
                            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this.mc.player, copyBeforeUse, EnumHand.MAIN_HAND);
                            this.mc.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                }

                boolean flag = block.removedByPlayer(iblockstate, world, pos, mc.player, false);

                if (flag) {
                    block.onPlayerDestroy(world, pos, iblockstate);
                }

                return flag;
            }
        }
    }

    public void clickBlockOffhand(final BlockPos loc, final EnumFacing face) {
        if (!this.currentGameType.isCreative() && (!this.isHittingBlock || !this.isHittingPosition(loc))) {
            if (this.isHittingBlock) {
                this.netHandler.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
            }

            final IBlockState blockState = this.mc.world.getBlockState(loc);
            this.mc.getTutorial().onHitBlock(this.mc.world, loc, blockState, 0.0F);
            this.netHandler.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));

            if (blockState.getMaterial() != Material.AIR
                    && blockState.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, loc) >= 1.0F) {
                this.onPlayerDestroyBlock(loc);
            } else {
                this.isHittingBlock = true;
                this.currentBlock = loc;
                this.currentItemHittingBlock = this.mc.player.getOffHandStack();
                this.curBlockDamageMP = 0.0F;
                this.stepSoundTickCounter = 0.0F;
                this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int) (this.curBlockDamageMP * 10.0F) - 1);
                this.isHittingBlock = false;
            }
        }

    }

    public void resetBlockRemoving() {
        if (this.isHittingBlock) {
            this.mc.getTutorial().onHitBlock(this.mc.world, this.currentBlock, this.mc.world.getBlockState(this.currentBlock), -1.0F);
            this.netHandler.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
            this.isHittingBlock = false;
            this.curBlockDamageMP = 0.0F;
            this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, -1);
            this.mc.player.resetCooldown();
        }
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
        this.syncCurrentPlayItem();

        if (this.blockHitDelay > 0) {
            --this.blockHitDelay;
            return true;
        } else if (this.currentGameType.isCreative() && this.mc.world.getWorldBorder().contains(posBlock)) {
            this.blockHitDelay = 5;
            this.mc.getTutorial().onHitBlock(this.mc.world, posBlock, this.mc.world.getBlockState(posBlock), 1.0F);
            this.netHandler.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, posBlock, directionFacing));
            clickBlockCreative(this.mc, this, posBlock, directionFacing);
            return true;
        } else if (this.isHittingPosition(posBlock)) {
            IBlockState iblockstate = this.mc.world.getBlockState(posBlock);
            Block block = iblockstate.getBlock();

            if (iblockstate.getMaterial() == Material.AIR) {
                this.isHittingBlock = false;
                return false;
            } else {
                this.curBlockDamageMP += iblockstate.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, posBlock);

                if (this.stepSoundTickCounter % 4.0F == 0.0F) {
                    SoundType soundtype = block.getSoundType(iblockstate, mc.world, posBlock, mc.player);
                    this.mc.getSoundHandler().playSound(new PositionedSoundRecord(soundtype.getHitSound(), SoundCategory.NEUTRAL, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, posBlock));
                }

                ++this.stepSoundTickCounter;
                this.mc.getTutorial().onHitBlock(this.mc.world, posBlock, iblockstate, MathHelper.clamp(this.curBlockDamageMP, 0.0F, 1.0F));

                if (this.curBlockDamageMP >= 1.0F) {
                    this.isHittingBlock = false;
                    this.netHandler.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
                    this.onPlayerDestroyBlock(posBlock);
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.blockHitDelay = 5;
                }

                this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int) (this.curBlockDamageMP * 10.0F) - 1);
                return true;
            }
        } else {
            return this.clickBlock(posBlock, directionFacing);
        }
    }

    public float getBlockReachDistance() {
        float attrib = (float) mc.player.getEntityAttribute(PlayerEntity.REACH_DISTANCE).getAttributeValue();
        return this.currentGameType.isCreative() ? attrib : attrib - 0.5F;
    }

    public void updateController() {
        this.syncCurrentPlayItem();

        if (this.netHandler.getNetworkManager().isChannelOpen()) {
            this.netHandler.getNetworkManager().processReceivedPackets();
        } else {
            this.netHandler.getNetworkManager().handleDisconnection();
        }
    }

    private boolean isHittingPosition(BlockPos pos) {
        ItemStack itemstack = this.mc.player.getOffHandStack();
        boolean flag = this.currentItemHittingBlock.isEmpty() && itemstack.isEmpty();

        if (!this.currentItemHittingBlock.isEmpty() && !itemstack.isEmpty()) {
            flag = !net.minecraftforge.client.ForgeHooksClient.shouldCauseBlockBreakReset(this.currentItemHittingBlock, itemstack);
        }

        return pos.equals(this.currentBlock) && flag;
    }

    private void syncCurrentPlayItem() {
        int i = this.mc.player.inventory.currentItem;

        if (i != this.currentPlayerItem) {
            this.currentPlayerItem = i;
            this.netHandler.sendPacket(new CPacketHeldItemChange(this.currentPlayerItem));
        }
    }

    public EnumActionResult processRightClickBlock(PlayerEntitySP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand) {
        this.syncCurrentPlayItem();
        ItemStack itemstack = player.getHeldItem(hand);
        float f = (float) (vec.x - (double) pos.getX());
        float f1 = (float) (vec.y - (double) pos.getY());
        float f2 = (float) (vec.z - (double) pos.getZ());
        boolean flag = false;

        if (!this.mc.world.getWorldBorder().contains(pos)) {
            return EnumActionResult.FAIL;
        } else {
            net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks
                    .onRightClickBlock(player, hand, pos, direction, net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(player, getBlockReachDistance() + 1));
            if (event.isCanceled()) {
                // Give the server a chance to fire event as well. That way server event is not dependant on client event.
                this.netHandler.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
                return event.getCancellationResult();
            }
            EnumActionResult result = EnumActionResult.PASS;

            if (this.currentGameType != GameType.SPECTATOR) {
                EnumActionResult ret = itemstack.onItemUseFirst(player, worldIn, pos, hand, direction, f, f1, f2);
                if (ret != EnumActionResult.PASS) {
                    // The server needs to process the item use as well. Otherwise onItemUseFirst won't ever be called on the server without causing weird bugs
                    this.netHandler.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
                    return ret;
                }

                IBlockState iblockstate = worldIn.getBlockState(pos);
                boolean bypass = player.getMainHandStack().doesSneakBypassUse(worldIn, pos, player) && player.getOffHandStack().doesSneakBypassUse(worldIn, pos, player);

                if ((!player.isSneaking() || bypass || event.getUseBlock() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW)) {
                    if (event.getUseBlock() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                        flag = iblockstate.getBlock().onBlockActivated(worldIn, pos, iblockstate, player, hand, direction, f, f1, f2);
                    if (flag) result = EnumActionResult.SUCCESS;
                }

                if (!flag && itemstack.getItem() instanceof ItemBlock) {
                    ItemBlock itemblock = (ItemBlock) itemstack.getItem();

                    if (!itemblock.canPlaceBlockOnSide(worldIn, pos, direction, player, itemstack)) {
                        return EnumActionResult.FAIL;
                    }
                }
            }

            this.netHandler.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));

            if (!flag && this.currentGameType != GameType.SPECTATOR || event.getUseItem() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW) {
                if (itemstack.isEmpty()) {
                    return EnumActionResult.PASS;
                } else if (player.getCooldownTracker().hasCooldown(itemstack.getItem())) {
                    return EnumActionResult.PASS;
                } else {
                    if (itemstack.getItem() instanceof ItemBlock && !player.canUseCommandBlock()) {
                        Block block = ((ItemBlock) itemstack.getItem()).getBlock();

                        if (block instanceof BlockCommandBlock || block instanceof BlockStructure) {
                            return EnumActionResult.FAIL;
                        }
                    }

                    if (this.currentGameType.isCreative()) {
                        int i = itemstack.getMetadata();
                        int j = itemstack.getCount();
                        if (event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) {
                            EnumActionResult enumactionresult = itemstack.onItemUse(player, worldIn, pos, hand, direction, f, f1, f2);
                            itemstack.setItemDamage(i);
                            itemstack.setCount(j);
                            return enumactionresult;
                        } else return result;
                    } else {
                        ItemStack copyForUse = itemstack.copy();
                        if (event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                            result = itemstack.onItemUse(player, worldIn, pos, hand, direction, f, f1, f2);
                        if (itemstack.isEmpty())
                            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyForUse, hand);
                        return result;
                    }
                }
            } else {
                return EnumActionResult.SUCCESS;
            }
        }
    }

    public EnumActionResult processRightClick(PlayerEntity player, World worldIn, EnumHand hand) {
        if (this.currentGameType == GameType.SPECTATOR) {
            return EnumActionResult.PASS;
        } else {
            this.syncCurrentPlayItem();
            this.netHandler.sendPacket(new CPacketPlayerTryUseItem(hand));
            ItemStack itemstack = player.getHeldItem(hand);

            if (player.getCooldownTracker().hasCooldown(itemstack.getItem())) {
                return EnumActionResult.PASS;
            } else {
                EnumActionResult cancelResult = net.minecraftforge.common.ForgeHooks.onItemRightClick(player, hand);
                if (cancelResult != null) return cancelResult;
                int i = itemstack.getCount();
                ActionResult<ItemStack> actionresult = itemstack.useItemRightClick(worldIn, player, hand);
                ItemStack itemstack1 = actionresult.getResult();

                if (itemstack1 != itemstack || itemstack1.getCount() != i) {
                    player.setHeldItem(hand, itemstack1);
                    if (itemstack1.isEmpty()) {
                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack, hand);
                    }
                }

                return actionresult.getType();
            }
        }
    }

    public PlayerEntitySP createPlayer(World p_192830_1_, StatisticsManager p_192830_2_, RecipeBook p_192830_3_) {
        return new PlayerEntitySP(this.mc, p_192830_1_, this.netHandler, p_192830_2_, p_192830_3_);
    }

    public void attackEntity(PlayerEntity playerIn, Entity targetEntity) {
        this.syncCurrentPlayItem();
        this.netHandler.sendPacket(new CPacketUseEntity(targetEntity));

        if (this.currentGameType != GameType.SPECTATOR) {
            playerIn.attackTargetEntityWithCurrentItem(targetEntity);
            playerIn.resetCooldown();
        }
    }

    public EnumActionResult interactWithEntity(PlayerEntity player, Entity target, EnumHand hand) {
        this.syncCurrentPlayItem();
        this.netHandler.sendPacket(new CPacketUseEntity(target, hand));
        return this.currentGameType == GameType.SPECTATOR ? EnumActionResult.PASS : player.interactOn(target, hand);
    }

    public EnumActionResult interactWithEntity(PlayerEntity player, Entity target, RayTraceResult ray, EnumHand hand) {
        this.syncCurrentPlayItem();
        Vec3d vec3d = new Vec3d(ray.hitVec.x - target.posX, ray.hitVec.y - target.posY, ray.hitVec.z - target.posZ);
        this.netHandler.sendPacket(new CPacketUseEntity(target, hand, vec3d));
        if (this.currentGameType == GameType.SPECTATOR)
            return EnumActionResult.PASS; // don't fire for spectators to match non-specific EntityInteract
        EnumActionResult cancelResult = net.minecraftforge.common.ForgeHooks.onInteractEntityAt(player, target, ray, hand);
        if (cancelResult != null) return cancelResult;
        return this.currentGameType == GameType.SPECTATOR ? EnumActionResult.PASS : target.applyPlayerInteraction(player, vec3d, hand);
    }

    public ItemStack windowClick(int windowId, int slotId, int mouseButton, ClickType type, PlayerEntity player) {
        short short1 = player.openContainer.getNextTransactionID(player.inventory);
        ItemStack itemstack = player.openContainer.slotClick(slotId, mouseButton, type, player);
        this.netHandler.sendPacket(new CPacketClickWindow(windowId, slotId, mouseButton, type, itemstack, short1));
        return itemstack;
    }

    public void func_194338_a(int p_194338_1_, IRecipe p_194338_2_, boolean p_194338_3_, PlayerEntity p_194338_4_) {
        this.netHandler.sendPacket(new CPacketPlaceRecipe(p_194338_1_, p_194338_2_, p_194338_3_));
    }

    public void sendEnchantPacket(int windowID, int button) {
        this.netHandler.sendPacket(new CPacketEnchantItem(windowID, button));
    }

    public void sendSlotPacket(ItemStack itemStackIn, int slotId) {
        if (this.currentGameType.isCreative()) {
            this.netHandler.sendPacket(new CPacketCreativeInventoryAction(slotId, itemStackIn));
        }
    }

    public void sendPacketDropItem(ItemStack itemStackIn) {
        if (this.currentGameType.isCreative() && !itemStackIn.isEmpty()) {
            this.netHandler.sendPacket(new CPacketCreativeInventoryAction(-1, itemStackIn));
        }
    }

    public void onStoppedUsingItem(PlayerEntity playerIn) {
        this.syncCurrentPlayItem();
        this.netHandler.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        playerIn.stopActiveHand();
    }

    public boolean gameIsSurvivalOrAdventure() {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    public boolean isNotCreative() {
        return !this.currentGameType.isCreative();
    }

    public boolean isInCreativeMode() {
        return this.currentGameType.isCreative();
    }

    public boolean extendedReach() {
        return this.currentGameType.isCreative();
    }

    public boolean isRidingHorse() {
        return this.mc.player.isRiding() && this.mc.player.getRidingEntity() instanceof AbstractHorse;
    }

    public boolean isSpectatorMode() {
        return this.currentGameType == GameType.SPECTATOR;
    }

    public GameType getCurrentGameType() {
        return this.currentGameType;
    }

    public boolean getIsHittingBlock() {
        return this.isHittingBlock;
    }

    public void pickItem(int index) {
        this.netHandler.sendPacket(new CPacketCustomPayload("MC|PickItem", (new PacketBuffer(Unpooled.buffer())).writeVarInt(index)));
    }
}
