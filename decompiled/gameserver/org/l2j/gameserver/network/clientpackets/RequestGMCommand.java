// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.GMViewWarehouseWithdrawList;
import org.l2j.gameserver.network.serverpackets.GMViewItemList;
import org.l2j.gameserver.network.serverpackets.GmViewQuestInfo;
import org.l2j.gameserver.network.serverpackets.GMViewSkillInfo;
import org.l2j.gameserver.network.serverpackets.GMViewPledgeInfo;
import org.l2j.gameserver.network.serverpackets.GMHennaInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.GMViewCharacterInfo;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public final class RequestGMCommand extends ClientPacket
{
    private String _targetName;
    private int _command;
    
    public void readImpl() {
        this._targetName = this.readString();
        this._command = this.readInt();
    }
    
    public void runImpl() {
        if (!((GameClient)this.client).getPlayer().isGM() || !((GameClient)this.client).getPlayer().getAccessLevel().allowAltG()) {
            return;
        }
        final Player player = World.getInstance().findPlayer(this._targetName);
        final Clan clan = ClanTable.getInstance().getClanByName(this._targetName);
        if (player == null && (clan == null || this._command != 6)) {
            return;
        }
        switch (this._command) {
            case 1: {
                ((GameClient)this.client).sendPacket(new GMViewCharacterInfo(player));
                ((GameClient)this.client).sendPacket(new GMHennaInfo(player));
                break;
            }
            case 2: {
                if (player != null && player.getClan() != null) {
                    ((GameClient)this.client).sendPacket(new GMViewPledgeInfo(player.getClan(), player));
                    break;
                }
                break;
            }
            case 3: {
                ((GameClient)this.client).sendPacket(new GMViewSkillInfo(player));
                break;
            }
            case 4: {
                ((GameClient)this.client).sendPacket(new GmViewQuestInfo(player));
                break;
            }
            case 5: {
                ((GameClient)this.client).sendPacket(new GMViewItemList(1, player));
                ((GameClient)this.client).sendPacket(new GMViewItemList(2, player));
                ((GameClient)this.client).sendPacket(new GMHennaInfo(player));
                break;
            }
            case 6: {
                if (player != null) {
                    ((GameClient)this.client).sendPacket(new GMViewWarehouseWithdrawList(1, player));
                    ((GameClient)this.client).sendPacket(new GMViewWarehouseWithdrawList(2, player));
                    break;
                }
                ((GameClient)this.client).sendPacket(new GMViewWarehouseWithdrawList(1, clan));
                ((GameClient)this.client).sendPacket(new GMViewWarehouseWithdrawList(2, clan));
                break;
            }
        }
    }
}
