package transfarmer.soulboundarmory.component.soulbound.item;

import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.skill.Skills;
import transfarmer.soulboundarmory.statistics.EnchantmentStorage;
import transfarmer.soulboundarmory.statistics.SkillStorage;
import transfarmer.soulboundarmory.statistics.StatisticType;
import transfarmer.soulboundarmory.statistics.Statistics;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static transfarmer.soulboundarmory.Main.PICK_COMPONENT;
import static transfarmer.soulboundarmory.statistics.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.Category.DATUM;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.EFFICIENCY;
import static transfarmer.soulboundarmory.statistics.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.REACH;
import static transfarmer.soulboundarmory.statistics.StatisticType.SKILL_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.XP;

public class PickComponent extends SoulboundToolComponent<IPickComponent> implements IPickComponent {
    public PickComponent(final ItemStack itemStack, final PlayerEntity player) {
        super(itemStack, player);

        this.statistics = Statistics.builder()
                .category(DATUM, XP, LEVEL, SKILL_POINTS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS)
                .category(ATTRIBUTE, EFFICIENCY, REACH, HARVEST_LEVEL)
                .min(0.5, EFFICIENCY).min(2, REACH).build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).getString().toLowerCase();

            return !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && !name.contains("soulbound") && !name.contains("holding") && !name.contains("smelt")
                    && !name.contains("mending");
        });
        this.skillStorage = new SkillStorage(Skills.PULL, Skills.AMBIDEXTERITY);
    }

    @Nonnull
    @Override
    public ComponentType<IPickComponent> getComponentType() {
        return PICK_COMPONENT;
    }

    @Override
    public Item getConsumableItem() {
        return Items.WOODEN_PICKAXE;
    }

    @Override
    public double getIncrease(final StatisticType statistic) {
        return statistic == EFFICIENCY
                ? 0.5
                : statistic == REACH
                ? 0.1
                : statistic == HARVEST_LEVEL
                ? 0.2
                : 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<String> getTooltip() {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(5);

        tooltip.add(String.format(" %s%s %s", Mappings.REACH_DISTANCE_FORMAT, FORMAT.format(this.getAttribute(REACH)), Mappings.REACH_DISTANCE_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.TOOL_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(EFFICIENCY)), Mappings.EFFICIENCY_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.HARVEST_LEVEL_FORMAT, FORMAT.format(this.getAttribute(HARVEST_LEVEL)), Mappings.HARVEST_LEVEL_NAME));

        tooltip.add("");
        tooltip.add("");

        return tooltip;
    }
}
