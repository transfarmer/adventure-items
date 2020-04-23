package transfarmer.soulboundarmory.util;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public class ReflectionUtil {
    @SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
    @NotNull
    public static <T> Capability<T> createCapability(Class<T> type, IStorage<T> storage, Callable<? extends T> factory) {
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
}
