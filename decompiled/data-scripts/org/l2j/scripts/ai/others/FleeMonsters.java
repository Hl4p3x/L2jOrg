// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class FleeMonsters extends AbstractNpcAI
{
    private static final int[] MOBS;
    private static final int FLEE_DISTANCE = 500;
    
    private FleeMonsters() {
        this.addAttackId(FleeMonsters.MOBS);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        npc.disableCoreAI(true);
        npc.setRunning();
        final Summon summon = isSummon ? attacker.getServitors().values().stream().findFirst().orElse((Summon)attacker.getPet()) : null;
        final ILocational attackerLoc = (ILocational)((summon == null) ? attacker : summon);
        final double radians = Math.toRadians(MathUtil.calculateAngleFrom(attackerLoc, (ILocational)npc));
        final int posX = (int)(npc.getX() + 500.0 * Math.cos(radians));
        final int posY = (int)(npc.getY() + 500.0 * Math.sin(radians));
        final int posZ = npc.getZ();
        final Location destination = GeoEngine.getInstance().canMoveToTargetLoc(npc.getX(), npc.getY(), npc.getZ(), posX, posY, posZ, attacker.getInstanceWorld());
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { destination });
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new FleeMonsters();
    }
    
    static {
        MOBS = new int[] { 20002, 20432 };
    }
}
