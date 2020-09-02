// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;

public class EventMethodNotification
{
    private final AbstractEventManager<?> manager;
    private final Method method;
    
    public EventMethodNotification(final AbstractEventManager<?> manager, final String methodName) throws NoSuchMethodException {
        this.manager = manager;
        this.method = manager.getClass().getDeclaredMethod(methodName, (Class<?>[])new Class[0]);
    }
    
    public AbstractEventManager<?> getManager() {
        return this.manager;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public void execute() throws Exception {
        if (this.method.trySetAccessible()) {
            if (Modifier.isStatic(this.method.getModifiers())) {
                this.method.invoke(null, new Object[0]);
            }
            else {
                this.method.invoke(this.manager, new Object[0]);
            }
        }
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/String;, this.manager.getClass(), this.method.getName());
    }
}
