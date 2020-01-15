package transfarmer.adventureitems.capability;


public class SoulWeapon implements ISoulWeapon {
    private WeaponType current = null;

    public enum WeaponType {
        BIGSWORD("BIGSWORD"),
        SWORD("SWORD"),
        DAGGER("DAGGER");

        private String name;

        WeaponType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static WeaponType get(String name) {
            switch (name) {
                case "BIGSWORD":
                    return BIGSWORD;
                case "SWORD":
                    return SWORD;
                case "DAGGER":
                    return DAGGER;
            }

            return null;
        }
    }

    @Override
    public WeaponType getCurrentType() {
        return current;
    }

    public void setCurrentType(WeaponType weaponType) {
        current = weaponType;
    }

}
