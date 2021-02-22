package io.github.ezforever.thatorthis;

import net.fabricmc.loader.ModContainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class HookedModContainerList implements InvocationHandler {
    final List<ModContainer> list;
    final Set<String> modDirs;

    HookedModContainerList(List<ModContainer> list, Set<String> modDirs) {
        this.list = Collections.synchronizedList(list);
        this.modDirs = modDirs;
    }

    // --- Implements InvocationHandler

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals("iterator"))
            FabricInternals.onHook(this);
        return method.invoke(list, args);
    }
}

