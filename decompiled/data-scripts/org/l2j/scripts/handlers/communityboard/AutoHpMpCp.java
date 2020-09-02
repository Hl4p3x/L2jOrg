// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCpChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerHpChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMpChange;
import java.util.ArrayList;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IParseBoardHandler;

public class AutoHpMpCp implements IParseBoardHandler
{
    private static final Logger LOGGER;
    private static final String[] CMD;
    private static Map<Integer, List<String>> _listenedPlayer;
    
    private AutoHpMpCp() {
        final ListenersContainer listeners = Listeners.players();
        listeners.addListener((AbstractEventListener)new ConsumerEventListener(listeners, EventType.ON_PLAYER_CP_CHANGE, e -> this.onPlayerCpChange(e.getActiveChar()), (Object)this));
        listeners.addListener((AbstractEventListener)new ConsumerEventListener(listeners, EventType.ON_PLAYER_HP_CHANGE, e -> this.onPlayerHpChange(e.getActiveChar()), (Object)this));
        listeners.addListener((AbstractEventListener)new ConsumerEventListener(listeners, EventType.ON_PLAYER_MP_CHANGE, e -> this.onPlayerMpChange(e.getActiveChar()), (Object)this));
    }
    
    public boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player player) {
        final String subCommand = tokens.nextToken();
        if (subCommand.equalsIgnoreCase("autocp")) {
            processCommand(player, "autocp", tokens);
        }
        else if (subCommand.equalsIgnoreCase("autohp")) {
            processCommand(player, "autohp", tokens);
        }
        else if (subCommand.equalsIgnoreCase("automp")) {
            processCommand(player, "automp", tokens);
        }
        final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
        HtmCache.getInstance().getHtm(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, customPath));
        return true;
    }
    
    private static int getPercentByCommand(final Player activeChar, final String command) {
        switch (command) {
            case "autocp": {
                return activeChar.getAutoCp();
            }
            case "autohp": {
                return activeChar.getAutoHp();
            }
            case "automp": {
                return activeChar.getAutoMp();
            }
            default: {
                return 0;
            }
        }
    }
    
    private static void setPercentByCommand(final Player activeChar, final String command, final int percent) {
        switch (command) {
            case "autocp": {
                activeChar.setAutoCp(percent);
                break;
            }
            case "autohp": {
                activeChar.setAutoHp(percent);
                break;
            }
            case "automp": {
                activeChar.setAutoMp(percent);
                break;
            }
        }
    }
    
    private static String processCommand(final Player activeChar, final String command, final StringTokenizer params) {
        if (getPercentByCommand(activeChar, command) > 0) {
            final List<String> listenedKeys = AutoHpMpCp._listenedPlayer.get(activeChar.getObjectId());
            if (listenedKeys == null) {
                AutoHpMpCp.LOGGER.error("Try to stop non existent command {} for {}", (Object)activeChar, (Object)command);
                return "";
            }
            AutoHpMpCp._listenedPlayer.get(activeChar.getObjectId()).remove(command);
            setPercentByCommand(activeChar, command, 0);
            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, command));
        }
        else {
            int percent;
            try {
                percent = Math.min(99, Integer.parseInt(params.nextToken()));
            }
            catch (NumberFormatException e) {
                activeChar.sendMessage("Incorrect number");
                return "";
            }
            if (percent <= 0) {
                activeChar.sendMessage("You can not specify zero or negative value!");
                return "";
            }
            final List<String> listenedKeys2 = AutoHpMpCp._listenedPlayer.get(activeChar.getObjectId());
            if (listenedKeys2 == null) {
                AutoHpMpCp._listenedPlayer.put(activeChar.getObjectId(), new ArrayList<String>());
            }
            AutoHpMpCp._listenedPlayer.get(activeChar.getObjectId()).add(command);
            setPercentByCommand(activeChar, command, percent);
            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, command, percent));
        }
        return "";
    }
    
    public void onPlayerCpChange(final Player player) {
        if (player.isDead()) {
            return;
        }
        final List<String> listenedKeys = AutoHpMpCp._listenedPlayer.get(player.getObjectId());
        if (listenedKeys == null || !listenedKeys.contains("autocp")) {
            return;
        }
        final int percent = player.getAutoCp();
        final int currentPercent = (int)(player.getCurrentCp() / (player.getMaxCp() / 100.0));
        if (percent <= 0 || currentPercent <= 0 || currentPercent > percent) {
            return;
        }
        AutoHpMpCp.LOGGER.info("onPlayerCpChange for {}", (Object)player);
    }
    
    public void onPlayerHpChange(final Player player) {
        if (player.isDead()) {
            return;
        }
        final List<String> listenedKeys = AutoHpMpCp._listenedPlayer.get(player.getObjectId());
        if (listenedKeys == null || !listenedKeys.contains("autohp")) {
            return;
        }
        final int percent = player.getAutoHp();
        final int currentPercent = (int)(player.getCurrentHp() / (player.getMaxHp() / 100.0));
        if (percent <= 0 || currentPercent <= 0 || currentPercent > percent) {
            return;
        }
        AutoHpMpCp.LOGGER.info("onPlayerHpChange for {}", (Object)player);
    }
    
    public void onPlayerMpChange(final Player player) {
        if (player.isDead()) {
            return;
        }
        final List<String> listenedKeys = AutoHpMpCp._listenedPlayer.get(player.getObjectId());
        if (listenedKeys == null || !listenedKeys.contains("automp")) {
            return;
        }
        final int percent = player.getAutoMp();
        final int currentPercent = (int)(player.getCurrentMp() / (player.getMaxMp() / 100.0));
        if (percent <= 0 || currentPercent <= 0 || currentPercent > percent) {
            return;
        }
        AutoHpMpCp.LOGGER.info("onPlayerMpChange for {}", (Object)player);
    }
    
    public static IParseBoardHandler provider() {
        return (IParseBoardHandler)new AutoHpMpCp();
    }
    
    public String[] getCommunityBoardCommands() {
        return AutoHpMpCp.CMD;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)HomeBoard.class);
        CMD = new String[] { "_bbsautohpmpcp" };
        AutoHpMpCp._listenedPlayer = new ConcurrentHashMap<Integer, List<String>>();
    }
}
