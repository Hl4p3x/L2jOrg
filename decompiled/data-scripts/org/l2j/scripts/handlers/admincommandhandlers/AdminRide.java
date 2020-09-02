// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminRide implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    private int _petRideId;
    private static final int PURPLE_MANED_HORSE_TRANSFORMATION_ID = 106;
    private static final int JET_BIKE_TRANSFORMATION_ID = 20001;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final Player player = this.getRideTarget(activeChar);
        if (player == null) {
            return false;
        }
        if (!command.startsWith("admin_ride")) {
            if (command.startsWith("admin_unride")) {
                if (player.getTransformationId() == 106) {
                    player.untransform();
                }
                if (player.getTransformationId() == 20001) {
                    player.untransform();
                }
                else {
                    player.dismount();
                }
            }
            return true;
        }
        if (player.isMounted() || player.hasSummon()) {
            BuilderUtil.sendSysMessage(activeChar, "Target already have a summon.");
            return false;
        }
        if (command.startsWith("admin_ride_wyvern")) {
            this._petRideId = 12621;
        }
        else if (command.startsWith("admin_ride_strider")) {
            this._petRideId = 12526;
        }
        else if (command.startsWith("admin_ride_wolf")) {
            this._petRideId = 16041;
        }
        else {
            if (command.startsWith("admin_ride_horse")) {
                if (player.isTransformed()) {
                    activeChar.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                }
                else {
                    player.transform(106, true);
                }
                return true;
            }
            if (command.startsWith("admin_ride_bike")) {
                if (player.isTransformed()) {
                    activeChar.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                }
                else {
                    player.transform(20001, true);
                }
                return true;
            }
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, command));
            return false;
        }
        player.mount(this._petRideId, 0, false);
        return false;
    }
    
    private Player getRideTarget(final Player activeChar) {
        Player player = null;
        if (activeChar.getTarget() == null || activeChar.getTarget().getObjectId() == activeChar.getObjectId() || !GameUtils.isPlayer(activeChar.getTarget())) {
            player = activeChar;
        }
        else {
            player = (Player)activeChar.getTarget();
        }
        return player;
    }
    
    public String[] getAdminCommandList() {
        return AdminRide.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_ride_horse", "admin_ride_bike", "admin_ride_wyvern", "admin_ride_strider", "admin_unride_wyvern", "admin_unride_strider", "admin_unride", "admin_ride_wolf", "admin_unride_wolf" };
    }
}
