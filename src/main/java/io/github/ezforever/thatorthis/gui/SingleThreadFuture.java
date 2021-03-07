package io.github.ezforever.thatorthis.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Consumer;
import java.util.function.Function;

// Um. A Future<> implementation. For single-threaded applications.
// Stupid. I know. But Minecraft's threading model for Screens is nothing but stupid.
@Environment(EnvType.CLIENT)
public class SingleThreadFuture<T> {
    private boolean resolved = false;
    private T result;
    private Consumer<T> delayedCallback;

    public SingleThreadFuture() {
        // Empty
    }

    public SingleThreadFuture(T result) {
        resolve(result);
    }

    public void resolve(T result) {
        // A resolved Future is a useless Future (unless others keep `then()`ing on it)
        if(resolved)
            throw new UnsupportedOperationException("The future has come");

        this.resolved = true;
        this.result = result;

        // Resolve delayed callback in the order they're `then()`ed
        if(delayedCallback != null)
            delayedCallback.accept(result);
    }

    public void resolve() {
        // For `SingleThreadFuture`<Void>`s
        resolve(null);
    }

    public SingleThreadFuture<T> then(Consumer<T> callback) {
        if(resolved) {
            // If already resolved, just call the callback and call it a day
            callback.accept(result);
            return this;
        } else {
            // Otherwise, create a new Future and link it to the chain
            SingleThreadFuture<T> newFuture = new SingleThreadFuture<>();
            delayedCallback = (T result) -> {
                callback.accept(result);
                newFuture.resolve(result);
            };
            return newFuture;
        }
    }

    public SingleThreadFuture<T> then(Runnable callback) {
        // Similar but for `SingleThreadFuture`<Void>`s
        if(resolved) {
            callback.run();
            return this;
        } else {
            SingleThreadFuture<T> newFuture = new SingleThreadFuture<>();
            delayedCallback = (T result) -> {
                callback.run();
                newFuture.resolve(result);
            };
            return newFuture;
        }
    }

    public <U> SingleThreadFuture<U> then(Function<T, U> callback) {
        // If need to map on the result
        SingleThreadFuture<U> newFuture = new SingleThreadFuture<>();
        if(resolved) {
            newFuture.resolve(callback.apply(result));
        } else {
            delayedCallback = (T result) -> {
                U newResult = callback.apply(result);
                newFuture.resolve(newResult);
            };
        }
        return newFuture;
    }
}
