// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor;

import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public abstract class Tower extends Npc
{
    public Tower(final NpcTemplate template) {
        super(template);
        this.setIsInvul(false);
    }
    
    @Override
    public boolean canBeAttacked() {
        return this.getCastle() != null && this.getCastle().getId() > 0 && this.getCastle().getSiege().isInProgress();
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return GameUtils.isPlayer(attacker) && this.getCastle() != null && this.getCastle().getId() > 0 && this.getCastle().getSiege().isInProgress() && this.getCastle().getSiege().checkIsAttacker(attacker.getClan());
    }
    
    @Override
    public void onAction(final Player player, final boolean interact) {
        if (!this.canTarget(player)) {
            return;
        }
        if (this != player.getTarget()) {
            player.setTarget(this);
        }
        else if (interact && this.isAutoAttackable(player) && Math.abs(player.getZ() - this.getZ()) < 100 && GeoEngine.getInstance().canSeeTarget(player, this)) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    @Override
    public void onForcedAttack(final Player player) {
        this.onAction(player);
    }
}
