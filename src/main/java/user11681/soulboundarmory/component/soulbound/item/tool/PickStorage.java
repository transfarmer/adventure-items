package user11681.soulboundarmory.component.soulbound.item.tool;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponentBase;
import user11681.soulboundarmory.component.statistics.EnchantmentStorage;
import user11681.soulboundarmory.component.statistics.SkillStorage;
import user11681.soulboundarmory.component.statistics.Statistic;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.component.statistics.Statistics;
import user11681.soulboundarmory.registry.Skills;
import user11681.usersmanual.collections.ArrayMap;
import user11681.usersmanual.collections.CollectionUtil;
import user11681.usersmanual.collections.OrderedArrayMap;
import user11681.usersmanual.text.StringifiedText;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static user11681.soulboundarmory.component.statistics.Category.ATTRIBUTE;
import static user11681.soulboundarmory.component.statistics.Category.DATUM;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.EFFICIENCY;
import static user11681.soulboundarmory.component.statistics.StatisticType.ENCHANTMENT_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.EXPERIENCE;
import static user11681.soulboundarmory.component.statistics.StatisticType.LEVEL;
import static user11681.soulboundarmory.component.statistics.StatisticType.MINING_LEVEL;
import static user11681.soulboundarmory.component.statistics.StatisticType.REACH;
import static user11681.soulboundarmory.component.statistics.StatisticType.SKILL_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;

public class PickStorage extends ToolStorage<PickStorage> {
    public PickStorage(final SoulboundComponentBase component, final Item item) {
        super(component, item);

        this.statistics = Statistics.create()
                .category(DATUM, EXPERIENCE, LEVEL, SKILL_POINTS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS)
                .category(ATTRIBUTE, EFFICIENCY, REACH, MINING_LEVEL)
                .min(0.5, EFFICIENCY).min(2, REACH)
                .max(3, MINING_LEVEL).build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).getString().toLowerCase();

            return enchantment.isAcceptableItem(new ItemStack(this.item)) && !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && !name.contains("soulbound") && !name.contains("holding") && !name.contains("smelt")
                    && !name.contains("mending");
        });
        this.skillStorage = new SkillStorage(Skills.PULL, Skills.AMBIDEXTERITY);
    }

    public static PickStorage get(final Entity entity) {
        return Components.TOOL_COMPONENT.get(entity).getStorage(StorageType.PICK_STORAGE);
    }

    public Item getConsumableItem() {
        return Items.WOODEN_PICKAXE;
    }

    @Override
    public Text getName() {
        return Mappings.SOULBOUND_PICK;
    }

    @Override
    public StorageType<PickStorage> getType() {
        return StorageType.PICK_STORAGE;
    }

    public Map<String, EntityAttributeModifier> getModifiers() {
        return Collections.singletonMap(ReachEntityAttributes.REACH.getId(), new EntityAttributeModifier(Main.REACH_MODIFIER_UUID, "Tool modifier", this.getAttributeRelative(REACH), ADDITION));
    }

    @Override
    public ArrayMap<Statistic, Text> getScreenAttributes() {
        final ArrayMap<Statistic, Text> entries = new OrderedArrayMap<>(3);

        entries.put(this.getStatistic(EFFICIENCY), new StringifiedText("%s%s: %s", Mappings.TOOL_EFFICIENCY_FORMAT, Mappings.TOOL_EFFICIENCY_NAME, this.formatStatistic(EFFICIENCY)));
        entries.put(this.getStatistic(MINING_LEVEL), new StringifiedText("%s%s: %s (%s)", Mappings.MINING_LEVEL_FORMAT, Mappings.MINING_LEVEL_NAME, this.formatStatistic(MINING_LEVEL), this.getMiningLevel()));
        entries.put(this.getStatistic(REACH), new StringifiedText("%s%s: %s", Mappings.REACH_FORMAT, Mappings.ATTACK_RANGE_NAME, this.formatStatistic(REACH)));

        return entries;
    }

    @Environment(EnvType.CLIENT)
    public List<Text> getTooltip() {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<Text> tooltip = new ArrayList<>(5);

        tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.REACH_FORMAT, FORMAT.format(this.getAttribute(REACH)), Mappings.ATTACK_RANGE_NAME.asFormattedString())));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.TOOL_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(EFFICIENCY)), Mappings.TOOL_EFFICIENCY_NAME.asFormattedString())));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.MINING_LEVEL_FORMAT, FORMAT.format(this.getAttribute(MINING_LEVEL)), Mappings.MINING_LEVEL_NAME.asFormattedString())));
        tooltip.add(new LiteralText(""));
        tooltip.add(new LiteralText(""));

        return tooltip;
    }

    public double getIncrease(final StatisticType statistic) {
        return statistic == EFFICIENCY
                ? 0.5
                : statistic == REACH
                ? 0.1
                : statistic == MINING_LEVEL
                ? 0.2
                : 0;
    }
}
