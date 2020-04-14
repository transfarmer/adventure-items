package transfarmer.soulboundarmory.capability.soulbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.v2.statistics.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class BaseSoulCapability implements ISoulCapability {
    protected EntityPlayer player;
    protected SoulType currentType;
    protected SoulDatum datum;
    protected Statistics statistics;
    protected int[][] data;
    protected float[][] attributes;
    protected int[][] enchantments;
    protected int boundSlot;
    protected int currentTab;

    protected BaseSoulCapability(final SoulDatum datum, final Statistics statistics) {
        this.datum = datum;
        this.statistics = statistics;
        this.boundSlot = -1;
        this.init();
    }

    @Override
    public void forEach(final BiConsumer<Integer, Integer> data, final BiConsumer<Integer, Integer> attributes, final BiConsumer<Integer, Integer> enchantments) {
        for (int itemIndex = 0; itemIndex < this.getItemAmount(); itemIndex++) {
            for (int valueIndex = 0; valueIndex < Math.max(this.getDatumAmount(), Math.max(this.getAttributeAmount(), this.getEnchantmentAmount())); valueIndex++) {
                if (valueIndex < this.getDatumAmount()) {
                    data.accept(itemIndex, valueIndex);
                }

                if (valueIndex < this.getAttributeAmount()) {
                    attributes.accept(itemIndex, valueIndex);
                }

                if (valueIndex < this.getEnchantmentAmount()) {
                    enchantments.accept(itemIndex, valueIndex);
                }
            }
        }
    }

    @Override
    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void initPlayer(final EntityPlayer player) {
        if (this.player == null) {
            this.player = player;
        }
    }

    @Override
    public void init() {
        this.data = new int[this.getItemAmount()][this.getDatumAmount()];
        this.attributes = new float[this.getItemAmount()][this.getAttributeAmount()];
        this.enchantments = new int[this.getItemAmount()][this.getEnchantmentAmount()];
    }

    @Override
    public boolean addDatum(final int amount, final SoulDatum datum, final SoulType type) {
        if (this.datum.xp.equals(datum)) {
            final int xp = this.data[type.getIndex()][this.datum.xp.getIndex()] += amount;

            if (xp >= this.getNextLevelXP(type) && this.canLevelUp(type)) {
                final int nextLevelXP = this.getNextLevelXP(type);

                this.addDatum(1, this.datum.level, type);
                this.addDatum(-nextLevelXP, this.datum.xp, type);

                return true;
            } else if (xp < 0) {
                final int currentLevelXP = this.getLevelXP(type, this.getDatum(this.datum.level, type) - 1);

                this.addDatum(-1, this.datum.level, type);
                this.addDatum(currentLevelXP, this.datum.xp, type);

                return false;
            }
        } else if (this.datum.level.equals(datum)) {
            final int sign = (int) Math.signum(amount);

            for (int i = 0; i < Math.abs(amount); i++) {
                final int level = this.data[type.getIndex()][this.datum.level.getIndex()] += sign;
                if (level % MainConfig.instance().getLevelsPerEnchantment() == 0) {
                    this.addDatum(sign, this.datum.enchantmentPoints, type);
                }

                if (level % MainConfig.instance().getLevelsPerSkill() == 0 && this.getDatum(this.datum.skills, type) < type.getSkills().length) {
                    this.addDatum(sign, this.datum.skills, type);
                }

                this.addDatum(sign, this.datum.attributePoints, type);
            }
        } else {
            this.data[type.getIndex()][datum.getIndex()] += amount;
        }

        return false;
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
    public int getDatumAmount() {
        return this.datum.getAmount();
    }

    @Override
    public boolean canLevelUp(final SoulType type) {
        return this.getDatum(this.datum.level, type) < MainConfig.instance().getMaxLevel() || MainConfig.instance().getMaxLevel() < 0;
    }

    @Override
    public int getNextLevelXP(final SoulType type) {
        return this.getLevelXP(type, this.getDatum(this.datum.level, type));
    }

    @Override
    public boolean hasSoulItem() {
        final Class<? extends ISoulItem> baseItemClass = this.getBaseItemClass();

        for (final ItemStack itemStack : this.getPlayer().inventory.mainInventory) {
            if (baseItemClass.isInstance(itemStack.getItem())) {
                return true;
            }
        }

        return baseItemClass.isInstance(this.getPlayer().getHeldItemOffhand().getItem());
    }

    @Override
    public ItemStack getEquippedItemStack() {
        final Class<? extends ISoulItem> baseItemClass = this.getBaseItemClass();
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
    public void update() {
        if (this.hasSoulItem()) {
            final Class<? extends ISoulItem> baseItemClass = this.getBaseItemClass();
            final InventoryPlayer inventory = this.getPlayer().inventory;
            final ItemStack equippedItemStack = this.getEquippedItemStack();
            final List<ItemStack> mainInventory = new ArrayList<>(this.getPlayer().inventory.mainInventory);
            mainInventory.add(this.getPlayer().getHeldItemOffhand());

            if (equippedItemStack != null && baseItemClass.isInstance(equippedItemStack.getItem())) {
                final SoulType type = SoulType.get(equippedItemStack);

                if (type != this.getCurrentType()) {
                    this.setCurrentType(type);
                }
            }

            if (this.getCurrentType() != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : mainInventory) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        final ItemStack newItemStack = this.getItemStack(itemStack);
                        final int index = mainInventory.indexOf(itemStack);

                        if (itemStack.getItem() == this.getCurrentType().getItem() && (firstSlot == -1 || index == 36)) {
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
}
