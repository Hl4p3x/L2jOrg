// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.EnumMap;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.skills.SkillCastingType;
import java.util.Map;

public final class ActionFailed extends ServerPacket
{
    public static final ActionFailed STATIC_PACKET;
    private static final Map<SkillCastingType, ActionFailed> STATIC_PACKET_BY_CASTING_TYPE;
    private final int _castingType;
    
    private ActionFailed() {
        this._castingType = 0;
    }
    
    private ActionFailed(final int castingType) {
        this._castingType = castingType;
    }
    
    public static ActionFailed get(final SkillCastingType castingType) {
        return ActionFailed.STATIC_PACKET_BY_CASTING_TYPE.getOrDefault(castingType, ActionFailed.STATIC_PACKET);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ACTION_FAIL);
        this.writeInt(this._castingType);
    }
    
    static {
        STATIC_PACKET = new ActionFailed();
        STATIC_PACKET_BY_CASTING_TYPE = new EnumMap<SkillCastingType, ActionFailed>(SkillCastingType.class);
        for (final SkillCastingType castingType : SkillCastingType.values()) {
            ActionFailed.STATIC_PACKET_BY_CASTING_TYPE.put(castingType, new ActionFailed(castingType.getClientBarId()));
        }
    }
}
