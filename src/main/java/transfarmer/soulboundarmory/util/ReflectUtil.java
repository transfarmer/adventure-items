package transfarmer.soulboundarmory.util;

import net.minecraft.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
public class ReflectUtil {
    @NotNull
    public static <T> Capability<T> createCapability(final Class<T> type, final IStorage<T> storage, final Callable<? extends T> factory) {
        try {
            final Constructor<Capability> constructor = Capability.class.getDeclaredConstructor(String.class, IStorage.class, Callable.class);
            constructor.setAccessible(true);

            final Capability<T> capability = constructor.newInstance(type.getName().intern(), storage, factory);
            constructor.setAccessible(false);

            return capability;
        } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
            Main.LOGGER.error(exception);
        }

        return null;
    }

    @Nonnull
    public static <T, U> U getFieldValue(final Class<?> clazz, final T object, final String fieldName) {
        try {
            final Field field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);
            final U value = (U) field.get(object);
            field.setAccessible(false);

            return value;
        } catch (final NoSuchFieldException | IllegalAccessException exception) {
            Main.LOGGER.error(exception);
        }

        return null;
    }

    public static UUID getAttackDamageModifier() {
        return getFieldValue(Item.class, null, Main.IS_DEBUG ? "ATTACK_DAMAGE_MODIFIER" : "field_111210_e");
    }

    public static UUID getAttackSpeedModifier() {
        return getFieldValue(Item.class, null, Main.IS_DEBUG ? "ATTACK_SPEED_MODIFIER": "field_185050_h");
    }
}
