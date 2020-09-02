// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public class FairyTrees extends AbstractNpcAI
{
    private static final int SOUL_GUARDIAN = 27189;
    private static final int[] MOBS;
    private static SkillHolder VENOMOUS_POISON;
    private static final int MIN_DISTANCE = 1500;
    
    private FairyTrees() {
        this.addKillId(FairyTrees.MOBS);
        this.addSpawnId(FairyTrees.MOBS);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)killer, 1500)) {
            for (int i = 0; i < 20; ++i) {
                final Npc guardian = addSpawn(27189, (IPositionable)npc, false, 30000L);
                final Playable attacker = (Playable)(isSummon ? ((Playable)killer.getServitors().values().stream().findFirst().orElse((Playable)killer.getPet())) : killer);
                this.addAttackPlayerDesire(guardian, attacker);
                if (Rnd.nextBoolean()) {
                    guardian.setTarget((WorldObject)attacker);
                    guardian.doCast(FairyTrees.VENOMOUS_POISON.getSkill());
                }
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onSpawn(final Npc npc) {
        npc.setRandomWalking(false);
        npc.setIsImmobilized(true);
        return super.onSpawn(npc);
    }
    
    public static AbstractNpcAI provider() {
        return new FairyTrees();
    }
    
    static {
        MOBS = new int[] { 27185, 27186, 27187, 27188 };
        FairyTrees.VENOMOUS_POISON = new SkillHolder(4243, 1);
    }
}
