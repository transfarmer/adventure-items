package transfarmer.soulboundarmory.capability.soulbound;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.statistics.SoulboundEnchantments;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.IndexedMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ENCHANTMENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;

public abstract class BaseEnchantable extends Base implements ICapabilityEnchantable {
    protected SoulboundEnchantments enchantments;

    protected BaseEnchantable(final ICapabilityType type, final IItem[] itemTypes, final ICategory[] categories,
                              final IStatistic[][] statistics, final double[][][] min, final Item[] items,
                              final Predicate<Enchantment> condition) {
        super(type, itemTypes, categories, statistics, min, items);

        this.enchantments = new SoulboundEnchantments(itemTypes, items, condition);
    }

    @Override
    public int getEnchantment(final IItem item, final Enchantment enchantment) {
        return this.getEnchantments(item).get(enchantment);
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

        this.addDatum(type, ENCHANTMENT_POINTS, -change);
        this.addDatum(type, SPENT_ENCHANTMENT_POINTS, change);

        this.enchantments.add(type, enchantment, change);
    }

    @Override
    public void resetEnchantments(final IItem item) {
        this.enchantments.reset(item);

        this.addDatum(item, ENCHANTMENT_POINTS, this.getDatum(item, SPENT_ENCHANTMENT_POINTS));
        this.setDatum(item, SPENT_ENCHANTMENT_POINTS, 0);
    }

    @Override
    public void reset(final IItem item, final ICategory category) {
        if (category == ENCHANTMENT) {
            this.resetEnchantments(item);
        } else {
            this.statistics.reset(item, category);

            this.addDatum(item, ATTRIBUTE_POINTS, this.getDatum(item, SPENT_ATTRIBUTE_POINTS));
            this.setDatum(item, SPENT_ATTRIBUTE_POINTS, 0);
        }
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
}
