// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.timers;

@FunctionalInterface
public interface IEventTimerEvent<T>
{
    void onTimerEvent(final TimerHolder<T> holder);
}
