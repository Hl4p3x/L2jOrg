// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.CommunityBoardHandler;

public final class RequestShowBoard extends ClientPacket
{
    private int _unknown;
    
    public void readImpl() {
        this._unknown = this.readInt();
    }
    
    public void runImpl() {
        CommunityBoardHandler.getInstance().handleParseCommand(Config.BBS_DEFAULT, ((GameClient)this.client).getPlayer());
    }
}
