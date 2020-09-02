// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import java.nio.file.Path;
import org.l2j.gameserver.model.events.AbstractScript;

public abstract class AbstractEvent extends AbstractScript
{
    @Override
    public final String getScriptName() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public final Path getScriptPath() {
        return null;
    }
    
    public boolean isOnEvent(final Player player) {
        return false;
    }
    
    public boolean isBlockingExit(final Player player) {
        return false;
    }
    
    public boolean isBlockingDeathPenalty(final Player player) {
        return false;
    }
    
    public boolean canRevive(final Player player) {
        return true;
    }
    
    public abstract void sendMessage(final SystemMessageId messageId);
}
