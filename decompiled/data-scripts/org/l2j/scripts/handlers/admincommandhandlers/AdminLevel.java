// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.commons.util.Util;
import java.util.StringTokenizer;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminLevel implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player gm) {
        final WorldObject target = gm.getTarget();
        if (!GameUtils.isPlayable(target)) {
            gm.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        int level = Util.parseNextInt(st, 0);
        if (level <= 0) {
            BuilderUtil.sendSysMessage(gm, "You must specify level greater than 0.");
            return false;
        }
        final Playable playableTarget = (Playable)target;
        final int maxAddLevel = LevelData.getInstance().getMaxLevel() - playableTarget.getLevel();
        if (actualCommand.equalsIgnoreCase("admin_set_level")) {
            level -= playableTarget.getLevel();
        }
        return playableTarget.getStats().addLevel((byte)Math.min(maxAddLevel, level));
    }
    
    public String[] getAdminCommandList() {
        return AdminLevel.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_add_level", "admin_set_level" };
    }
}
