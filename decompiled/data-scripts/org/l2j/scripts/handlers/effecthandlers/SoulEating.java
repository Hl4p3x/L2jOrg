// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class SoulEating extends AbstractEffect
{
    private final int expNeeded;
    private final int power;
    
    private SoulEating(final StatsSet params) {
        this.expNeeded = params.getInt("experience");
        this.power = params.getInt("power");
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effected)) {
            effected.addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)effected, EventType.ON_PLAYABLE_EXP_CHANGED, event -> this.onExperienceReceived(event.getPlayable(), event.getNewExp() - event.getOldExp()), (Object)this));
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (GameUtils.isPlayer((WorldObject)effected)) {
            effected.removeListenerIf(EventType.ON_PLAYABLE_EXP_CHANGED, listener -> listener.getOwner() == this);
        }
    }
    
    public void pump(final Creature effected, final Skill skill) {
        effected.getStats().mergeAdd(Stat.MAX_SOULS, (double)this.power);
    }
    
    private void onExperienceReceived(final Playable playable, final long exp) {
        if (GameUtils.isPlayer((WorldObject)playable) && exp >= this.expNeeded) {
            final Player player = playable.getActingPlayer();
            final int maxSouls = (int)player.getStats().getValue(Stat.MAX_SOULS, 0.0);
            if (player.getChargedSouls() >= maxSouls) {
                playable.sendPacket(SystemMessageId.SOUL_CANNOT_BE_ABSORBED_ANYMORE);
                return;
            }
            player.increaseSouls(1);
            if (GameUtils.isNpc(player.getTarget())) {
                final Npc npc = (Npc)playable.getTarget();
                player.broadcastPacket((ServerPacket)new ExSpawnEmitter((Creature)player, (Creature)npc, ExSpawnEmitter.SpawnEmitterType.BLUE_SOUL_EATEN), 500);
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new SoulEating(data);
        }
        
        public String effectName() {
            return "soul-eating";
        }
    }
}
