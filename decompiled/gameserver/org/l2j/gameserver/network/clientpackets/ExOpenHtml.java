// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.ExPremiumManagerShowHtml;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.GameClient;

public class ExOpenHtml extends ClientPacket
{
    private static final String COMMON_HTML_PATH = "data/html/common/%d.htm";
    private byte dialogId;
    
    @Override
    protected void readImpl() throws Exception {
        this.dialogId = this.readByte();
    }
    
    @Override
    protected void runImpl() {
        switch (this.dialogId) {
            case 5: {
                ((GameClient)this.client).sendPacket(new ExPremiumManagerShowHtml(HtmCache.getInstance().getHtm(((GameClient)this.client).getPlayer(), String.format("data/html/common/%d.htm", this.dialogId))));
                break;
            }
        }
    }
}
