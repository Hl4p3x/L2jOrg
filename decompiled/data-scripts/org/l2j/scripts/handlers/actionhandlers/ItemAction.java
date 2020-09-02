// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionhandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionHandler;

public class ItemAction implements IActionHandler
{
    public boolean action(final Player player, final WorldObject target, final boolean interact) {
        final Castle castle = CastleManager.getInstance().getCastle((ILocational)target);
        if (castle != null && SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getId(), target.getId()) != null && (player.getClan() == null || castle.getOwnerId() != player.getClanId() || !player.hasClanPrivilege(ClanPrivilege.CS_MERCENARIES))) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING);
            player.setTarget(target);
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            return false;
        }
        if (!player.isFlying()) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, new Object[] { target });
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2ItemInstance;
    }
}
