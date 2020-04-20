package transfarmer.soulboundarmory.capability.soulbound.tool;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.BaseEnchantable;
import transfarmer.soulboundarmory.capability.soulbound.ISkillable;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.IItemSoulboundTool;
import transfarmer.soulboundarmory.item.ISoulboundItem;
import transfarmer.soulboundarmory.item.ItemSoulboundPick;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.base.enumeration.Skill;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.ISkill;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.minecraft.init.Enchantments.UNBREAKING;
import static net.minecraft.init.Enchantments.VANISHING_CURSE;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_PICK;
import static transfarmer.soulboundarmory.statistics.base.enumeration.CapabilityType.TOOL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.DATUM;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.PICK;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.REACH_DISTANCE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILLS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public class Tool extends BaseEnchantable implements ITool, ISkillable {
    public Tool() {
        super(TOOL, new IItem[]{PICK}, new ICategory[]{DATUM, ATTRIBUTE},
                new IStatistic[][]{
                        {XP, LEVEL, SKILLS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS},
                        {EFFICIENCY_ATTRIBUTE, REACH_DISTANCE, HARVEST_LEVEL}
                }, new double[][][]{{{0, 0, 0, 0, 0, 0, 0}, {0.5, 2, 0}}},
                new ItemSoulboundPick[]{SOULBOUND_PICK},
                (final Enchantment enchantment) -> {
                    final String name = enchantment.getName().toLowerCase();

                    return !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                            && !name.contains("soulbound") && !name.contains("holding") && !name.contains("smelt")
                            && !name.contains("mending");
                }
        );

        this.currentTab = 0;
    }

    @Override
    public double getAttributeRelative(final IItem type, final IStatistic attribute) {
        if (attribute == REACH_DISTANCE) {
            return this.getAttribute(type, REACH_DISTANCE) - 3;
        }

        return this.statistics.get(type, attribute).doubleValue();
    }

    @Override
    public double getAttributeTotal(final IItem item, final IStatistic statistic) {
        return this.getAttribute(item, statistic);
    }

    @Override
    public void addAttribute(final IItem item, final IStatistic attribute, final int amount) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            this.addDatum(item, ATTRIBUTE_POINTS, -sign);
            this.addDatum(item, SPENT_ATTRIBUTE_POINTS, sign);

            if (attribute.equals(HARVEST_LEVEL) && this.getAttribute(item, HARVEST_LEVEL) + sign * this.getIncrease(item, HARVEST_LEVEL) >= 2.9999) {
                this.setAttribute(item, attribute, 3);

                return;
            }

            final Statistic statistic = this.getStatistic(item, attribute);

            if (statistic.doubleValue() + sign * this.getIncrease(item, attribute) <= statistic.min()) {
                statistic.setValue(statistic.min());

                return;
            }

            statistic.addInPlace(sign * this.getIncrease(item, attribute));
        }
    }

    @Override
    public double getIncrease(final IItem type, final IStatistic statistic) {
        if (type == PICK) {
            return statistic == EFFICIENCY_ATTRIBUTE
                    ? 0.5
                    : statistic == REACH_DISTANCE
                    ? 0.1
                    : statistic == HARVEST_LEVEL
                    ? 0.2
                    : 0;
        }

        return 0;
    }

    @Override
    public int getLevelXP(final IItem type, final int level) {
        return this.canLevelUp(type)
                ? MainConfig.instance().getInitialToolXP() + (int) Math.round(4 * Math.pow(level, 1.25))
                : -1;
    }

    @Override
    public int onLevel(final IItem item, final int sign) {
        final int level = super.onLevel(item, sign);

        if (level % MainConfig.instance().getLevelsPerSkill() == 0 && this.getDatum(item, SKILLS) < this.getSkills(item).length) {
            this.addDatum(item, SKILLS, sign);
        }

        return level;
    }

    @Override
    @SideOnly(CLIENT)
    public List<String> getTooltip(final IItem type) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(5);

        tooltip.add(String.format(" %s%s %s", Mappings.REACH_DISTANCE_FORMAT, FORMAT.format(this.getAttribute(type, REACH_DISTANCE)), Mappings.REACH_DISTANCE_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.TOOL_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(type, EFFICIENCY_ATTRIBUTE)), Mappings.EFFICIENCY_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.HARVEST_LEVEL_FORMAT, FORMAT.format(this.getAttribute(type, HARVEST_LEVEL)), Mappings.HARVEST_LEVEL_NAME));

        tooltip.add("");
        tooltip.add("");

        return tooltip;
    }

    @Override
    public List<net.minecraft.item.Item> getConsumableItems() {
        return Collections.singletonList(Items.WOODEN_PICKAXE);
    }

    @Override
    @SideOnly(CLIENT)
    public void onKeyPress() {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final ItemStack equippedItemStack = this.getEquippedItemStack();

        if (equippedItemStack != null) {
            if (equippedItemStack.getItem() instanceof IItemSoulboundTool) {
                if (this.currentTab < 0) {
                    minecraft.displayGuiScreen(new SoulToolMenu(0));
                } else {
                    minecraft.displayGuiScreen(new SoulToolMenu());
                }
            } else {
                minecraft.displayGuiScreen(new SoulToolMenu(-1));
            }
        }
    }

    @Override
    public void refresh() {
        this.refresh(this.currentTab);
    }

    @Override
    public void refresh(final int tab) {
        final Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.currentScreen instanceof SoulToolMenu) {
            minecraft.displayGuiScreen(new SoulToolMenu(tab));
        }
    }

    @Override
    public Class<? extends ISoulboundItem> getBaseItemClass() {
        return IItemSoulboundTool.class;
    }

    @Override
    public ISkill[] getSkills(final IItem type) {
        return new ISkill[]{Skill.TELEPORTATION, Skill.AMBIDEXTERITY};
    }
}
