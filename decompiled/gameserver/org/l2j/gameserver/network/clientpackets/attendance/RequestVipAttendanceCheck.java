// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.attendance;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.attendance.ExConfirmVipAttendanceCheck;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.AttendanceSettings;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestVipAttendanceCheck extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!((AttendanceSettings)Configurator.getSettings((Class)AttendanceSettings.class)).enabled()) {
            player.sendPacket(SystemMessageId.DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU_ATTENDANCE_CHECK);
            return;
        }
        if (((AttendanceSettings)Configurator.getSettings((Class)AttendanceSettings.class)).vipOnly() && player.getVipTier() <= 0) {
            player.sendPacket(SystemMessageId.YOUR_VIP_LEVEL_IS_TOO_LOW_TO_RECEIVE_THE_REWARD);
            return;
        }
        final int delay = ((AttendanceSettings)Configurator.getSettings((Class)AttendanceSettings.class)).delay();
        if (player.getUptime() < delay * 60 * 1000) {
            player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_REDEEM_YOUR_REWARD_S1_MINUTES_AFTER_LOGGING_IN_S2_MINUTES_LEFT).addInt(delay)).addInt((int)(delay - player.getUptime())));
            return;
        }
        final boolean isRewardAvailable = player.canReceiveAttendance();
        byte rewardIndex = player.lastAttendanceReward();
        final ItemHolder reward = AttendanceRewardData.getInstance().getRewards().get(rewardIndex);
        final ItemTemplate itemTemplate = ItemEngine.getInstance().getTemplate(reward.getId());
        final long weight = itemTemplate.getWeight() * reward.getCount();
        final long slots = itemTemplate.isStackable() ? 1L : reward.getCount();
        if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
            return;
        }
        if (isRewardAvailable) {
            ++rewardIndex;
            player.updateAttendanceReward(rewardIndex);
            player.addItem("Attendance Reward", reward, player, true);
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_VE_RECEIVED_YOUR_VIP_ATTENDANCE_REWARD_FOR_DAY_S1);
            msg.addInt(rewardIndex);
            player.sendPacket(msg);
            player.sendPacket(new ExConfirmVipAttendanceCheck(isRewardAvailable, rewardIndex));
        }
    }
}
