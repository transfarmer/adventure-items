package transfarmer.soulboundarmory.reflect;

import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.util.ReflectUtil;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

@SuppressWarnings({"ConstantConditions", "unchecked"})
public class FieldWrapper {
    protected final Field field;
    protected final Object object;

    public FieldWrapper(final String field) {
        this(null, field);
    }

    public FieldWrapper(final Field field) {
        this(null, field);
    }

    public FieldWrapper(final Object object, final String field) {
        this(object, ReflectUtil.getNewestField(object, field));
    }

    public FieldWrapper(final Object object, final Field field) {
        this.object = object;
        this.field = field;
        this.field.setAccessible(true);
    }

    @Nonnull
    public <T> T get() {
        try {
            return (T) this.field.get(this.object);
        } catch (final IllegalAccessException exception) {
            Main.LOGGER.error(exception);
        }

        return null;
    }

    public void set(final Object value) {
        try {
            this.field.set(this.object, value);
        } catch (final IllegalAccessException exception) {
            Main.LOGGER.error(exception);
        }
    }
}
