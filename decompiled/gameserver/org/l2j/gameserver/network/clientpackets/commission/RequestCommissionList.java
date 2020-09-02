// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.function.Predicate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.commission.CommissionItemType;
import org.l2j.gameserver.model.commission.CommissionTreeType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestCommissionList extends ClientPacket
{
    private int _treeViewDepth;
    private int _itemType;
    private int _type;
    private int _grade;
    private String _query;
    
    public void readImpl() {
        this._treeViewDepth = this.readInt();
        this._itemType = this.readInt();
        this._type = this.readInt();
        this._grade = this.readInt();
        this._query = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!CommissionManager.isPlayerAllowedToInteract(player)) {
            ((GameClient)this.client).sendPacket(ExCloseCommission.STATIC_PACKET);
            return;
        }
        Predicate<ItemTemplate> filter = i -> true;
        switch (this._treeViewDepth) {
            case 1: {
                final CommissionTreeType commissionTreeType = CommissionTreeType.findByClientId(this._itemType);
                if (commissionTreeType != null) {
                    filter = filter.and(i -> commissionTreeType.getCommissionItemTypes().contains(i.getCommissionItemType()));
                    break;
                }
                break;
            }
            case 2: {
                final CommissionItemType commissionItemType = CommissionItemType.findByClientId(this._itemType);
                if (commissionItemType != null) {
                    filter = filter.and(i -> i.getCommissionItemType() == commissionItemType);
                    break;
                }
                break;
            }
        }
        switch (this._type) {
            case 0: {
                filter = filter.and(i -> true);
                break;
            }
            case 1: {
                filter = filter.and(i -> true);
                break;
            }
        }
        switch (this._grade) {
            case 0: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.NONE);
                break;
            }
            case 1: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.D);
                break;
            }
            case 2: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.C);
                break;
            }
            case 3: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.B);
                break;
            }
            case 4: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.A);
                break;
            }
            case 5: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.S);
                break;
            }
        }
        filter = filter.and(i -> this._query.isEmpty() || i.getName().toLowerCase().contains(this._query.toLowerCase()));
        CommissionManager.getInstance().showAuctions(player, filter);
    }
}
