// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.serverpackets.ManagePledgePower;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;

public final class RequestPledgePower extends ClientPacket
{
    private int _rank;
    private int _action;
    private int _privs;
    
    public void readImpl() {
        this._rank = this.readInt();
        this._action = this.readInt();
        if (this._action == 2) {
            this._privs = this.readInt();
        }
        else {
            this._privs = 0;
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        player.sendPacket(new ManagePledgePower(((GameClient)this.client).getPlayer().getClan(), this._action, this._rank));
        if (this._action == 2 && player.isClanLeader()) {
            if (this._rank == 9) {
                this._privs &= (ClanPrivilege.CL_VIEW_WAREHOUSE.getBitmask() | ClanPrivilege.CH_OPEN_DOOR.getBitmask() | ClanPrivilege.CS_OPEN_DOOR.getBitmask());
            }
            player.getClan().setRankPrivs(this._rank, this._privs);
        }
    }
}
