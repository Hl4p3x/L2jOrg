// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionshifthandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.StaticObject;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionShiftHandler;

public class StaticWorldObjectActionShift implements IActionShiftHandler
{
    public boolean action(final Player activeChar, final WorldObject target, final boolean interact) {
        if (activeChar.isGM()) {
            activeChar.setTarget(target);
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new StaticObject((StaticWorldObject)target) });
            final NpcHtmlMessage html = new NpcHtmlMessage(0, 1, invokedynamic(makeConcatWithConstants:(IIIIIILjava/lang/String;)Ljava/lang/String;, target.getX(), target.getY(), target.getZ(), target.getObjectId(), target.getId(), ((StaticWorldObject)target).getMeshIndex(), target.getClass().getSimpleName()));
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2StaticObjectInstance;
    }
}
