// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.voicedcommandhandlers;

import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IVoicedCommandHandler;

public final class ExperienceGain implements IVoicedCommandHandler
{
    private final String[] COMMANDS;
    
    public ExperienceGain() {
        this.COMMANDS = new String[] { "expoff", "expon" };
    }
    
    public boolean useVoicedCommand(final String command, final Player player, final String params) {
        if (command.equals("expoff")) {
            if (player.getExpOff()) {
                player.addListener((AbstractEventListener)new FunctionEventListener((ListenersContainer)player, EventType.ON_PLAYABLE_EXP_CHANGED, event -> this.onExperienceReceived(event.getPlayable(), event.getNewExp() - event.getOldExp()), (Object)this));
                player.setExpOff(false);
                player.sendMessage("Experience gain is disabled.");
            }
        }
        else if (command.equals("expon") && !player.getExpOff()) {
            player.removeListenerIf(EventType.ON_PLAYABLE_EXP_CHANGED, listener -> listener.getOwner() == this);
            player.setExpOff(true);
            player.sendMessage("Experience gain is enabled.");
        }
        return true;
    }
    
    private TerminateReturn onExperienceReceived(final Playable playable, final long exp) {
        if (GameUtils.isPlayer((WorldObject)playable) && playable.getActingPlayer().isDead()) {
            return new TerminateReturn(false, false, false);
        }
        return new TerminateReturn(true, true, true);
    }
    
    public String[] getVoicedCommandList() {
        return this.COMMANDS;
    }
    
    public static IVoicedCommandHandler provider() {
        return (IVoicedCommandHandler)new ExperienceGain();
    }
}
