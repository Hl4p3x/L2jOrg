// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.database.data.CommunityFavorite;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.CommunityDAO;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IParseBoardHandler;

public class FavoriteBoard implements IParseBoardHandler
{
    private static final Logger LOGGER;
    private static final String[] COMMANDS;
    
    public String[] getCommunityBoardCommands() {
        return FavoriteBoard.COMMANDS;
    }
    
    public boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player player) {
        if (tokens.hasMoreTokens()) {
            final String nextToken = tokens.nextToken();
            switch (nextToken) {
                case "add": {
                    this.addFavorite(player);
                    break;
                }
                case "del": {
                    this.deleteFavorite(player, Util.parseNextInt(tokens, -1));
                    break;
                }
            }
        }
        else {
            this.showFavorites(player);
        }
        return true;
    }
    
    protected void showFavorites(final Player player) {
        final String favoriteTemplate = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/favorite_list.html");
        final StringBuilder favoriteBuilder = new StringBuilder();
        ((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).findFavorites(player.getObjectId()).forEach(favorite -> this.addFavoriteLink(favoriteTemplate, favoriteBuilder, favorite));
        final String html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/favorite.html");
        CommunityBoardHandler.separateAndSend(html.replace("%fav_list%", favoriteBuilder.toString()), player);
    }
    
    protected void deleteFavorite(final Player player, final int id) {
        if (id >= 1) {
            ((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).deleteFavorite(player.getObjectId(), id);
        }
        this.showFavorites(player);
    }
    
    protected void addFavorite(final Player player) {
        final String[] parts;
        CommunityFavorite favorite;
        Util.doIfNonNull((Object)CommunityBoardHandler.getInstance().removeBypass(player), bypass -> {
            parts = bypass.split("&", 2);
            if (parts.length != 2) {
                FavoriteBoard.LOGGER.warn("Couldn't add favorite link, {} it's not a valid bypass!", (Object)bypass);
            }
            else {
                favorite = new CommunityFavorite();
                favorite.setPlayerId(player.getObjectId());
                favorite.setTitle(parts[0].trim());
                favorite.setBypass(parts[1].trim());
                ((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).save((Object)favorite);
            }
        });
    }
    
    protected void addFavoriteLink(final String favoriteTemplate, final StringBuilder favoriteBuilder, final CommunityFavorite favorite) {
        favoriteBuilder.append(favoriteTemplate.replace("%fav_bypass%", favorite.getBypass()).replace("%fav_title%", favorite.getTitle()).replace("%fav_add_date%", Util.formatDateTime(favorite.getDate())).replace("%fav_id%", String.valueOf(favorite.getId())));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)FavoriteBoard.class);
        COMMANDS = new String[] { "_bbsgetfav" };
    }
}
