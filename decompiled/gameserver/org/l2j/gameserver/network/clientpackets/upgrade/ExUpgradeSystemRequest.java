// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.upgrade;

import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.upgrade.UpgradeItemEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExUpgradeSystemRequest extends ClientPacket
{
    private int objectId;
    private int upgradeId;
    
    @Override
    protected void readImpl() throws Exception {
        this.objectId = this.readInt();
        this.upgradeId = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        UpgradeItemEngine.getInstance().upgradeItem(((GameClient)this.client).getPlayer(), this.objectId, UpgradeType.RARE, this.upgradeId);
    }
}
