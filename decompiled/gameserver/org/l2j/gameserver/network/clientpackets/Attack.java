// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;

public final class Attack extends ClientPacket
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
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        if (GameUtils.isPlayable(player) && player.isInBoat()) {
            player.sendPacket(SystemMessageId.THIS_IS_NOT_ALLOWED_WHILE_RIDING_A_FERRY_OR_BOAT);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (Objects.nonNull(info)) {
            for (final AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(-1)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }
        WorldObject target;
        if (player.getTargetId() == this._objectId) {
            target = player.getTarget();
        }
        else {
            target = World.getInstance().findObject(this._objectId);
        }
        if (Objects.isNull(target)) {
            return;
        }
        if ((!target.isTargetable() || player.isTargetingDisabled()) && !player.canOverrideCond(PcCondOverride.TARGET_ALL)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (target.getInstanceWorld() != player.getInstanceWorld() || !target.isVisibleFor(player)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        player.onActionRequest();
        if (player.getTarget() != target) {
            target.onAction(player);
        }
        else if (target.getObjectId() != player.getObjectId() && player.getPrivateStoreType() == PrivateStoreType.NONE && player.getActiveRequester() == null) {
            target.onForcedAttack(player);
        }
        else {
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
