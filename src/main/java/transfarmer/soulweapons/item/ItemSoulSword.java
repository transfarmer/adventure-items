package transfarmer.soulweapons.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.client.Client;
import transfarmer.soulweapons.network.server.SLightning;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SKILLS;
import static transfarmer.soulweapons.data.SoulWeaponType.SWORD;

public class ItemSoulSword extends ItemSoulWeapon {
    public ItemSoulSword() {
        super(2, -2.4F, 4.5F);
    }

    @Override
    public int getMaxItemUseDuration(final ItemStack itemStack) {
        return 1200;
    }

    @Override
    public EnumAction getItemUseAction(final ItemStack itemStack) {
        return EnumAction.BLOCK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ISoulWeapon capability = player.getCapability(CAPABILITY, null);
        if (!world.isRemote && FMLCommonHandler.instance().getSide().isClient()
                && capability.getDatum(SKILLS, SWORD) >= 1 && capability.getLightningCooldown() <= 0) {
            Main.CHANNEL.sendToServer(new SLightning(Client.ENTITY_RENDERER.raycast()));

            return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
    }
}
