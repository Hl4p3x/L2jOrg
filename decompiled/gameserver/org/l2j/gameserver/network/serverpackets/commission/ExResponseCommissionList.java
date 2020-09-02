// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.commission;

import java.time.Instant;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Collections;
import org.l2j.gameserver.model.commission.CommissionItem;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

public class ExResponseCommissionList extends AbstractItemPacket
{
    public static final int MAX_CHUNK_SIZE = 120;
    private final CommissionListReplyType _replyType;
    private final List<CommissionItem> _items;
    private final int _chunkId;
    private final int _listIndexStart;
    
    public ExResponseCommissionList(final CommissionListReplyType replyType) {
        this(replyType, Collections.emptyList(), 0);
    }
    
    public ExResponseCommissionList(final CommissionListReplyType replyType, final List<CommissionItem> items) {
        this(replyType, items, 0);
    }
    
    public ExResponseCommissionList(final CommissionListReplyType replyType, final List<CommissionItem> items, final int chunkId) {
        this(replyType, items, chunkId, 0);
    }
    
    public ExResponseCommissionList(final CommissionListReplyType replyType, final List<CommissionItem> items, final int chunkId, final int listIndexStart) {
        this._replyType = replyType;
        this._items = items;
        this._chunkId = chunkId;
        this._listIndexStart = listIndexStart;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_LIST);
        this.writeInt(this._replyType.getClientId());
        switch (this._replyType) {
            case PLAYER_AUCTIONS:
            case AUCTIONS: {
                this.writeInt((int)Instant.now().getEpochSecond());
                this.writeInt(this._chunkId);
                int chunkSize = this._items.size() - this._listIndexStart;
                if (chunkSize > 120) {
                    chunkSize = 120;
                }
                this.writeInt(chunkSize);
                for (int i = this._listIndexStart; i < this._listIndexStart + chunkSize; ++i) {
                    final CommissionItem commissionItem = this._items.get(i);
                    this.writeLong(commissionItem.getCommissionId());
                    this.writeLong(commissionItem.getPricePerUnit());
                    this.writeInt(0);
                    this.writeInt((commissionItem.getDurationInDays() - 1) / 2);
                    this.writeInt((int)commissionItem.getEndTime().getEpochSecond());
                    this.writeString((CharSequence)null);
                    this.writeItem(commissionItem.getItemInfo());
                }
                break;
            }
        }
    }
    
    public enum CommissionListReplyType
    {
        PLAYER_AUCTIONS_EMPTY(-2), 
        ITEM_DOES_NOT_EXIST(-1), 
        PLAYER_AUCTIONS(2), 
        AUCTIONS(3);
        
        private final int _clientId;
        
        private CommissionListReplyType(final int clientId) {
            this._clientId = clientId;
        }
        
        public int getClientId() {
            return this._clientId;
        }
    }
}
