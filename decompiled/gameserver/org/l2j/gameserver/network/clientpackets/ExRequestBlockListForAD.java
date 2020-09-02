// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.GameClient;

public class ExRequestBlockListForAD extends ClientPacket
{
    private String name;
    private String message;
    
    @Override
    protected void readImpl() throws Exception {
        this.name = this.readString();
        this.message = this.readString();
    }
    
    @Override
    protected void runImpl() {
        if (!this.message.toLowerCase().contains("adena")) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        final int reportedId = PlayerNameTable.getInstance().getIdByName(this.name);
        BlockList.addToBlockList(player, reportedId);
        ReportTable.getInstance().reportAdenaADS(player.getObjectId(), reportedId);
    }
}
