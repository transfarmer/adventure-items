package transfarmer.soulweapons.capability;

import java.util.function.BiConsumer;

public class SoulWeaponHelper {
    public static void forEachDatumAndAttribute(BiConsumer<Integer, Integer> datumConsumer,
                                                BiConsumer<Integer, Integer> attributeConsumer) {
        for (int weaponIndex = 0; weaponIndex <= 2; weaponIndex++) {
            for (int valueIndex = 0; valueIndex <= 3; valueIndex++) {
                datumConsumer.accept(weaponIndex, valueIndex);
            }

            for (int valueIndex = 0; valueIndex <= 4; valueIndex++) {
                attributeConsumer.accept(weaponIndex, valueIndex);
            }
        }
    }
}
