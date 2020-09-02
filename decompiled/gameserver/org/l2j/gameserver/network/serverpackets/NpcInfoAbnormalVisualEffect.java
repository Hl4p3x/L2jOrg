// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Npc;

public class NpcInfoAbnormalVisualEffect extends ServerPacket
{
    private final Npc _npc;
    
    public NpcInfoAbnormalVisualEffect(final Npc npc) {
        this._npc = npc;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.NPC_INFO_ABNORMAL_VISUAL_EFFECT);
        this.writeInt(this._npc.getObjectId());
        this.writeInt(this._npc.getTransformationDisplayId());
        final Set<AbnormalVisualEffect> abnormalVisualEffects = this._npc.getEffectList().getCurrentAbnormalVisualEffects();
        this.writeInt(abnormalVisualEffects.size());
        for (final AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            this.writeShort((short)abnormalVisualEffect.getClientId());
        }
    }
}
