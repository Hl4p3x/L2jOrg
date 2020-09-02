// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.luckygame;

import java.util.Iterator;
import java.util.Map;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import org.l2j.gameserver.enums.LuckyGameItemType;
import java.util.EnumMap;
import org.l2j.gameserver.enums.LuckyGameType;
import org.l2j.gameserver.enums.LuckyGameResultType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExBettingLuckyGameResult extends ServerPacket
{
    public static final ExBettingLuckyGameResult NORMAL_INVALID_ITEM_COUNT;
    public static final ExBettingLuckyGameResult LUXURY_INVALID_ITEM_COUNT;
    public static final ExBettingLuckyGameResult NORMAL_INVALID_CAPACITY;
    public static final ExBettingLuckyGameResult LUXURY_INVALID_CAPACITY;
    private final LuckyGameResultType _result;
    private final LuckyGameType _type;
    private final EnumMap<LuckyGameItemType, List<ItemHolder>> rewards;
    private final int _ticketCount;
    private final int _size;
    
    public ExBettingLuckyGameResult(final LuckyGameResultType result, final LuckyGameType type) {
        this._result = result;
        this._type = type;
        this.rewards = new EnumMap<LuckyGameItemType, List<ItemHolder>>(LuckyGameItemType.class);
        this._ticketCount = 0;
        this._size = 0;
    }
    
    public ExBettingLuckyGameResult(final LuckyGameResultType result, final LuckyGameType type, final EnumMap<LuckyGameItemType, List<ItemHolder>> rewards, final int ticketCount) {
        this._result = result;
        this._type = type;
        this.rewards = rewards;
        this._ticketCount = ticketCount;
        this._size = (int)rewards.values().stream().mapToLong(i -> i.stream().count()).sum();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BETTING_LUCKY_GAME_RESULT);
        this.writeInt(this._result.getClientId());
        this.writeInt(this._type.ordinal());
        this.writeInt(this._ticketCount);
        this.writeInt(this._size);
        for (final Map.Entry<LuckyGameItemType, List<ItemHolder>> reward : this.rewards.entrySet()) {
            for (final ItemHolder item : reward.getValue()) {
                this.writeInt(reward.getKey().getClientId());
                this.writeInt(item.getId());
                this.writeInt((int)item.getCount());
            }
        }
    }
    
    static {
        NORMAL_INVALID_ITEM_COUNT = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_ITEM_COUNT, LuckyGameType.NORMAL);
        LUXURY_INVALID_ITEM_COUNT = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_ITEM_COUNT, LuckyGameType.LUXURY);
        NORMAL_INVALID_CAPACITY = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_CAPACITY, LuckyGameType.NORMAL);
        LUXURY_INVALID_CAPACITY = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_CAPACITY, LuckyGameType.LUXURY);
    }
}
