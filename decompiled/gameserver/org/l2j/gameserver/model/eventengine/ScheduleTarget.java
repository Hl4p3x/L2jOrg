// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface ScheduleTarget {
}
