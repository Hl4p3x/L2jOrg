// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public final class RequestTargetCanceld extends ClientPacket
{
    private int _unselect;
    
    public void readImpl() {
        this._unselect = this.readShort();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player.isLockedTarget()) {
            player.sendPacket(SystemMessageId.FAILED_TO_REMOVE_ENMITY);
            return;
        }
        if (this._unselect == 0) {
            if (player.isCastingNow()) {
                player.abortAllSkillCasters();
            }
        }
        else {
            player.setTarget(null);
        }
    }
}
