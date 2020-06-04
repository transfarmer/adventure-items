package user11681.soulboundarmory.component.soulbound.item.weapon;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
import user11681.usersmanual.item.ItemModifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static user11681.soulboundarmory.Main.IMPACT;
import static user11681.soulboundarmory.component.statistics.Category.ATTRIBUTE;
import static user11681.soulboundarmory.component.statistics.Category.DATUM;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_DAMAGE;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_RANGE;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_SPEED;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static user11681.soulboundarmory.component.statistics.StatisticType.EFFICIENCY;
import static user11681.soulboundarmory.component.statistics.StatisticType.ENCHANTMENT_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.EXPERIENCE;
import static user11681.soulboundarmory.component.statistics.StatisticType.KNOCKBACK;
import static user11681.soulboundarmory.component.statistics.StatisticType.LEVEL;
import static user11681.soulboundarmory.component.statistics.StatisticType.REACH;
import static user11681.soulboundarmory.component.statistics.StatisticType.SKILL_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;

public class DaggerStorage extends WeaponStorage<DaggerStorage> {
    public DaggerStorage(final SoulboundComponentBase component, final Item item) {
        super(component, item);

        this.statistics = Statistics.create()
                .category(DATUM, EXPERIENCE, LEVEL, SKILL_POINTS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS)
                .category(ATTRIBUTE, ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL_STRIKE_PROBABILITY, EFFICIENCY, ATTACK_RANGE, REACH)
                .min(2, ATTACK_SPEED, ATTACK_DAMAGE, REACH)
                .max(1, CRITICAL_STRIKE_PROBABILITY).build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).asString().toLowerCase();

            return enchantment.isAcceptableItem(new ItemStack(this.item)) && !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && (enchantment == IMPACT || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skillStorage = new SkillStorage(Skills.NOURISHMENT, Skills.THROWING, Skills.SHADOW_CLONE, Skills.RETURN, Skills.SNEAK_RETURN);
    }

    public static DaggerStorage get(final Entity entity) {
        return Components.WEAPON_COMPONENT.get(entity).getStorage(StorageType.DAGGER_STORAGE);
    }

    @Override
    public Text getName() {
        return Mappings.SOULBOUND_DAGGER;
    }

    @Override
    public StorageType<DaggerStorage> getType() {
        return StorageType.DAGGER_STORAGE;
    }

    @Override
    public Item getConsumableItem() {
        return Items.WOODEN_SWORD;
    }

    public Map<String, EntityAttributeModifier> getModifiers() {
        return CollectionUtil.hashMap(
                new String[]{
                        EntityAttributes.ATTACK_SPEED.getId(),
                        EntityAttributes.ATTACK_DAMAGE.getId(),
                        ReachEntityAttributes.ATTACK_RANGE.getId(),
                        ReachEntityAttributes.REACH.getId()
                },
                new EntityAttributeModifier(ItemModifiers.ATTACK_SPEED_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(ATTACK_SPEED), ADDITION),
                new EntityAttributeModifier(ItemModifiers.ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(ATTACK_DAMAGE), ADDITION),
                new EntityAttributeModifier(Main.ATTACK_RANGE_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(ATTACK_RANGE), ADDITION),
                new EntityAttributeModifier(Main.REACH_MODIFIER_UUID, "Tool modifier", this.getAttributeRelative(REACH), ADDITION)
        );
    }

    @Override
    public ArrayMap<Statistic, Text> getScreenAttributes() {
        final ArrayMap<Statistic, Text> entries = new OrderedArrayMap<>();
        final Statistic critical = this.getStatistic(CRITICAL_STRIKE_PROBABILITY);

        entries.put(this.getStatistic(ATTACK_SPEED), new TranslatableText("%s%s: %s", Mappings.ATTACK_SPEED_FORMAT, Mappings.ATTACK_SPEED_NAME, this.formatStatistic(ATTACK_SPEED)));
        entries.put(this.getStatistic(ATTACK_DAMAGE), new TranslatableText("%s%s: %s", Mappings.ATTACK_DAMAGE_FORMAT, Mappings.ATTACK_DAMAGE_NAME, this.formatStatistic(ATTACK_DAMAGE)));
        entries.put(critical, new TranslatableText("%s%s: %s%%", Mappings.CRITICAL_STRIKE_PROBABILITY_FORMAT, Mappings.CRITICAL_STRIKE_PROBABILITY_NAME, FORMAT.format(critical.doubleValue() * 100)));
        entries.put(this.getStatistic(EFFICIENCY), new TranslatableText("%s%s: %s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.WEAPON_EFFICIENCY_NAME, this.formatStatistic(EFFICIENCY)));

        return entries;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<Text> getTooltip() {
        final NumberFormat format = DecimalFormat.getInstance();
        final List<Text> tooltip = new ArrayList<>();

        tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.ATTACK_SPEED_FORMAT, format.format(this.getAttribute(ATTACK_SPEED)), Mappings.ATTACK_SPEED_NAME)));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.ATTACK_DAMAGE_FORMAT, format.format(this.getAttributeTotal(ATTACK_DAMAGE)), Mappings.ATTACK_DAMAGE_NAME)));
        tooltip.add(new LiteralText(""));
        tooltip.add(new LiteralText(""));

        if (this.getAttribute(CRITICAL_STRIKE_PROBABILITY) > 0) {
            tooltip.add(new LiteralText(String.format(" %s%s%% %s", Mappings.CRITICAL_STRIKE_PROBABILITY_FORMAT, format.format(this.getAttribute(CRITICAL_STRIKE_PROBABILITY) * 100), Mappings.CRITICAL_STRIKE_PROBABILITY_NAME)));
        }

        if (this.getAttribute(EFFICIENCY) > 0) {
            tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.TOOL_EFFICIENCY_FORMAT, format.format(this.getAttribute(EFFICIENCY)), Mappings.TOOL_EFFICIENCY_NAME)));
        }

        return tooltip;
    }

    @Override
    public double getIncrease(final StatisticType statistic) {
        return statistic == ATTACK_SPEED
                ? 0.04
                : statistic == ATTACK_DAMAGE
                ? 0.05
                : statistic == CRITICAL_STRIKE_PROBABILITY
                ? 0.02
                : statistic == KNOCKBACK
                ? 0.2
                : statistic == EFFICIENCY
                ? 0.15
                : 0;
    }
}
