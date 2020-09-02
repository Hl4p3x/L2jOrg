// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances.ResidenceOfKingProcella;

import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.RaidBoss;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.instances.AbstractInstance;

public class ResidenceOfKingProcella extends AbstractInstance
{
    private static final int WIRI = 34048;
    private static final int PROCELLA = 29107;
    private static final int PROCELLA_GUARDIAN_1 = 29112;
    private static final int PROCELLA_GUARDIAN_2 = 29113;
    private static final int PROCELLA_GUARDIAN_3 = 29114;
    private static final int PROCELLA_STORM = 29115;
    private static final SkillHolder HURRICANE_SUMMON;
    private static final int HURRICANE_BOLT = 50043;
    private static final SkillHolder HURRICANE_BOLT_LV_1;
    private static final int TEMPLATE_ID = 197;
    private static int STORM_MAX_COUNT;
    private int _procellaStormCount;
    private RaidBoss _procella;
    private Monster _minion1;
    private Monster _minion2;
    private Monster _minion3;
    
    private ResidenceOfKingProcella() {
        super(new int[] { 197 });
        this.addStartNpc(34048);
        this.addKillId(new int[] { 29107, 29112, 29113, 29114 });
        this.addInstanceEnterId(new int[] { 197 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "ENTER": {
                this.enterInstance(player, npc, 197);
                this._procella = (RaidBoss)addSpawn(29107, 212862, 179828, -15489, 49151, false, 0L, true, player.getInstanceId());
                this.startQuestTimer("SPAWN_MINION", (long)(300000 + getRandom(-15000, 15000)), (Npc)this._procella, player);
                this.startQuestTimer("SPAWN_STORM", 5000L, (Npc)this._procella, player);
                this._procellaStormCount = 0;
                break;
            }
            case "SPAWN_MINION": {
                if (npc.getId() == 29107) {
                    this._minion1 = (Monster)addSpawn(29112, 212663, 179421, -15486, 31011, true, 0L, true, npc.getInstanceId());
                    this._minion2 = (Monster)addSpawn(29113, 213258, 179822, -15486, 12001, true, 0L, true, npc.getInstanceId());
                    this._minion3 = (Monster)addSpawn(29114, 212558, 179974, -15486, 12311, true, 0L, true, npc.getInstanceId());
                    this.startQuestTimer("HIDE_PROCELLA", 1000L, (Npc)this._procella, (Player)null);
                    break;
                }
                break;
            }
            case "SPAWN_STORM": {
                if (this._procellaStormCount < ResidenceOfKingProcella.STORM_MAX_COUNT) {
                    this._procella.useMagic(ResidenceOfKingProcella.HURRICANE_SUMMON.getSkill());
                    final Npc procellaStorm = addSpawn(29115, this._procella.getX() + getRandom(-500, 500), this._procella.getY() + getRandom(-500, 500), this._procella.getZ(), 31011, true, 0L, true, npc.getInstanceId());
                    procellaStorm.setRandomWalking(true);
                    ++this._procellaStormCount;
                    this.startQuestTimer("SPAWN_STORM", 60000L, (Npc)this._procella, (Player)null);
                    this.startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100L, procellaStorm, player);
                    break;
                }
                break;
            }
            case "HIDE_PROCELLA": {
                if (this._procella.isInvisible()) {
                    this._procella.setInvisible(false);
                    break;
                }
                this._procella.setInvisible(true);
                this.startQuestTimer("SPAWN_MINION", (long)(300000 + getRandom(-15000, 15000)), (Npc)this._procella, player);
                break;
            }
            case "CHECK_CHAR_INSIDE_RADIUS_NPC": {
                final Instance world = npc.getInstanceWorld();
                if (world != null) {
                    final Player plr = (Player)world.getPlayers().stream().findAny().orElse(null);
                    if (plr != null && MathUtil.isInsideRadius3D((ILocational)plr, (ILocational)npc, 100)) {
                        npc.abortAttack();
                        npc.abortCast();
                        npc.setTarget((WorldObject)plr);
                        if (plr.getAffectedSkillLevel(50043) == 1) {
                            npc.abortCast();
                            this.startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100L, npc, player);
                        }
                        else if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingProcella.HURRICANE_BOLT_LV_1.getSkill())) {
                            npc.doCast(ResidenceOfKingProcella.HURRICANE_BOLT_LV_1.getSkill());
                        }
                        this.startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100L, npc, player);
                    }
                    else {
                        this.startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100L, npc, player);
                    }
                    break;
                }
                break;
            }
        }
        return null;
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        if (npc.getId() == 29107) {
            final Instance world = npc.getInstanceWorld();
            if (world != null) {
                this.cancelQuestTimer("SPAWN_MINION", npc, player);
                this.cancelQuestTimer("SPAWN_STORM", npc, player);
                this.cancelQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", npc, player);
                world.finishInstance();
            }
        }
        else if (this._minion1.isDead() && this._minion2.isDead() && this._minion3.isDead()) {
            this.startQuestTimer("HIDE_PROCELLA", 1000L, (Npc)this._procella, (Player)null);
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public static ResidenceOfKingProcella provider() {
        return new ResidenceOfKingProcella();
    }
    
    static {
        HURRICANE_SUMMON = new SkillHolder(50042, 1);
        HURRICANE_BOLT_LV_1 = new SkillHolder(50043, 1);
        ResidenceOfKingProcella.STORM_MAX_COUNT = 16;
    }
}
