// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadRankingInfo extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_RANKING_INFO);
        this.writeByte(0);
        this.writeByte(0);
        this.writeByte(true);
        this.writeInt(12);
        this.writeInt(1);
        this.writeInt(3);
        this.writeInt(2);
        for (int i = 0; i < 2; ++i) {
            this.writeSizedString(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            this.writeSizedString(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            this.writeInt(i + 1);
            this.writeInt(i);
            this.writeInt(1);
            this.writeInt(76 + i);
            this.writeInt(88 + i);
            this.writeInt(4);
            this.writeInt(4 + i);
            this.writeInt(5 + i);
            this.writeInt(100 + i);
            this.writeInt(2 + i);
            this.writeInt(5 + i);
        }
    }
}
