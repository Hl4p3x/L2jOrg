// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestExCubeGameReadyAnswer extends ClientPacket
{
    private static final Logger LOGGER;
    private int _arena;
    private int _answer;
    
    public void readImpl() {
        this._arena = this.readInt() + 1;
        this._answer = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        switch (this._answer) {
            case 0: {
                break;
            }
            case 1: {
                HandysBlockCheckerManager.getInstance().increaseArenaVotes(this._arena);
                break;
            }
            default: {
                RequestExCubeGameReadyAnswer.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._answer));
                break;
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestExCubeGameReadyAnswer.class);
    }
}
