// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionhandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionHandler;

public class DecoyAction implements IActionHandler
{
    public boolean action(final Player activeChar, final WorldObject target, final boolean interact) {
        if (activeChar.isLockedTarget() && activeChar.getLockedTarget() != target) {
            activeChar.sendPacket(SystemMessageId.FAILED_TO_CHANGE_ENMITY);
            return false;
        }
        activeChar.setTarget(target);
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2Decoy;
    }
}
