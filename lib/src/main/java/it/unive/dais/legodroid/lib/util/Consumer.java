package it.unive.dais.legodroid.lib.util;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Functional interface that represents a function with a parameter of type {@link T} and no result type.
 * @param <T> the type of the parameter.
 */
@FunctionalInterface
public interface Consumer<T> {
    /**
     * Call the function.
     * @param data the parameter.
     */
    void call(T data) throws InterruptedException, ExecutionException, IOException;
}
