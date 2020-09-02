// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionhandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionHandler;

public class ArtefactAction implements IActionHandler
{
    public boolean action(final Player activeChar, final WorldObject target, final boolean interact) {
        if (!((Npc)target).canTarget(activeChar)) {
            return false;
        }
        if (activeChar.getTarget() != target) {
            activeChar.setTarget(target);
        }
        else if (interact && !((Npc)target).canInteract(activeChar)) {
            activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, new Object[] { target });
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2ArtefactInstance;
    }
}
