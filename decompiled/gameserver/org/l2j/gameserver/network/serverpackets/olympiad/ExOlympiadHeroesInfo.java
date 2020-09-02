// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadHeroesInfo extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_HERO_AND_LEGEND_INFO);
        this.writeShort(1024);
        this.writeSizedString((CharSequence)"Legend name");
        this.writeSizedString((CharSequence)"Legend clan name");
        this.writeInt(1);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(88);
        this.writeInt(85);
        this.writeInt(5);
        this.writeInt(4);
        this.writeInt(1);
        this.writeInt(100);
        this.writeInt(4);
        this.writeInt(40);
        for (int i = 0; i < 40; ++i) {
            this.writeSizedString(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            this.writeSizedString(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            this.writeInt(i % 2 + 1);
            this.writeInt(0);
            this.writeInt(i % 2);
            this.writeInt(88 + i);
            this.writeInt(85);
            this.writeInt(i % 4 + 1);
            this.writeInt(4 + i);
            this.writeInt(1 + i);
            this.writeInt(100 + i);
            this.writeInt(5);
        }
    }
}
