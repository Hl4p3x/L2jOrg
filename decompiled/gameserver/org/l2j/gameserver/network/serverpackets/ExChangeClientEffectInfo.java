// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExChangeClientEffectInfo extends ServerPacket
{
    public static final ExChangeClientEffectInfo STATIC_FREYA_DEFAULT;
    public static final ExChangeClientEffectInfo STATIC_FREYA_DESTROYED;
    private final int _type;
    private final int _key;
    private final int _value;
    
    private ExChangeClientEffectInfo(final int type, final int key, final int value) {
        this._type = type;
        this._key = key;
        this._value = value;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CLIENT_EFFECT_INFO);
        this.writeInt(this._type);
        this.writeInt(this._key);
        this.writeInt(this._value);
    }
    
    static {
        STATIC_FREYA_DEFAULT = new ExChangeClientEffectInfo(0, 0, 1);
        STATIC_FREYA_DESTROYED = new ExChangeClientEffectInfo(0, 0, 2);
    }
}
