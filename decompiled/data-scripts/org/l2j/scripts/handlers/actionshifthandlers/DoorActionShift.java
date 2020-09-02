// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionshifthandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.util.HtmlUtil;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.StaticObject;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionShiftHandler;

public class DoorActionShift implements IActionShiftHandler
{
    public boolean action(final Player activeChar, final WorldObject target, final boolean interact) {
        if (activeChar.isGM()) {
            activeChar.setTarget(target);
            final Door door = (Door)target;
            final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByDoorId(door.getId());
            final Castle castle = door.getCastle();
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new StaticObject(door, activeChar.isGM()) });
            final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
            html.setFile(activeChar, "data/html/admin/doorinfo.htm");
            html.replace("%hpGauge%", HtmlUtil.getHpGauge(250, (long)door.getCurrentHp(), (long)door.getMaxHp(), false));
            html.replace("%mpGauge%", HtmlUtil.getMpGauge(250, (long)door.getCurrentMp(), (long)door.getMaxMp(), false));
            html.replace("%doorName%", door.getName());
            html.replace("%objId%", String.valueOf(door.getObjectId()));
            html.replace("%doorId%", String.valueOf(door.getId()));
            html.replace("%position%", invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, door.getX(), door.getY(), door.getZ()));
            html.replace("%node1%", invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, door.getX(0), door.getY(0), door.getZMin()));
            html.replace("%node2%", invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, door.getX(1), door.getY(1), door.getZMin()));
            html.replace("%node3%", invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, door.getX(2), door.getY(2), door.getZMax()));
            html.replace("%node4%", invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, door.getX(3), door.getY(3), door.getZMax()));
            html.replace("%clanHall%", (clanHall != null) ? clanHall.getName() : "None");
            html.replace("%castle%", (castle != null) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, castle.getName()) : "None");
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2DoorInstance;
    }
}
