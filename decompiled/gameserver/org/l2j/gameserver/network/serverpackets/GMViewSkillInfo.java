// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;

public class GMViewSkillInfo extends ServerPacket
{
    private final Player _activeChar;
    private final Collection<Skill> _skills;
    
    public GMViewSkillInfo(final Player cha) {
        this._activeChar = cha;
        this._skills = this._activeChar.getSkillList();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GM_VIEW_SKILL_INFO);
        this.writeString((CharSequence)this._activeChar.getName());
        this.writeInt(this._skills.size());
        final boolean isDisabled = this._activeChar.getClan() != null && this._activeChar.getClan().getReputationScore() < 0;
        for (final Skill skill : this._skills) {
            this.writeInt((int)(skill.isPassive() ? 1 : 0));
            this.writeShort((short)skill.getDisplayLevel());
            this.writeShort((short)skill.getSubLevel());
            this.writeInt(skill.getDisplayId());
            this.writeInt(0);
            this.writeByte((byte)(byte)((isDisabled && skill.isClanSkill()) ? 1 : 0));
            this.writeByte((byte)(byte)(skill.isEnchantable() ? 1 : 0));
        }
    }
}
