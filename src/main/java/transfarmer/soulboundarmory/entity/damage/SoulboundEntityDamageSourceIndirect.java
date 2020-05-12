package transfarmer.soulboundarmory.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SoulboundEntityDamageSourceIndirect extends EntityDamageSourceIndirect implements ISoulboundDamageSource {
    private ItemStack itemStack;

    public SoulboundEntityDamageSourceIndirect(final String damageType, final Entity source,
                                               @Nullable final Entity indirectSource) {
        super(damageType, source, indirectSource);
    }


    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public DamageSource setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;

        return this;
    }

    @Override
    @NotNull
    public DamageSource setProjectile() {
        super.setProjectile();

        return this;
    }
}
