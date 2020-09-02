// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.engine.olympiad.OlympiadEngine;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.gameserver.model.olympiad.AbstractOlympiadGame;
import org.l2j.gameserver.model.olympiad.OlympiadGameNonClassed;
import org.l2j.gameserver.model.olympiad.Participant;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminOlympiad implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command);
        final String nextToken;
        final String cmd = nextToken = st.nextToken();
        switch (nextToken) {
            case "admin_olympiad_game": {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(activeChar, "Syntax: //olympiad_game <player name>");
                    return false;
                }
                final Player player = World.getInstance().findPlayer(st.nextToken());
                if (player == null) {
                    activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
                    return false;
                }
                if (player == activeChar) {
                    activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
                    return false;
                }
                if (!this.checkplayer(player, activeChar) || !this.checkplayer(activeChar, activeChar)) {
                    return false;
                }
                for (int i = 0; i < OlympiadGameManager.getInstance().getNumberOfStadiums(); ++i) {
                    final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(i);
                    if (task != null) {
                        synchronized (task) {
                            if (!task.isRunning()) {
                                final Participant[] players = { new Participant(activeChar, 1), new Participant(player, 2) };
                                task.attachGame((AbstractOlympiadGame)new OlympiadGameNonClassed(i, players));
                                return true;
                            }
                        }
                    }
                }
                break;
            }
        }
        return false;
    }
    
    private int parseInt(final StringTokenizer st, final int defaultVal) {
        final String token = st.nextToken();
        if (!Util.isDigit(token)) {
            return -1;
        }
        return Integer.decode(token);
    }
    
    private boolean checkplayer(final Player player, final Player activeChar) {
        if (player.isSubClassActive()) {
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return false;
        }
        if (player.getClassId().level() < 3) {
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return false;
        }
        if (OlympiadEngine.getInstance().getOlympiadPoints(player) <= 0) {
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return false;
        }
        if (OlympiadManager.getInstance().isRegistered(player)) {
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return false;
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminOlympiad.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_olympiad_game" };
    }
}
