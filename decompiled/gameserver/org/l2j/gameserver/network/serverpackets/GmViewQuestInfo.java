// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.quest.QuestState;
import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.quest.Quest;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;

public class GmViewQuestInfo extends ServerPacket
{
    private final Player _activeChar;
    private final List<Quest> _questList;
    
    public GmViewQuestInfo(final Player cha) {
        this._activeChar = cha;
        this._questList = cha.getAllActiveQuests();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GM_VIEW_QUEST_INFO);
        this.writeString((CharSequence)this._activeChar.getName());
        this.writeShort((short)this._questList.size());
        for (final Quest quest : this._questList) {
            final QuestState qs = this._activeChar.getQuestState(quest.getName());
            this.writeInt(quest.getId());
            this.writeInt((qs == null) ? 0 : qs.getCond());
        }
        this.writeShort((short)0);
    }
}
