// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerBaseStats extends Condition
{
    private final BaseStat _stat;
    private final int _value;
    
    public ConditionPlayerBaseStats(final Creature player, final BaseStat stat, final int value) {
        this._stat = stat;
        this._value = value;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        final Player player = effector.getActingPlayer();
        switch (this._stat) {
            case Int: {
                return player.getINT() >= this._value;
            }
            case Str: {
                return player.getSTR() >= this._value;
            }
            case Con: {
                return player.getCON() >= this._value;
            }
            case Dex: {
                return player.getDEX() >= this._value;
            }
            case Men: {
                return player.getMEN() >= this._value;
            }
            case Wit: {
                return player.getWIT() >= this._value;
            }
            default: {
                return false;
            }
        }
    }
}
