// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.attendance;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.attendance.ExVipAttendanceItemList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.AttendanceSettings;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestVipAttendanceItemList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!((AttendanceSettings)Configurator.getSettings((Class)AttendanceSettings.class)).enabled()) {
            activeChar.sendPacket(SystemMessageId.DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU_ATTENDANCE_CHECK);
            return;
        }
        activeChar.sendPacket(new ExVipAttendanceItemList(activeChar));
    }
}
