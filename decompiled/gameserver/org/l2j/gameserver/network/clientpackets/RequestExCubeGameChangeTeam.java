// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.slf4j.Logger;

public final class RequestExCubeGameChangeTeam extends ClientPacket
{
    private static final Logger LOGGER;
    private int _arena;
    private int _team;
    
    public void readImpl() {
        this._arena = this.readInt() + 1;
        this._team = this.readInt();
    }
    
    public void runImpl() {
        if (HandysBlockCheckerManager.getInstance().arenaIsBeingUsed(this._arena)) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        switch (this._team) {
            case 0:
            case 1: {
                HandysBlockCheckerManager.getInstance().changePlayerToTeam(player, this._arena, this._team);
                break;
            }
            case -1: {
                final int team = HandysBlockCheckerManager.getInstance().getHolder(this._arena).getPlayerTeam(player);
                if (team > -1) {
                    HandysBlockCheckerManager.getInstance().removePlayer(player, this._arena, team);
                    break;
                }
                break;
            }
            default: {
                RequestExCubeGameChangeTeam.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._team));
                break;
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestExCubeGameChangeTeam.class);
    }
}
