// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.olympiad;

public enum OlympiadState
{
    SCHEDULED, 
    STARTED, 
    MATCH_MAKING;
    
    public boolean matchesInProgress() {
        return this == OlympiadState.STARTED || this == OlympiadState.MATCH_MAKING;
    }
}
