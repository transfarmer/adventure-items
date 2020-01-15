package transfarmer.adventureitems.capabilities;


public class SoulWeapon implements ISoulWeapon {
    private Type current = null;

    public enum Type {
        BIGSWORD("BIGSWORD"),
        SWORD("SWORD"),
        DAGGER("DAGGER");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Type get(String name) {
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
    public Type getCurrentType() {
        return current;
    }

    public void setCurrentType(Type type) {
        current = type;
    }

}
