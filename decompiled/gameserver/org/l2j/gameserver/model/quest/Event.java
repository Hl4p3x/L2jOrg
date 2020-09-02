// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.quest;

import org.l2j.gameserver.model.actor.instance.Player;

public abstract class Event extends Quest
{
    public Event() {
        super(-1);
    }
    
    public abstract boolean eventStart(final Player eventMaker);
    
    public abstract boolean eventStop();
    
    public abstract boolean eventBypass(final Player activeChar, final String bypass);
}
