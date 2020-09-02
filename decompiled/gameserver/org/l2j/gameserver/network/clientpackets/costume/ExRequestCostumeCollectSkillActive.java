// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeCollectionSkillActive;
import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestCostumeCollectSkillActive extends ClientPacket
{
    private int collectionId;
    
    @Override
    protected void readImpl() throws Exception {
        this.collectionId = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (CostumeEngine.getInstance().activeCollection(player, this.collectionId)) {
            ((GameClient)this.client).sendPacket(new ExCostumeCollectionSkillActive());
        }
    }
}
