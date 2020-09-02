// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;

public class RecoGiveTask implements Runnable
{
    private final Player _player;
    
    public RecoGiveTask(final Player player) {
        this._player = player;
    }
    
    @Override
    public void run() {
        if (this._player != null) {
            int recoToGive = 1;
            if (!this._player.isRecoTwoHoursGiven()) {
                recoToGive = 10;
                this._player.setRecoTwoHoursGiven(true);
            }
            this._player.setRecomLeft(this._player.getRecomLeft() + recoToGive);
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_OBTAINED_S1_RECOMMENDATION_S);
            sm.addInt(recoToGive);
            this._player.sendPacket(sm);
            this._player.sendPacket(new UserInfo(this._player));
        }
    }
}
