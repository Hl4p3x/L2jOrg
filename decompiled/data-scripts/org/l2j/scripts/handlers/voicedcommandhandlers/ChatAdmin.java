// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.voicedcommandhandlers;

import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.commons.util.Util;
import java.util.StringTokenizer;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IVoicedCommandHandler;

public class ChatAdmin implements IVoicedCommandHandler
{
    private static final String[] VOICED_COMMANDS;
    
    public boolean useVoicedCommand(final String command, final Player activeChar, final String params) {
        if (!AdminData.getInstance().hasAccess(command, activeChar.getAccessLevel())) {
            return false;
        }
        switch (command) {
            case "banchat":
            case "chatban": {
                if (params == null) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: .banchat name [minutes]");
                    return true;
                }
                final StringTokenizer st = new StringTokenizer(params);
                if (!st.hasMoreTokens()) {
                    break;
                }
                final String name = st.nextToken();
                long expirationTime = 0L;
                if (st.hasMoreTokens()) {
                    final String token = st.nextToken();
                    if (Util.isDigit(token)) {
                        expirationTime = Integer.parseInt(token);
                    }
                }
                final int objId = PlayerNameTable.getInstance().getIdByName(name);
                if (objId <= 0) {
                    BuilderUtil.sendSysMessage(activeChar, "Player not found!");
                    return false;
                }
                final Player player = World.getInstance().findPlayer(objId);
                if (player == null || !player.isOnline()) {
                    BuilderUtil.sendSysMessage(activeChar, "Player not online!");
                    return false;
                }
                if (player.isChatBanned()) {
                    BuilderUtil.sendSysMessage(activeChar, "Player is already punished!");
                    return false;
                }
                if (player == activeChar) {
                    BuilderUtil.sendSysMessage(activeChar, "You can't ban yourself!");
                    return false;
                }
                if (player.isGM()) {
                    BuilderUtil.sendSysMessage(activeChar, "You can't ban a GM!");
                    return false;
                }
                if (AdminData.getInstance().hasAccess(command, player.getAccessLevel())) {
                    BuilderUtil.sendSysMessage(activeChar, "You can't ban moderator!");
                    return false;
                }
                PunishmentManager.getInstance().startPunishment(new PunishmentTask((Object)objId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, System.currentTimeMillis() + expirationTime * 1000L * 60L, "Chat banned by moderator", activeChar.getName()));
                if (expirationTime > 0L) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, player.getName(), expirationTime));
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                }
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                player.getEffectList().startAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.NO_CHAT });
                break;
            }
            case "unbanchat":
            case "chatunban": {
                if (params == null) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: .unbanchat name");
                    return true;
                }
                final StringTokenizer st = new StringTokenizer(params);
                if (!st.hasMoreTokens()) {
                    break;
                }
                final String name = st.nextToken();
                final int objId2 = PlayerNameTable.getInstance().getIdByName(name);
                if (objId2 <= 0) {
                    BuilderUtil.sendSysMessage(activeChar, "Player not found!");
                    return false;
                }
                final Player player2 = World.getInstance().findPlayer(objId2);
                if (player2 == null || !player2.isOnline()) {
                    BuilderUtil.sendSysMessage(activeChar, "Player not online!");
                    return false;
                }
                if (!player2.isChatBanned()) {
                    BuilderUtil.sendSysMessage(activeChar, "Player is not chat banned!");
                    return false;
                }
                PunishmentManager.getInstance().stopPunishment((Object)objId2, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player2.getName()));
                player2.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                player2.getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.NO_CHAT });
                break;
            }
        }
        return true;
    }
    
    public String[] getVoicedCommandList() {
        return ChatAdmin.VOICED_COMMANDS;
    }
    
    static {
        VOICED_COMMANDS = new String[] { "banchat", "chatban", "unbanchat", "chatunban" };
    }
}
