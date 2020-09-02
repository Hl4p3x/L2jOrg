// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.data.xml.DoorDataManager;
import java.util.StringTokenizer;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.enums.TeleportType;
import org.l2j.gameserver.data.xml.impl.TeleportersData;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class Doormen extends Folk
{
    public Doormen(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2DoormenInstance);
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return GameUtils.isMonster(attacker) || super.isAutoAttackable(attacker);
    }
    
    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (command.startsWith("Chat")) {
            this.showChatWindow(player);
            return;
        }
        if (command.startsWith("open_doors")) {
            if (this.isOwnerClan(player)) {
                if (this.isUnderSiege()) {
                    this.cannotManageDoors(player);
                }
                else {
                    this.openDoors(player, command);
                }
            }
            return;
        }
        if (command.startsWith("close_doors")) {
            if (this.isOwnerClan(player)) {
                if (this.isUnderSiege()) {
                    this.cannotManageDoors(player);
                }
                else {
                    this.closeDoors(player, command);
                }
            }
            return;
        }
        if (command.startsWith("tele")) {
            if (this.isOwnerClan(player)) {
                final TeleportHolder holder = TeleportersData.getInstance().getHolder(this.getId(), TeleportType.OTHER.name());
                if (holder != null) {
                    final int locId = Integer.parseInt(command.substring(5).trim());
                    holder.doTeleport(player, this, locId);
                }
            }
            return;
        }
        super.onBypassFeedback(player, command);
    }
    
    @Override
    public void showChatWindow(final Player player) {
        player.sendPacket(ActionFailed.STATIC_PACKET);
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        if (!this.isOwnerClan(player)) {
            html.setFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getTemplate().getId()));
        }
        else {
            html.setFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getTemplate().getId()));
        }
        html.replace("%objectId%", String.valueOf(this.getObjectId()));
        player.sendPacket(html);
    }
    
    protected void openDoors(final Player player, final String command) {
        final StringTokenizer st = new StringTokenizer(command.substring(10), ", ");
        st.nextToken();
        while (st.hasMoreTokens()) {
            DoorDataManager.getInstance().getDoor(Integer.parseInt(st.nextToken())).openMe();
        }
    }
    
    protected void closeDoors(final Player player, final String command) {
        final StringTokenizer st = new StringTokenizer(command.substring(11), ", ");
        st.nextToken();
        while (st.hasMoreTokens()) {
            DoorDataManager.getInstance().getDoor(Integer.parseInt(st.nextToken())).closeMe();
        }
    }
    
    protected void cannotManageDoors(final Player player) {
        player.sendPacket(ActionFailed.STATIC_PACKET);
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        html.setFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getTemplate().getId()));
        player.sendPacket(html);
    }
    
    protected boolean isOwnerClan(final Player player) {
        return true;
    }
    
    protected boolean isUnderSiege() {
        return false;
    }
}
