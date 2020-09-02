// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Iterator;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.awt.Color;
import org.l2j.gameserver.network.serverpackets.ExServerPrimitive;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminDoorControl implements IAdminCommandHandler
{
    private static DoorDataManager _doorTable;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        try {
            if (command.startsWith("admin_open ")) {
                final int doorId = Integer.parseInt(command.substring(11));
                if (AdminDoorControl._doorTable.getDoor(doorId) != null) {
                    AdminDoorControl._doorTable.getDoor(doorId).openMe();
                }
                else {
                    for (final Castle castle : CastleManager.getInstance().getCastles()) {
                        if (castle.getDoor(doorId) != null) {
                            castle.getDoor(doorId).openMe();
                        }
                    }
                }
            }
            else if (command.startsWith("admin_close ")) {
                final int doorId = Integer.parseInt(command.substring(12));
                if (AdminDoorControl._doorTable.getDoor(doorId) != null) {
                    AdminDoorControl._doorTable.getDoor(doorId).closeMe();
                }
                else {
                    for (final Castle castle : CastleManager.getInstance().getCastles()) {
                        if (castle.getDoor(doorId) != null) {
                            castle.getDoor(doorId).closeMe();
                        }
                    }
                }
            }
            else if (command.equals("admin_closeall")) {
                for (final Door door2 : AdminDoorControl._doorTable.getDoors()) {
                    door2.closeMe();
                }
                for (final Castle castle2 : CastleManager.getInstance().getCastles()) {
                    for (final Door door3 : castle2.getDoors()) {
                        door3.closeMe();
                    }
                }
            }
            else if (command.equals("admin_openall")) {
                for (final Door door2 : AdminDoorControl._doorTable.getDoors()) {
                    door2.openMe();
                }
                for (final Castle castle2 : CastleManager.getInstance().getCastles()) {
                    for (final Door door3 : castle2.getDoors()) {
                        door3.openMe();
                    }
                }
            }
            else if (command.equals("admin_open")) {
                final WorldObject target = activeChar.getTarget();
                if (GameUtils.isDoor(target)) {
                    ((Door)target).openMe();
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, "Incorrect target.");
                }
            }
            else if (command.equals("admin_close")) {
                final WorldObject target = activeChar.getTarget();
                if (GameUtils.isDoor(target)) {
                    ((Door)target).closeMe();
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, "Incorrect target.");
                }
            }
            else if (command.equals("admin_showdoors")) {
                final ExServerPrimitive packet;
                final Color color;
                World.getInstance().forEachVisibleObject((WorldObject)activeChar, (Class)Door.class, door -> {
                    packet = new ExServerPrimitive(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, door.getId()), activeChar.getX(), activeChar.getY(), -16000);
                    color = (door.isOpen() ? Color.GREEN : Color.RED);
                    packet.addLine(color, door.getX(0), door.getY(0), door.getZMin(), door.getX(1), door.getY(1), door.getZMin());
                    packet.addLine(color, door.getX(1), door.getY(1), door.getZMin(), door.getX(2), door.getY(2), door.getZMax());
                    packet.addLine(color, door.getX(2), door.getY(2), door.getZMax(), door.getX(3), door.getY(3), door.getZMax());
                    packet.addLine(color, door.getX(3), door.getY(3), door.getZMax(), door.getX(0), door.getY(0), door.getZMin());
                    packet.addLine(color, door.getX(0), door.getY(0), door.getZMax(), door.getX(1), door.getY(1), door.getZMax());
                    packet.addLine(color, door.getX(1), door.getY(1), door.getZMax(), door.getX(2), door.getY(2), door.getZMin());
                    packet.addLine(color, door.getX(2), door.getY(2), door.getZMin(), door.getX(3), door.getY(3), door.getZMin());
                    packet.addLine(color, door.getX(3), door.getY(3), door.getZMin(), door.getX(0), door.getY(0), door.getZMax());
                    packet.addLine(color, door.getX(0), door.getY(0), door.getZMin(), door.getX(1), door.getY(1), door.getZMax());
                    packet.addLine(color, door.getX(2), door.getY(2), door.getZMin(), door.getX(3), door.getY(3), door.getZMax());
                    packet.addLine(color, door.getX(0), door.getY(0), door.getZMax(), door.getX(1), door.getY(1), door.getZMin());
                    packet.addLine(color, door.getX(2), door.getY(2), door.getZMax(), door.getX(3), door.getY(3), door.getZMin());
                    activeChar.sendPacket(new ServerPacket[] { (ServerPacket)packet });
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, door.getId()));
                    return;
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminDoorControl.ADMIN_COMMANDS;
    }
    
    static {
        AdminDoorControl._doorTable = DoorDataManager.getInstance();
        ADMIN_COMMANDS = new String[] { "admin_open", "admin_close", "admin_openall", "admin_closeall", "admin_showdoors" };
    }
}
