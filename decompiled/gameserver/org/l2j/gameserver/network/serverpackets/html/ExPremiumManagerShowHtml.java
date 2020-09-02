// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.html;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPremiumManagerShowHtml extends AbstractHtmlPacket
{
    public ExPremiumManagerShowHtml(final String html) {
        super(html);
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PREMIUM_MANAGER_SHOW_HTML);
        this.writeInt(this.getNpcObjId());
        this.writeString((CharSequence)this.getHtml());
        this.writeInt(-1);
        this.writeInt(0);
    }
    
    @Override
    public HtmlActionScope getScope() {
        return HtmlActionScope.PREMIUM_HTML;
    }
}
