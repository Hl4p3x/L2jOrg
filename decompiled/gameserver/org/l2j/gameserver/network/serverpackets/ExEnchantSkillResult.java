// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExEnchantSkillResult extends ServerPacket
{
    public static final ExEnchantSkillResult STATIC_PACKET_TRUE;
    public static final ExEnchantSkillResult STATIC_PACKET_FALSE;
    private final boolean _enchanted;
    
    private ExEnchantSkillResult(final boolean enchanted) {
        this._enchanted = enchanted;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENCHANT_SKILL_RESULT);
        this.writeInt((int)(this._enchanted ? 1 : 0));
    }
    
    static {
        STATIC_PACKET_TRUE = new ExEnchantSkillResult(true);
        STATIC_PACKET_FALSE = new ExEnchantSkillResult(false);
    }
}
