// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.olympiad.OlympiadInfo;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadMatchResult extends ServerPacket
{
    private final boolean _tie;
    private final List<OlympiadInfo> _winnerList;
    private final List<OlympiadInfo> _loserList;
    private int _winTeam;
    private int _loseTeam;
    
    public ExOlympiadMatchResult(final boolean tie, final int winTeam, final List<OlympiadInfo> winnerList, final List<OlympiadInfo> loserList) {
        this._loseTeam = 2;
        this._tie = tie;
        this._winTeam = winTeam;
        this._winnerList = winnerList;
        this._loserList = loserList;
        if (this._winTeam == 2) {
            this._loseTeam = 1;
        }
        else if (this._winTeam == 0) {
            this._winTeam = 1;
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_GFX_OLYMPIAD);
        this.writeInt(1);
        this.writeInt((int)(this._tie ? 1 : 0));
        this.writeString((CharSequence)this._winnerList.get(0).getName());
        this.writeInt(this._winTeam);
        this.writeInt(this._winnerList.size());
        for (final OlympiadInfo info : this._winnerList) {
            this.writeParticipant(info);
        }
        this.writeInt(this._loseTeam);
        this.writeInt(this._loserList.size());
        for (final OlympiadInfo info : this._loserList) {
            this.writeParticipant(info);
        }
    }
    
    private void writeParticipant(final OlympiadInfo info) {
        this.writeString((CharSequence)info.getName());
        this.writeString((CharSequence)info.getClanName());
        this.writeInt(info.getClanId());
        this.writeInt(info.getClassId());
        this.writeInt(info.getDamage());
        this.writeInt(info.getCurrentPoints());
        this.writeInt(info.getDiffPoints());
        this.writeInt(0);
    }
}
