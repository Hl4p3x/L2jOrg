// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.rank;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.rank.ExRankList;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRankingCharRankers extends ClientPacket
{
    private byte group;
    private byte scope;
    private int race;
    
    @Override
    protected void readImpl() throws Exception {
        this.group = this.readByte();
        this.scope = this.readByte();
        this.race = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ExRankList(this.group, this.scope, this.race));
    }
}
