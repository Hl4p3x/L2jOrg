// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.upgrade;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.upgrade.UpgradeItemEngine;
import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExUpgradeSystemNormalRequest extends ClientPacket
{
    private int objectId;
    private int type;
    private int upgradeId;
    
    @Override
    protected void readImpl() throws Exception {
        this.objectId = this.readInt();
        this.type = this.readInt();
        this.upgradeId = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        Util.doIfNonNull((Object)UpgradeType.ofId(this.type), upgradeType -> UpgradeItemEngine.getInstance().upgradeItem(((GameClient)this.client).getPlayer(), this.objectId, upgradeType, this.upgradeId));
    }
}
