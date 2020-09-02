// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionshifthandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionShiftHandler;

public class ItemActionShift implements IActionShiftHandler
{
    public boolean action(final Player activeChar, final WorldObject target, final boolean interact) {
        if (activeChar.isGM()) {
            final NpcHtmlMessage html = new NpcHtmlMessage(0, 1, invokedynamic(makeConcatWithConstants:(IIILorg/l2j/gameserver/model/Location;Ljava/lang/String;)Ljava/lang/String;, target.getObjectId(), target.getId(), ((Item)target).getOwnerId(), target.getLocation(), target.getClass().getSimpleName()));
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2ItemInstance;
    }
}
