package transfarmer.soulboundarmory.capability.soulbound.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTab;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSoulbound;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.network.client.S2COpenGUI;
import transfarmer.soulboundarmory.network.client.S2CSync;
import transfarmer.soulboundarmory.network.server.C2STab;
import transfarmer.soulboundarmory.network.server.C2SUpgradeSkill;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillLevelable;
import transfarmer.soulboundarmory.statistics.Skills;
import transfarmer.soulboundarmory.statistics.SoulboundEnchantments;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.Statistics;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.IndexedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper.REACH_DISTANCE_UUID;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.REACH_DISTANCE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILL_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public abstract class SoulboundBase implements SoulboundCapability {
    protected final ICapabilityType type;
    protected Statistics statistics;
    protected SoulboundEnchantments enchantments;
    protected Skills skills;
    protected List<IItem> itemTypes;
    protected List<Item> items;
    protected IItem item;
    protected EntityPlayer player;
    protected boolean isRemote;
    protected int boundSlot;
    protected int currentTab;

    protected SoulboundBase(final ICapabilityType type, final IItem[] itemTypes, final Item[] items) {
        this.type = type;
        this.itemTypes = Arrays.asList(itemTypes);
        this.items = Arrays.asList(items);
        this.boundSlot = -1;
        this.currentTab = 0;
    }

    @Override
    public String toString() {
        return String.format("%s@%s{\n%s\n}", super.toString(), this.getPlayer(), this.statistics.toString());
    }

    @Override
    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void initPlayer(final EntityPlayer player) {
        if (this.getPlayer() == null) {
            this.player = player;
            this.isRemote = player.world.isRemote;
        }
    }

    @Override
    public void reset() {
        for (final IItem item : this.itemTypes) {
            this.reset(item);
            this.resetEnchantments(item);
            this.resetSkills(item);
        }
    }

    @Override
    public void reset(final IItem item) {
        this.statistics.reset(item);
        this.enchantments.reset(item);
        this.skills.reset(item);
    }

    @Override
    public void reset(final IItem item, final ICategory category) {
        this.statistics.reset(item, category);

        if (category == ATTRIBUTE) {
            this.addDatum(item, ATTRIBUTE_POINTS, this.getDatum(item, SPENT_ATTRIBUTE_POINTS));
            this.setDatum(item, SPENT_ATTRIBUTE_POINTS, 0);
        }
    }

    @Override
    public void resetSkills(final IItem item) {
        this.skills.reset(item);
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
    public int getDatum(final IStatistic statistic) {
        return this.getDatum(this.item, statistic);
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
    public boolean addDatum(final IStatistic statistic, final int amount) {
        return this.addDatum(this.item, statistic, amount);
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
    public double getAttribute(final IItem item, final IStatistic statistic) {
        return this.statistics.get(item, statistic).doubleValue();
    }

    @Override
    public void setAttribute(final IItem type, final IStatistic statistic, final double value) {
        this.statistics.set(type, statistic, value);
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
    public List<Skill> getSkills() {
        return this.getSkills(this.item);
    }

    @Override
    public List<Skill> getSkills(final IItem item) {
        final List<Skill> skills = new ArrayList<>(this.skills.get(item).values());

        skills.sort(Comparator.comparingInt(Skill::getTier));

        return skills;
    }

    @Override
    public Skill getSkill(final Skill skill) {
        return this.getSkill(this.item, skill);
    }

    @Override
    public Skill getSkill(final IItem item, final Skill skill) {
        return this.getSkill(item, skill.getRegistryName());
    }

    @Override
    public Skill getSkill(final String skill) {
        return this.getSkill(this.item, skill);
    }

    @Override
    public Skill getSkill(final IItem item, final String skill) {
        return this.skills.get(item, skill);
    }

    @Override
    public boolean hasSkill(final IItem item, final String skill) {
        return this.skills.contains(item, skill);
    }

    @Override
    public boolean hasSkill(final IItem item, final Skill skill) {
        return this.skills.contains(item, skill);
    }

    @Override
    public boolean hasSkill(final IItem item, final SkillLevelable skill, final int level) {
        return this.skills.contains(item, skill, level);
    }

    @Override
    public void upgradeSkill(final IItem item, final Skill skill) {
        if (this.isRemote) {
            Main.CHANNEL.sendToServer(new C2SUpgradeSkill(this.type, item, skill));
        } else {
            final int points = this.getDatum(SKILL_POINTS);
            final int cost = skill.getCost();

            if (skill.canBeLearned(points)) {
                skill.learn();

                this.addDatum(SKILL_POINTS, -cost);
            } else if (skill instanceof SkillLevelable) {
                final SkillLevelable levelable = (SkillLevelable) skill;

                if (levelable.canBeUpgraded(points)) {
                    levelable.upgrade();

                    this.addDatum(SKILL_POINTS, -cost);
                }
            }

        }
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

        if (this.isRemote) {
            Main.CHANNEL.sendToServer(new C2STab(this.type, tab));
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

    @Override
    public int getNextLevelXP(final IItem type) {
        return this.getLevelXP(type, this.getDatum(type, LEVEL));
    }

    @Override
    public int getEnchantment(final IItem item, final Enchantment enchantment) {
        return this.getEnchantments(item).getOrDefault(enchantment, 0);
    }

    @Override
    public IndexedMap<Enchantment, Integer> getEnchantments() {
        return this.getEnchantments(this.item);
    }

    @Override
    public IndexedMap<Enchantment, Integer> getEnchantments(final IItem item) {
        return this.enchantments.get(item);
    }

    @Override
    public void addEnchantment(final IItem type, final Enchantment enchantment, final int value) {
        final int current = this.getEnchantment(type, enchantment);
        final int change = Math.max(0, current + value) - current;

        this.statistics.add(type, ENCHANTMENT_POINTS, -change);
        this.statistics.add(type, SPENT_ENCHANTMENT_POINTS, change);

        this.enchantments.add(type, enchantment, change);
    }

    @Override
    public void resetEnchantments(final IItem item) {
        this.enchantments.reset(item);

        this.statistics.add(item, ENCHANTMENT_POINTS, this.statistics.get(item, SPENT_ENCHANTMENT_POINTS));
        this.statistics.set(item, SPENT_ENCHANTMENT_POINTS, 0);
    }

    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return this.getItemStack(this.getItemType(itemStack));
    }

    @Override
    public ItemStack getItemStack(final IItem type) {
        final ItemStack itemStack = new ItemStack(this.getItem(type));
        final Map<String, AttributeModifier> attributeModifiers = this.getAttributeModifiers(type);
        final Map<Enchantment, Integer> enchantments = this.getEnchantments(type);

        for (final String name : attributeModifiers.keySet()) {
            itemStack.addAttributeModifier(name, attributeModifiers.get(name), MAINHAND);
        }

        for (final Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            final Integer level = entry.getValue();

            if (level > 0) {
                itemStack.addEnchantment(entry.getKey(), level);
            }
        }

        return itemStack;
    }

    @Override
    public Map<String, AttributeModifier> getAttributeModifiers(final IItem type) {
        return CollectionUtil.hashMap(
                EntityPlayer.REACH_DISTANCE.getName(),
                new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", this.getAttributeRelative(type, REACH_DISTANCE), ADD)
        );
    }

    @Override
    public void openGUI(final int tab) {
        if (this.isRemote) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final GuiScreen currentScreen = minecraft.currentScreen;

            if (currentScreen instanceof GuiTabSoulbound && this.currentTab == tab) {
                ((GuiTabSoulbound) currentScreen).refresh();
            } else {
                final List<GuiTab> tabs = this.getTabs();

                minecraft.displayGuiScreen(tabs.get(MathHelper.clamp(tab, 0, tabs.size() - 1)));
            }
        } else {
            Main.CHANNEL.sendTo(new S2COpenGUI(this.type, tab), (EntityPlayerMP) this.player);
        }
    }

    @Override
    public boolean hasSoulItem() {
        final Class<? extends ItemSoulbound> baseItemClass = this.getBaseItemClass();

        for (final ItemStack itemStack : this.getPlayer().inventory.mainInventory) {
            if (baseItemClass.isInstance(itemStack.getItem())) {
                return true;
            }
        }

        return baseItemClass.isInstance(this.getPlayer().getHeldItemOffhand().getItem());
    }

    @Override
    public ItemStack getEquippedItemStack() {
        final Class<? extends ItemSoulbound> baseItemClass = this.getBaseItemClass();
        final ItemStack mainhandStack = this.getPlayer().getHeldItemMainhand();
        final ItemStack offhandStack = this.getPlayer().getHeldItemOffhand();

        if (baseItemClass.isInstance(mainhandStack.getItem())) {
            return mainhandStack;
        }

        if (baseItemClass.isInstance(offhandStack.getItem())) {
            return offhandStack;
        }

        final List<Item> consumableItems = this.getConsumableItems();

        if (consumableItems.contains(mainhandStack.getItem())) {
            return mainhandStack;
        }

        if (consumableItems.contains(offhandStack.getItem())) {
            return offhandStack;
        }

        return null;
    }

    @Override
    public void onTick() {
        if (this.hasSoulItem()) {
            final Class<? extends ItemSoulbound> baseItemClass = this.getBaseItemClass();
            final InventoryPlayer inventory = this.getPlayer().inventory;
            final List<ItemStack> mainInventory = new ArrayList<>(this.getPlayer().inventory.mainInventory);
            final ItemStack equippedItemStack = this.getEquippedItemStack();
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
        tag.setInteger("slot", this.getBoundSlot());
        tag.setTag("statistics", this.statistics.serializeNBT());
        tag.setTag("enchantments", this.enchantments.serializeNBT());
        tag.setTag("skills", this.skills.serializeNBT());

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        this.setItemType(tag.getInteger("index"));
        this.setCurrentTab(tag.getInteger("tab"));
        this.bindSlot(tag.getInteger("slot"));
        this.statistics.deserializeNBT(tag.getCompoundTag("statistics"));
        this.enchantments.deserializeNBT(tag.getCompoundTag("enchantments"));
        this.skills.deserializeNBT(tag.getCompoundTag("skills"));
    }

    @Override
    public void sync() {
        if (!this.player.world.isRemote) {
            Main.CHANNEL.sendTo(new S2CSync(this.type, this.serializeNBT()), (EntityPlayerMP) this.getPlayer());
        } else {
            Main.CHANNEL.sendToServer(new C2STab(this.type, this.currentTab));

            if (Minecraft.getMinecraft().currentScreen instanceof GuiTabSoulbound) {
                this.refresh();
            }
        }
    }
}
