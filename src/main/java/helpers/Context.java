package helpers;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sasha on 27.03.16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class Context {
    @NotNull
    private final Map<Class, Object> contextMap = new HashMap<>();

    public void put(@NotNull Class clazz, @NotNull Object object){
        contextMap.put(clazz, object);
    }

    @NotNull
    public <T> T get(@NotNull Class<T> clazz){
        //noinspection unchecked
        return (T) contextMap.get(clazz);
    }
}
