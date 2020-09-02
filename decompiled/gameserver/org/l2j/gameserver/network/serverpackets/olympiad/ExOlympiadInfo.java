// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.olympiad.OlympiadRuleType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadInfo extends ServerPacket
{
    private final boolean open;
    private final OlympiadRuleType type;
    private final int remainTime;
    
    private ExOlympiadInfo(final boolean open, final OlympiadRuleType type, final int remainTime) {
        this.open = open;
        this.type = type;
        this.remainTime = remainTime;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_INFO);
        this.writeByte(this.open);
        this.writeInt(this.remainTime);
        this.writeByte(this.type.ordinal());
    }
    
    public static ExOlympiadInfo show(final OlympiadRuleType type, final int remainTime) {
        return new ExOlympiadInfo(true, type, remainTime);
    }
    
    public static ExOlympiadInfo hide(final OlympiadRuleType type) {
        return new ExOlympiadInfo(false, type, 0);
    }
}
