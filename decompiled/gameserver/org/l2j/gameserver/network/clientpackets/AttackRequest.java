// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.WorldObject;
import java.util.Iterator;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.GameClient;

public final class AttackRequest extends ClientPacket
{
    private int _objectId;
    private int _originX;
    private int _originY;
    private int _originZ;
    private int _attackId;
    
    public void readImpl() {
        this._objectId = this.readInt();
        this._originX = this.readInt();
        this._originY = this.readInt();
        this._originZ = this.readInt();
        this._attackId = this.readByte();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final BuffInfo info = activeChar.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (final AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(-1)) {
                    activeChar.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }
        WorldObject target;
        if (activeChar.getTargetId() == this._objectId) {
            target = activeChar.getTarget();
        }
        else {
            target = World.getInstance().findObject(this._objectId);
        }
        if (target == null) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if ((!target.isTargetable() || activeChar.isTargetingDisabled()) && !activeChar.canOverrideCond(PcCondOverride.TARGET_ALL)) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (target.getInstanceWorld() != activeChar.getInstanceWorld() || !target.isVisibleFor(activeChar)) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (activeChar.getTarget() != target) {
            target.onAction(activeChar);
        }
        else if (target.getObjectId() != activeChar.getObjectId() && activeChar.getPrivateStoreType() == PrivateStoreType.NONE && activeChar.getActiveRequester() == null) {
            target.onForcedAttack(activeChar);
        }
        else {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
