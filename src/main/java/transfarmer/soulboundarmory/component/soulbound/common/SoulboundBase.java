package transfarmer.soulboundarmory.component.soulbound.common;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import transfarmer.farmerlib.util.CollectionUtil;
import transfarmer.farmerlib.util.IndexedLinkedHashMap;
import transfarmer.farmerlib.util.IndexedMap;
import transfarmer.farmerlib.util.ItemUtil;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.MainClient;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SoulboundTab;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.SoulboundItem;
import transfarmer.soulboundarmory.network.Packets;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.statistics.IItem;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static transfarmer.soulboundarmory.MainClient.CLIENT;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.SKILL_POINTS;

public abstract class SoulboundBase implements ISoulboundComponent {
    protected IndexedMap<IItem, Boolean> itemTypes;
    protected List<Item> items;
    protected IItem item;
    protected PlayerEntity player;
    protected boolean isClient;
    protected int boundSlot;
    protected int currentTab;

    protected SoulboundBase(final PlayerEntity player, final IItem[] itemTypes, final Item[] items) {
        this.player = player;
        this.itemTypes = new IndexedLinkedHashMap<>(itemTypes.length);

        for (final IItem item : itemTypes) {
            this.itemTypes.put(item, false);
        }

        this.items = Arrays.asList(items);
        this.boundSlot = -1;
        this.currentTab = 0;
    }

    @Override
    public String toString() {
        return String.format("%s@%s{\n%s\n}", super.toString(), this.getEntity(), this.statistics.toString());
    }

    @Nonnull
    @Override
    public PlayerEntity getEntity() {
        return this.player;
    }

    @Override
    public IItem getItemType(final ItemStack itemStack) {
        return this.getItemType(itemStack.getItem());
    }

    @Override
    public IItem getItemType(final Item item) {
        return this.getItemType(this.items.indexOf(item));
    }

    @Override
    public IItem getItemType(final String item) {
        return IItem.get(item);
    }

    @Override
    public IItem getItemType(final int index) {
        return index == -1 ? null : this.itemTypes.getKey(index);
    }

    @Override
    public IItem getItemType() {
        final ItemStack equippedItemStack = this.getEquippedItemStack();
        IItem item = null;

        if (equippedItemStack != null && this.getBaseItemClass().isInstance(equippedItemStack.getItem())) {
            item = this.getItemType(equippedItemStack);
        }

        return item;
    }

    @Override
    public void setItemType(final int index) {
        this.setItemType(this.getItemType(index));
    }

    @Override
    public void setItemType(final IItem item) {
        this.item = item;

        if (item != null) {
            this.setUnlocked(item, true);
        }
    }

    @Override
    public int getIndex(final IItem item) {
        return this.itemTypes.indexOfKey(item);
    }

    @Override
    public int getIndex() {
        return this.getIndex(this.getItemType());
    }

    @Override
    public Item getItem(final IItem item) {
        return this.items.get(this.itemTypes.indexOfKey(item));
    }

    @Override
    public Item getItem() {
        return this.getItem(this.item);
    }

    @Override
    public int getBoundSlot() {
        return this.boundSlot;
    }

    @Override
    public void bindSlot(final int boundSlot) {
        this.boundSlot = boundSlot;
    }

    @Override
    public void unbindSlot() {
        this.boundSlot = -1;
    }

    @Override
    public int getCurrentTab() {
        return this.currentTab;
    }

    @SuppressWarnings("VariableUseSideOnly")
    @Override
    public void setCurrentTab(final int tab) {
        this.currentTab = tab;

        if (this.isClient) {
            this.sync();
        }
    }

    @Override
    public boolean canLevelUp(final IItem item) {
        return this.getDatum(item, LEVEL) < MainConfig.instance().getMaxLevel() || MainConfig.instance().getMaxLevel() < 0;
    }

    @Override
    public int onLevel(final IItem item, final int sign) {
        final int level = this.statistics.add(item, LEVEL, sign).intValue();

        if (level % MainConfig.instance().getLevelsPerEnchantment() == 0) {
            this.addDatum(item, ENCHANTMENT_POINTS, sign);
        }

        if (level % MainConfig.instance().getLevelsPerSkill() == 0) {
            this.addDatum(item, SKILL_POINTS, sign);
        }

        this.addDatum(item, ATTRIBUTE_POINTS, sign);

        return level;
    }

