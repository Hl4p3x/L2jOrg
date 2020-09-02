// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.handler.UserCommandHandler;
import org.l2j.gameserver.network.GameClient;

public class BypassUserCmd extends ClientPacket
{
    private int _command;
    
    public void readImpl() {
        this._command = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final IUserCommandHandler handler = UserCommandHandler.getInstance().getHandler(Integer.valueOf(this._command));
        if (Objects.isNull(handler)) {
            if (player.isGM()) {
                player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._command));
            }
        }
        else {
            handler.useUserCommand(this._command, player);
        }
    }
}
