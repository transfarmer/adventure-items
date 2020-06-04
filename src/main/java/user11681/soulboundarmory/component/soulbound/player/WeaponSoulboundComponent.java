package user11681.soulboundarmory.component.soulbound.player;

import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.registry.ModItems;

public class WeaponSoulboundComponent extends SoulboundComponentBase {
    public WeaponSoulboundComponent(final PlayerEntity player) {
        super(player);

        this.store(new DaggerStorage(this, ModItems.SOULBOUND_DAGGER));
        this.store(new SwordStorage(this, ModItems.SOULBOUND_SWORD));
        this.store(new GreatswordStorage(this, ModItems.SOULBOUND_GREATSWORD));
        this.store(new StaffStorage(this, ModItems.SOULBOUND_STAFF));
    }

    @Nonnull
    @Override
    public ComponentType<SoulboundComponentBase> getComponentType() {
        return Components.WEAPON_COMPONENT;
    }
}
