// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.CrumaTower;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class SummonPc extends AbstractNpcAI
{
    private static final int PORTA = 20213;
    private static final int PERUM = 20221;
    private static final SkillHolder SUMMON_PC;
    
    private SummonPc() {
        this.addAttackId(new int[] { 20213, 20221 });
        this.addSpellFinishedId(new int[] { 20213, 20221 });
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        final int chance = getRandom(100);
        final boolean attacked = npc.getVariables().getBoolean("attacked", false);
        if (!MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)attacker, 300) && !attacked) {
            if (chance < 50) {
                if (SummonPc.SUMMON_PC.getSkill().getMpConsume() < npc.getCurrentMp() && SummonPc.SUMMON_PC.getSkill().getHpConsume() < npc.getCurrentHp() && !npc.isSkillDisabled(SummonPc.SUMMON_PC.getSkill())) {
                    npc.setTarget((WorldObject)attacker);
                    npc.doCast(SummonPc.SUMMON_PC.getSkill());
                }
                if (SummonPc.SUMMON_PC.getSkill().getMpConsume() < npc.getCurrentMp() && SummonPc.SUMMON_PC.getSkill().getHpConsume() < npc.getCurrentHp() && !npc.isSkillDisabled(SummonPc.SUMMON_PC.getSkill())) {
                    npc.setTarget((WorldObject)attacker);
                    npc.doCast(SummonPc.SUMMON_PC.getSkill());
                    npc.getVariables().set("attacked", true);
                }
            }
        }
        else if (!MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)attacker, 100) && !attacked) {
            final Attackable monster = (Attackable)npc;
            if (monster.getMostHated() != null && ((monster.getMostHated() == attacker && chance < 50) || chance < 10) && SummonPc.SUMMON_PC.getSkill().getMpConsume() < npc.getCurrentMp() && SummonPc.SUMMON_PC.getSkill().getHpConsume() < npc.getCurrentHp() && !npc.isSkillDisabled(SummonPc.SUMMON_PC.getSkill())) {
                npc.setTarget((WorldObject)attacker);
                npc.doCast(SummonPc.SUMMON_PC.getSkill());
                npc.getVariables().set("attacked", true);
            }
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    public String onSpellFinished(final Npc npc, final Player player, final Skill skill) {
        if (skill.getId() == SummonPc.SUMMON_PC.getSkillId() && !npc.isDead() && npc.getVariables().getBoolean("attacked", false)) {
            player.teleToLocation((ILocational)npc);
            npc.getVariables().set("attacked", false);
        }
        return super.onSpellFinished(npc, player, skill);
    }
    
    public static AbstractNpcAI provider() {
        return new SummonPc();
    }
    
    static {
        SUMMON_PC = new SkillHolder(4161, 1);
    }
}
