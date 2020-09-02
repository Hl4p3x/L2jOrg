// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Objects;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExUISetting extends ServerPacket
{
    public static final String UI_KEY_MAPPING_VAR = "UI_KEY_MAPPING";
    public static final String SPLIT_VAR = "\t";
    private final byte[] _uiKeyMapping;
    
    public ExUISetting(final Player player) {
        if (player.getUiKeyMapping() != null && !player.getUiKeyMapping().trim().equalsIgnoreCase("")) {
            this._uiKeyMapping = this.getByteArray(player.getUiKeyMapping(), "UI_KEY_MAPPING", "\t");
        }
        else {
            this._uiKeyMapping = null;
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_UI_SETTING);
        if (this._uiKeyMapping != null) {
            this.writeInt(this._uiKeyMapping.length);
            this.writeBytes(this._uiKeyMapping);
        }
        else {
            this.writeInt(0);
        }
    }
    
    public byte[] getByteArray(final Object val, final String key, final String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        if (val == null) {
            throw new IllegalArgumentException("Byte value required, but not specified");
        }
        if (val instanceof Number) {
            return new byte[] { ((Number)val).byteValue() };
        }
        int c = 0;
        final String[] vals = ((String)val).split(splitOn);
        final byte[] result = new byte[vals.length];
        for (final String v : vals) {
            try {
                result[c++] = Byte.parseByte(v);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
            }
        }
        return result;
    }
}
