// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;

public final class RequestSkillList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player cha = ((GameClient)this.client).getPlayer();
        if (cha != null) {
            cha.sendSkillList();
        }
    }
}
