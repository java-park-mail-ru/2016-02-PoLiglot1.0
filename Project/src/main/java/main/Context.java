package main;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.lang.IllegalStateException;

/**
 * Created by Snach on 26.03.16.
 */
public class Context {
    @NotNull
    private final Map<Class, Object> contextMap = new HashMap<>();
    @SuppressWarnings("ConstantNamingConvention")
    private static final Logger logger = new Logger(Context.class);

    public void put(@NotNull Class clazz, @NotNull Object object){

        if (contextMap.containsKey(clazz)) {
            throw new IllegalStateException("Context already has a " + clazz.getName() + " instance");
        } else {
            contextMap.put(clazz, object);
        }
    }

    @Nullable
    public <T> T get(@NotNull Class<T> clazz){

        //noinspection unchecked
        if (!contextMap.containsKey(clazz)) {
            logger.log("Context don\'t have " + clazz.getName());
            return null;
        } else {
            //noinspection unchecked
            return (T) contextMap.get(clazz);
        }
    }
}
