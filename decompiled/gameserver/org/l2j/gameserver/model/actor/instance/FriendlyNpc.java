// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.ai.FriendlyNpcAI;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAttack;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Attackable;

public class FriendlyNpc extends Attackable
{
    private boolean _isAutoAttackable;
    private int _baseHateAmount;
    
    public FriendlyNpc(final NpcTemplate template) {
        super(template);
        this._isAutoAttackable = true;
        this._baseHateAmount = 1;
        this.setInstanceType(InstanceType.FriendlyNpcInstance);
    }
    
    public int getHateBaseAmount() {
        return this._baseHateAmount;
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return this._isAutoAttackable && !GameUtils.isPlayable(attacker) && !(attacker instanceof FriendlyNpc);
    }
    
    @Override
    public void setAutoAttackable(final boolean state) {
        this._isAutoAttackable = state;
    }
    
    @Override
    public void addDamage(final Creature attacker, final int damage, final Skill skill) {
        if (!GameUtils.isPlayable(attacker) && !(attacker instanceof FriendlyNpc)) {
            super.addDamage(attacker, damage, skill);
        }
        if (GameUtils.isAttackable(attacker)) {
            EventDispatcher.getInstance().notifyEventAsync(new OnAttackableAttack(null, this, damage, skill, false), this);
        }
    }
    
    @Override
    public void addDamageHate(final Creature attacker, final int damage, final int aggro) {
        if (!GameUtils.isPlayable(attacker) && !(attacker instanceof FriendlyNpc)) {
            super.addDamageHate(attacker, damage, aggro);
        }
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        if (GameUtils.isAttackable(killer)) {
            EventDispatcher.getInstance().notifyEventAsync(new OnAttackableKill(null, this, false), this);
        }
        return true;
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
            if (!this.canInteract(player)) {
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
    protected CreatureAI initAI() {
        return new FriendlyNpcAI(this);
    }
}
