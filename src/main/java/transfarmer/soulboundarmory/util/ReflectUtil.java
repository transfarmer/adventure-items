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
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
public class ReflectUtil {
    @NotNull
    public static <T> Capability<T> createCapability(final Class<T> type, final IStorage<T> storage,
                                                     final Callable<? extends T> factory) {
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

    public static <U> U getFieldValue(final Object object, final String fieldName) {
        return getFieldValue(object.getClass(), object, fieldName);
    }

    @Nonnull
    public static <U> U getFieldValue(final Class<?> clazz, final Object object, final String fieldName) {
        return getFieldValue(object, getNewestField(clazz, fieldName));
    }

    @Nonnull
    public static <T> T getFieldValue(final Object object, final Field field) {
        try {
            final T value;

            field.setAccessible(true);
            value = (T) field.get(object);
            field.setAccessible(false);

            return value;
        } catch (final IllegalAccessException exception) {
            Main.LOGGER.error(exception);
        }

        return null;
    }

    public static void setField(final Object object, final String fieldName, final Object value) {
        setField(object.getClass(), object, fieldName, value);
    }

    public static void setField(final Class<?> clazz, final Object object, final String fieldName, final Object value) {
        try {
            final Field field = getNewestField(clazz, fieldName);

            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        } catch (final IllegalAccessException exception) {
            Main.LOGGER.error(exception);
        }
    }

    public static Method getNewestMethod(final Object object, final String methodName, final Class<?>... parameterTypes) {
        return getNewestMethod(object.getClass(), methodName, parameterTypes);
    }

    public static Method getNewestMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (final NoSuchMethodException exception) {
            return getNewestMethod(clazz.getSuperclass(), methodName, parameterTypes);
        }
    }

    public static Field getNewestField(final Object object, final String fieldName) {
        return getNewestField(object.getClass(), fieldName);
    }

    private static Field getNewestField(final Class<?> clazz, final String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (final NoSuchFieldException exception) {
            return getNewestField(clazz.getSuperclass(), fieldName);
        }
    }

    public static UUID getAttackDamageModifier() {
        return getFieldValue(Item.class, null, Main.IS_DEBUG ? "ATTACK_DAMAGE_MODIFIER" : "field_111210_e");
    }

    public static UUID getAttackSpeedModifier() {
        return getFieldValue(Item.class, null, Main.IS_DEBUG ? "ATTACK_SPEED_MODIFIER" : "field_185050_h");
    }
}
