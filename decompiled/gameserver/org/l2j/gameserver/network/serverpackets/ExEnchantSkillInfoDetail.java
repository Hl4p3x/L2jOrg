// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Set;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.EnchantSkillHolder;
import org.l2j.gameserver.enums.SkillEnchantType;

public class ExEnchantSkillInfoDetail extends ServerPacket
{
    private final SkillEnchantType _type;
    private final int _skillId;
    private final int _skillLvl;
    private final int _skillSubLvl;
    private final EnchantSkillHolder _enchantSkillHolder;
    
    public ExEnchantSkillInfoDetail(final SkillEnchantType type, final int skillId, final int skillLvl, final int skillSubLvl, final Player player) {
        this._type = type;
        this._skillId = skillId;
        this._skillLvl = skillLvl;
        this._skillSubLvl = skillSubLvl;
        this._enchantSkillHolder = null;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENCHANT_SKILL_INFO_DETAIL);
        this.writeInt(this._type.ordinal());
        this.writeInt(this._skillId);
        this.writeShort((short)this._skillLvl);
        this.writeShort((short)this._skillSubLvl);
        if (this._enchantSkillHolder != null) {
            this.writeLong(this._enchantSkillHolder.getSp(this._type));
            this.writeInt(this._enchantSkillHolder.getChance(this._type));
            final Set<ItemHolder> holders = this._enchantSkillHolder.getRequiredItems(this._type);
            this.writeInt(holders.size());
            holders.forEach(holder -> {
                this.writeInt(holder.getId());
                this.writeInt((int)holder.getCount());
            });
        }
    }
}
