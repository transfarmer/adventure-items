package transfarmer.soulboundarmory.component.soulbound.common;

import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
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
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.network.C2S.C2SSkill;
import transfarmer.soulboundarmory.network.C2S.C2SSync;
import transfarmer.soulboundarmory.network.S2C.S2COpenGUI;
import transfarmer.soulboundarmory.network.S2C.S2CRefresh;
import transfarmer.soulboundarmory.network.S2C.S2CSync;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static net.minecraftforge.common.util.Constants.EntityAttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.MainClient.CLIENT;
import static transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil.REACH_DISTANCE_UUID;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.REACH_DISTANCE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILL_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public abstract class SoulboundBase implements ISoulboundComponent {
    protected final ICapabilityType type;
    protected Statistics statistics;
    protected SoulboundEnchantments enchantments;
    protected Skills skills;
    protected IndexedMap<IItem, Boolean> itemTypes;
    protected List<Item> items;
    protected IItem item;
    protected PlayerEntity player;
    protected boolean isRemote;
    protected int boundSlot;
    protected int currentTab;

    protected SoulboundBase(final PlayerEntity player, final ICapabilityType type, final IItem[] itemTypes,
                            final Item[] items) {
        this.player = player;
        this.type = type;
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
        return String.format("%s@%s{\n%s\n}", super.toString(), this.getPlayer(), this.statistics.toString());
    }

    @Override
    public PlayerEntity getPlayer() {
        return this.player;
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
        return index == -1 ? null : this.itemTypes.getKey(index);
    }

    @Override
    public IItem getItemType() {
        return this.item;
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
    public boolean isUnlocked(final int index) {
        return this.isUnlocked(this.getItemType(index));
    }

    @Override
    public boolean isUnlocked(final IItem item) {
        return this.itemTypes.getOrDefault(item, false);
    }

    @Override
    public void setUnlocked(final IItem item, final boolean unlocked) {
        this.itemTypes.put(item, unlocked);
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
    public SkillLevelable getSkillLevelable(final IItem item, final String skill) {
        return (SkillLevelable) this.getSkill(item, skill);
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
            MainClient.PACKET_REGISTRY.sendToServer(new C2SSkill(this.type, item, skill));
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
            MainClient.PACKET_REGISTRY.sendToServer(new C2SSync(this.type, this.toCompoundTagClient()));
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

        this.addDatum(item, ENCHANTMENT_POINTS, this.getDatum(item, SPENT_ENCHANTMENT_POINTS));
        this.setDatum(item, SPENT_ENCHANTMENT_POINTS, 0);
    }

    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return this.getItemStack(this.getItemType(itemStack));
    }

    @Override
    public List<Item> getConsumableItems() {
        final List<Item> items = new ArrayList<>();

        for (final IItem item : this.itemTypes) {
            items.add(this.getConsumableItem(item));
        }

        return items;
    }

    @Override
    public boolean canUnlock(final int index) {
        return this.canUnlock(this.getItemType(index));
    }

    @Override
    public boolean canUnlock(final IItem item) {
        return this.canConsume(this.getEquippedItemStack().getItem(), item);
    }

    @Override
    public boolean canConsume(final Item item, final int index) {
        return this.canConsume(item, this.getItemType(index));
    }

    @Override
    public boolean canConsume(final Item item, final IItem type) {
        return this.getConsumableItem(type) == item;
    }

    @Override
    public ItemStack getItemStack(final IItem type) {
        final ItemStack itemStack = new ItemStack(this.getItem(type));
        final Map<String, EntityAttributeModifier> attributeModifiers = this.getEntityAttributeModifiers(type);
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
    public Map<String, EntityAttributeModifier> getEntityAttributeModifiers(final IItem type) {
        return CollectionUtil.hashMap(
                PlayerEntity.REACH_DISTANCE.getName(),
                new EntityAttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", this.getAttributeRelative(type, REACH_DISTANCE), ADDITION)
        );
    }

    @Override
    public void refresh() {
        if (this.isRemote) {
            if (CLIENT.currentScreen instanceof SoulboundTab) {
                final ItemStack itemStack = this.getEquippedItemStack();

                if (itemStack != null) {
                    if (itemStack.getItem() instanceof ItemSoulbound) {
                        this.openGUI();
                    } else {
                        this.openGUI(0);
                    }
                }
            }
        } else {
            Main.PACKET_REGISTRY.sendToPlayer((ServerPlayerEntity) this.player, new S2CRefresh(this, this.item));
        }
    }

    @Override
    public void openGUI() {
        this.openGUI(ItemUtil.getEquippedItemStack(this.player.inventory, ItemSoulbound.class) == null ? 0 : this.currentTab);
    }

    @Override
    public void openGUI(final int tab) {
        if (this.isRemote) {
            final Screen currentScreen = CLIENT.currentScreen;

            if (this.item != null && currentScreen instanceof SoulboundTab && this.currentTab == tab) {
                ((SoulboundTab) currentScreen).refresh();
            } else {
                final List<ScreenTab> tabs = this.getTabs();

                CLIENT.openScreen(tabs.get(MathHelper.clamp(tab, 0, tabs.size() - 1)));
            }
        } else {
            Main.PACKET_REGISTRY.sendToPlayer(this.player, new S2COpenGUI(this.type, tab));
        }
    }

    @Override
    public boolean hasSoulboundItem() {
        final Class<? extends ItemSoulbound> baseItemClass = this.getBaseItemClass();

        for (final ItemStack itemStack : this.getPlayer().inventory.main) {
            if (baseItemClass.isInstance(itemStack.getItem())) {
                return true;
            }
        }

        return baseItemClass.isInstance(this.getPlayer().getOffHandStack().getItem());
    }

    @Override
    public ItemStack getEquippedItemStack() {
        final Class<? extends ItemSoulbound> baseItemClass = this.getBaseItemClass();
        final ItemStack mainhandStack = this.getPlayer().getMainHandStack();
        final ItemStack offhandStack = this.getPlayer().getOffHandStack();
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
            final Class<? extends ItemSoulbound> baseItemClass = this.getBaseItemClass();
            final PlayerInventory inventory = this.getPlayer().inventory;
            final List<ItemStack> main = CollectionUtil.arrayList(inventory.main, inventory.offHand);
            final ItemStack equippedItemStack = this.getEquippedItemStack();

            if (equippedItemStack != null && baseItemClass.isInstance(equippedItemStack.getItem())) {
                this.setItemType(this.getItemType(equippedItemStack));
            }

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
                        } else if (!this.getPlayer().isCreative() && (index != firstSlot || firstSlot != -1)) {
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
        final CompoundTag types = new CompoundTag();

        for (final IItem item : this.itemTypes) {
            types.putBoolean(item.toString(), this.itemTypes.get(item));
        }

        tag.putInt("index", this.getIndex());
        tag.putInt("tab", this.getCurrentTab());
        tag.putInt("slot", this.getBoundSlot());
        tag.put("statistics", this.statistics.serializeNBT());
        tag.put("enchantments", this.enchantments.serializeNBT());
        tag.put("skills", this.skills.serializeNBT());
        tag.put("types", types);

        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        this.setItemType(tag.getInt("index"));
        this.setCurrentTab(tag.getInt("tab"));
        this.bindSlot(tag.getInt("slot"));
        this.statistics.deserializeNBT(tag.getCompound("statistics"));
        this.enchantments.deserializeNBT(tag.getCompound("enchantments"));
        this.skills.deserializeNBT(tag.getCompound("skills"));

        final CompoundTag types = tag.getCompound("types");

        for (final String key : types.getKeys()) {
            final IItem item = IItem.get(key);

            if (item != null) {
                this.itemTypes.put(item, types.getBoolean(key));
            }
        }
    }

    @Override
    public CompoundTag toCompoundTagClient() {
        final CompoundTag tag = new CompoundTag();

        tag.putInt("tab", this.currentTab);

        return tag;
    }

    @Override
    public void sync() {
        if (!this.isRemote) {
            Main.PACKET_REGISTRY.sendToPlayer(this.getPlayer(), new S2CSync(this.type, this.toTag()));
        } else {
            MainClient.PACKET_REGISTRY.sendToServer(new C2SSync(this.type, this.toCompoundTagClient()));
        }
    }
}
