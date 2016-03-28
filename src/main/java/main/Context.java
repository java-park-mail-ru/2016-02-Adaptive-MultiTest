package main;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a.serebrennikova on 27.03.16.
 */
public class Context {
    @NotNull
    private final Map<Class, Object> contextMap = new HashMap<>();

    public void put(@NotNull Class clazz, @NotNull Object object){
        contextMap.put(clazz, object);
    }

    @NotNull
    public <T> T get(@NotNull Class<T> clazz){
        return (T) contextMap.get(clazz);
    }
}
