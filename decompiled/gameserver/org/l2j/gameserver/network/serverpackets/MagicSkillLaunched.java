// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Arrays;
import java.util.Collections;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import java.util.Collection;
import org.l2j.gameserver.model.skills.SkillCastingType;

public class MagicSkillLaunched extends ServerPacket
{
    private final int _charObjId;
    private final int _skillId;
    private final int _skillLevel;
    private final SkillCastingType _castingType;
    private final Collection<WorldObject> _targets;
    
    public MagicSkillLaunched(final Creature cha, final int skillId, final int skillLevel, final SkillCastingType castingType, Collection<WorldObject> targets) {
        this._charObjId = cha.getObjectId();
        this._skillId = skillId;
        this._skillLevel = skillLevel;
        this._castingType = castingType;
        if (targets == null) {
            targets = (Collection<WorldObject>)Collections.singletonList(cha);
        }
        this._targets = targets;
    }
    
    public MagicSkillLaunched(final Creature cha, final int skillId, final int skillLevel, final SkillCastingType castingType, final WorldObject... targets) {
        this(cha, skillId, skillLevel, castingType, (Collection<WorldObject>)((targets == null) ? Collections.singletonList(cha) : Arrays.asList((Creature[])targets)));
    }
    
    public MagicSkillLaunched(final Creature cha, final int skillId, final int skillLevel) {
        this(cha, skillId, skillId, SkillCastingType.NORMAL, (Collection<WorldObject>)Collections.singletonList(cha));
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MAGIC_SKILL_LAUNCHED);
        this.writeInt(this._castingType.getClientBarId());
        this.writeInt(this._charObjId);
        this.writeInt(this._skillId);
        this.writeInt(this._skillLevel);
        this.writeInt(this._targets.size());
        for (final WorldObject target : this._targets) {
            this.writeInt(target.getObjectId());
        }
    }
}
