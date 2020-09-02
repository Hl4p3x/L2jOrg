// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.html;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class NpcHtmlMessage extends AbstractHtmlPacket
{
    private final int _itemId;
    
    public NpcHtmlMessage() {
        this._itemId = 0;
    }
    
    public NpcHtmlMessage(final int npcObjId) {
        super(npcObjId);
        this._itemId = 0;
    }
    
    public NpcHtmlMessage(final String html) {
        super(html);
        this._itemId = 0;
    }
    
    public NpcHtmlMessage(final int npcObjId, final String html) {
        super(npcObjId, html);
        this._itemId = 0;
    }
    
    public NpcHtmlMessage(final int npcObjId, final int itemId) {
        super(npcObjId);
        if (itemId < 0) {
            throw new IllegalArgumentException();
        }
        this._itemId = itemId;
    }
    
    public NpcHtmlMessage(final int npcObjId, final int itemId, final String html) {
        super(npcObjId, html);
        if (itemId < 0) {
            throw new IllegalArgumentException();
        }
        this._itemId = itemId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.NPC_HTML_MESSAGE);
        this.writeInt(this.getNpcObjId());
        this.writeString((CharSequence)this.getHtml());
        this.writeInt(this._itemId);
        this.writeInt(0);
    }
    
    @Override
    public HtmlActionScope getScope() {
        return (this._itemId == 0) ? HtmlActionScope.NPC_HTML : HtmlActionScope.NPC_ITEM_HTML;
    }
}
