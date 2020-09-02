// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.olympiad;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadRankingInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestOlympiadRanking extends ClientPacket
{
    private byte type;
    private byte scope;
    private boolean currentSeason;
    private int classId;
    private int worldId;
    
    @Override
    protected void readImpl() throws Exception {
        this.type = this.readByte();
        this.scope = this.readByte();
        this.currentSeason = this.readBoolean();
        this.classId = this.readInt();
        this.worldId = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ExOlympiadRankingInfo());
    }
}
