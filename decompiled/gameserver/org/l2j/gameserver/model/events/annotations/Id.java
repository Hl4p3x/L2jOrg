// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Annotation;

@Repeatable(Ids.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Id {
    int[] value();
}
