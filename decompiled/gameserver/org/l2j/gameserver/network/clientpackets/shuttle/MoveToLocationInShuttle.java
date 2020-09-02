// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.shuttle.ExMoveToLocationInShuttle;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.serverpackets.shuttle.ExStopMoveInShuttle;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public final class MoveToLocationInShuttle extends ClientPacket
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
        if (this._targetX == this._originX && this._targetY == this._originY && this._targetZ == this._originZ) {
            activeChar.sendPacket(new ExStopMoveInShuttle(activeChar, this._boatId));
            return;
        }
        if (activeChar.isAttackingNow() && activeChar.getActiveWeaponItem() != null && activeChar.getActiveWeaponItem().getItemType() == WeaponType.BOW) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (activeChar.isSitting() || activeChar.isMovementDisabled()) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        activeChar.setInVehiclePosition(new Location(this._targetX, this._targetY, this._targetZ));
        activeChar.broadcastPacket(new ExMoveToLocationInShuttle(activeChar, this._originX, this._originY, this._originZ));
    }
}
