// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadSpelledInfo extends ServerPacket
{
    private final int _playerId;
    private final List<BuffInfo> _effects;
    private final List<Skill> _effects2;
    
    public ExOlympiadSpelledInfo(final Player player) {
        this._effects = new ArrayList<BuffInfo>();
        this._effects2 = new ArrayList<Skill>();
        this._playerId = player.getObjectId();
    }
    
    public void addSkill(final BuffInfo info) {
        this._effects.add(info);
    }
    
    public void addSkill(final Skill skill) {
        this._effects2.add(skill);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_SPELLED_INFO);
        this.writeInt(this._playerId);
        this.writeInt(this._effects.size() + this._effects2.size());
        for (final BuffInfo info : this._effects) {
            if (info != null && info.isInUse()) {
                this.writeInt(info.getSkill().getDisplayId());
                this.writeShort((short)info.getSkill().getDisplayLevel());
                this.writeShort((short)0);
                this.writeInt(info.getSkill().getAbnormalType().getClientId());
                this.writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime());
            }
        }
        for (final Skill skill : this._effects2) {
            if (skill != null) {
                this.writeInt(skill.getDisplayId());
                this.writeShort((short)skill.getDisplayLevel());
                this.writeShort((short)0);
                this.writeInt(skill.getAbnormalType().getClientId());
                this.writeShort((short)(-1));
            }
        }
    }
}
