package transfarmer.soulboundarmory.capability.soulbound;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ISoulboundItem;
import transfarmer.soulboundarmory.network.client.S2CSync;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.Statistics;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.ISkill;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper.REACH_DISTANCE_UUID;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.REACH_DISTANCE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public abstract class Base implements IItemCapability {
    protected ICapabilityType type;
    protected Statistics statistics;
    protected List<IItem> itemTypes;
    protected List<Item> items;
    protected IItem item;
    protected EntityPlayer player;
    protected UUID playerUUID;
    protected int boundSlot;
    protected int currentTab;

    protected Base(final ICapabilityType type, final IItem[] itemTypes, ICategory[] categories, IStatistic[][] statistics, double[][][] min, final Item[] items) {
        this.type = type;
        this.statistics = new Statistics(itemTypes, categories, statistics, min);
        this.boundSlot = -1;
        this.items = CollectionUtil.arrayList(items);
        this.itemTypes = CollectionUtil.arrayList(itemTypes);
    }

    @Override
    public String toString() {
        return String.format("%s@%s{\n%s\n}", super.toString(), this.getPlayer(), this.statistics.toString());
    }

    @Override
    public EntityPlayer getPlayer() {
        if (this.player == null) {
            this.player = (EntityPlayer) EntityUtil.getEntity(this.playerUUID);
        }

        return this.player;
    }

    @Override
    public void initPlayer(final EntityPlayer player) {
        if (this.getPlayer() == null) {
            this.player = player;
            this.playerUUID = player.getUniqueID();
        }
    }

    @Override
    public void reset() {
        this.statistics.reset();
    }

    @Override
    public void reset(final IItem item) {
        this.statistics.reset(item);
    }

    @Override
    public ICapabilityType getType() {
        return this.type;
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
        return index == -1 ? null : this.itemTypes.get(index);
    }

    @Override
    public IItem getItemType() {
        return this.item;
    }

    @Override
    public void setItemType(final IItem type) {
        this.item = type;
    }

    @Override
    public void setItemType(final int index) {
        this.item = this.getItemType(index);
    }

    @Override
    public int getIndex(final IItem type) {
        return this.itemTypes.indexOf(type);
    }

    @Override
    public int getIndex() {
        return this.getIndex(this.getItemType());
    }

    @Override
    public Item getItem(final IItem item) {
        return this.items.get(this.itemTypes.indexOf(item));
    }

    @Override
    public Item getItem() {
        return this.getItem(this.item);
    }

    @Override
    public Statistic getStatistic(final IItem item, final IStatistic statistic) {
        return this.statistics.get(item, statistic);
    }

    @Override
    public Statistic getStatistic(final IItem item, final ICategory category, final IStatistic statistic) {
        return this.statistics.get(item, category, statistic);
    }

    @Override
    public int getDatum(final IItem type, final IStatistic datum) {
        return this.statistics.get(type, datum).intValue();
    }

    @Override
    public void setDatum(final IItem type, final IStatistic datum, final int value) {
        this.statistics.set(type, datum, value);
    }

    @Override
    public boolean addDatum(final IItem item, final IStatistic datum, final int amount) {
        if (datum == XP) {
            final int xp = this.statistics.add(item, XP, amount).intValue();

            if (xp >= this.getNextLevelXP(item) && this.canLevelUp(item)) {
                final int nextLevelXP = this.getNextLevelXP(item);

                this.addDatum(item, LEVEL, 1);
                this.addDatum(item, XP, -nextLevelXP);

                return true;
            } else if (xp < 0) {
                final int currentLevelXP = this.getLevelXP(item, this.getDatum(item, LEVEL) - 1);

                this.addDatum(item, LEVEL, -1);
                this.addDatum(item, XP, currentLevelXP);

                return false;
            }
        } else if (datum == LEVEL) {
            final int sign = (int) Math.signum(amount);

            for (int i = 0; i < Math.abs(amount); i++) {
                this.onLevel(item, sign);
            }
        } else {
            this.statistics.add(item, datum, amount);
        }

        return false;
    }

    @Override
    public double getAttribute(final IItem item, final IStatistic staitsitc) {
        return this.statistics.get(item, staitsitc).doubleValue();
    }

    @Override
    public void setAttribute(final IItem type, final IStatistic attribute, final double value) {
        this.statistics.set(type, attribute, value);
    }

    @Override
    public int size(final ICategory category) {
        return this.size(this.item, category);
    }

    @Override
    public int size(final IItem item, final ICategory category) {
        return this.statistics.get(item).get(category).size();
    }

    @Override
    public ISkill[] getSkills() {
        return this.getSkills(this.item);
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

    @Override
    public void setCurrentTab(final int tab) {
        this.currentTab = tab;
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

        this.addDatum(item, ATTRIBUTE_POINTS, sign);

        return level;
    }

    @Override
    public int getNextLevelXP(final IItem type) {
        return this.getLevelXP(type, this.getDatum(type, LEVEL));
    }

    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return this.getItemStack(this.getItemType(itemStack));
    }

    @Override
    public Map<String, AttributeModifier> getAttributeModifiers(final IItem type) {
        return CollectionUtil.hashMap(
                new String[]{EntityPlayer.REACH_DISTANCE.getName()},
                new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", this.getAttributeRelative(type, REACH_DISTANCE), ADD)
        );
    }

    @Override
    public boolean hasSoulItem() {
        final Class<? extends ISoulboundItem> baseItemClass = this.getBaseItemClass();

        for (final ItemStack itemStack : this.getPlayer().inventory.mainInventory) {
            if (baseItemClass.isInstance(itemStack.getItem())) {
                return true;
            }
        }

        return baseItemClass.isInstance(this.getPlayer().getHeldItemOffhand().getItem());
    }

    @Override
    public ItemStack getEquippedItemStack() {
        final Class<? extends ISoulboundItem> baseItemClass = this.getBaseItemClass();
        final ItemStack mainhandStack = this.getPlayer().getHeldItemMainhand();

        if (baseItemClass.isInstance(mainhandStack.getItem())) {
            return mainhandStack;
        }

        final List<Item> consumableItems = this.getConsumableItems();

        if (consumableItems.contains(mainhandStack.getItem())) {
            return mainhandStack;
        }

        final ItemStack offhandStack = this.getPlayer().getHeldItemOffhand();

        if (baseItemClass.isInstance(offhandStack.getItem())) {
            return offhandStack;
        }

        if (consumableItems.contains(offhandStack.getItem())) {
            return mainhandStack;
        }

        return null;
    }

    @Override
    public void onTick() {
        if (this.hasSoulItem()) {
            final Class<? extends ISoulboundItem> baseItemClass = this.getBaseItemClass();
            final InventoryPlayer inventory = this.getPlayer().inventory;
            final ItemStack equippedItemStack = this.getEquippedItemStack();
            final List<ItemStack> mainInventory = new ArrayList<>(this.getPlayer().inventory.mainInventory);
            mainInventory.add(this.getPlayer().getHeldItemOffhand());

            if (equippedItemStack != null && baseItemClass.isInstance(equippedItemStack.getItem())) {
                final IItem type = this.getItemType(equippedItemStack);

                if (type != this.getItemType()) {
                    this.setItemType(type);
                }
            }

            if (this.getItemType() != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : mainInventory) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        final ItemStack newItemStack = this.getItemStack(itemStack);
                        final int index = mainInventory.indexOf(itemStack);

                        if (itemStack.getItem() == this.getItem(this.getItemType()) && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (this.getBoundSlot() != -1) {
                                this.bindSlot(firstSlot);
                            }

                            if (!SoulItemHelper.areDataEqual(itemStack, newItemStack)) {
                                if (itemStack.hasDisplayName()) {
                                    newItemStack.setStackDisplayName(itemStack.getDisplayName());
                                }

                                inventory.setInventorySlotContents(firstSlot, newItemStack);
                            }
                        } else if (!this.getPlayer().isCreative() && index != firstSlot) {
                            inventory.deleteStack(itemStack);
                        }
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("index", this.getIndex());
        tag.setInteger("tab", this.getCurrentTab());
        tag.setInteger("boundSlot", this.getBoundSlot());
        tag.setTag("statistics", this.statistics.serializeNBT());

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        this.setItemType(tag.getInteger("index"));
        this.setCurrentTab(tag.getInteger("tab"));
        this.bindSlot(tag.getInteger("boundSlot"));
        this.statistics.deserializeNBT(tag.getCompoundTag("statistics"));
    }

    @Override
    public void sync() {
        if (!this.getPlayer().world.isRemote) {
            Main.CHANNEL.sendTo(new S2CSync(this.type, this.serializeNBT()), (EntityPlayerMP) this.getPlayer());
        }
    }
}
