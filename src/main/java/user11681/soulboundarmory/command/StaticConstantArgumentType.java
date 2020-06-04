package user11681.soulboundarmory.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.server.command.CommandSource;
import user11681.soulboundarmory.Main;
import user11681.usersmanual.reflect.ReflectUtil;

public class StaticConstantArgumentType<T> implements ArgumentType<T> {
    protected final Class<T> clazz;
    protected final Map<String, Field> validFields;

    protected StaticConstantArgumentType(final Class<T> clazz) {
        this.clazz = clazz;
        this.validFields = new LinkedHashMap<>();
    }

    @Override
    public T parse(final StringReader reader) throws CommandSyntaxException {
        final String input = reader.readString();
        final Map<String, Field> fields = this.validFields;

        for (final String name : fields.keySet()) {
            if (Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE).matcher(input).find()) {
                try {
                    //noinspection unchecked
                    return (T) fields.get(name).get(null);
                } catch (final IllegalAccessException ignored) {
                }
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.validFields.values().parallelStream().map(Field::getName).collect(Collectors.toList()), builder);
    }

    public static <T> StaticConstantArgumentType<T> allConstants(final Class<T> clazz) {
        final StaticConstantArgumentType<T> type = new StaticConstantArgumentType<>(clazz);
        final Map<String, Field> fields = type.validFields;

        for (final Field field : type.clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                fields.put(field.getName(), field);
            }
        }

        return type;
    }

    public static <T, U> StaticConstantArgumentType<T> allConstants(final Class<T> holderClass, final Class<U> fieldClass) {
        final StaticConstantArgumentType<T> type = new StaticConstantArgumentType<>(holderClass);
        final Map<String, Field> fields = type.validFields;

        for (final Field field : type.clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && fieldClass.isInstance(ReflectUtil.getFieldValue(field))) {
                fields.put(field.getName(), field);
            }
        }

        return type;
    }

    @SafeVarargs
    public static <T> StaticConstantArgumentType<T> excludeConstants(final Class<T> clazz, final T... values) {
        return excludeConstants(clazz, Arrays.asList(values));
    }

    @SafeVarargs
    public static <T> StaticConstantArgumentType<T> includeConstants(final Class<T> clazz, final T... values) {
        return includeConstants(clazz, Arrays.asList(values));
    }

    public static <T> StaticConstantArgumentType<T> excludeConstants(final Class<T> clazz, final List<T> values) {
        return excludeConstants(clazz, values::contains);
    }

    public static <T> StaticConstantArgumentType<T> includeConstants(final Class<T> clazz, final List<T> values) {
        return includeConstants(clazz, values::contains);
    }

    public static <T> StaticConstantArgumentType<T> excludeConstants(final Class<T> clazz, final Predicate<T> exclude) {
        return includeConstants(clazz, exclude.negate());
    }

    public static <T> StaticConstantArgumentType<T> includeConstants(final Class<T> clazz, final Predicate<T> include) {
        final StaticConstantArgumentType<T> type = new StaticConstantArgumentType<>(clazz);
        final Map<String, Field> fields = type.validFields;

        for (final Field field : type.clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                final String name = field.getName();

                try {
                    //noinspection unchecked
                    if (include.test((T) field.get(null))) {
                        fields.put(name, field);
                    }
                } catch (final IllegalAccessException exception) {
                    Main.LOGGER.warn(String.format("Unable to access public static final field %s.", name), exception);
                }
            }
        }

        return type;
    }
}
