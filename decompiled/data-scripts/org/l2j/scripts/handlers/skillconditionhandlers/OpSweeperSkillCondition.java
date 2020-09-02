// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.util.GameUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpSweeperSkillCondition implements SkillCondition
{
    private OpSweeperSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final AtomicBoolean canSweep = new AtomicBoolean(false);
        if (caster.getActingPlayer() != null) {
            final Player sweeper = caster.getActingPlayer();
            if (skill != null) {
                Attackable a;
                final AtomicBoolean atomicBoolean;
                final Player player;
                skill.forEachTargetAffected((Creature)sweeper, target, o -> {
                    if (GameUtils.isAttackable((WorldObject)o)) {
                        a = o;
                        if (a.isDead()) {
                            if (a.isSpoiled()) {
                                atomicBoolean.set(a.checkSpoilOwner(player, true));
                                if (atomicBoolean.get()) {
                                    atomicBoolean.set(!a.isOldCorpse(player, Config.CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY, true));
                                }
                                if (atomicBoolean.get()) {
                                    atomicBoolean.set(player.getInventory().checkInventorySlotsAndWeight(a.getSpoilLootItems(), true, true));
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
        return canSweep.get();
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final OpSweeperSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "OpSweeper";
        }
        
        static {
            INSTANCE = new OpSweeperSkillCondition();
        }
    }
}
