// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class StaticMethodTask extends Task
{
    private final String className;
    private final String methodName;
    private final String[] args;
    
    public StaticMethodTask(final String className, final String methodName, final String[] args) {
        this.className = className;
        this.methodName = methodName;
        this.args = args;
    }
    
    @Override
    public void execute(final TaskExecutionContext context) throws RuntimeException {
        Class<?> classObject;
        try {
            classObject = Class.forName(this.className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.className), (Throwable)e);
        }
        Method methodObject;
        try {
            final Class<?>[] argTypes = (Class<?>[])new Class[] { String[].class };
            methodObject = classObject.getMethod(this.methodName, argTypes);
        }
        catch (NoSuchMethodException e2) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.methodName, this.className), (Throwable)e2);
        }
        final int modifiers = methodObject.getModifiers();
        if (!Modifier.isStatic(modifiers)) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.methodName, this.className));
        }
        try {
            methodObject.invoke(null, this.args);
        }
        catch (Exception e3) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.methodName, this.className));
        }
    }
}
