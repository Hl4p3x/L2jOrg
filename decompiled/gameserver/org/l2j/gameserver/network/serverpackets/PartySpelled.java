// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.List;

public class PartySpelled extends ServerPacket
{
    private final List<BuffInfo> _effects;
    private final List<Skill> _effects2;
    private final Creature _activeChar;
    
    public PartySpelled(final Creature cha) {
        this._effects = new ArrayList<BuffInfo>();
        this._effects2 = new ArrayList<Skill>();
        this._activeChar = cha;
    }
    
    public void addSkill(final BuffInfo info) {
        this._effects.add(info);
    }
    
    public void addSkill(final Skill skill) {
        this._effects2.add(skill);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PARTY_SPELLED_INFO);
        this.writeInt(GameUtils.isServitor(this._activeChar) ? 2 : (GameUtils.isPet(this._activeChar) ? 1 : 0));
        this.writeInt(this._activeChar.getObjectId());
        this.writeInt(this._effects.size() + this._effects2.size());
        for (final BuffInfo info : this._effects) {
            if (info != null && info.isInUse()) {
                this.writeInt(info.getSkill().getDisplayId());
                this.writeShort((short)info.getSkill().getDisplayLevel());
                this.writeInt(info.getSkill().getAbnormalType().getClientId());
                this.writeOptionalD(info.getTime());
            }
        }
        for (final Skill skill : this._effects2) {
            if (skill != null) {
                this.writeInt(skill.getDisplayId());
                this.writeShort((short)skill.getDisplayLevel());
                this.writeInt(skill.getAbnormalType().getClientId());
                this.writeShort((short)(-1));
            }
        }
    }
}
