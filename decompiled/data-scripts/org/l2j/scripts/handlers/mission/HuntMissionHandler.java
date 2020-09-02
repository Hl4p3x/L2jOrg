// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.mission;

import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.Config;
import java.util.Objects;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.function.Predicate;
import org.l2j.commons.util.Util;
import java.util.Arrays;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import java.util.List;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;

public class HuntMissionHandler extends AbstractMissionHandler
{
    private final int requiredLevel;
    private final int maxLevel;
    private final List<Integer> monsters;
    private final int classLevel;
    
    private HuntMissionHandler(final MissionDataHolder holder) {
        super(holder);
        this.requiredLevel = holder.getParams().getInt("minLevel", 0);
        this.maxLevel = holder.getParams().getInt("maxLevel", 127);
        this.classLevel = holder.getParams().getInt("classLevel", 0);
        final String monsters = holder.getParams().getString("monsters", "");
        this.monsters = Arrays.stream(monsters.split(" ")).filter(Util::isInteger).map((Function<? super String, ?>)Integer::parseInt).collect((Collector<? super Object, ?, List<Integer>>)Collectors.toList());
    }
    
    public void init() {
        Listeners.Monsters().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_ATTACKABLE_KILL, (Consumer)this::onKill, (Object)this));
    }
    
    private void onKill(final OnAttackableKill event) {
        final Attackable monster = event.getTarget();
        if (!this.monsters.isEmpty() && !this.monsters.contains(monster.getId())) {
            return;
        }
        final Player player = event.getAttacker();
        if (player.getLevel() < this.requiredLevel || player.getLevel() > this.maxLevel || player.getLevel() - monster.getLevel() > 5 || player.getClassId().level() < this.classLevel) {
            return;
        }
        if (!this.canStart(player)) {
            return;
        }
        final Party party = player.getParty();
        if (Objects.isNull(party)) {
            this.onKillProgress(player);
        }
        else {
            final CommandChannel channel = party.getCommandChannel();
            final List<Player> members = (List<Player>)(Objects.isNull(channel) ? party.getMembers() : channel.getMembers());
            members.stream().filter(member -> MathUtil.isInsideRadius3D(member, (ILocational)monster, Config.ALT_PARTY_RANGE)).forEach((Consumer<? super Object>)this::onKillProgress);
        }
    }
    
    private void onKillProgress(final Player player) {
        final MissionPlayerData entry = this.getPlayerEntry(player, true);
        if (entry.getStatus() == MissionStatus.NOT_AVAILABLE) {
            if (entry.increaseProgress() >= this.getRequiredCompletion()) {
                entry.setStatus(MissionStatus.AVAILABLE);
                this.notifyAvailablesReward(player);
            }
            this.storePlayerEntry(entry);
        }
    }
    
    public static class Factory implements MissionHandlerFactory
    {
        public AbstractMissionHandler create(final MissionDataHolder data) {
            return new HuntMissionHandler(data);
        }
        
        public String handlerName() {
            return "hunt";
        }
    }
}
