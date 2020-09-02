// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPledgeClassicRaidInfo extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_CLASSIC_RAID_INFO);
        this.writeInt(Util.zeroIfNullOrElse((Object)client.getPlayer().getClan(), Clan::getArenaProgress));
        this.writeInt(5);
        final SkillHolder skill;
        ClanRewardManager.getInstance().forEachReward(ClanRewardType.ARENA, reward -> {
            skill = reward.getSkillReward();
            this.writeInt(skill.getSkillId());
            this.writeInt(skill.getLevel());
        });
    }
}
