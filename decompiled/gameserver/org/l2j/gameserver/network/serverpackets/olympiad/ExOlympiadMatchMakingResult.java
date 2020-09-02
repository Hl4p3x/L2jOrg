// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.olympiad.OlympiadRuleType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadMatchMakingResult extends ServerPacket
{
    private final boolean registered;
    private final OlympiadRuleType ruleType;
    
    public ExOlympiadMatchMakingResult(final boolean registered, final OlympiadRuleType ruleType) {
        this.registered = registered;
        this.ruleType = ruleType;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_MATCH_MAKING_RESULT);
        this.writeByte(this.registered);
        this.writeByte(this.ruleType.ordinal());
    }
}
