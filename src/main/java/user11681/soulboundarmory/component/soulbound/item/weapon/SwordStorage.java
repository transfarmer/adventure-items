package user11681.soulboundarmory.component.soulbound.item.weapon;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
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
import user11681.usersmanual.text.StringifiedText;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static user11681.soulboundarmory.Main.IMPACT;
import static user11681.soulboundarmory.component.statistics.Category.ATTRIBUTE;
import static user11681.soulboundarmory.component.statistics.Category.DATUM;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_DAMAGE;
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

public class SwordStorage extends WeaponStorage<SwordStorage> {
    protected int lightningCooldown;

    public SwordStorage(final SoulboundComponentBase component, final Item item) {
        super(component, item);

        this.statistics = Statistics.create()
                .category(DATUM, EXPERIENCE, LEVEL, SKILL_POINTS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS)
                .category(ATTRIBUTE, ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL_STRIKE_PROBABILITY, KNOCKBACK, EFFICIENCY, REACH)
                .min(1.6, ATTACK_SPEED).min(4, ATTACK_DAMAGE).min(3, REACH)
                .max(1, CRITICAL_STRIKE_PROBABILITY).build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).asString().toLowerCase();

            return enchantment.isAcceptableItem(new ItemStack(this.item)) && !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && (enchantment == IMPACT || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skillStorage = new SkillStorage(Skills.NOURISHMENT, Skills.SUMMON_LIGHTNING);
    }

    public static SwordStorage get(final Entity entity) {
        return Components.WEAPON_COMPONENT.get(entity).getStorage(StorageType.SWORD_STORAGE);
    }

    @Override
    public Text getName() {
        return Mappings.SOULBOUND_SWORD;
    }

    @Override
    public StorageType<SwordStorage> getType() {
        return StorageType.SWORD_STORAGE;
    }

    public int getLightningCooldown() {
        return this.lightningCooldown;
    }

    public void setLightningCooldown(final int ticks) {
        this.lightningCooldown = ticks;
    }

    public void resetLightningCooldown() {
        if (!this.getPlayer().isCreative()) {
            this.lightningCooldown = (int) Math.round(96 / this.getAttribute(ATTACK_SPEED));
        }
    }

    public Map<String, EntityAttributeModifier> getModifiers() {
        return CollectionUtil.hashMap(
                new String[]{
                        EntityAttributes.ATTACK_SPEED.getId(),
                        EntityAttributes.ATTACK_DAMAGE.getId(),
                },
                new EntityAttributeModifier(ItemModifiers.ATTACK_SPEED_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(ATTACK_SPEED), ADDITION),
                new EntityAttributeModifier(ItemModifiers.ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(ATTACK_DAMAGE), ADDITION)
        );
    }

    @Override
    public ArrayMap<Statistic, Text> getScreenAttributes() {
        final ArrayMap<Statistic, Text> entries = new OrderedArrayMap<>();

        entries.put(this.getStatistic(ATTACK_SPEED), new StringifiedText("%s%s: %s", Mappings.ATTACK_SPEED_FORMAT, Mappings.ATTACK_SPEED_NAME, this.formatStatistic(ATTACK_SPEED)));
        entries.put(this.getStatistic(ATTACK_DAMAGE), new StringifiedText("%s%s: %s", Mappings.ATTACK_DAMAGE_FORMAT, Mappings.ATTACK_DAMAGE_NAME, this.formatStatistic(ATTACK_DAMAGE)));
        entries.put(this.getStatistic(CRITICAL_STRIKE_PROBABILITY), new StringifiedText("%s%s: %s%%", Mappings.CRITICAL_STRIKE_PROBABILITY_FORMAT, Mappings.CRITICAL_STRIKE_PROBABILITY_NAME, this.formatStatistic(CRITICAL_STRIKE_PROBABILITY)));
        entries.put(this.getStatistic(KNOCKBACK), new StringifiedText("%s%s: %s", Mappings.KNOCKBACK_FORMAT, Mappings.KNOCKBACK_ATTRIBUTE_NAME, this.formatStatistic(KNOCKBACK)));
        entries.put(this.getStatistic(EFFICIENCY), new StringifiedText("%s%s: %s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.WEAPON_EFFICIENCY_NAME, this.formatStatistic(EFFICIENCY)));

        return entries;
    }

    @Environment(EnvType.CLIENT)
    public List<Text> getTooltip() {
        final NumberFormat format = DecimalFormat.getInstance();
        final List<Text> tooltip = new ArrayList<>();

        tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.ATTACK_SPEED_FORMAT, format.format(this.getAttribute(ATTACK_SPEED)), Mappings.ATTACK_SPEED_NAME.asFormattedString())));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.ATTACK_DAMAGE_FORMAT, format.format(this.getAttributeTotal(ATTACK_DAMAGE)), Mappings.ATTACK_DAMAGE_NAME.asFormattedString())));
        tooltip.add(new LiteralText(""));
        tooltip.add(new LiteralText(""));
        tooltip.add(new LiteralText(String.format(" %s%s%% %s", Mappings.CRITICAL_STRIKE_PROBABILITY_FORMAT, format.format(this.getAttribute(CRITICAL_STRIKE_PROBABILITY) * 100), Mappings.CRITICAL_STRIKE_PROBABILITY_NAME.asFormattedString())));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.KNOCKBACK_FORMAT, format.format(this.getAttribute(KNOCKBACK)), Mappings.KNOCKBACK_ATTRIBUTE_NAME.asFormattedString())));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Mappings.TOOL_EFFICIENCY_FORMAT, format.format(this.getAttribute(EFFICIENCY)), Mappings.TOOL_EFFICIENCY_NAME.asFormattedString())));

        return tooltip;
    }

    public Item getConsumableItem() {
        return Items.WOODEN_SWORD;
    }

    public double getIncrease(final StatisticType statistic) {
        return statistic == ATTACK_SPEED
                ? 0.03
                : statistic == ATTACK_DAMAGE
                ? 0.075
                : statistic == CRITICAL_STRIKE_PROBABILITY
                ? 0.015
                : statistic == KNOCKBACK
                ? 0.35
                : statistic == EFFICIENCY
                ? 0.2
                : 0;
    }

    public void tick() {
        if (!this.isClient) {
            if (this.lightningCooldown > 0) {
                this.lightningCooldown--;
            }
        }
    }

    @Override
    public void fromTag(@Nonnull final CompoundTag tag) {
        super.fromTag(tag);

        this.setLightningCooldown(tag.getInt("lightningCooldown"));
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull CompoundTag tag) {
        tag = super.toTag(tag);

        tag.putInt("lightningCooldown", this.getLightningCooldown());

        return tag;
    }
}
