// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.html;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public final class NpcQuestHtmlMessage extends AbstractHtmlPacket
{
    private final int _questId;
    
    public NpcQuestHtmlMessage(final int npcObjId, final int questId) {
        super(npcObjId);
        this._questId = questId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_NPC_QUEST_HTML_MESSAGE);
        this.writeInt(this.getNpcObjId());
        this.writeString((CharSequence)this.getHtml());
        this.writeInt(this._questId);
    }
    
    @Override
    public HtmlActionScope getScope() {
        return HtmlActionScope.NPC_QUEST_HTML;
    }
}
