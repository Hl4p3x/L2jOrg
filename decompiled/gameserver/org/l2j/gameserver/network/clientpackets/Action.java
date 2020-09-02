// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.WorldObject;
import java.util.Iterator;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.effects.AbstractEffect;
import java.util.Objects;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class Action extends ClientPacket
{
    private static final Logger LOGGER;
    private int objectId;
    private int actionId;
    
    public void readImpl() {
        this.objectId = this.readInt();
        this.readInt();
        this.readInt();
        this.readInt();
        this.actionId = this.readByte();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player.inObserverMode()) {
            player.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (Objects.nonNull(info)) {
            for (final AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(-4)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }
        WorldObject obj;
        if (player.getTargetId() == this.objectId) {
            obj = player.getTarget();
        }
        else {
            obj = World.getInstance().findObject(this.objectId);
        }
        if (Objects.isNull(obj)) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if ((!obj.isTargetable() || player.isTargetingDisabled()) && !player.canOverrideCond(PcCondOverride.TARGET_ALL)) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (obj.getInstanceWorld() != player.getInstanceWorld() || !obj.isVisibleFor(player)) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.getActiveRequester() != null) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        player.onActionRequest();
        switch (this.actionId) {
            case 0: {
                obj.onAction(player);
                break;
            }
            case 1: {
                if (!player.isGM() && (!GameUtils.isNpc(obj) || !Config.ALT_GAME_VIEWNPC)) {
                    obj.onAction(player, false);
                    break;
                }
                obj.onActionShift(player);
                break;
            }
            default: {
                Action.LOGGER.warn("Character: {} requested invalid action: {}", (Object)player.getName(), (Object)this.actionId);
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                break;
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Action.class);
    }
}
