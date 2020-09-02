// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.Creature;

public final class ConditionSiegeZone extends Condition
{
    public static final int COND_NOT_ZONE = 1;
    public static final int COND_CAST_ATTACK = 2;
    public static final int COND_CAST_DEFEND = 4;
    public static final int COND_CAST_NEUTRAL = 8;
    public static final int COND_FORT_ATTACK = 16;
    public static final int COND_FORT_DEFEND = 32;
    public static final int COND_FORT_NEUTRAL = 64;
    private final int _value;
    private final boolean _self;
    
    public ConditionSiegeZone(final int value, final boolean self) {
        this._value = value;
        this._self = self;
    }
    
    public static boolean checkIfOk(final Creature activeChar, final Castle castle, final int value) {
        if (!GameUtils.isPlayer(activeChar)) {
            return false;
        }
        final Player player = (Player)activeChar;
        if (castle == null || castle.getId() <= 0) {
            if ((value & 0x1) != 0x0) {
                return true;
            }
        }
        else if (!castle.getZone().isActive()) {
            if ((value & 0x1) != 0x0) {
                return true;
            }
        }
        else {
            if ((value & 0x2) != 0x0 && player.isRegisteredOnThisSiegeField(castle.getId()) && player.getSiegeState() == 1) {
                return true;
            }
            if ((value & 0x4) != 0x0 && player.isRegisteredOnThisSiegeField(castle.getId()) && player.getSiegeState() == 2) {
                return true;
            }
            if ((value & 0x8) != 0x0 && player.getSiegeState() == 0) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Creature target = this._self ? effector : effected;
        final Castle castle = CastleManager.getInstance().getCastle(target);
        if (castle == null) {
            return (this._value & 0x1) != 0x0;
        }
        return checkIfOk(target, castle, this._value);
    }
}
