// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances.ResidenceOfKingIgnis;

import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Map;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.instances.AbstractInstance;

public class ResidenceOfKingIgnis extends AbstractInstance
{
    private static final int TARA = 34047;
    private static final int FREYA = 29109;
    private static final int IGNIS = 29105;
    private static SkillHolder FIRE_RAG_1;
    private static SkillHolder FIRE_RAG_2;
    private static SkillHolder FIRE_RAG_3;
    private static SkillHolder FIRE_RAG_4;
    private static SkillHolder FIRE_RAG_5;
    private static SkillHolder FIRE_RAG_6;
    private static SkillHolder FIRE_RAG_7;
    private static SkillHolder FIRE_RAG_8;
    private static SkillHolder FIRE_RAG_9;
    private static SkillHolder FIRE_RAG_10;
    private static SkillHolder FREYA_SAFETY_ZONE;
    private static final Map<Player, Integer> _playerFireRage;
    private static final int TEMPLATE_ID = 195;
    
    private ResidenceOfKingIgnis() {
        super(new int[] { 195 });
        this.addStartNpc(34047);
        this.addTalkId(29109);
        this.addKillId(29105);
        this.addAttackId(29105);
        this.addInstanceLeaveId(new int[] { 195 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "ENTER": {
                this.enterInstance(player, npc, 195);
                break;
            }
            case "REMOVE_FIRE_RAGE": {
                if (!player.isAffectedBySkill(ResidenceOfKingIgnis.FIRE_RAG_1)) {
                    npc.broadcastSay(ChatType.NPC_SHOUT, "I help you only when you affected by Fire Rage skill.");
                    break;
                }
                final int playerFireRage = ResidenceOfKingIgnis._playerFireRage.getOrDefault(player, 0);
                if (playerFireRage < 5) {
                    ResidenceOfKingIgnis._playerFireRage.put(player, playerFireRage + 1);
                    player.stopSkillEffects(true, ResidenceOfKingIgnis.FIRE_RAG_1.getSkillId());
                    player.doCast(ResidenceOfKingIgnis.FREYA_SAFETY_ZONE.getSkill());
                    npc.broadcastSay(ChatType.NPC_SHOUT, "Bless with you. Lets finish fight!");
                    break;
                }
                npc.broadcastSay(ChatType.NPC_SHOUT, "You cannot use my power again.");
                player.sendMessage("Freya: You cannot use my power again.");
                break;
            }
            case "CAST_FIRE_RAGE_1": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_1.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_1.getSkill());
                    break;
                }
                break;
            }
            case "CAST_FIRE_RAGE_2": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_2.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_2.getSkill());
                    break;
                }
                break;
            }
            case "CAST_FIRE_RAGE_3": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_3.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_3.getSkill());
                    break;
                }
                break;
            }
            case "CAST_FIRE_RAGE_4": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_4.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_4.getSkill());
                    break;
                }
                break;
            }
            case "CAST_FIRE_RAGE_5": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_5.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_5.getSkill());
                    break;
                }
                break;
            }
            case "CAST_FIRE_RAGE_6": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_6.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_6.getSkill());
                    break;
                }
                break;
            }
            case "CAST_FIRE_RAGE_7": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_7.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_7.getSkill());
                    break;
                }
                break;
            }
            case "CAST_FIRE_RAGE_8": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_8.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_8.getSkill());
                    break;
                }
                break;
            }
            case "CAST_FIRE_RAGE_9": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_9.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_9.getSkill());
                    break;
                }
                break;
            }
            case "CAST_FIRE_RAGE_10": {
                if (SkillCaster.checkUseConditions((Creature)npc, ResidenceOfKingIgnis.FIRE_RAG_10.getSkill())) {
                    npc.doCast(ResidenceOfKingIgnis.FIRE_RAG_10.getSkill());
                    break;
                }
                break;
            }
        }
        return null;
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon, final Skill skill) {
        if (npc.getCurrentHp() < npc.getMaxHp() * 0.99 && npc.getCurrentHp() > npc.getMaxHp() * 0.7) {
            this.startQuestTimer("CAST_FIRE_RAGE_1", 1000L, npc, (Player)null);
        }
        else if (npc.getCurrentHp() < npc.getMaxHp() * 0.7 && npc.getCurrentHp() > npc.getMaxHp() * 0.5) {
            this.startQuestTimer("CAST_FIRE_RAGE_2", 1000L, npc, (Player)null);
        }
        else if (npc.getCurrentHp() < npc.getMaxHp() * 0.5 && npc.getCurrentHp() > npc.getMaxHp() * 0.4) {
            this.startQuestTimer("CAST_FIRE_RAGE_3", 1000L, npc, (Player)null);
        }
        else if (npc.getCurrentHp() < npc.getMaxHp() * 0.4 && npc.getCurrentHp() > npc.getMaxHp() * 0.25) {
            this.startQuestTimer("CAST_FIRE_RAGE_4", 1000L, npc, (Player)null);
        }
        else if (npc.getCurrentHp() < npc.getMaxHp() * 0.25 && npc.getCurrentHp() > npc.getMaxHp() * 0.15) {
            this.startQuestTimer("CAST_FIRE_RAGE_5", 1000L, npc, (Player)null);
        }
        else if (npc.getCurrentHp() < npc.getMaxHp() * 0.15 && npc.getCurrentHp() > npc.getMaxHp() * 0.1) {
            this.startQuestTimer("CAST_FIRE_RAGE_6", 1000L, npc, (Player)null);
        }
        else if (npc.getCurrentHp() < npc.getMaxHp() * 0.1 && npc.getCurrentHp() > npc.getMaxHp() * 0.7) {
            this.startQuestTimer("CAST_FIRE_RAGE_7", 1000L, npc, (Player)null);
        }
        else if (npc.getCurrentHp() < npc.getMaxHp() * 0.7 && npc.getCurrentHp() > npc.getMaxHp() * 0.5) {
            this.startQuestTimer("CAST_FIRE_RAGE_8", 1000L, npc, (Player)null);
        }
        else if (npc.getCurrentHp() < npc.getMaxHp() * 0.5 && npc.getCurrentHp() > npc.getMaxHp() * 0.3) {
            this.startQuestTimer("CAST_FIRE_RAGE_9", 1000L, npc, (Player)null);
        }
        else if (npc.getCurrentHp() < npc.getMaxHp() * 0.3) {
            this.startQuestTimer("CAST_FIRE_RAGE_10", 1000L, npc, (Player)null);
        }
        return super.onAttack(npc, attacker, damage, isSummon, skill);
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        final Instance world = npc.getInstanceWorld();
        if (world != null) {
            world.finishInstance();
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public static AbstractInstance provider() {
        return new ResidenceOfKingIgnis();
    }
    
    static {
        ResidenceOfKingIgnis.FIRE_RAG_1 = new SkillHolder(50050, 1);
        ResidenceOfKingIgnis.FIRE_RAG_2 = new SkillHolder(50050, 2);
        ResidenceOfKingIgnis.FIRE_RAG_3 = new SkillHolder(50050, 3);
        ResidenceOfKingIgnis.FIRE_RAG_4 = new SkillHolder(50050, 4);
        ResidenceOfKingIgnis.FIRE_RAG_5 = new SkillHolder(50050, 5);
        ResidenceOfKingIgnis.FIRE_RAG_6 = new SkillHolder(50050, 6);
        ResidenceOfKingIgnis.FIRE_RAG_7 = new SkillHolder(50050, 7);
        ResidenceOfKingIgnis.FIRE_RAG_8 = new SkillHolder(50050, 8);
        ResidenceOfKingIgnis.FIRE_RAG_9 = new SkillHolder(50050, 9);
        ResidenceOfKingIgnis.FIRE_RAG_10 = new SkillHolder(50050, 10);
        ResidenceOfKingIgnis.FREYA_SAFETY_ZONE = new SkillHolder(50052, 1);
        _playerFireRage = new ConcurrentHashMap<Player, Integer>();
    }
}
