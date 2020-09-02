// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.custom.listeners;

import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.annotations.Priority;
import org.l2j.gameserver.model.events.annotations.NpcLevelRange;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.events.impl.item.OnItemCreate;
import org.l2j.gameserver.model.events.annotations.Range;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
import org.l2j.gameserver.model.events.annotations.Id;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAttack;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import java.util.function.Consumer;
import org.l2j.gameserver.engine.scripting.annotations.Disabled;
import org.l2j.scripts.ai.AbstractNpcAI;

@Disabled
public class ListenerTest extends AbstractNpcAI
{
    private static final int[] ELPIES;
    
    private ListenerTest() {
        this.setAttackableAttackId((Consumer)this::onAttackableAttack, ListenerTest.ELPIES);
        Listeners.Global().addListener((AbstractEventListener)new ConsumerEventListener(Listeners.Global(), EventType.ON_PLAYER_DLG_ANSWER, event -> this.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/actor/instance/Player;II)Ljava/lang/String;, this.getClass().getSimpleName(), event.getActiveChar(), event.getAnswer(), event.getMessageId())), (Object)this));
    }
    
    private void onAttackableAttack(final OnAttackableAttack event) {
        this.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/actor/Attackable;ILorg/l2j/gameserver/engine/skill/api/Skill;)Ljava/lang/String;, this.getClass().getSimpleName(), event.getClass().getSimpleName(), event.getAttacker(), event.getTarget(), event.getDamage(), event.getSkill()));
    }
    
    @RegisterEvent(EventType.ON_CREATURE_DEATH)
    @RegisterType(ListenerRegisterType.NPC)
    @Id({ 20432 })
    private void onCreatureKill(final OnCreatureDeath event) {
        this.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Lorg/l2j/gameserver/model/actor/Creature;Lorg/l2j/gameserver/model/actor/Creature;)Ljava/lang/String;, this.getClass().getSimpleName(), event.getClass().getSimpleName(), event.getAttacker(), event.getTarget()));
    }
    
    @RegisterEvent(EventType.ON_CASTLE_SIEGE_START)
    @RegisterType(ListenerRegisterType.CASTLE)
    @Range(from = 1, to = 9)
    private void onSiegeStart(final OnCastleSiegeStart event) {
        this.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), event.getSiege().getCastle().getName(), event.getSiege().getCastle().getId()));
    }
    
    @RegisterEvent(EventType.ON_ITEM_CREATE)
    @RegisterType(ListenerRegisterType.ITEM)
    @Id({ 5575 })
    private void onItemCreate(final OnItemCreate event) {
        this.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/item/instance/Item;Lorg/l2j/gameserver/model/actor/Creature;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, this.getClass().getSimpleName(), event.getItem(), event.getActiveChar(), event.getProcess(), event.getReference()));
    }
    
    @RegisterEvent(EventType.ON_CREATURE_DEATH)
    @RegisterType(ListenerRegisterType.NPC)
    @NpcLevelRange(from = 1, to = 10)
    @Priority(100)
    private void OnCreatureKill(final OnCreatureDeath event) {
        if (Rnd.get(100) >= 70) {
            return;
        }
        if (event.getAttacker() != null && GameUtils.isPlayable((WorldObject)event.getAttacker()) && GameUtils.isAttackable((WorldObject)event.getTarget())) {
            final Attackable monster = (Attackable)event.getTarget();
            monster.dropItem((Creature)event.getAttacker().getActingPlayer(), new ItemHolder(57, (long)Rnd.get(100, 1000)));
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogin(final OnPlayerLogin event) {
        this.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this.getClass().getSimpleName(), event.getPlayer()));
    }
    
    @RegisterEvent(EventType.ON_CREATURE_DEATH)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    @Priority(Integer.MAX_VALUE)
    private TerminateReturn onPlayerDeath(final OnCreatureDeath event) {
        if (event.getTarget().isGM()) {
            this.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/actor/Creature;)Ljava/lang/String;, this.getClass().getSimpleName(), event.getTarget()));
            return new TerminateReturn(true, true, true);
        }
        return null;
    }
    
    public static AbstractNpcAI provider() {
        return new ListenerTest();
    }
    
    static {
        ELPIES = new int[] { 20432 };
    }
}
