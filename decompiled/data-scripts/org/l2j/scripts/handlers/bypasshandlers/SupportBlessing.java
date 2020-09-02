// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class SupportBlessing implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        final Npc npc = (Npc)target;
        if (player.getLevel() > 70) {
            npc.showChatWindow(player, "data/html/default/SupportBlessingHighLevel.htm");
            return true;
        }
        npc.setTarget((WorldObject)player);
        SkillCaster.triggerCast((Creature)npc, (Creature)player, CommonSkill.BLESSING_OF_PROTECTION.getSkill());
        return false;
    }
    
    public String[] getBypassList() {
        return SupportBlessing.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "GiveBlessing" };
    }
}
