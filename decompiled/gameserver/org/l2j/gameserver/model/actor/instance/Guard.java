// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Attackable;

public class Guard extends Attackable
{
    public Guard(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2GuardInstance);
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return GameUtils.isMonster(attacker) || super.isAutoAttackable(attacker);
    }
    
    @Override
    public void addDamage(final Creature attacker, final int damage, final Skill skill) {
        super.addDamage(attacker, damage, skill);
        this.getAI().startFollow(attacker);
        this.addDamageHate(attacker, 0, 10);
        World.getInstance().forEachVisibleObjectInRange(this, Guard.class, 500, guard -> {
            guard.getAI().startFollow(attacker);
            guard.addDamageHate(attacker, 0, 10);
        });
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        this.setRandomWalking(this.getTemplate().isRandomWalkEnabled());
        if (this.getWorldRegion().isActive()) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
    
    @Override
    public String getHtmlPath(final int npcId, final int val) {
        String pom = "";
        if (val == 0) {
            pom = Integer.toString(npcId);
        }
        else {
            pom = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npcId, val);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pom);
    }
    
    @Override
    public void onAction(final Player player, final boolean interact) {
        if (!this.canTarget(player)) {
            return;
        }
        if (this.getObjectId() != player.getTargetId()) {
            player.setTarget(this);
        }
        else if (interact) {
            if (this.containsTarget(player)) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
            }
            else if (!this.canInteract(player)) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
            }
            else {
                player.setLastFolkNPC(this);
                if (this.hasListener(EventType.ON_NPC_QUEST_START)) {
                    player.setLastQuestNpcObject(this.getObjectId());
                }
                if (this.hasListener(EventType.ON_NPC_FIRST_TALK)) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcFirstTalk(this, player), this);
                }
                else {
                    this.showChatWindow(player, 0);
                }
            }
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
}
