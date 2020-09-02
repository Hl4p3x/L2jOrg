// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.GameClient;

public class RequestSkillCoolTime extends ClientPacket
{
    @Override
    protected void runImpl() {
    }
    
    @Override
    protected void readImpl() throws Exception {
        ((GameClient)this.client).sendPacket(new SkillCoolTime(((GameClient)this.client).getPlayer()));
    }
}
