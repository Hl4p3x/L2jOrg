// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances.LastImperialTomb;

import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.engine.geo.GeoEngine;
import java.util.ArrayList;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.scripts.ai.AbstractNpcAI;

public class ScarletVanHalisha extends AbstractNpcAI
{
    private static final int HALISHA2 = 29046;
    private static final int HALISHA3 = 29047;
    private static final int FRINTEZZA_DAEMON_ATTACK = 5014;
    private static final int FRINTEZZA_DAEMON_CHARGE = 5015;
    private static final int YOKE_OF_SCARLET = 5016;
    private static final int FRINTEZZA_DAEMON_MORPH = 5018;
    private static final int FRINTEZZA_DAEMON_FIELD = 5019;
    private Creature _target;
    private Skill _skill;
    private long _lastRangedSkillTime;
    private final int _rangedSkillMinCoolTime = 60000;
    
    public ScarletVanHalisha() {
        this.addAttackId(new int[] { 29046, 29047 });
        this.addKillId(new int[] { 29046, 29047 });
        this.addSpellFinishedId(new int[] { 29046, 29047 });
        this.registerMobs(29046, 29047);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "ATTACK": {
                if (npc != null) {
                    this.getSkillAI(npc);
                    break;
                }
                break;
            }
            case "RANDOM_TARGET": {
                this._target = this.getRandomTarget(npc, null);
                break;
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onSpellFinished(final Npc npc, final Player player, final Skill skill) {
        this.getSkillAI(npc);
        return super.onSpellFinished(npc, player, skill);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        this.startQuestTimer("RANDOM_TARGET", 5000L, npc, (Player)null, true);
        this.startQuestTimer("ATTACK", 500L, npc, (Player)null, true);
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        this.cancelQuestTimers("ATTACK");
        this.cancelQuestTimers("RANDOM_TARGET");
        return super.onKill(npc, killer, isSummon);
    }
    
    private Skill getRndSkills(final Npc npc) {
        switch (npc.getId()) {
            case 29046: {
                if (Rnd.get(100) < 10) {
                    return SkillEngine.getInstance().getSkill(5015, 2);
                }
                if (Rnd.get(100) < 10) {
                    return SkillEngine.getInstance().getSkill(5015, 5);
                }
                if (Rnd.get(100) < 2) {
                    return SkillEngine.getInstance().getSkill(5016, 1);
                }
                return SkillEngine.getInstance().getSkill(5014, 2);
            }
            case 29047: {
                if (Rnd.get(100) < 10) {
                    return SkillEngine.getInstance().getSkill(5015, 3);
                }
                if (Rnd.get(100) < 10) {
                    return SkillEngine.getInstance().getSkill(5015, 6);
                }
                if (Rnd.get(100) < 10) {
                    return SkillEngine.getInstance().getSkill(5015, 2);
                }
                if (this._lastRangedSkillTime + 60000L < System.currentTimeMillis() && Rnd.get(100) < 10) {
                    return SkillEngine.getInstance().getSkill(5019, 1);
                }
                if (this._lastRangedSkillTime + 60000L < System.currentTimeMillis() && Rnd.get(100) < 10) {
                    return SkillEngine.getInstance().getSkill(5018, 1);
                }
                if (Rnd.get(100) < 2) {
                    return SkillEngine.getInstance().getSkill(5016, 1);
                }
                return SkillEngine.getInstance().getSkill(5014, 3);
            }
            default: {
                return SkillEngine.getInstance().getSkill(5014, 1);
            }
        }
    }
    
    private synchronized void getSkillAI(final Npc npc) {
        if (npc.isInvul() || npc.isCastingNow()) {
            return;
        }
        if (Rnd.get(100) < 30 || this._target == null || this._target.isDead()) {
            this._skill = this.getRndSkills(npc);
            this._target = this.getRandomTarget(npc, this._skill);
        }
        final Creature target = this._target;
        Skill skill = this._skill;
        if (skill == null) {
            skill = this.getRndSkills(npc);
        }
        if (npc.isPhysicalMuted()) {
            return;
        }
        if (target == null || target.isDead()) {
            return;
        }
        if (GameUtils.checkIfInRange(skill.getCastRange(), (WorldObject)npc, (WorldObject)target, true)) {
            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            npc.setTarget((WorldObject)target);
            this._target = null;
            npc.doCast(skill);
        }
        else {
            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, new Object[] { target, null });
            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { target, null });
        }
    }
    
    private Creature getRandomTarget(final Npc npc, final Skill skill) {
        final ArrayList<Creature> result = new ArrayList<Creature>();
        for (final WorldObject obj : npc.getInstanceWorld().getPlayers()) {
            if (obj.isTargetable()) {
                if (obj.getActingPlayer().isInvisible()) {
                    continue;
                }
                if (((Creature)obj).getZ() < npc.getZ() - 100 && ((Creature)obj).getZ() > npc.getZ() + 100) {
                    continue;
                }
                if (!GeoEngine.getInstance().canSeeTarget(obj, (WorldObject)npc)) {
                    continue;
                }
            }
            if (obj.isTargetable()) {
                int skillRange = 150;
                if (skill != null) {
                    switch (skill.getId()) {
                        case 5014: {
                            skillRange = 150;
                            break;
                        }
                        case 5015: {
                            skillRange = 400;
                            break;
                        }
                        case 5016: {
                            skillRange = 200;
                            break;
                        }
                        case 5018:
                        case 5019: {
                            this._lastRangedSkillTime = System.currentTimeMillis();
                            skillRange = 550;
                            break;
                        }
                    }
                }
                if (!GameUtils.checkIfInRange(skillRange, (WorldObject)npc, obj, true) || ((Creature)obj).isDead()) {
                    continue;
                }
                result.add((Creature)obj);
            }
        }
        return (Creature)Rnd.get((List)result);
    }
    
    public static void main(final String[] args) {
        new ScarletVanHalisha();
    }
}
