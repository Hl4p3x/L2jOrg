// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.network.GameClient;

public final class SendBypassBuildCmd extends ClientPacket
{
    public static final int GM_MESSAGE = 9;
    public static final int ANNOUNCEMENT = 10;
    private String _command;
    
    public void readImpl() {
        this._command = this.readString();
        if (this._command != null) {
            this._command = this._command.trim();
        }
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        AdminCommandHandler.getInstance().useAdminCommand(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._command), true);
    }
}
