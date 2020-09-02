// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances.ResidenceOfQueenNebula;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.instances.AbstractInstance;

public class ResidenceOfQueenNebula extends AbstractInstance
{
    private static final int IRIS = 34046;
    private static final int NEBULA = 29106;
    private static final int WATER_SLIME = 29111;
    private static final int TEMPLATE_ID = 196;
    private static final int AQUA_RAGE = 50036;
    private static SkillHolder AQUA_RAGE_1;
    private static SkillHolder AQUA_RAGE_2;
    private static SkillHolder AQUA_RAGE_3;
    private static SkillHolder AQUA_RAGE_4;
    private static SkillHolder AQUA_RAGE_5;
    private static SkillHolder AQUA_SUMMON;
    
    private ResidenceOfQueenNebula() {
        super(new int[] { 196 });
        this.addStartNpc(34046);
        this.addKillId(new int[] { 29106, 29111 });
        this.addAttackId(29106);
        this.addInstanceLeaveId(new int[] { 196 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "ENTER": {
                this.enterInstance(player, npc, 196);
                break;
            }
            case "SPAWN_WATER_SLIME": {
                final Instance world = npc.getInstanceWorld();
                if (world != null) {
                    final Player plr = (Player)world.getPlayers().stream().findAny().orElse(null);
                    this.startQuestTimer("CAST_AQUA_RAGE", (long)(60000 + getRandom(-15000, 15000)), npc, plr);
                    if (npc.getId() == 29106) {
                        npc.doCast(ResidenceOfQueenNebula.AQUA_SUMMON.getSkill());
                        for (int i = 0; i < getRandom(4, 6); ++i) {
                            addSpawn(npc, 29111, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, -1L, true, npc.getInstanceId());
                            this.startQuestTimer("SPAWN_WATER_SLIME", 300000L, npc, (Player)null);
                        }
                    }
                    break;
                }
                break;
            }
            case "PLAYER_PARA": {
                if (player.getAffectedSkillLevel(50036) == 5) {
                    player.getEffectList().startAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.FROZEN_PILLAR });
                    player.setIsImmobilized(true);
                    this.startQuestTimer("PLAYER_UNPARA", 5000L, npc, player);
                    break;
                }
                break;
            }
            case "PLAYER_UNPARA": {
                player.getEffectList().stopSkillEffects(true, ResidenceOfQueenNebula.AQUA_RAGE_5.getSkill());
                player.setIsImmobilized(false);
                break;
            }
            case "CAST_AQUA_RAGE": {
                this.startQuestTimer("CAST_AQUA_RAGE", 5000L, npc, player);
                if (!MathUtil.isInsideRadius3D((ILocational)player, (ILocational)npc, 1000)) {
                    break;
                }
                if (player.getAffectedSkillLevel(50036) == 1) {
                    if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfQueenNebula.AQUA_RAGE_2.getSkill())) {
                        npc.doCast(ResidenceOfQueenNebula.AQUA_RAGE_2.getSkill());
                        break;
                    }
                    break;
                }
                else if (player.getAffectedSkillLevel(50036) == 2) {
                    if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfQueenNebula.AQUA_RAGE_3.getSkill())) {
                        npc.doCast(ResidenceOfQueenNebula.AQUA_RAGE_3.getSkill());
                        break;
                    }
                    break;
                }
                else if (player.getAffectedSkillLevel(50036) == 3) {
                    if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfQueenNebula.AQUA_RAGE_4.getSkill())) {
                        npc.doCast(ResidenceOfQueenNebula.AQUA_RAGE_4.getSkill());
                        break;
                    }
                    break;
                }
                else if (player.getAffectedSkillLevel(50036) == 4) {
                    if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfQueenNebula.AQUA_RAGE_5.getSkill())) {
                        npc.doCast(ResidenceOfQueenNebula.AQUA_RAGE_5.getSkill());
                        this.startQuestTimer("PLAYER_PARA", 100L, npc, player);
                        break;
                    }
                    break;
                }
                else {
                    if (player.getAffectedSkillLevel(50036) == 5) {
                        npc.abortCast();
                        break;
                    }
                    if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfQueenNebula.AQUA_RAGE_1.getSkill())) {
                        npc.doCast(ResidenceOfQueenNebula.AQUA_RAGE_1.getSkill());
                        break;
                    }
                    break;
                }
                break;
            }
        }
        return null;
    }
    
    public String onSpawn(final Npc npc) {
        this.startQuestTimer("SPAWN_WATER_SLIME", 300000L, npc, (Player)null);
        return super.onSpawn(npc);
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        switch (npc.getId()) {
            case 29106: {
                this.cancelQuestTimer("CAST_AQUA_RAGE", npc, player);
                this.cancelQuestTimer("SPAWN_WATER_SLIME", npc, player);
                final Instance world = npc.getInstanceWorld();
                if (world != null) {
                    world.finishInstance();
                }
            }
            case 29111: {
                if (player.getAffectedSkillLevel(50036) == 1) {
                    if (getRandom(100) < 50) {
                        player.stopSkillEffects(ResidenceOfQueenNebula.AQUA_RAGE_1.getSkill());
                        break;
                    }
                    break;
                }
                else if (player.getAffectedSkillLevel(50036) == 2) {
                    if (getRandom(100) < 50) {
                        player.stopSkillEffects(ResidenceOfQueenNebula.AQUA_RAGE_2.getSkill());
                        final Skill skill = SkillEngine.getInstance().getSkill(50036, 1);
                        skill.applyEffects((Creature)player, (Creature)player);
                        break;
                    }
                    break;
                }
                else if (player.getAffectedSkillLevel(50036) == 3) {
                    if (getRandom(100) < 50) {
                        player.stopSkillEffects(ResidenceOfQueenNebula.AQUA_RAGE_3.getSkill());
                        final Skill skill = SkillEngine.getInstance().getSkill(50036, 2);
                        skill.applyEffects((Creature)player, (Creature)player);
                        break;
                    }
                    break;
                }
                else {
                    if (player.getAffectedSkillLevel(50036) == 4 && getRandom(100) < 50) {
                        player.stopSkillEffects(ResidenceOfQueenNebula.AQUA_RAGE_4.getSkill());
                        final Skill skill = SkillEngine.getInstance().getSkill(50036, 3);
                        skill.applyEffects((Creature)player, (Creature)player);
                        break;
                    }
                    break;
                }
                break;
            }
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public static ResidenceOfQueenNebula provider() {
        return new ResidenceOfQueenNebula();
    }
    
    static {
        ResidenceOfQueenNebula.AQUA_RAGE_1 = new SkillHolder(50036, 1);
        ResidenceOfQueenNebula.AQUA_RAGE_2 = new SkillHolder(50036, 2);
        ResidenceOfQueenNebula.AQUA_RAGE_3 = new SkillHolder(50036, 3);
        ResidenceOfQueenNebula.AQUA_RAGE_4 = new SkillHolder(50036, 4);
        ResidenceOfQueenNebula.AQUA_RAGE_5 = new SkillHolder(50036, 5);
        ResidenceOfQueenNebula.AQUA_SUMMON = new SkillHolder(50037, 1);
    }
}
