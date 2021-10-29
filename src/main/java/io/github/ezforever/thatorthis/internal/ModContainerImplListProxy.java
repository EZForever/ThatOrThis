package io.github.ezforever.thatorthis.internal;

import net.fabricmc.loader.impl.ModContainerImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;

class ModContainerImplListProxy implements InvocationHandler {
    @SuppressWarnings("unchecked")
    public static List<ModContainerImpl> create(List<ModContainerImpl> list, Runnable callback) {
        return (List<ModContainerImpl>) Proxy.newProxyInstance(
                list.getClass().getClassLoader(),
                list.getClass().getInterfaces(),
                new ModContainerImplListProxy(list, callback)
        );
    }

    // ---

    private final List<ModContainerImpl> list;
    private final Runnable callback;

    private ModContainerImplListProxy(List<ModContainerImpl> list, Runnable callback) {
        this.list = Collections.synchronizedList(list);
        this.callback = callback;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals("iterator"))
            callback.run();
        return method.invoke(list, args);
    }
}
