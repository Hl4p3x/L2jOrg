// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.holders.MovieHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class EndScenePlayer extends ClientPacket
{
    private static final Logger LOGGER;
    private int _movieId;
    
    public void readImpl() {
        this._movieId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || this._movieId == 0) {
            return;
        }
        final MovieHolder holder = activeChar.getMovieHolder();
        if (holder == null || holder.getMovie().getClientId() != this._movieId) {
            EndScenePlayer.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lio/github/joealisson/mmocore/Client;I)Ljava/lang/String;, this.client, this._movieId));
            return;
        }
        activeChar.stopMovie();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EndScenePlayer.class);
    }
}
