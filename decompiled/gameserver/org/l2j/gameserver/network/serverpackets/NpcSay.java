// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.actor.Npc;
import java.util.List;
import org.l2j.gameserver.enums.ChatType;

public final class NpcSay extends ServerPacket
{
    private final int _objectId;
    private final ChatType _textType;
    private final int _npcId;
    private final int _npcString;
    private String _text;
    private List<String> _parameters;
    
    public NpcSay(final int objectId, final ChatType messageType, final int npcId, final String text) {
        this._objectId = objectId;
        this._textType = messageType;
        this._npcId = 1000000 + npcId;
        this._npcString = -1;
        this._text = text;
    }
    
    public NpcSay(final Npc npc, final ChatType messageType, final String text) {
        this._objectId = npc.getObjectId();
        this._textType = messageType;
        this._npcId = 1000000 + npc.getTemplate().getDisplayId();
        this._npcString = -1;
        this._text = text;
    }
    
    public NpcSay(final int objectId, final ChatType messageType, final int npcId, final NpcStringId npcString) {
        this._objectId = objectId;
        this._textType = messageType;
        this._npcId = 1000000 + npcId;
        this._npcString = npcString.getId();
    }
    
    public NpcSay(final Npc npc, final ChatType messageType, final NpcStringId npcString) {
        this._objectId = npc.getObjectId();
        this._textType = messageType;
        this._npcId = 1000000 + npc.getTemplate().getDisplayId();
        this._npcString = npcString.getId();
    }
    
    public NpcSay addStringParameter(final String text) {
        if (this._parameters == null) {
            this._parameters = new ArrayList<String>();
        }
        this._parameters.add(text);
        return this;
    }
    
    public NpcSay addStringParameters(final String... params) {
        if (params != null && params.length > 0) {
            if (this._parameters == null) {
                this._parameters = new ArrayList<String>();
            }
            for (final String item : params) {
                if (item != null && item.length() > 0) {
                    this._parameters.add(item);
                }
            }
        }
        return this;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.NPC_SAY);
        this.writeInt(this._objectId);
        this.writeInt(this._textType.getClientId());
        this.writeInt(this._npcId);
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
