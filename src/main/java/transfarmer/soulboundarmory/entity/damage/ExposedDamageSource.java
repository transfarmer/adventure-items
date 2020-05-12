package transfarmer.soulboundarmory.entity.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import transfarmer.farmerlib.reflect.FieldWrapper;

public class ExposedDamageSource extends DamageSource {
    protected final DamageSource delegate;
    public final FieldWrapper<Boolean, DamageSource> unblockable;

    protected ExposedDamageSource(final DamageSource delegate) {
        super(delegate.name);

        this.delegate = delegate;
        this.unblockable = new FieldWrapper<>(delegate, "isUnblockable");
    }

    public static ExposedDamageSource player(final PlayerEntity player) {
        return new ExposedDamageSource(DamageSource.player(player));
    }
}
