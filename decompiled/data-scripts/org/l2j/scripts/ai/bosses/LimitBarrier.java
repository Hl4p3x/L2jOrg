// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.bosses;

import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.model.WorldObject;
import java.util.List;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Map;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class LimitBarrier extends AbstractNpcAI
{
    private static final int[] RAID_BOSSES;
    private static final SkillHolder LIMIT_BARRIER;
    private static final Map<Npc, Integer> RAIDBOSS_HITS;
    
    private LimitBarrier() {
        this.addAttackId(LimitBarrier.RAID_BOSSES);
        this.addKillId(LimitBarrier.RAID_BOSSES);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "RESTORE_FULL_HP": {
                final int hits = LimitBarrier.RAIDBOSS_HITS.getOrDefault(npc, 0);
                if (!npc.isDead()) {
                    if (hits < Config.RAIDBOSS_LIMIT_BARRIER) {
                        if (player != null) {
                            npc.broadcastPacket((ServerPacket)new ExShowScreenMessage(NpcStringId.YOU_HAVE_FAILED_TO_DESTROY_THE_LIMIT_BARRIER_NTHE_RAID_BOSS_FULLY_RECOVERS_ITS_HEALTH, 2, 5000, true, new String[0]));
                        }
                        npc.setCurrentHp((double)npc.getStats().getMaxHp(), true);
                    }
                    else if (hits > Config.RAIDBOSS_LIMIT_BARRIER && player != null) {
                        npc.broadcastPacket((ServerPacket)new ExShowScreenMessage(NpcStringId.YOU_HAVE_DESTROYED_THE_LIMIT_BARRIER, 2, 5000, true, new String[0]));
                    }
                    npc.stopSkillEffects(true, LimitBarrier.LIMIT_BARRIER.getSkillId());
                }
                LimitBarrier.RAIDBOSS_HITS.put(npc, 0);
                break;
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon, final Skill skill) {
        if (npc.isAffectedBySkill(LimitBarrier.LIMIT_BARRIER.getSkillId())) {
            final int hits = LimitBarrier.RAIDBOSS_HITS.getOrDefault(npc, 0);
            LimitBarrier.RAIDBOSS_HITS.put(npc, hits + 1);
        }
        if (!npc.isAffectedBySkill(LimitBarrier.LIMIT_BARRIER.getSkillId()) && (this.getQuestTimers().get("RESTORE_FULL_HP") == null || this.getQuestTimers().get("RESTORE_FULL_HP").size() == 0) && this.canCastBarrier(npc)) {
            this.startQuestTimer("RESTORE_FULL_HP", 15000L, npc, attacker);
            npc.setTarget((WorldObject)npc);
            npc.abortAttack();
            npc.abortCast();
            npc.doCast(LimitBarrier.LIMIT_BARRIER.getSkill(), SkillCastingType.SIMULTANEOUS);
            npc.broadcastPacket((ServerPacket)new ExShowScreenMessage(NpcStringId.THE_RAID_BOSS_USES_THE_LIMIT_BARRIER_NFOCUS_YOUR_ATTACKS_TO_DESTROY_THE_LIMIT_BARRIER_IN_15_SEC, 2, 5000, true, new String[0]));
        }
        return super.onAttack(npc, attacker, damage, isSummon, skill);
    }
    
    private boolean canCastBarrier(final Npc npc) {
        return (npc.getCurrentHp() < npc.getMaxHp() * 0.9 && npc.getCurrentHp() > npc.getMaxHp() * 0.87) || (npc.getCurrentHp() < npc.getMaxHp() * 0.6 && npc.getCurrentHp() > npc.getMaxHp() * 0.58) || (npc.getCurrentHp() < npc.getMaxHp() * 0.3 && npc.getCurrentHp() > npc.getMaxHp() * 0.28);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        LimitBarrier.RAIDBOSS_HITS.remove(npc);
        return super.onKill(npc, killer, isSummon);
    }
    
    public static LimitBarrier provider() {
        return new LimitBarrier();
    }
    
    static {
        RAID_BOSSES = new int[] { 29001, 29006, 29014, 25010, 25013, 25050, 25067, 25070, 25089, 25099, 25103, 25119, 25159, 25122, 25131, 25137, 25176, 25217, 25230, 25241, 25418, 25420, 25434, 25460, 25463, 25473, 25475, 25744, 25745, 18049, 25051, 25106, 25125, 25163, 25226, 25234, 25252, 25255, 25256, 25263, 25407, 25423, 25453, 25478, 25738, 25739, 25742, 25743, 25746, 25747, 25748, 25749, 25750, 25751, 25754, 25755, 25756, 25757, 25758, 25759, 25760, 25761, 25762, 25763, 25766, 25767, 25768, 25769, 25770, 25772, 25773, 25774, 25775, 25776, 25777, 25779, 25780, 25781, 25782, 25783, 25784, 25787, 25788, 25789, 25790, 25791, 25792, 25792 };
        LIMIT_BARRIER = new SkillHolder(32203, 1);
        RAIDBOSS_HITS = new ConcurrentHashMap<Npc, Integer>();
    }
}
