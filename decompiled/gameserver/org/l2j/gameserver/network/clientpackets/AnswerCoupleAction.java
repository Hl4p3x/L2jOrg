// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExRotation;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public class AnswerCoupleAction extends ClientPacket
{
    private int _charObjId;
    private int _actionId;
    private int _answer;
    
    public void readImpl() {
        this._actionId = this.readInt();
        this._answer = this.readInt();
        this._charObjId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        final Player target = World.getInstance().findPlayer(this._charObjId);
        if (activeChar == null || target == null) {
            return;
        }
        if (target.getMultiSocialTarget() != activeChar.getObjectId() || target.getMultiSociaAction() != this._actionId) {
            return;
        }
        if (this._answer == 0) {
            target.sendPacket(SystemMessageId.THE_COUPLE_ACTION_WAS_DENIED);
        }
        else if (this._answer == 1) {
            final int distance = (int)MathUtil.calculateDistance2D(activeChar, target);
            if (distance > 125 || distance < 15 || activeChar.getObjectId() == target.getObjectId()) {
                ((GameClient)this.client).sendPacket(SystemMessageId.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
                target.sendPacket(SystemMessageId.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
                return;
            }
            int heading = MathUtil.calculateHeadingFrom(activeChar, target);
            activeChar.broadcastPacket(new ExRotation(activeChar.getObjectId(), heading));
            activeChar.setHeading(heading);
            heading = MathUtil.calculateHeadingFrom(target, activeChar);
            target.setHeading(heading);
            target.broadcastPacket(new ExRotation(target.getObjectId(), heading));
            activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), this._actionId));
            target.broadcastPacket(new SocialAction(this._charObjId, this._actionId));
        }
        else if (this._answer == -1) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_SET_TO_REFUSE_COUPLE_ACTIONS_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(activeChar);
            target.sendPacket(sm);
        }
        target.setMultiSocialAction(0, 0);
    }
}
