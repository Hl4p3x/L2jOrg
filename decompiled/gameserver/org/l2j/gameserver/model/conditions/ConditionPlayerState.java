// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.base.PlayerState;

public class ConditionPlayerState extends Condition
{
    public final PlayerState _check;
    public final boolean _required;
    
    public ConditionPlayerState(final PlayerState check, final boolean required) {
        this._check = check;
        this._required = required;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Player player = effector.getActingPlayer();
        switch (this._check) {
            case RESTING: {
                if (player != null) {
                    return player.isSitting() == this._required;
                }
                return !this._required;
            }
            case MOVING: {
                return effector.isMoving() == this._required;
            }
            case RUNNING: {
                return effector.isRunning() == this._required;
            }
            case STANDING: {
                if (player != null) {
                    return this._required != (player.isSitting() || player.isMoving());
                }
                return this._required != effector.isMoving();
            }
            case FLYING: {
                return effector.isFlying() == this._required;
            }
            case BEHIND: {
                return effector.isBehind(effected) == this._required;
            }
            case FRONT: {
                return effector.isInFrontOf(effected) == this._required;
            }
            case CHAOTIC: {
                if (player != null) {
                    return player.getReputation() < 0 == this._required;
                }
                return !this._required;
            }
            case OLYMPIAD: {
                if (player != null) {
                    return player.isInOlympiadMode() == this._required;
                }
                return !this._required;
            }
            default: {
                return !this._required;
            }
        }
    }
}
