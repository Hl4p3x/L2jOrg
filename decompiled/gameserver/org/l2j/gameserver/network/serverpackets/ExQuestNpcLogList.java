// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.NpcStringId;
import java.util.ArrayList;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import java.util.List;

public class ExQuestNpcLogList extends ServerPacket
{
    private final int _questId;
    private final List<NpcLogListHolder> _npcLogList;
    
    public ExQuestNpcLogList(final int questId) {
        this._npcLogList = new ArrayList<NpcLogListHolder>();
        this._questId = questId;
    }
    
    public void addNpc(final int npcId, final int count) {
        this._npcLogList.add(new NpcLogListHolder(npcId, false, count));
    }
    
    public void addNpcString(final NpcStringId npcStringId, final int count) {
        this._npcLogList.add(new NpcLogListHolder(npcStringId.getId(), true, count));
    }
    
    public void add(final NpcLogListHolder holder) {
        this._npcLogList.add(holder);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_QUEST_NPC_LOG_LIST);
        this.writeInt(this._questId);
        this.writeByte((byte)this._npcLogList.size());
        for (final NpcLogListHolder holder : this._npcLogList) {
            this.writeInt(holder.isNpcString() ? holder.getId() : (holder.getId() + 1000000));
            this.writeByte((byte)(byte)(holder.isNpcString() ? 1 : 0));
            this.writeInt(holder.getCount());
        }
    }
}
