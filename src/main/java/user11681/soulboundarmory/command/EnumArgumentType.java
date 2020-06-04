package user11681.soulboundarmory.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import user11681.soulboundarmory.Main;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T>{
    protected final Class<T> clazz;
    protected final List<T> values;

    protected EnumArgumentType(final Class<T> enumClass, final Predicate<T> include) {
        this.clazz = enumClass;

        final List<T> values = this.values = new ArrayList<>();

        for (final Field field : enumClass.getDeclaredFields()) {
            try {
                //noinspection unchecked
                final T value = (T) field.get(null);
                if (include.test(value)) {
                    values.add(value);
                }
            } catch (final IllegalAccessException exception) {
                Main.LOGGER.error(String.format("Cannot access enum %s:", field.getName()), exception);
            }
        }
    }

    protected EnumArgumentType(final Class<T> enumClass) {
        this.clazz = enumClass;
        final List<T> values = this.values = new ArrayList<>();

        for (final Field field : enumClass.getDeclaredFields()) {
            try {
                //noinspection unchecked
                values.add((T) field.get(null));
            } catch (final IllegalAccessException exception) {
                Main.LOGGER.error(String.format("Cannot access enum %s:", field.getName()), exception);
            }
        }
    }

    @Override
    public T parse(final StringReader reader) throws CommandSyntaxException {
        return Enum.valueOf(this.clazz, reader.readString());
    }

    public static <T extends Enum<T>> EnumArgumentType<T> enumeration(final Class<T> enumClass) {
        return new EnumArgumentType<>(enumClass);
    }

    public static <T extends Enum<T>> EnumArgumentType<T> include(final Class<T> enumClass, final Predicate<T> include) {
        return new EnumArgumentType<>(enumClass, include);
    }

    public static <T extends Enum<T>> EnumArgumentType<T> exclude(final Class<T> enumClass, final Predicate<T> exclude) {
        return new EnumArgumentType<>(enumClass, exclude.negate());
    }

    public List<T> getValues() {

        return values;
    }
}
