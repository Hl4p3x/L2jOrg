// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminSuperHaste implements IAdminCommandHandler
{
    static final String[] ADMIN_COMMANDS;
    private static final int SUPER_HASTE_ID = 7029;
    
    public boolean useAdminCommand(final String command, final Player player) {
        final StringTokenizer st = new StringTokenizer(command);
        final String nextToken;
        final String cmd = nextToken = st.nextToken();
        switch (nextToken) {
            case "admin_superhaste":
            case "admin_speed": {
                try {
                    final int val = Integer.parseInt(st.nextToken());
                    final boolean sendMessage = player.isAffectedBySkill(7029);
                    player.stopSkillEffects(val == 0 && sendMessage, 7029);
                    if (val >= 1 && val <= 4) {
                        int time = 0;
                        if (st.hasMoreTokens()) {
                            time = Integer.parseInt(st.nextToken());
                        }
                        final Skill superHasteSkill = SkillEngine.getInstance().getSkill(7029, val);
                        superHasteSkill.applyEffects((Creature)player, (Creature)player, true, time);
                    }
                }
                catch (Exception e) {
                    player.sendMessage("Usage: //superhaste <Effect level (0-4)> <Time in seconds>");
                }
                break;
            }
            case "admin_superhaste_menu":
            case "admin_speed_menu": {
                AdminHtml.showAdminHtml(player, "gm_menu.htm");
                break;
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminSuperHaste.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_superhaste", "admin_superhaste_menu", "admin_speed", "admin_speed_menu" };
    }
}
