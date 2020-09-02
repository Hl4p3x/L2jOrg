// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ChairSit;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.ai.NextAction;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class SitStand implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isSitting() || !player.isMoving() || player.isFakeDeath()) {
            this.useSit(player, player.getTarget());
        }
        else {
            final NextAction nextAction = new NextAction(CtrlEvent.EVT_ARRIVED, CtrlIntention.AI_INTENTION_MOVE_TO, () -> this.useSit(player, player.getTarget()));
            player.getAI().setNextAction(nextAction);
        }
    }
    
    private boolean useSit(final Player activeChar, final WorldObject target) {
        if (activeChar.getMountType() != MountType.NONE) {
            return false;
        }
        if (!activeChar.isSitting() && target instanceof StaticWorldObject && ((StaticWorldObject)target).getType() == 1 && MathUtil.isInsideRadius2D((ILocational)activeChar, (ILocational)target, 150)) {
            final ChairSit cs = new ChairSit(activeChar, target.getId());
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)cs });
            activeChar.sitDown();
            activeChar.broadcastPacket((ServerPacket)cs);
            return true;
        }
        if (activeChar.isFakeDeath()) {
            activeChar.stopEffects(EffectFlag.FAKE_DEATH);
        }
        else if (activeChar.isSitting()) {
            activeChar.standUp();
        }
        else {
            activeChar.sitDown();
        }
        return true;
    }
}
