package transfarmer.adventureitems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import transfarmer.adventureitems.SoulWeapons.WeaponType;
import transfarmer.adventureitems.capability.ISoulWeapon;

import java.util.function.Supplier;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.fml.network.NetworkEvent.*;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.*;

public class UpdateWeaponData {
    private WeaponType weaponType;
    private int level, points, special, maxSpecial, hardness, knockback, attackDamage, critical;

    public UpdateWeaponData(PacketBuffer buffer) {}

    public UpdateWeaponData(final WeaponType weaponType, final int level, final int points,
                            final int special, final int maxSpecial, final int hardness, final int knockback,
                            final int attackDamage, final int critical) {
        this.weaponType = weaponType;
        this.level = level;
        this.points = points;
        this.special = special;
        this.maxSpecial = maxSpecial;
        this.hardness = hardness;
        this.knockback = knockback;
        this.attackDamage = attackDamage;
        this.critical = critical;
    }

    public void encode(PacketBuffer buffer) {}

    public void handle(Supplier<Context> contextSupplier) {
        Context context = contextSupplier.get();
        DistExecutor.runWhenOn(CLIENT, () -> this::clientHandle);
        context.setPacketHandled(true);
    }

    @OnlyIn(CLIENT)
    public void clientHandle() {
        Minecraft.getInstance().player.getCapability(SOUL_WEAPON).ifPresent((ISoulWeapon capability) ->
            capability.setData(this.weaponType, this.level, this.points, this.special, this.maxSpecial,
                    this.hardness, this.knockback, this.attackDamage, this.critical)
        );
    }
}
