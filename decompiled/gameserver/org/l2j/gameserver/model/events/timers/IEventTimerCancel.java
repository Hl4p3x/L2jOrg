// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.timers;

@FunctionalInterface
public interface IEventTimerCancel<T>
{
    void onTimerCancel(final TimerHolder<T> holder);
}
