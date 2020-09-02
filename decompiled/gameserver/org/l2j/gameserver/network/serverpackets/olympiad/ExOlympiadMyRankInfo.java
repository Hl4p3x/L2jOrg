// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadMyRankInfo extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_MY_RANKING_INFO);
        this.writeInt(2020);
        this.writeInt(3);
        this.writeInt(1);
        this.writeInt(2);
        this.writeInt(5);
        this.writeInt(2);
        this.writeInt(100);
        this.writeInt(3);
        this.writeInt(8);
        this.writeInt(1);
        this.writeInt(150);
        this.writeInt(5);
        this.writeInt(2);
        this.writeInt(3);
        for (int i = 0; i < 3; ++i) {
            this.writeSizedString(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            this.writeByte(i % 2 == 0);
            this.writeInt(75 + i);
            this.writeInt(88 + i);
        }
    }
}
