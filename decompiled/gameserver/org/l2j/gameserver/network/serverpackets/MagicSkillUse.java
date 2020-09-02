// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.IPositionable;
import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Arrays;
import org.l2j.gameserver.util.GameUtils;
import java.util.Collections;
import org.l2j.gameserver.model.Location;
import java.util.List;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCastingType;

public final class MagicSkillUse extends ServerPacket
{
    private final int _skillId;
    private final int _skillLevel;
    private final int _hitTime;
    private final int _reuseGroup;
    private final int _reuseDelay;
    private final int _actionId;
    private final SkillCastingType _castingType;
    private final Creature _activeChar;
    private final WorldObject _target;
    private final List<Integer> _unknown;
    private final List<Location> _groundLocations;
    
    public MagicSkillUse(final Creature cha, final WorldObject target, final int skillId, final int skillLevel, final int hitTime, final int reuseDelay, final int reuseGroup, final int actionId, final SkillCastingType castingType) {
        this._unknown = Collections.emptyList();
        this._activeChar = cha;
        this._target = target;
        this._skillId = skillId;
        this._skillLevel = skillLevel;
        this._hitTime = hitTime;
        this._reuseGroup = reuseGroup;
        this._reuseDelay = reuseDelay;
        this._actionId = actionId;
        this._castingType = castingType;
        Location skillWorldPos = null;
        if (GameUtils.isPlayer(cha)) {
            final Player player = cha.getActingPlayer();
            if (player.getCurrentSkillWorldPosition() != null) {
                skillWorldPos = player.getCurrentSkillWorldPosition();
            }
        }
        this._groundLocations = ((skillWorldPos != null) ? Arrays.asList(skillWorldPos) : Collections.emptyList());
    }
    
    public MagicSkillUse(final Creature cha, final WorldObject target, final int skillId, final int skillLevel, final int hitTime, final int reuseDelay) {
        this(cha, target, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
    }
    
    public MagicSkillUse(final Creature cha, final int skillId, final int skillLevel, final int hitTime, final int reuseDelay) {
        this(cha, cha, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MAGIC_SKILL_USE);
        this.writeInt(this._castingType.getClientBarId());
        this.writeInt(this._activeChar.getObjectId());
        this.writeInt(this._target.getObjectId());
        this.writeInt(this._skillId);
        this.writeInt(this._skillLevel);
        this.writeInt(this._hitTime);
        this.writeInt(this._reuseGroup);
        this.writeInt(this._reuseDelay);
        this.writeInt(this._activeChar.getX());
        this.writeInt(this._activeChar.getY());
        this.writeInt(this._activeChar.getZ());
        this.writeShort((short)this._unknown.size());
        for (final int unknown : this._unknown) {
            this.writeShort((short)unknown);
        }
        this.writeShort((short)this._groundLocations.size());
        for (final IPositionable target : this._groundLocations) {
            this.writeInt(target.getX());
            this.writeInt(target.getY());
            this.writeInt(target.getZ());
        }
        this.writeInt(this._target.getX());
        this.writeInt(this._target.getY());
        this.writeInt(this._target.getZ());
        this.writeInt((int)((this._actionId >= 0) ? 1 : 0));
        this.writeInt(Math.max(this._actionId, 0));
    }
}
