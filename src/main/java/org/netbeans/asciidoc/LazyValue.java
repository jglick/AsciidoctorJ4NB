package org.netbeans.asciidoc;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.jtrim.utils.ExceptionHelper;

public final class LazyValue<T> implements Supplier<T> {
    private final Supplier<? extends T> valueFactory;
    private final AtomicReference<T> valueRef;

    public LazyValue(Supplier<? extends T> valueFactory) {
        ExceptionHelper.checkNotNullArgument(valueFactory, "valueFactory");

        this.valueFactory = valueFactory;
        this.valueRef = new AtomicReference<>(null);
    }

    @Override
    public T get() {
        T result = valueRef.get();
        if (result == null) {
            result = valueFactory.get();
            if (!valueRef.compareAndSet(null, result)) {
                result = valueRef.get();
            }
        }
        return result;
    }
}
