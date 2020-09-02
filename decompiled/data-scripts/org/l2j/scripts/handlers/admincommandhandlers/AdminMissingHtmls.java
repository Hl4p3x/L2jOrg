// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.FlyTerrainObject;
import java.util.Collections;
import org.l2j.gameserver.model.actor.instance.Guard;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.instance.Warehouse;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.instance.Fisherman;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Observation;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import java.util.ArrayList;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminMissingHtmls implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        final String lowerCase = actualCommand.toLowerCase();
        switch (lowerCase) {
            case "admin_geomap_missing_htmls": {
                final int x = (activeChar.getX() + 294912 >> 15) + 11;
                final int y = (activeChar.getY() + 262144 >> 15) + 10;
                final int topLeftX = (x - 20) * 32768;
                final int topLeftY = (y - 18) * 32768;
                final int bottomRightX = (x - 20) * 32768 + 32768 - 1;
                final int bottomRightY = (y - 18) * 32768 + 32768 - 1;
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(IIIIII)Ljava/lang/String;, x, y, topLeftX, topLeftY, bottomRightX, bottomRightY));
                final List<Integer> results = new ArrayList<Integer>();
                for (final WorldObject obj : World.getInstance().getVisibleObjects()) {
                    if (GameUtils.isNpc(obj) && !GameUtils.isMonster(obj) && !GameUtils.isArtifact(obj) && !(obj instanceof Observation) && !results.contains(obj.getId())) {
                        final Npc npc = (Npc)obj;
                        if (npc.getLocation().getX() <= topLeftX || npc.getLocation().getX() >= bottomRightX || npc.getLocation().getY() <= topLeftY || npc.getLocation().getY() >= bottomRightY || !npc.isTalkable() || npc.hasListener(EventType.ON_NPC_FIRST_TALK) || (!npc.getHtmlPath(npc.getId(), 0).equals("data/html/npcdefault.htm") && (!(obj instanceof Fisherman) || !Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId())))) && (!(obj instanceof Warehouse) || !Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId())))) && (obj instanceof Merchant || obj instanceof Fisherman || !Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId())))) && (!(obj instanceof Guard) || !Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId())))))) {
                            continue;
                        }
                        results.add(npc.getId());
                    }
                }
                Collections.sort(results);
                for (final int id : results) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                }
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, results.size()));
                break;
            }
            case "admin_world_missing_htmls": {
                BuilderUtil.sendSysMessage(activeChar, "Missing htmls for the whole world.");
                final List<Integer> results2 = new ArrayList<Integer>();
                for (final WorldObject obj2 : World.getInstance().getVisibleObjects()) {
                    if (GameUtils.isNpc(obj2) && !GameUtils.isMonster(obj2) && !GameUtils.isArtifact(obj2) && !(obj2 instanceof Observation) && !results2.contains(obj2.getId())) {
                        final Npc npc2 = (Npc)obj2;
                        if (!npc2.isTalkable() || npc2.hasListener(EventType.ON_NPC_FIRST_TALK) || (!npc2.getHtmlPath(npc2.getId(), 0).equals("data/html/npcdefault.htm") && (!(obj2 instanceof Fisherman) || !Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc2.getId())))) && (!(obj2 instanceof Warehouse) || !Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc2.getId())))) && (obj2 instanceof Merchant || obj2 instanceof Fisherman || !Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc2.getId())))) && (!(obj2 instanceof Guard) || !Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc2.getId())))))) {
                            continue;
                        }
                        results2.add(npc2.getId());
                    }
                }
                Collections.sort(results2);
                for (final int id2 : results2) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id2));
                }
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, results2.size()));
                break;
            }
            case "admin_next_missing_html": {
                for (final WorldObject obj3 : World.getInstance().getVisibleObjects()) {
                    if (GameUtils.isNpc(obj3) && !GameUtils.isMonster(obj3) && !GameUtils.isArtifact(obj3) && !(obj3 instanceof Observation) && !(obj3 instanceof FlyTerrainObject)) {
                        final Npc npc3 = (Npc)obj3;
                        if (npc3.isTalkable() && !npc3.hasListener(EventType.ON_NPC_FIRST_TALK) && (npc3.getHtmlPath(npc3.getId(), 0).equals("data/html/npcdefault.htm") || (obj3 instanceof Fisherman && Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc3.getId())))) || (obj3 instanceof Warehouse && Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc3.getId())))) || (obj3 instanceof Merchant && !(obj3 instanceof Fisherman) && Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc3.getId())))) || (obj3 instanceof Guard && Util.isNullOrEmpty((CharSequence)HtmCache.getInstance().getHtm((Player)null, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc3.getId())))))) {
                            activeChar.teleToLocation((ILocational)npc3);
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc3.getId()));
                            break;
                        }
                        continue;
                    }
                }
                break;
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminMissingHtmls.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_geomap_missing_htmls", "admin_world_missing_htmls" };
    }
}
