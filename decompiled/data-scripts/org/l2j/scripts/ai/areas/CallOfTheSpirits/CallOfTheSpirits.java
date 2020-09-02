// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.CallOfTheSpirits;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.slf4j.Logger;
import org.l2j.scripts.ai.AbstractNpcAI;

public class CallOfTheSpirits extends AbstractNpcAI
{
    private static Logger LOGGER;
    private static final int MONSTEREYE = 20068;
    private static final int GRANITEGOLEM = 20083;
    private static final int GUARDIANGOLEM = 21656;
    private static final int GOUL = 20201;
    private static final int CORPSETRACKER = 20202;
    private static final int GUARDIANDRECO = 21654;
    private static final int LETOWARRIOR = 20580;
    private static final int LETOSHAMAN = 20581;
    private static final int GUARDIANRAIDO = 21655;
    private static final int GIANTMONSTEREYE = 20556;
    private static final int DIREWYRM = 20557;
    private static final int GUARDIANWYRM = 21657;
    private static final int LIZARDWARRIOR = 20643;
    private static final int LIZARDMATRIACH = 20645;
    private static final int GUARDIANHARIT = 21658;
    private static final int CRIMSONDRAKE = 20670;
    private static final int PALIBATI = 20673;
    private static final int GUARDIANPALIBATI = 21660;
    
    private CallOfTheSpirits() {
        this.addKillId(new int[] { 20068, 20083, 20201, 20202, 20580, 20581, 20556, 20557, 20643, 20645, 20670, 20673 });
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (Rnd.chance(10)) {
            Npc spawnMonster = null;
            switch (npc.getId()) {
                case 20068:
                case 20083: {
                    spawnMonster = addSpawn(21656, (IPositionable)npc, false, 300000L);
                    break;
                }
                case 20201:
                case 20202: {
                    spawnMonster = addSpawn(21654, (IPositionable)npc, false, 300000L);
                    break;
                }
                case 20580:
                case 20581: {
                    spawnMonster = addSpawn(21655, (IPositionable)npc, false, 300000L);
                    break;
                }
                case 20556:
                case 20557: {
                    spawnMonster = addSpawn(21657, (IPositionable)npc, false, 300000L);
                    break;
                }
                case 20643:
                case 20645: {
                    spawnMonster = addSpawn(21658, (IPositionable)npc, false, 300000L);
                    break;
                }
                case 20670:
                case 20673: {
                    spawnMonster = addSpawn(21660, (IPositionable)npc, false, 300000L);
                    break;
                }
                default: {
                    spawnMonster = null;
                    break;
                }
            }
            final Playable attacker = (Playable)(isSummon ? ((Playable)killer.getServitors().values().stream().findFirst().orElse((Playable)killer.getPet())) : killer);
            this.addAttackPlayerDesire(spawnMonster, attacker);
            npc.deleteMe();
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new CallOfTheSpirits();
    }
    
    static {
        CallOfTheSpirits.LOGGER = LoggerFactory.getLogger((Class)CallOfTheSpirits.class);
    }
}
