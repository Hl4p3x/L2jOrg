// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.l2j.gameserver.network.serverpackets.ExUserInfoAbnormalVisualEffect;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;

public final class BuilderUtil
{
    private BuilderUtil() {
    }
    
    public static void sendSysMessage(final Player player, final String message) {
        if (Config.GM_STARTUP_BUILDER_HIDE) {
            player.sendPacket(new CreatureSay(0, ChatType.GENERAL, "SYS", message));
        }
        else {
            player.sendMessage(message);
        }
    }
    
    public static void sendSystemMessage(final Player player, final String message, final Object... args) {
        sendSysMessage(player, String.format(message, args));
    }
    
    public static void sendHtmlMessage(final Player player, final String message) {
        player.sendPacket(new CreatureSay(0, ChatType.GENERAL, "HTML", message));
    }
    
    public static boolean setHiding(final Player player, final boolean hide) {
        if (player.isInvisible() && hide) {
            return false;
        }
        if (!player.isInvisible() && !hide) {
            return false;
        }
        player.setSilenceMode(hide);
        player.setIsInvul(hide);
        player.setInvisible(hide);
        player.broadcastUserInfo();
        player.sendPacket(new ExUserInfoAbnormalVisualEffect(player));
        return true;
    }
}
