// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;

public abstract class PlayableAI extends CreatureAI
{
    public PlayableAI(final Playable playable) {
        super(playable);
    }
    
    @Override
    protected void onIntentionAttack(final Creature target) {
        if (GameUtils.isPlayable(target)) {
            if (target.getActingPlayer().isProtectionBlessingAffected() && this.actor.getActingPlayer().getLevel() - target.getActingPlayer().getLevel() >= 10 && this.actor.getActingPlayer().getReputation() < 0 && !target.isInsideZone(ZoneType.PVP)) {
                this.actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                this.clientActionFailed();
                return;
            }
            if (this.actor.getActingPlayer().isProtectionBlessingAffected() && target.getActingPlayer().getLevel() - this.actor.getActingPlayer().getLevel() >= 10 && target.getActingPlayer().getReputation() < 0 && !target.isInsideZone(ZoneType.PVP)) {
                this.actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                this.clientActionFailed();
                return;
            }
        }
        super.onIntentionAttack(target);
    }
    
    @Override
    protected void onIntentionCast(final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove) {
        if (GameUtils.isPlayable(target) && skill.isBad()) {
            if (target.getActingPlayer().isProtectionBlessingAffected() && this.actor.getActingPlayer().getLevel() - target.getActingPlayer().getLevel() >= 10 && this.actor.getActingPlayer().getReputation() < 0 && !target.isInsideZone(ZoneType.PVP)) {
                this.actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                this.clientActionFailed();
                return;
            }
            if (this.actor.getActingPlayer().isProtectionBlessingAffected() && target.getActingPlayer().getLevel() - this.actor.getActingPlayer().getLevel() >= 10 && target.getActingPlayer().getReputation() < 0 && !target.isInsideZone(ZoneType.PVP)) {
                this.actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                this.clientActionFailed();
                return;
            }
        }
        super.onIntentionCast(skill, target, item, forceUse, dontMove);
    }
}
