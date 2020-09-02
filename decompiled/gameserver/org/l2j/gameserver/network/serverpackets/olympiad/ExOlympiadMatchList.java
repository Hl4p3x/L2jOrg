// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.model.olympiad.AbstractOlympiadGame;
import java.util.Iterator;
import org.l2j.gameserver.model.olympiad.OlympiadGameClassed;
import org.l2j.gameserver.model.olympiad.OlympiadGameNonClassed;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import java.util.ArrayList;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadMatchList extends ServerPacket
{
    private final List<OlympiadGameTask> _games;
    
    public ExOlympiadMatchList() {
        this._games = new ArrayList<OlympiadGameTask>();
        for (int i = 0; i < OlympiadGameManager.getInstance().getNumberOfStadiums(); ++i) {
            final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(i);
            if (task != null && task.isGameStarted()) {
                if (!task.isBattleFinished()) {
                    this._games.add(task);
                }
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_GFX_OLYMPIAD);
        this.writeInt(0);
        this.writeInt(this._games.size());
        this.writeInt(0);
        for (final OlympiadGameTask curGame : this._games) {
            final AbstractOlympiadGame game = curGame.getGame();
            if (game != null) {
                this.writeInt(game.getStadiumId());
                if (game instanceof OlympiadGameNonClassed) {
                    this.writeInt(1);
                }
                else if (game instanceof OlympiadGameClassed) {
                    this.writeInt(2);
                }
                else {
                    this.writeInt(0);
                }
                this.writeInt(curGame.isRunning() ? 2 : 1);
                this.writeString((CharSequence)game.getPlayerNames()[0]);
                this.writeString((CharSequence)game.getPlayerNames()[1]);
            }
        }
    }
}
