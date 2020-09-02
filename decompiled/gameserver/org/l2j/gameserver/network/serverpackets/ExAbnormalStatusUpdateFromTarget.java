// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.List;
import org.l2j.gameserver.model.actor.Creature;

public class ExAbnormalStatusUpdateFromTarget extends ServerPacket
{
    private final Creature _character;
    private final List<BuffInfo> _effects;
    
    public ExAbnormalStatusUpdateFromTarget(final Creature character) {
        this._character = character;
        this._effects = character.getEffectList().getEffects().stream().filter(Objects::nonNull).filter(BuffInfo::isInUse).filter(b -> !b.getSkill().isToggle()).collect((Collector<? super BuffInfo, ?, List<BuffInfo>>)Collectors.toList());
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ABNORMAL_STATUS_UPDATE_FROM_TARGET);
        this.writeInt(this._character.getObjectId());
        this.writeShort((short)this._effects.size());
        for (final BuffInfo info : this._effects) {
            this.writeInt(info.getSkill().getDisplayId());
            this.writeShort((short)info.getSkill().getDisplayLevel());
            this.writeShort((short)info.getSkill().getAbnormalType().getClientId());
            this.writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime());
            this.writeInt(info.getEffectorObjectId());
        }
    }
}
