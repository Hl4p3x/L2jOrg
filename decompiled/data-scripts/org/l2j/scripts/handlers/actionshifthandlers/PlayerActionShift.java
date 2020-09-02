// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionshifthandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionShiftHandler;

public class PlayerActionShift implements IActionShiftHandler
{
    public boolean action(final Player activeChar, final WorldObject target, final boolean interact) {
        if (activeChar.isGM()) {
            if (activeChar.getTarget() != target) {
                activeChar.setTarget(target);
            }
            AdminCommandHandler.getInstance().useAdminCommand(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, target.getName()), true);
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2PcInstance;
    }
}
