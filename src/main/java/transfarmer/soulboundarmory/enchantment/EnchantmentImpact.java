package transfarmer.soulboundarmory.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;

public class EnchantmentImpact extends Enchantment {
    protected EnchantmentImpact(final Rarity rarity, final EnumEnchantmentType type,
                                final EntityEquipmentSlot[] slots) {
        super(rarity, type, slots);

        this.setName("impact");
        this.setRegistryName(Main.MOD_ID, "impact");
    }

    @Override
    public int getMinEnchantability(final int enchantmentLevel) {
        return enchantmentLevel * 11 - 10;
    }

    @Override
    public int getMaxEnchantability(final int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float calcDamageByCreature(final int level, @NotNull final EnumCreatureAttribute creatureType) {
        return 1 + Math.max(0, level - 1) / 2F;
    }

    @Override
    @NotNull
    public String getName() {
        return String.format("enchantment.%s.%s", Main.MOD_ID, this.name);
    }

    @Override
    public boolean canApply(@NotNull final ItemStack stack) {
        return super.canApply(stack);
    }
}
