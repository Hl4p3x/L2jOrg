// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.NpcStringId;
import java.util.List;

public class ExShowScreenMessage extends ServerPacket
{
    public static final byte TOP_LEFT = 1;
    public static final byte TOP_CENTER = 2;
    public static final byte TOP_RIGHT = 3;
    public static final byte MIDDLE_LEFT = 4;
    public static final byte MIDDLE_CENTER = 5;
    public static final byte MIDDLE_RIGHT = 6;
    public static final byte BOTTOM_CENTER = 7;
    public static final byte BOTTOM_RIGHT = 8;
    private final int _type;
    private final int _sysMessageId;
    private final int _unk1;
    private final int _unk2;
    private final int _unk3;
    private final boolean _fade;
    private final int _size;
    private final int _position;
    private final boolean _effect;
    private final String _text;
    private final int _time;
    private final int _npcString;
    private List<String> _parameters;
    
    public ExShowScreenMessage(final String text, final int time) {
        this._parameters = null;
        this._type = 2;
        this._sysMessageId = -1;
        this._unk1 = 0;
        this._unk2 = 0;
        this._unk3 = 0;
        this._fade = false;
        this._position = 2;
        this._text = text;
        this._time = time;
        this._size = 0;
        this._effect = false;
        this._npcString = -1;
    }
    
    public ExShowScreenMessage(final String text, final int position, final int time) {
        this._parameters = null;
        this._type = 2;
        this._sysMessageId = -1;
        this._unk1 = 0;
        this._unk2 = 0;
        this._unk3 = 0;
        this._fade = false;
        this._position = position;
        this._text = text;
        this._time = time;
        this._size = 0;
        this._effect = false;
        this._npcString = -1;
    }
    
    public ExShowScreenMessage(final String text, final int position, final int time, final int size, final boolean fade, final boolean showEffect) {
        this._parameters = null;
        this._type = 1;
        this._sysMessageId = -1;
        this._unk1 = 0;
        this._unk2 = 0;
        this._unk3 = 0;
        this._fade = fade;
        this._position = position;
        this._text = text;
        this._time = time;
        this._size = size;
        this._effect = showEffect;
        this._npcString = -1;
    }
    
    public ExShowScreenMessage(final NpcStringId npcString, final int position, final int time, final String... params) {
        this._parameters = null;
        this._type = 2;
        this._sysMessageId = -1;
        this._unk1 = 0;
        this._unk2 = 0;
        this._unk3 = 0;
        this._fade = false;
        this._position = position;
        this._text = null;
        this._time = time;
        this._size = 0;
        this._effect = false;
        this._npcString = npcString.getId();
        if (params != null) {
            this.addStringParameter(params);
        }
    }
    
    public ExShowScreenMessage(final SystemMessageId systemMsg, final int position, final int time, final String... params) {
        this._parameters = null;
        this._type = 2;
        this._sysMessageId = systemMsg.getId();
        this._unk1 = 0;
        this._unk2 = 0;
        this._unk3 = 0;
        this._fade = false;
        this._position = position;
        this._text = null;
        this._time = time;
        this._size = 0;
        this._effect = false;
        this._npcString = -1;
        if (params != null) {
            this.addStringParameter(params);
        }
    }
    
    public ExShowScreenMessage(final NpcStringId npcString, final int position, final int time, final boolean showEffect, final String... params) {
        this._parameters = null;
        this._type = 2;
        this._sysMessageId = -1;
        this._unk1 = 0;
        this._unk2 = 0;
        this._unk3 = 0;
        this._fade = false;
        this._position = position;
        this._text = null;
        this._time = time;
        this._size = 0;
        this._effect = showEffect;
        this._npcString = npcString.getId();
        if (params != null) {
            this.addStringParameter(params);
        }
    }
    
    public ExShowScreenMessage(final int type, final int messageId, final int position, final int unk1, final int size, final int unk2, final int unk3, final boolean showEffect, final int time, final boolean fade, final String text, final NpcStringId npcString, final String params) {
        this._parameters = null;
        this._type = type;
        this._sysMessageId = messageId;
        this._unk1 = unk1;
        this._unk2 = unk2;
        this._unk3 = unk3;
        this._fade = fade;
        this._position = position;
        this._text = text;
        this._time = time;
        this._size = size;
        this._effect = showEffect;
        this._npcString = npcString.getId();
    }
    
    public void addStringParameter(final String... params) {
        if (this._parameters == null) {
            this._parameters = new ArrayList<String>();
        }
        for (final String param : params) {
            this._parameters.add(param);
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_SCREEN_MESSAGE);
        this.writeInt(this._type);
        this.writeInt(this._sysMessageId);
        this.writeInt(this._position);
        this.writeInt(this._unk1);
        this.writeInt(this._size);
        this.writeInt(this._unk2);
        this.writeInt(this._unk3);
        this.writeInt((int)(this._effect ? 1 : 0));
        this.writeInt(this._time);
        this.writeInt((int)(this._fade ? 1 : 0));
        this.writeInt(this._npcString);
        if (this._npcString == -1) {
            this.writeString((CharSequence)this._text);
        }
        else if (this._parameters != null) {
            for (final String s : this._parameters) {
                this.writeString((CharSequence)s);
            }
        }
    }
}
