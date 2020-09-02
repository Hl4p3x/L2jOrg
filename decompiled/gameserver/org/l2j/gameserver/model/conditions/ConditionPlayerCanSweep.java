// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanSweep extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerCanSweep(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final AtomicBoolean canSweep = new AtomicBoolean(false);
        if (effector.getActingPlayer() != null) {
            final Player sweeper = effector.getActingPlayer();
            if (skill != null) {
                Attackable target;
                final AtomicBoolean atomicBoolean;
                final Player player;
                skill.forEachTargetAffected(sweeper, effected, o -> {
                    if (GameUtils.isAttackable(o)) {
                        target = o;
                        if (target.isDead()) {
                            if (target.isSpoiled()) {
                                atomicBoolean.set(target.checkSpoilOwner(player, true));
                                if (atomicBoolean.get()) {
                                    atomicBoolean.set(!target.isOldCorpse(player, Config.CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY, true));
                                }
                                if (atomicBoolean.get()) {
                                    atomicBoolean.set(player.getInventory().checkInventorySlotsAndWeight(target.getSpoilLootItems(), true, true));
                                }
                            }
                            else {
                                player.sendPacket(SystemMessageId.SWEEPER_FAILED_TARGET_NOT_SPOILED);
                            }
                        }
                    }
                    return;
                });
            }
        }
        return this._val == canSweep.get();
    }
}
