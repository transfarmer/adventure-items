package transfarmer.adventureitems;


public class SoulWeapons {
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
}
