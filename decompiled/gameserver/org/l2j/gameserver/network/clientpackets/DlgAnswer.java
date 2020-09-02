// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.DoorRequest;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.Objects;
import org.l2j.gameserver.model.holders.SummonRequest;
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
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayerDlgAnswer(player, this.messageId, this.answer, this.requesterId), player, TerminateReturn.class);
        if (term != null && term.terminate()) {
            return;
        }
        if (this.messageId == 1983) {
            if (player.removeAction(PlayerAction.ADMIN_COMMAND)) {
                final String cmd = player.getAdminConfirmCmd();
                player.setAdminConfirmCmd(null);
                if (this.answer == 0) {
                    return;
                }
                AdminCommandHandler.getInstance().useAdminCommand(player, cmd, false);
            }
        }
        else if (this.messageId == SystemMessageId.C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT.getId() || this.messageId == SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW.getId()) {
            player.reviveAnswer(this.answer);
        }
        else if (this.messageId == SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId()) {
            final SummonRequest request = player.getRequest(SummonRequest.class);
            if (this.answer == 1 && Objects.nonNull(request) && request.getTarget().getObjectId() == this.requesterId) {
                player.teleToLocation(request.getTarget().getLocation(), true);
                player.removeRequest(SummonRequest.class);
            }
        }
        else if (this.messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId()) {
            final DoorRequest request2 = player.getRequest(DoorRequest.class);
            if (Objects.nonNull(request2) && request2.getDoor() == player.getTarget() && this.answer == 1) {
                request2.getDoor().openMe();
                player.removeRequest(DoorRequest.class);
            }
        }
        else if (this.messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId()) {
            final DoorRequest request2 = player.getRequest(DoorRequest.class);
            if (Objects.nonNull(request2) && request2.getDoor() == player.getTarget() && this.answer == 1) {
                request2.getDoor().closeMe();
                player.removeRequest(DoorRequest.class);
            }
        }
    }
}
