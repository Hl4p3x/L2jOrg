// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.training;

import org.l2j.gameserver.model.holders.TrainingHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.training.ExTrainingZone_Leaving;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class NotifyTrainingRoomEnd extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final TrainingHolder holder = activeChar.getTraingCampInfo();
        if (holder == null) {
            return;
        }
        if (holder.isTraining()) {
            holder.setEndTime(System.currentTimeMillis());
            activeChar.setTraingCampInfo(holder);
            activeChar.enableAllSkills();
            activeChar.setIsInvul(false);
            activeChar.setInvisible(false);
            activeChar.setIsImmobilized(false);
            activeChar.teleToLocation(activeChar.getLastLocation());
            activeChar.sendPacket(ExTrainingZone_Leaving.STATIC_PACKET);
            holder.setEndTime(System.currentTimeMillis());
            activeChar.setTraingCampInfo(holder);
        }
    }
}
