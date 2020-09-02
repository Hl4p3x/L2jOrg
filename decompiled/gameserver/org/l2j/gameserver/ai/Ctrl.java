// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.actor.Creature;

public interface Ctrl
{
    Creature getActor();
    
    CtrlIntention getIntention();
    
    void setIntention(final CtrlIntention intention);
    
    void setIntention(final CtrlIntention intention, final Object... args);
    
    void notifyEvent(final CtrlEvent evt);
    
    void notifyEvent(final CtrlEvent evt, final Object arg0);
    
    void notifyEvent(final CtrlEvent evt, final Object arg0, final Object arg1);
}
