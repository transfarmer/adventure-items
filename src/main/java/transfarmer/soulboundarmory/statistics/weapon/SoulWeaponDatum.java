package transfarmer.soulboundarmory.statistics.weapon;

import transfarmer.soulboundarmory.statistics.SoulDatum;

public class SoulWeaponDatum extends SoulDatum {
    public static final SoulDatum WEAPON_DATA = new SoulWeaponDatum();

    public SoulDatum attackCooldown;

    protected SoulWeaponDatum(final int index, final String name) {
        super(index, name);
    }

    protected SoulWeaponDatum() {
        super();

        this.attackCooldown = new SoulWeaponDatum(this.index++, "attackCooldown");
        this.data.add(this.attackCooldown);
    }
}
