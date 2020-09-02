// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.List;
import java.lang.reflect.Method;

public class EventMethodNotification
{
    private final AbstractEventManager<?> _manager;
    private final Method _method;
    private final Object[] _args;
    
    public EventMethodNotification(final AbstractEventManager<?> manager, final String methodName, final List<Object> args) throws NoSuchMethodException {
        this._manager = manager;
        this._method = manager.getClass().getDeclaredMethod(methodName, (Class<?>[])args.stream().map((Function<? super Object, ?>)Object::getClass).toArray(Class[]::new));
        this._args = args.toArray();
    }
    
    public AbstractEventManager<?> getManager() {
        return this._manager;
    }
    
    public Method getMethod() {
        return this._method;
    }
    
    public void execute() throws Exception {
        if (Modifier.isStatic(this._method.getModifiers())) {
            this.invoke(null);
        }
        else {
            for (final Method method : this._manager.getClass().getMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && this._manager.getClass().isAssignableFrom(method.getReturnType()) && method.getParameterCount() == 0) {
                    final Object instance = method.invoke(null, new Object[0]);
                    this.invoke(instance);
                }
            }
        }
    }
    
    private void invoke(final Object instance) throws Exception {
        final boolean wasAccessible = this._method.canAccess(instance);
        if (!wasAccessible) {
            this._method.setAccessible(true);
        }
        this._method.invoke(instance, this._args);
        this._method.setAccessible(wasAccessible);
    }
}