    @SuppressWarnings("VariableUseSideOnly")
    @Override
    public void refresh() {
        if (this.isClient) {
            if (CLIENT.currentScreen instanceof SoulboundTab) {
                final ItemStack itemStack = this.getEquippedItemStack();

                if (itemStack != null) {
                    if (itemStack.getItem() instanceof SoulboundItem) {
                        this.openGUI();
                    } else {
                        this.openGUI(0);
                    }
                }
            }
        } else {
            Main.PACKET_REGISTRY.sendToPlayer(this.player, Packets.S2C_REFRESH, new ExtendedPacketBuffer(this));
        }
    }

    @Override
    public void openGUI() {
        this.openGUI(ItemUtil.getEquippedItemStack(this.player.inventory, SoulboundItem.class) == null ? 0 : this.currentTab);
    }

    @SuppressWarnings({"LocalVariableDeclarationSideOnly", "VariableUseSideOnly", "MethodCallSideOnly"})
    @Override
    public void openGUI(final int tab) {
        if (this.isClient) {
            final Screen currentScreen = CLIENT.currentScreen;

            if (this.item != null && currentScreen instanceof SoulboundTab && this.currentTab == tab) {
                ((SoulboundTab) currentScreen).refresh();
            } else {
                final List<ScreenTab> tabs = this.getTabs();

                CLIENT.openScreen(tabs.get(MathHelper.clamp(tab, 0, tabs.size() - 1)));
            }
        } else {
            Main.PACKET_REGISTRY.sendToPlayer(this.player, Packets.S2C_OPEN_GUI, new ExtendedPacketBuffer(this).writeInt(tab));
        }
    }

    @Override
    public boolean hasSoulboundItem() {
        final Class<? extends SoulboundItem> baseItemClass = this.getBaseItemClass();

        for (final ItemStack itemStack : this.getEntity().inventory.main) {
            if (baseItemClass.isInstance(itemStack.getItem())) {
                return true;
            }
        }

        return baseItemClass.isInstance(this.getEntity().getOffHandStack().getItem());
    }

    @Override
    public ItemStack getEquippedItemStack() {
        final Class<? extends SoulboundItem> baseItemClass = this.getBaseItemClass();
        final ItemStack mainhandStack = this.getEntity().getMainHandStack();
        final ItemStack offhandStack = this.getEntity().getOffHandStack();
        final Item mainhandItem = mainhandStack.getItem();
        final Item offhandItem = offhandStack.getItem();

        if (baseItemClass.isInstance(mainhandItem)) {
            return mainhandStack;
        }

        if (baseItemClass.isInstance(offhandItem)) {
            return offhandStack;
        }

        final List<Item> consumableItems = this.getConsumableItems();

        if (consumableItems.contains(mainhandItem)) {
            return mainhandStack;
        }

        if (consumableItems.contains(offhandItem)) {
            return offhandStack;
        }

        return null;
    }

    @Override
    public void onTick() {
        if (this.hasSoulboundItem()) {
            final Class<? extends SoulboundItem> baseItemClass = this.getBaseItemClass();
            final PlayerInventory inventory = this.getEntity().inventory;
            final List<ItemStack> main = CollectionUtil.arrayList(inventory.main, inventory.offHand);
            final IItem currentItem = this.getItemType();

            if (currentItem != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : main) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        final ItemStack newItemStack = this.getItemStack(itemStack);
                        final int index = main.indexOf(itemStack);

                        if (itemStack.getItem() == this.getItem(currentItem) && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (this.getBoundSlot() != -1) {
                                this.bindSlot(firstSlot);
                            }

                            if (!SoulboundItemUtil.areDataEqual(itemStack, newItemStack)) {
                                if (itemStack.hasCustomName()) {
                                    newItemStack.setCustomName(itemStack.getName());
                                }

                                inventory.setInvStack(firstSlot, newItemStack);
                            }
                        } else if (!this.getEntity().isCreative() && (index != firstSlot || firstSlot != -1)) {
                            inventory.removeOne(itemStack);
                        }
                    }
                }
            }
        }
    }

    public CompoundTag toTag() {
        return this.toTag(new CompoundTag());
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        tag.putInt("tab", this.getCurrentTab());
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        this.setCurrentTab(tag.getInt("tab"));
    }

    @Override
    public CompoundTag toClientTag() {
        final CompoundTag tag = new CompoundTag();

        tag.putInt("tab", this.currentTab);

        return tag;
    }

    @SuppressWarnings("VariableUseSideOnly")
    @Override
    public void sync() {
        if (!this.isClient) {
            Main.PACKET_REGISTRY.sendToPlayer(this.getEntity(), Packets.S2C_SYNC, new ExtendedPacketBuffer(this).writeCompoundTag(this.toTag()));
        } else {
            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SYNC, new ExtendedPacketBuffer(this).writeCompoundTag(this.toClientTag()));
        }
    }
}
