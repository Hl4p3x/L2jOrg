// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import java.util.Iterator;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.handler.IBypassHandler;

public class SupportMagic implements IBypassHandler
{
    private static final String[] COMMANDS;
    private static final SkillHolder HASTE_1;
    private static final SkillHolder HASTE_2;
    private static final SkillHolder CUBIC;
    private static final SkillHolder[] FIGHTER_BUFFS;
    private static final SkillHolder[] MAGE_BUFFS;
    private static final SkillHolder[] SUMMON_BUFFS;
    private static final int LOWEST_LEVEL = 6;
    private static final int CUBIC_LOWEST = 16;
    private static final int CUBIC_HIGHEST = 34;
    private static final int HASTE_LEVEL_2;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        if (command.equalsIgnoreCase(SupportMagic.COMMANDS[0])) {
            makeSupportMagic(player, (Npc)target, true);
        }
        else if (command.equalsIgnoreCase(SupportMagic.COMMANDS[1])) {
            makeSupportMagic(player, (Npc)target, false);
        }
        return true;
    }
    
    private static void makeSupportMagic(final Player player, final Npc npc, final boolean isSummon) {
        final int level = player.getLevel();
        if (isSummon && !player.hasServitors()) {
            npc.showChatWindow(player, "data/html/default/SupportMagicNoSummon.htm");
            return;
        }
        if (level < 6) {
            npc.showChatWindow(player, "data/html/default/SupportMagicLowLevel.htm");
            return;
        }
        if (level > Config.MAX_NEWBIE_BUFF_LEVEL) {
            npc.showChatWindow(player, "data/html/default/SupportMagicHighLevel.htm");
            return;
        }
        if (player.getClassId().level() == 3) {
            player.sendMessage("Only adventurers who have not completed their 3rd class transfer may receive these buffs.");
            return;
        }
        if (isSummon) {
            for (final Summon s : player.getServitors().values()) {
                npc.setTarget((WorldObject)s);
                for (final SkillHolder skill : SupportMagic.SUMMON_BUFFS) {
                    SkillCaster.triggerCast((Creature)npc, (Creature)s, skill.getSkill());
                }
                if (level >= SupportMagic.HASTE_LEVEL_2) {
                    SkillCaster.triggerCast((Creature)npc, (Creature)s, SupportMagic.HASTE_2.getSkill());
                }
                else {
                    SkillCaster.triggerCast((Creature)npc, (Creature)s, SupportMagic.HASTE_1.getSkill());
                }
            }
        }
        else {
            npc.setTarget((WorldObject)player);
            if (player.isInCategory(CategoryType.BEGINNER_MAGE)) {
                for (final SkillHolder skill2 : SupportMagic.MAGE_BUFFS) {
                    SkillCaster.triggerCast((Creature)npc, (Creature)player, skill2.getSkill());
                }
            }
            else {
                for (final SkillHolder skill2 : SupportMagic.FIGHTER_BUFFS) {
                    SkillCaster.triggerCast((Creature)npc, (Creature)player, skill2.getSkill());
                }
                if (level >= SupportMagic.HASTE_LEVEL_2) {
                    SkillCaster.triggerCast((Creature)npc, (Creature)player, SupportMagic.HASTE_2.getSkill());
                }
                else {
                    SkillCaster.triggerCast((Creature)npc, (Creature)player, SupportMagic.HASTE_1.getSkill());
                }
            }
            if (level >= 16 && level <= 34) {
                SkillCaster.triggerCast((Creature)npc, (Creature)player, SupportMagic.CUBIC.getSkill());
            }
        }
    }
    
    public String[] getBypassList() {
        return SupportMagic.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "supportmagicservitor", "supportmagic" };
        HASTE_1 = new SkillHolder(4327, 1);
        HASTE_2 = new SkillHolder(5632, 1);
        CUBIC = new SkillHolder(4338, 1);
        FIGHTER_BUFFS = new SkillHolder[] { new SkillHolder(4322, 1), new SkillHolder(4323, 1), new SkillHolder(5637, 1), new SkillHolder(4324, 1), new SkillHolder(4325, 1), new SkillHolder(4326, 1) };
        MAGE_BUFFS = new SkillHolder[] { new SkillHolder(4322, 1), new SkillHolder(4323, 1), new SkillHolder(5637, 1), new SkillHolder(4328, 1), new SkillHolder(4329, 1), new SkillHolder(4330, 1), new SkillHolder(4331, 1) };
        SUMMON_BUFFS = new SkillHolder[] { new SkillHolder(4322, 1), new SkillHolder(4323, 1), new SkillHolder(5637, 1), new SkillHolder(4324, 1), new SkillHolder(4325, 1), new SkillHolder(4326, 1), new SkillHolder(4328, 1), new SkillHolder(4329, 1), new SkillHolder(4330, 1), new SkillHolder(4331, 1) };
        HASTE_LEVEL_2 = Config.MAX_NEWBIE_BUFF_LEVEL + 1;
    }
}
