// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.mission;

import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionStatus;
import java.util.List;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;

public class BossMissionHandler extends AbstractMissionHandler
{
    private final int _amount;
    
    private BossMissionHandler(final MissionDataHolder holder) {
        super(holder);
        this._amount = holder.getRequiredCompletions();
    }
    
    public void init() {
        Listeners.Monsters().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_ATTACKABLE_KILL, (Consumer)this::onAttackableKill, (Object)this));
    }
    
    private void onAttackableKill(final OnAttackableKill event) {
        final Attackable monster = event.getTarget();
        final Player player = event.getAttacker();
        if (monster.isRaid() && monster.getInstanceId() > 0 && player != null) {
            final Party party = player.getParty();
            if (party != null) {
                final CommandChannel channel = party.getCommandChannel();
                final List<Player> members = (List<Player>)((channel != null) ? channel.getMembers() : party.getMembers());
                members.stream().filter(member -> MathUtil.isInsideRadius3D(member, (ILocational)monster, Config.ALT_PARTY_RANGE)).forEach((Consumer<? super Object>)this::processPlayerProgress);
            }
            else {
                this.processPlayerProgress(player);
            }
        }
    }
    
    private void processPlayerProgress(final Player player) {
        final MissionPlayerData entry = this.getPlayerEntry(player, true);
        if (entry.getStatus() == MissionStatus.NOT_AVAILABLE) {
            if (entry.increaseProgress() >= this._amount) {
                entry.setStatus(MissionStatus.AVAILABLE);
            }
            this.storePlayerEntry(entry);
        }
    }
    
    public static class Factory implements MissionHandlerFactory
    {
        public AbstractMissionHandler create(final MissionDataHolder data) {
            return new BossMissionHandler(data);
        }
        
        public String handlerName() {
            return "boss";
        }
    }
}
