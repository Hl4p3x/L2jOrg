// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.util.GameUtils;
import java.util.StringTokenizer;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.HashMap;
import io.github.joealisson.primitive.IntMap;
import java.util.Map;
import org.slf4j.Logger;

public final class CommunityBoardHandler implements IHandler<IParseBoardHandler, String>
{
    private static final Logger LOGGER;
    private final Map<String, IParseBoardHandler> handlers;
    private final IntMap<String> bypasses;
    
    private CommunityBoardHandler() {
        this.handlers = new HashMap<String, IParseBoardHandler>();
        this.bypasses = (IntMap<String>)new CHashIntMap();
    }
    
    @Override
    public void registerHandler(final IParseBoardHandler handler) {
        for (final String cmd : handler.getCommunityBoardCommands()) {
            this.handlers.put(cmd.toLowerCase(), handler);
        }
    }
    
    @Override
    public synchronized void removeHandler(final IParseBoardHandler handler) {
        for (final String cmd : handler.getCommunityBoardCommands()) {
            this.handlers.remove(cmd.toLowerCase());
        }
    }
    
    @Override
    public IParseBoardHandler getHandler(final String cmd) {
        return this.handlers.get(cmd.toLowerCase());
    }
    
    @Override
    public int size() {
        return this.handlers.size();
    }
    
    public boolean isCommunityBoardCommand(final String cmd) {
        final int whitespaceIndex = cmd.indexOf(" ");
        if (whitespaceIndex < 0) {
            return Objects.nonNull(this.getHandler(cmd));
        }
        return Objects.nonNull(this.getHandler(cmd.substring(0, whitespaceIndex)));
    }
    
    public void handleParseCommand(final String command, final Player player) {
        if (Objects.isNull(player) || Util.isNullOrEmpty((CharSequence)command)) {
            return;
        }
        if (!Config.ENABLE_COMMUNITY_BOARD) {
            player.sendPacket(SystemMessageId.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
            return;
        }
        final StringTokenizer tokens = new StringTokenizer(command);
        final IParseBoardHandler cb = this.getHandler(tokens.nextToken());
        if (Objects.isNull(cb)) {
            CommunityBoardHandler.LOGGER.warn("Couldn't find parse handler for command {}!", (Object)command);
            return;
        }
        cb.parseCommunityBoardCommand(command, tokens, player);
        this.addBypass(player, cb.name(), command);
    }
    
    public void handleWriteCommand(final Player player, final String url, final String arg1, final String arg2, final String arg3, final String arg4, final String arg5) {
        if (player == null) {
            return;
        }
        if (!Config.ENABLE_COMMUNITY_BOARD) {
            player.sendPacket(SystemMessageId.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
            return;
        }
        final IParseBoardHandler cb = this.getHandler(url);
        if (Objects.isNull(cb)) {
            CommunityBoardHandler.LOGGER.warn("Couldn't find write handler for command {}!", (Object)url);
            return;
        }
        final IParseBoardHandler parseBoardHandler = cb;
        final IWriteBoardHandler writable;
        if (!(parseBoardHandler instanceof IWriteBoardHandler) || (writable = (IWriteBoardHandler)parseBoardHandler) != parseBoardHandler) {
            CommunityBoardHandler.LOGGER.warn("{} doesn't implement write!", (Object)cb.getClass().getSimpleName());
        }
        else {
            writable.writeCommunityBoardCommand(player, arg1, arg2, arg3, arg4, arg5);
        }
    }
    
    public void addBypass(final Player player, final String title, final String bypass) {
        this.bypasses.put(player.getObjectId(), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, title, bypass));
    }
    
    public String removeBypass(final Player player) {
        return (String)this.bypasses.remove(player.getObjectId());
    }
    
    public static void separateAndSend(final String html, final Player player) {
        GameUtils.sendCBHtml(player, html);
    }
    
    public static CommunityBoardHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CommunityBoardHandler.class);
    }
    
    private static class Singleton
    {
        private static final CommunityBoardHandler INSTANCE;
        
        static {
            INSTANCE = new CommunityBoardHandler();
        }
    }
}
