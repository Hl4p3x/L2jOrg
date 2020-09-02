// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class PlaySound extends ServerPacket
{
    private final int _unknown1;
    private final String _soundFile;
    private final int _unknown3;
    private final int _unknown4;
    private final int _unknown5;
    private final int _unknown6;
    private final int _unknown7;
    private final int _unknown8;
    
    public PlaySound(final String soundFile) {
        this._unknown1 = 0;
        this._soundFile = soundFile;
        this._unknown3 = 0;
        this._unknown4 = 0;
        this._unknown5 = 0;
        this._unknown6 = 0;
        this._unknown7 = 0;
        this._unknown8 = 0;
    }
    
    public PlaySound(final int unknown1, final String soundFile, final int unknown3, final int unknown4, final int unknown5, final int unknown6, final int unknown7) {
        this._unknown1 = unknown1;
        this._soundFile = soundFile;
        this._unknown3 = unknown3;
        this._unknown4 = unknown4;
        this._unknown5 = unknown5;
        this._unknown6 = unknown6;
        this._unknown7 = unknown7;
        this._unknown8 = 0;
    }
    
    public String getSoundName() {
        return this._soundFile;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PLAY_SOUND);
        this.writeInt(this._unknown1);
        this.writeString((CharSequence)this._soundFile);
        this.writeInt(this._unknown3);
        this.writeInt(this._unknown4);
        this.writeInt(this._unknown5);
        this.writeInt(this._unknown6);
        this.writeInt(this._unknown7);
        this.writeInt(this._unknown8);
    }
}
