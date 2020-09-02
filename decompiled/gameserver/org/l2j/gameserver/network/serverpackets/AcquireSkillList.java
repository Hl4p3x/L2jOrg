// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Collection;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.SkillLearn;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;

public class AcquireSkillList extends ServerPacket
{
    final Player player;
    final List<SkillLearn> _learnable;
    
    public AcquireSkillList(final Player player) {
        this.player = player;
        (this._learnable = SkillTreesData.getInstance().getAvailableSkills(player, player.getClassId(), false, false)).addAll(SkillTreesData.getInstance().getNextAvailableSkills(player, player.getClassId(), false, false));
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ACQUIRE_SKILL_LIST);
        this.writeShort((short)this._learnable.size());
        for (final SkillLearn skill : this._learnable) {
            if (skill == null) {
                continue;
            }
            this.writeInt(skill.getSkillId());
            this.writeShort((short)skill.getSkillLevel());
            this.writeLong(skill.getLevelUpSp());
            this.writeByte((byte)skill.getGetLevel());
            this.writeShort((short)0);
            if (skill.getRequiredItems().size() > 0) {
                for (final ItemHolder item : skill.getRequiredItems()) {
                    this.writeByte((byte)1);
                    this.writeInt(item.getId());
                    this.writeLong(item.getCount());
                }
            }
            else {
                this.writeByte((byte)0);
            }
            this.writeByte((byte)0);
        }
    }
}
