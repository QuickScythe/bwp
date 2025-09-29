package com.bwp.utils;

import java.util.Objects;

@FunctionalInterface
public interface MapEntryConsumer<K, V> {

    /**
     * Performs this operation on the given argument.
     *
     * @param key   Key.
     * @param value Value.
     */
    void accept(K key, V value);

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default MapEntryConsumer<K, V> andThen(MapEntryConsumer<? super K, ? super V> after) {
        Objects.requireNonNull(after);
        return (K k, V v) -> {
            accept(k, v);
            after.accept(k, v);
        };
    }
}
