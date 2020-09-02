// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.Movie;

public class ExStartScenePlayer extends ServerPacket
{
    private final Movie _movie;
    
    public ExStartScenePlayer(final Movie movie) {
        this._movie = movie;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_START_SCENE_PLAYER);
        this.writeInt(this._movie.getClientId());
    }
}
