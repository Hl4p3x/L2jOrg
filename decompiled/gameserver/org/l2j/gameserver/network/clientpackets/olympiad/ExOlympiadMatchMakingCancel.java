// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.olympiad;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadRecord;
import org.l2j.gameserver.engine.olympiad.OlympiadRuleType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExOlympiadMatchMakingCancel extends ClientPacket
{
    private byte ruleType;
    
    @Override
    protected void readImpl() throws Exception {
        this.ruleType = this.readByte();
    }
    
    @Override
    protected void runImpl() {
        Olympiad.getInstance().unregisterPlayer(((GameClient)this.client).getPlayer(), OlympiadRuleType.of(this.ruleType));
        ((GameClient)this.client).sendPacket(new ExOlympiadRecord());
    }
}
