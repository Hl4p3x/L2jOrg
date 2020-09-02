// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.DoorRequestHolder;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.holders.SummonRequestHolder;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.network.GameClient;

public final class DlgAnswer extends ClientPacket
{
    public static final int ANY_STRING = 1983;
    private int messageId;
    private int answer;
    private int requesterId;
    
    public void readImpl() {
        this.messageId = this.readInt();
        this.answer = this.readInt();
        this.requesterId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayerDlgAnswer(activeChar, this.messageId, this.answer, this.requesterId), activeChar, TerminateReturn.class);
        if (term != null && term.terminate()) {
            return;
        }
        if (this.messageId == 1983) {
            if (activeChar.removeAction(PlayerAction.ADMIN_COMMAND)) {
                final String cmd = activeChar.getAdminConfirmCmd();
                activeChar.setAdminConfirmCmd(null);
                if (this.answer == 0) {
                    return;
                }
                AdminCommandHandler.getInstance().useAdminCommand(activeChar, cmd, false);
            }
        }
        else if (this.messageId == SystemMessageId.C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT.getId() || this.messageId == SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW.getId()) {
            activeChar.reviveAnswer(this.answer);
        }
        else if (this.messageId == SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId()) {
            final SummonRequestHolder holder = activeChar.removeScript(SummonRequestHolder.class);
            if (this.answer == 1 && holder != null && holder.getTarget().getObjectId() == this.requesterId) {
                activeChar.teleToLocation(holder.getTarget().getLocation(), true);
            }
        }
        else if (this.messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId()) {
            final DoorRequestHolder holder2 = activeChar.removeScript(DoorRequestHolder.class);
            if (holder2 != null && holder2.getDoor() == activeChar.getTarget() && this.answer == 1) {
                holder2.getDoor().openMe();
            }
        }
        else if (this.messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId()) {
            final DoorRequestHolder holder2 = activeChar.removeScript(DoorRequestHolder.class);
            if (holder2 != null && holder2.getDoor() == activeChar.getTarget() && this.answer == 1) {
                holder2.getDoor().closeMe();
            }
        }
    }
}
