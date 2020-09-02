// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.GameUtils;
import java.util.StringTokenizer;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminVitality implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (activeChar == null) {
            return false;
        }
        if (!Config.ENABLE_VITALITY) {
            BuilderUtil.sendSysMessage(activeChar, "Vitality is not enabled on the server!");
            return false;
        }
        int vitality = 0;
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String cmd = st.nextToken();
        if (GameUtils.isPlayer(activeChar.getTarget())) {
            final Player target = (Player)activeChar.getTarget();
            if (cmd.equals("admin_set_vitality")) {
                try {
                    vitality = Integer.parseInt(st.nextToken());
                }
                catch (Exception e) {
                    BuilderUtil.sendSysMessage(activeChar, "Incorrect vitality");
                }
                target.setVitalityPoints(vitality, true);
                target.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, vitality));
            }
            else if (cmd.equals("admin_full_vitality")) {
                target.setVitalityPoints(140000, true);
                target.sendMessage("Admin completly recharged your Vitality");
            }
            else if (cmd.equals("admin_empty_vitality")) {
                target.setVitalityPoints(0, true);
                target.sendMessage("Admin completly emptied your Vitality");
            }
            else if (cmd.equals("admin_get_vitality")) {
                vitality = target.getVitalityPoints();
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, vitality));
            }
            return true;
        }
        BuilderUtil.sendSysMessage(activeChar, "Target not found or not a player");
        return false;
    }
    
    public String[] getAdminCommandList() {
        return AdminVitality.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_set_vitality", "admin_full_vitality", "admin_empty_vitality", "admin_get_vitality" };
    }
}
