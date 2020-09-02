// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.network.serverpackets.MagicSkillLaunched;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.enums.AdminTeleportType;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMoveRequest;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.StopMove;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;

public class MoveBackwardToLocation extends ClientPacket
{
    private int _targetX;
    private int _targetY;
    private int _targetZ;
    private int _originX;
    private int _originY;
    private int _originZ;
    private int _movementMode;
    
    public void readImpl() {
        this._targetX = this.readInt();
        this._targetY = this.readInt();
        this._targetZ = this.readInt();
        this._originX = this.readInt();
        this._originY = this.readInt();
        this._originZ = this.readInt();
        this._movementMode = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Config.PLAYER_MOVEMENT_BLOCK_TIME > 0 && !player.isGM() && player.getNotMoveUntil() > System.currentTimeMillis()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (this._targetX == this._originX && this._targetY == this._originY && this._targetZ == this._originZ) {
            player.sendPacket(new StopMove(player));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (DoorDataManager.getInstance().checkIfDoorsBetween(player.getX(), player.getY(), player.getZ(), this._targetX, this._targetY, this._targetZ, player.getInstanceWorld(), false)) {
            player.stopMove(player.getLastServerPosition());
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        this._targetZ += player.getTemplate().getCollisionHeight();
        if (!player.isCursorKeyMovementActive() && (player.isInFrontOf(new Location(this._targetX, this._targetY, this._targetZ)) || player.isOnSideOf(new Location(this._originX, this._originY, this._originZ)))) {
            player.setCursorKeyMovementActive(true);
        }
        if (this._movementMode == 1) {
            player.setCursorKeyMovement(false);
            final TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerMoveRequest(player, new Location(this._targetX, this._targetY, this._targetZ)), player, TerminateReturn.class);
            if (terminate != null && terminate.terminate()) {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }
        else {
            if (!Config.ENABLE_KEYBOARD_MOVEMENT) {
                return;
            }
            player.setCursorKeyMovement(true);
            if (!player.isCursorKeyMovementActive()) {
                return;
            }
        }
        final AdminTeleportType teleMode = player.getTeleMode();
        switch (teleMode) {
            case DEMONIC: {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                player.teleToLocation(new Location(this._targetX, this._targetY, this._targetZ));
                player.setTeleMode(AdminTeleportType.NORMAL);
                break;
            }
            case CHARGE: {
                player.setXYZ(this._targetX, this._targetY, this._targetZ);
                Broadcast.toSelfAndKnownPlayers(player, new MagicSkillUse(player, 30012, 10, 500, 0));
                Broadcast.toSelfAndKnownPlayers(player, new FlyToLocation(player, this._targetX, this._targetY, this._targetZ, FlyToLocation.FlyType.CHARGE));
                Broadcast.toSelfAndKnownPlayers(player, new MagicSkillLaunched(player, 30012, 10));
                player.sendPacket(ActionFailed.STATIC_PACKET);
                break;
            }
            default: {
                final double dx = this._targetX - player.getX();
                final double dy = this._targetY - player.getY();
                if (player.isControlBlocked() || dx * dx + dy * dy > 9.801E7) {
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(this._targetX, this._targetY, this._targetZ));
                break;
            }
        }
        player.onActionRequest();
    }
}
