// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Boat;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.MoveToLocationInVehicle;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Vehicle;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.instancemanager.BoatManager;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.serverpackets.StopMoveInVehicle;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;

public final class RequestMoveToLocationInVehicle extends ClientPacket
{
    private int _boatId;
    private int _targetX;
    private int _targetY;
    private int _targetZ;
    private int _originX;
    private int _originY;
    private int _originZ;
    
    public void readImpl() {
        this._boatId = this.readInt();
        this._targetX = this.readInt();
        this._targetY = this.readInt();
        this._targetZ = this.readInt();
        this._originX = this.readInt();
        this._originY = this.readInt();
        this._originZ = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (Config.PLAYER_MOVEMENT_BLOCK_TIME > 0 && !activeChar.isGM() && activeChar.getNotMoveUntil() > System.currentTimeMillis()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (this._targetX == this._originX && this._targetY == this._originY && this._targetZ == this._originZ) {
            ((GameClient)this.client).sendPacket(new StopMoveInVehicle(activeChar, this._boatId));
            return;
        }
        if (activeChar.isAttackingNow() && activeChar.getActiveWeaponItem() != null && activeChar.getActiveWeaponItem().getItemType() == WeaponType.BOW) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (activeChar.isSitting() || activeChar.isMovementDisabled()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (activeChar.hasSummon()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_SHOULD_RELEASE_YOUR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (activeChar.isTransformed()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (activeChar.isInBoat()) {
            final Boat boat = activeChar.getBoat();
            if (boat.getObjectId() != this._boatId) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }
        else {
            final Boat boat = BoatManager.getInstance().getBoat(this._boatId);
            if (boat == null || !MathUtil.isInsideRadius3D(boat, activeChar, 300)) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            activeChar.setVehicle(boat);
        }
        final Location pos = new Location(this._targetX, this._targetY, this._targetZ);
        final Location originPos = new Location(this._originX, this._originY, this._originZ);
        activeChar.setInVehiclePosition(pos);
        activeChar.broadcastPacket(new MoveToLocationInVehicle(activeChar, pos, originPos));
    }
}
