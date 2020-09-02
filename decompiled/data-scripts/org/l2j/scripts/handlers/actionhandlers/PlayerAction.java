// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionhandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionHandler;

public class PlayerAction implements IActionHandler
{
    public boolean action(final Player activeChar, final WorldObject target, final boolean interact) {
        if (activeChar.isControlBlocked()) {
            return false;
        }
        if (activeChar.isLockedTarget() && activeChar.getLockedTarget() != target) {
            activeChar.sendPacket(SystemMessageId.FAILED_TO_CHANGE_ENMITY);
            return false;
        }
        if (activeChar.getTarget() != target) {
            activeChar.setTarget(target);
        }
        else if (interact) {
            if (((Player)target).getPrivateStoreType() != PrivateStoreType.NONE) {
                activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, new Object[] { target });
            }
            else if (target.isAutoAttackable((Creature)activeChar)) {
                if (GeoEngine.getInstance().canSeeTarget((WorldObject)activeChar, target)) {
                    activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { target });
                    activeChar.onActionRequest();
                }
            }
            else {
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
                if (GeoEngine.getInstance().canSeeTarget((WorldObject)activeChar, target)) {
                    activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, new Object[] { target });
                }
            }
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2PcInstance;
    }
}
