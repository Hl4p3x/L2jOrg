// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.List;

public class AbnormalStatusUpdate extends ServerPacket
{
    private final List<BuffInfo> _effects;
    
    public AbnormalStatusUpdate() {
        this._effects = new ArrayList<BuffInfo>();
    }
    
    public void addSkill(final BuffInfo info) {
        if (!info.getSkill().isHealingPotionSkill()) {
            this._effects.add(info);
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ABNORMAL_STATUS_UPDATE);
        this.writeShort(this._effects.size());
        for (final BuffInfo info : this._effects) {
            if (info != null && info.isInUse()) {
                this.writeInt(info.getSkill().getDisplayId());
                this.writeShort(info.getSkill().getDisplayLevel());
                this.writeInt(info.getSkill().getAbnormalType().getClientId());
                this.writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime());
            }
        }
    }
}
