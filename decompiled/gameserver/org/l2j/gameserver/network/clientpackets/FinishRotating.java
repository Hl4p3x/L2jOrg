// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.StopRotation;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.Config;

public final class FinishRotating extends ClientPacket
{
    private int _degree;
    private int _unknown;
    
    public void readImpl() {
        this._degree = this.readInt();
        this._unknown = this.readInt();
    }
    
    public void runImpl() {
        if (!Config.ENABLE_KEYBOARD_MOVEMENT) {
            return;
        }
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final StopRotation sr = new StopRotation(activeChar.getObjectId(), this._degree, 0);
        activeChar.broadcastPacket(sr);
    }
}
