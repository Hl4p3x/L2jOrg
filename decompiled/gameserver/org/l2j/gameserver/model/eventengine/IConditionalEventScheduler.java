// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

public interface IConditionalEventScheduler
{
    boolean test();
    
    void run();
}
