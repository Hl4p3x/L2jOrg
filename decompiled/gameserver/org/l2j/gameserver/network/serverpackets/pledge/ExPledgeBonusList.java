// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import java.util.Comparator;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeBonusList extends ServerPacket
{
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_BONUS_LIST);
        this.writeByte(0);
        ClanRewardManager.getInstance().getClanRewardBonuses(ClanRewardType.MEMBERS_ONLINE).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus -> this.writeInt(bonus.getSkillReward().getSkillId()));
        this.writeByte(1);
        ClanRewardManager.getInstance().getClanRewardBonuses(ClanRewardType.HUNTING_MONSTERS).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus -> this.writeInt(bonus.getItemReward().getId()));
    }
}
