// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.html;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class TutorialShowHtml extends AbstractHtmlPacket
{
    private final TutorialWindowType type;
    
    public TutorialShowHtml(final String html) {
        super(html);
        this.type = TutorialWindowType.STANDARD;
    }
    
    public TutorialShowHtml(final String html, final TutorialWindowType type) {
        super(html);
        this.type = type;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TUTORIAL_SHOW_HTML);
        this.writeInt(this.type.getId());
        this.writeString((CharSequence)this.getHtml());
    }
    
    @Override
    public HtmlActionScope getScope() {
        return HtmlActionScope.TUTORIAL_HTML;
    }
}
