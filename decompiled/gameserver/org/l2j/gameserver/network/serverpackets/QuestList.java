// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import java.util.LinkedList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.QuestState;
import java.util.List;

public class QuestList extends ServerPacket
{
    private final List<QuestState> _activeQuests;
    private final byte[] _oneTimeQuestMask;
    
    public QuestList(final Player player) {
        this._activeQuests = new LinkedList<QuestState>();
        this._oneTimeQuestMask = new byte[128];
        for (final QuestState qs : player.getAllQuestStates()) {
            final int questId = qs.getQuest().getId();
            if (questId > 0) {
                if (qs.isStarted()) {
                    this._activeQuests.add(qs);
                }
                else {
                    if (!qs.isCompleted() || (questId > 255 && questId < 10256) || questId > 11023) {
                        continue;
                    }
                    final byte[] oneTimeQuestMask = this._oneTimeQuestMask;
                    final int n = questId % 10000 / 8;
                    oneTimeQuestMask[n] |= (byte)(1 << questId % 8);
                }
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.QUESTLIST);
        this.writeShort(this._activeQuests.size());
        for (final QuestState qs : this._activeQuests) {
            this.writeInt(qs.getQuest().getId());
            this.writeInt(qs.getCond());
        }
        this.writeBytes(this._oneTimeQuestMask);
    }
}
