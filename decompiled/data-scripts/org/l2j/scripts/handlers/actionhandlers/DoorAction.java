// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionhandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.holders.DoorRequest;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionHandler;

public class DoorAction implements IActionHandler
{
    public boolean action(final Player player, final WorldObject target, final boolean interact) {
        if (player.getTarget() != target) {
            player.setTarget(target);
        }
        else if (interact) {
            final Door door = (Door)target;
            final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByDoorId(door.getId());
            if (target.isAutoAttackable((Creature)player)) {
                if (Math.abs(player.getZ() - target.getZ()) < 400) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { target });
                }
            }
            else if (player.getClan() != null && clanHall != null && player.getClanId() == clanHall.getOwnerId()) {
                if (!MathUtil.isInsideRadius2D((ILocational)door, (ILocational)player, 250)) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, new Object[] { target });
                }
                else {
                    player.addRequest((AbstractRequest)new DoorRequest(player, door));
                    if (!door.isOpen()) {
                        player.sendPacket(new ServerPacket[] { (ServerPacket)new ConfirmDlg(SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE) });
                    }
                    else {
                        player.sendPacket(new ServerPacket[] { (ServerPacket)new ConfirmDlg(SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE) });
                    }
                }
            }
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2DoorInstance;
    }
}
