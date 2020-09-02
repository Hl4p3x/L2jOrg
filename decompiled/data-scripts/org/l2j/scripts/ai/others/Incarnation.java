// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import org.l2j.gameserver.model.events.impl.character.OnCreatureAttack;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.annotations.Id;
import org.l2j.gameserver.model.events.annotations.Ids;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSpawn;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Incarnation extends AbstractNpcAI
{
    @RegisterEvent(EventType.ON_NPC_SPAWN)
    @RegisterType(ListenerRegisterType.NPC)
    @Ids({ @Id({ 13302 }), @Id({ 13303 }), @Id({ 13304 }), @Id({ 13305 }), @Id({ 13455 }), @Id({ 13456 }), @Id({ 13457 }) })
    public void onNpcSpawn(final OnNpcSpawn event) {
        final Npc npc = event.getNpc();
        if (npc.getSummoner() != null) {
            npc.getSummoner().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)npc, EventType.ON_CREATURE_ATTACK, e -> this.onOffense(npc, e.getAttacker(), (WorldObject)e.getTarget()), (Object)this));
            npc.getSummoner().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)npc, EventType.ON_CREATURE_SKILL_FINISH_CAST, e -> this.onOffense(npc, e.getCaster(), e.getTarget()), (Object)this));
        }
    }
    
    public void onOffense(final Npc npc, final Creature attacker, final WorldObject target) {
        if (attacker == target || npc.getSummoner() == null) {
            return;
        }
        npc.setRunning();
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { target });
    }
    
    public static AbstractNpcAI provider() {
        return new Incarnation();
    }
}
