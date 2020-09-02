// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances.ResidenceOfKingPetram;

import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.RaidBoss;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.instances.AbstractInstance;

public class ResidenceOfKingPetram extends AbstractInstance
{
    private static final int TRITAN = 34049;
    private static final int PETRAM = 29108;
    private static final int PETRAM_PIECE = 29116;
    private static final int PETRAM_FRAGMENT = 29117;
    private static SkillHolder EARTh_ENERGY;
    private static SkillHolder EARTh_FURY;
    private static SkillHolder TEST;
    private static final int TEMPLATE_ID = 198;
    private RaidBoss _petram;
    private Monster _minion_1;
    private Monster _minion_2;
    private Monster _minion_3;
    private Monster _minion_4;
    private boolean _spawned_minions;
    
    public ResidenceOfKingPetram() {
        super(new int[] { 198 });
        this._petram = null;
        this._minion_1 = null;
        this._minion_2 = null;
        this._minion_3 = null;
        this._minion_4 = null;
        this.addStartNpc(34049);
        this.addKillId(new int[] { 29108, 29116, 29117 });
        this.addAttackId(29108);
        this.addInstanceLeaveId(new int[] { 198 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "ENTER": {
                this.enterInstance(player, npc, 198);
                if (player.getInstanceWorld() != null) {
                    this._petram = (RaidBoss)player.getInstanceWorld().getNpc(29108);
                    break;
                }
                break;
            }
            case "SPAWN_MINION": {
                this._petram.useMagic(ResidenceOfKingPetram.EARTh_ENERGY.getSkill());
                if (!this._spawned_minions) {
                    this._minion_1 = (Monster)addSpawn(npc, 29116, 221543, 191530, -15486, 1131, false, -1L, true, npc.getInstanceId());
                    this._minion_2 = (Monster)addSpawn(npc, 29117, 222069, 192019, -15486, 49364, false, -1L, true, npc.getInstanceId());
                    this._minion_3 = (Monster)addSpawn(npc, 29116, 222595, 191479, -15486, 34013, false, -1L, true, npc.getInstanceId());
                    this._minion_4 = (Monster)addSpawn(npc, 29117, 222077, 191017, -15486, 16383, false, -1L, true, npc.getInstanceId());
                    this._spawned_minions = true;
                }
                this.startQuestTimer("SUPPORT_PETRAM", 3000L, npc, (Player)null);
                break;
            }
            case "SUPPORT_PETRAM": {
                this._minion_1.setTarget((WorldObject)this._petram);
                this._minion_1.useMagic(ResidenceOfKingPetram.TEST.getSkill());
                this._minion_2.setTarget((WorldObject)this._petram);
                this._minion_2.useMagic(ResidenceOfKingPetram.TEST.getSkill());
                this._minion_3.setTarget((WorldObject)this._petram);
                this._minion_3.useMagic(ResidenceOfKingPetram.TEST.getSkill());
                this._minion_4.setTarget((WorldObject)this._petram);
                this._minion_4.useMagic(ResidenceOfKingPetram.TEST.getSkill());
                this.startQuestTimer("SUPPORT_PETRAM", 10100L, npc, (Player)null);
                break;
            }
            case "INVUL_MODE": {
                this._petram.useMagic(ResidenceOfKingPetram.EARTh_FURY.getSkill());
                if (this._petram.isInvul()) {
                    this._petram.setIsInvul(false);
                    this._petram.broadcastSay(ChatType.NPC_SHOUT, "Nooooo... Nooooo...");
                    break;
                }
                this._petram.setIsInvul(true);
                this._petram.broadcastSay(ChatType.NPC_SHOUT, "HaHa, fighters lets kill them. Now Im invul!!!");
                break;
            }
        }
        return null;
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon, final Skill skill) {
        if (npc.getId() == 29108) {
            if (npc.getCurrentHp() < npc.getMaxHp() * 0.7 && npc.getCurrentHp() > npc.getMaxHp() * 0.68) {
                this.startQuestTimer("INVUL_MODE", 1000L, npc, (Player)null);
                this.startQuestTimer("SPAWN_MINION", 1000L, npc, (Player)null);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.4 && npc.getCurrentHp() > npc.getMaxHp() * 0.38) {
                this.startQuestTimer("INVUL_MODE", 1000L, npc, (Player)null);
                this.startQuestTimer("SPAWN_MINION", 1000L, npc, (Player)null);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.2 && npc.getCurrentHp() > npc.getMaxHp() * 0.18) {
                this.startQuestTimer("INVUL_MODE", 1000L, npc, (Player)null);
                this.startQuestTimer("SPAWN_MINION", 1000L, npc, (Player)null);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.1 && npc.getCurrentHp() > npc.getMaxHp() * 0.08) {
                this.startQuestTimer("INVUL_MODE", 1000L, npc, (Player)null);
                this.startQuestTimer("SPAWN_MINION", 1000L, npc, (Player)null);
            }
        }
        return super.onAttack(npc, attacker, damage, isSummon, skill);
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        if (npc.getId() == 29108) {
            final Instance world = npc.getInstanceWorld();
            if (world != null) {
                world.finishInstance();
            }
        }
        else if (this._minion_1.isDead() && this._minion_2.isDead() && this._minion_3.isDead() && this._minion_4.isDead()) {
            this.startQuestTimer("INVUL_MODE", 3000L, (Npc)this._petram, (Player)null);
            this._spawned_minions = false;
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public static ResidenceOfKingPetram provider() {
        return new ResidenceOfKingPetram();
    }
    
    static {
        ResidenceOfKingPetram.EARTh_ENERGY = new SkillHolder(50066, 1);
        ResidenceOfKingPetram.EARTh_FURY = new SkillHolder(50059, 1);
        ResidenceOfKingPetram.TEST = new SkillHolder(5712, 1);
    }
}
