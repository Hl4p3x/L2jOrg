// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Objects;
import org.l2j.gameserver.data.xml.impl.TeleportersData;
import org.l2j.gameserver.enums.TeleportType;
import org.l2j.gameserver.network.serverpackets.teleport.ExTeleportFavoritesList;
import org.l2j.gameserver.network.serverpackets.teleport.ExShowTeleportUi;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.slf4j.Logger;

public final class Teleporter extends Folk
{
    private static final Logger LOGGER;
    
    public Teleporter(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2TeleporterInstance);
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return GameUtils.isMonster(attacker) || super.isAutoAttackable(attacker);
    }
    
    @Override
    public void onBypassFeedback(final Player player, final String command) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String nextToken = st.nextToken();
        switch (nextToken) {
            case "requestTeleport": {
                player.sendPacket(ExShowTeleportUi.OPEN);
                player.sendPacket(new ExTeleportFavoritesList(true));
                break;
            }
            case "showTeleports": {
                final String listName = st.hasMoreTokens() ? st.nextToken() : TeleportType.NORMAL.name();
                final TeleportHolder holder = TeleportersData.getInstance().getHolder(this.getId(), listName);
                if (Objects.isNull(holder)) {
                    Teleporter.LOGGER.warn("Player {} requested show teleports for list with name {}  at NPC {}!", new Object[] { player.getObjectId(), listName, this.getId() });
                    return;
                }
                holder.showTeleportList(player, this);
                break;
            }
            case "teleport": {
                if (st.countTokens() != 2) {
                    Teleporter.LOGGER.warn("Player {} send unhandled teleport command: {}", (Object)player, (Object)command);
                    return;
                }
                final String listName = st.nextToken();
                final TeleportHolder holder = TeleportersData.getInstance().getHolder(this.getId(), listName);
                if (Objects.isNull(holder)) {
                    Teleporter.LOGGER.warn("Player {} requested unknown teleport list: {} for npc: {}!", new Object[] { player, listName, this.getId() });
                    return;
                }
                holder.doTeleport(player, this, Util.parseNextInt(st, -1));
                break;
            }
            case "chat": {
                int val = 0;
                try {
                    val = Integer.parseInt(command.substring(5));
                }
                catch (IndexOutOfBoundsException ex) {}
                catch (NumberFormatException ex2) {}
                this.showChatWindow(player, val);
                break;
            }
            default: {
                super.onBypassFeedback(player, command);
                break;
            }
        }
    }
    
    @Override
    public String getHtmlPath(final int npcId, final int val) {
        final String pom = (val == 0) ? String.valueOf(npcId) : invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npcId, val);
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pom);
    }
    
    @Override
    public void showChatWindow(final Player player) {
        if (CastleManager.getInstance().getCastle(this) == null) {
            super.showChatWindow(player);
            return;
        }
        String filename = "data/html/teleporter/castleteleporter-no.htm";
        if (player.getClan() != null && this.getCastle().getOwnerId() == player.getClanId()) {
            filename = this.getHtmlPath(this.getId(), 0);
        }
        else if (this.getCastle().getSiege().isInProgress()) {
            filename = "data/html/teleporter/castleteleporter-busy.htm";
        }
        this.sendHtmlMessage(player, filename);
    }
    
    private void sendHtmlMessage(final Player player, final String filename) {
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        html.setFile(player, filename);
        html.replace("%objectId%", String.valueOf(this.getObjectId()));
        html.replace("%npcname%", this.getName());
        player.sendPacket(html);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Teleporter.class);
    }
}
