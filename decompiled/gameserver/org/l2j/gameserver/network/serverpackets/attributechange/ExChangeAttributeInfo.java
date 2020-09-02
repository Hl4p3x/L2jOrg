// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.attributechange;

import java.util.HashMap;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.AttributeType;
import java.util.Map;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExChangeAttributeInfo extends ServerPacket
{
    private static final Map<AttributeType, Byte> ATTRIBUTE_MASKS;
    private final int _crystalItemId;
    private int _attributes;
    private int _itemObjId;
    
    public ExChangeAttributeInfo(final int crystalItemId, final Item item) {
        ExChangeAttributeInfo.ATTRIBUTE_MASKS.put(AttributeType.FIRE, (Byte)1);
        ExChangeAttributeInfo.ATTRIBUTE_MASKS.put(AttributeType.WATER, (Byte)2);
        ExChangeAttributeInfo.ATTRIBUTE_MASKS.put(AttributeType.WIND, (Byte)4);
        ExChangeAttributeInfo.ATTRIBUTE_MASKS.put(AttributeType.EARTH, (Byte)8);
        ExChangeAttributeInfo.ATTRIBUTE_MASKS.put(AttributeType.HOLY, (Byte)16);
        ExChangeAttributeInfo.ATTRIBUTE_MASKS.put(AttributeType.DARK, (Byte)32);
        this._crystalItemId = crystalItemId;
        this._attributes = 0;
        for (final AttributeType e : AttributeType.ATTRIBUTE_TYPES) {
            if (e != item.getAttackAttributeType()) {
                this._attributes |= ExChangeAttributeInfo.ATTRIBUTE_MASKS.get(e);
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHANGE_ATTRIBUTE_INFO);
        this.writeInt(this._crystalItemId);
        this.writeInt(this._attributes);
        this.writeInt(this._itemObjId);
    }
    
    static {
        ATTRIBUTE_MASKS = new HashMap<AttributeType, Byte>();
    }
}
