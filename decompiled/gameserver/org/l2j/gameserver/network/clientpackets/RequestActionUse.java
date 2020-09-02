// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.data.xml.model.ActionData;
import java.util.Iterator;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.PlayerActionHandler;
import org.l2j.gameserver.network.serverpackets.RecipeShopManageList;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.data.xml.ActionManager;
import org.l2j.gameserver.model.actor.transform.Transform;
import java.util.Arrays;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.effects.AbstractEffect;
import java.util.Objects;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestActionUse extends ClientPacket
{
    private static final Logger LOGGER;
    private int actionId;
    private boolean ctrlPressed;
    private boolean shiftPressed;
    
    public void readImpl() {
        this.actionId = this.readInt();
        this.ctrlPressed = this.readIntAsBoolean();
        this.shiftPressed = this.readBoolean();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if ((player.isFakeDeath() && this.actionId != 0) || player.isDead() || player.isControlBlocked()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (Objects.nonNull(info)) {
            for (final AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(this.actionId)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }
        final int[] allowedActions = player.isTransformed() ? ExBasicActionList.ACTIONS_ON_TRANSFORM : ExBasicActionList.DEFAULT_ACTION_LIST;
        if (Arrays.binarySearch(allowedActions, this.actionId) < 0) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            RequestActionUse.LOGGER.warn("Player {} used action which he does not have! Id = {} transform: {}", new Object[] { player, this.actionId, player.getTransformation().orElse(null) });
            return;
        }
        final ActionData action = ActionManager.getInstance().getActionData(this.actionId);
        if (!Objects.nonNull(action)) {
            if (this.actionId == 51) {
                if (player.isAlikeDead()) {
                    ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                if (player.isSellingBuffs()) {
                    ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
                    player.setPrivateStoreType(PrivateStoreType.NONE);
                    player.broadcastUserInfo();
                }
                if (player.isSitting()) {
                    player.standUp();
                }
                ((GameClient)this.client).sendPacket(new RecipeShopManageList(player, false));
            }
            else {
                RequestActionUse.LOGGER.warn("{}: unhandled action type {}", (Object)player, (Object)this.actionId);
            }
            return;
        }
        final IPlayerActionHandler handler = PlayerActionHandler.getInstance().getHandler(action.getHandler());
        if (Objects.nonNull(handler)) {
            handler.useAction(player, action, this.ctrlPressed, this.shiftPressed);
            return;
        }
        RequestActionUse.LOGGER.warn("Couldn't find handler with name: {}", (Object)action.getHandler());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestActionUse.class);
    }
}
