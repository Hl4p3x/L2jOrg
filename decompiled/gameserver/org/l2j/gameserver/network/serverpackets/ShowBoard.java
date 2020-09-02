// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import java.util.List;

public class ShowBoard extends ServerPacket
{
    private final String _content;
    private int _showBoard;
    
    public ShowBoard(final String htmlCode, final String id) {
        this._showBoard = 1;
        this._content = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, id, htmlCode);
    }
    
    public ShowBoard() {
        this._showBoard = 1;
        this._showBoard = 0;
        this._content = "";
    }
    
    public ShowBoard(final List<String> arg) {
        this._showBoard = 1;
        final StringBuilder builder = new StringBuilder(256).append("1002\b");
        for (final String str : arg) {
            builder.append(str).append("\b");
        }
        this._content = builder.toString();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SHOW_BOARD);
        this.writeByte(this._showBoard);
        this.writeString((CharSequence)"bypass _bbshome");
        this.writeString((CharSequence)"bypass _bbsgetfav");
        this.writeString((CharSequence)"bypass _bbsloc");
        this.writeString((CharSequence)"bypass _bbsclan");
        this.writeString((CharSequence)"bypass _bbsmemo");
        this.writeString((CharSequence)"bypass _bbsmail");
        this.writeString((CharSequence)"bypass _bbsfriends");
        this.writeString((CharSequence)"bypass _bbsgetfav add");
        this.writeString((CharSequence)this._content);
    }
}
