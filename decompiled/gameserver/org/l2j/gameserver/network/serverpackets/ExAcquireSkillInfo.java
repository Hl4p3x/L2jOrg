// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.Objects;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;

public class ExAcquireSkillInfo extends ServerPacket
{
    private final int _id;
    private final int _level;
    private final int _dualClassLevel;
    private final long _spCost;
    private final int _minLevel;
    private final List<ItemHolder> _itemReq;
    private final List<Skill> _skillRem;
    
    public ExAcquireSkillInfo(final Player player, final SkillLearn skillLearn) {
        this._id = skillLearn.getSkillId();
        this._level = skillLearn.getSkillLevel();
        this._dualClassLevel = skillLearn.getDualClassLevel();
        this._spCost = skillLearn.getLevelUpSp();
        this._minLevel = skillLearn.getGetLevel();
        this._itemReq = skillLearn.getRequiredItems();
        final Stream<Object> stream = skillLearn.getRemoveSkills().stream();
        Objects.requireNonNull(player);
        this._skillRem = stream.map((Function<? super Object, ?>)player::getKnownSkill).filter(Objects::nonNull).collect((Collector<? super Object, ?, List<Skill>>)Collectors.toList());
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ACQUIRE_SKILL_INFO);
        this.writeInt(this._id);
        this.writeInt(this._level);
        this.writeLong(this._spCost);
        this.writeShort((short)this._minLevel);
        this.writeShort((short)this._dualClassLevel);
        this.writeInt(this._itemReq.size());
        for (final ItemHolder holder : this._itemReq) {
            this.writeInt(holder.getId());
            this.writeLong(holder.getCount());
        }
        this.writeInt(this._skillRem.size());
        for (final Skill skill : this._skillRem) {
            this.writeInt(skill.getId());
            this.writeInt(skill.getLevel());
        }
    }
}
