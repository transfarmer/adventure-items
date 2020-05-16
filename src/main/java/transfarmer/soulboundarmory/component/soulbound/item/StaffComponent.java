package transfarmer.soulboundarmory.component.soulbound.item;

import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.skill.staff.SkillEndermanacle;
import transfarmer.soulboundarmory.skill.staff.SkillFireball;
import transfarmer.soulboundarmory.skill.staff.SkillHealing;
import transfarmer.soulboundarmory.skill.staff.SkillPenetration;
import transfarmer.soulboundarmory.skill.staff.SkillVulnerability;
import transfarmer.soulboundarmory.statistics.EnchantmentStorage;
import transfarmer.soulboundarmory.statistics.SkillStorage;
import transfarmer.soulboundarmory.statistics.Statistics;
import transfarmer.soulboundarmory.statistics.StatisticType;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static transfarmer.soulboundarmory.Main.IMPACT;
import static transfarmer.soulboundarmory.Main.STAFF_COMPONENT;
import static transfarmer.soulboundarmory.statistics.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.Category.DATUM;
import static transfarmer.soulboundarmory.statistics.Item.STAFF;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static transfarmer.soulboundarmory.statistics.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.SKILL_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.XP;

public class StaffComponent extends SoulboundItemComponent<StaffComponent> implements IStaffComponent {
    protected int fireballCooldown;
    protected int spell;

    public StaffComponent(final ItemStack itemStack) {
        super(itemStack);

        this.statistics = Statistics.builder()
                .category(DATUM, XP, LEVEL, SKILL_POINTS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS)
                .category(ATTRIBUTE, ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL_STRIKE_PROBABILITY)
                .min(0.48, ATTACK_SPEED).min(8, ATTACK_DAMAGE).build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).asString().toLowerCase();

            return !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && (enchantment == IMPACT || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skillStorage = new SkillStorage(new SkillHealing(), new SkillFireball(), new SkillVulnerability(), new SkillPenetration(), new SkillEndermanacle());
    }

    @Nonnull
    @Override
    public ComponentType<StaffComponent> getComponentType() {
        return STAFF_COMPONENT;
    }

    @Override
    public int getFireballCooldown() {
        return this.fireballCooldown;
    }

    @Override
    public void setFireballCooldown(final int ticks) {
        this.fireballCooldown = ticks;
    }

    @Override
    public void resetFireballCooldown() {
        this.fireballCooldown = (int) Math.round(20 / this.getAttribute(ATTACK_SPEED));
    }

    @Override
    public int getSpell() {
        return this.spell;
    }

    @Override
    public void setSpell(final int spell) {
        this.spell = spell;
    }

    @Override
    public void cycleSpells(final int spells) {
        this.spell = Math.abs((this.spell + spells) % 2);

        this.sync();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<String> getTooltip() {
        final NumberFormat format = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>();

        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_SPEED_FORMAT, format.format(this.getAttribute(ATTACK_SPEED)), Mappings.ATTACK_SPEED_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_DAMAGE_FORMAT, format.format(this.getAttributeTotal(ATTACK_DAMAGE)), Mappings.ATTACK_DAMAGE_NAME));

        tooltip.add("");
        tooltip.add("");

        if (this.getAttribute(CRITICAL_STRIKE_PROBABILITY) > 0) {
            tooltip.add(String.format(" %s%s%% %s", Mappings.CRITICAL_FORMAT, format.format(this.getAttribute(CRITICAL_STRIKE_PROBABILITY) * 100), Mappings.CRITICAL_NAME));
        }

        return tooltip;
    }

    @Override
    public double getIncrease(final StatisticType statistic) {
        return statistic == ATTACK_SPEED
                ? 0.08
                : statistic == ATTACK_DAMAGE
                ? 0.2
                : statistic == CRITICAL_STRIKE_PROBABILITY
                ? 0.04
                : 0;

    }
}
