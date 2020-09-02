// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.Config;
import java.util.ArrayList;
import org.l2j.gameserver.model.SkillLearn;
import java.util.List;
import org.l2j.gameserver.model.base.AcquireSkillType;

public class AcquireSkillInfo extends ServerPacket
{
    private final AcquireSkillType _type;
    private final int _id;
    private final int _level;
    private final long _spCost;
    private final List<Req> _reqs;
    
    public AcquireSkillInfo(final AcquireSkillType skillType, final SkillLearn skillLearn) {
        this._id = skillLearn.getSkillId();
        this._level = skillLearn.getSkillLevel();
        this._spCost = skillLearn.getLevelUpSp();
        this._type = skillType;
        this._reqs = new ArrayList<Req>();
        if (skillType != AcquireSkillType.PLEDGE || Config.LIFE_CRYSTAL_NEEDED) {
            for (final ItemHolder item : skillLearn.getRequiredItems()) {
                if (!Config.DIVINE_SP_BOOK_NEEDED && this._id == CommonSkill.DIVINE_INSPIRATION.getId()) {
                    continue;
                }
                this._reqs.add(new Req(99, item.getId(), item.getCount(), 50));
            }
        }
    }
    
    public AcquireSkillInfo(final AcquireSkillType skillType, final SkillLearn skillLearn, final int sp) {
        this._id = skillLearn.getSkillId();
        this._level = skillLearn.getSkillLevel();
        this._spCost = sp;
        this._type = skillType;
        this._reqs = new ArrayList<Req>();
        for (final ItemHolder item : skillLearn.getRequiredItems()) {
            this._reqs.add(new Req(99, item.getId(), item.getCount(), 50));
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ACQUIRE_SKILL_INFO);
        this.writeInt(this._id);
        this.writeInt(this._level);
        this.writeLong(this._spCost);
        this.writeInt(this._type.getId());
        this.writeInt(this._reqs.size());
        for (final Req temp : this._reqs) {
            this.writeInt(temp.type);
            this.writeInt(temp.itemId);
            this.writeLong(temp.count);
            this.writeInt(temp.unk);
        }
    }
    
    private static class Req
    {
        public int itemId;
        public long count;
        public int type;
        public int unk;
        
        public Req(final int pType, final int pItemId, final long itemCount, final int pUnk) {
            this.itemId = pItemId;
            this.type = pType;
            this.count = itemCount;
            this.unk = pUnk;
        }
    }
}
