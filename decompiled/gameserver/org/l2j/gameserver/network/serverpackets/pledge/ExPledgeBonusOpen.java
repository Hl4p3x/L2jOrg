// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeBonusOpen extends ServerPacket
{
    private static final Logger LOGGER;
    private final Player player;
    
    public ExPledgeBonusOpen(final Player player) {
        this.player = player;
    }
    
    public void writeImpl(final GameClient client) throws InvalidDataPacketException {
        final Clan clan = this.player.getClan();
        if (clan == null) {
            ExPledgeBonusOpen.LOGGER.warn("Player: {} attempting to write to a null clan!", (Object)this.player);
            throw new InvalidDataPacketException();
        }
        final ClanRewardBonus highestMembersOnlineBonus = ClanRewardManager.getInstance().getHighestReward(ClanRewardType.MEMBERS_ONLINE);
        final ClanRewardBonus highestHuntingBonus = ClanRewardManager.getInstance().getHighestReward(ClanRewardType.HUNTING_MONSTERS);
        final ClanRewardBonus membersOnlineBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(clan);
        final ClanRewardBonus huntingBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(clan);
        if (highestMembersOnlineBonus == null) {
            ExPledgeBonusOpen.LOGGER.warn("Couldn't find highest available clan members online bonus!!");
            throw new InvalidDataPacketException();
        }
        if (highestHuntingBonus == null) {
            ExPledgeBonusOpen.LOGGER.warn("Couldn't find highest available clan hunting bonus!!");
            throw new InvalidDataPacketException();
        }
        if (highestMembersOnlineBonus.getSkillReward() == null) {
            ExPledgeBonusOpen.LOGGER.warn("Couldn't find skill reward for highest available members online bonus!!");
            throw new InvalidDataPacketException();
        }
        if (highestHuntingBonus.getItemReward() == null) {
            ExPledgeBonusOpen.LOGGER.warn("Couldn't find item reward for highest available hunting bonus!!");
            throw new InvalidDataPacketException();
        }
        this.writeId(ServerExPacketId.EX_PLEDGE_BONUS_UI_OPEN);
        this.writeInt(highestMembersOnlineBonus.getRequiredAmount());
        this.writeInt(clan.getMaxOnlineMembers());
        this.writeByte(0);
        this.writeInt((membersOnlineBonus != null) ? highestMembersOnlineBonus.getSkillReward().getSkillId() : 0);
        this.writeByte((membersOnlineBonus != null) ? membersOnlineBonus.getLevel() : 0);
        this.writeByte((int)((membersOnlineBonus != null) ? 1 : 0));
        this.writeInt(highestHuntingBonus.getRequiredAmount());
        this.writeInt(clan.getHuntingPoints());
        this.writeByte(0);
        this.writeInt((huntingBonus != null) ? highestHuntingBonus.getItemReward().getId() : 0);
        this.writeByte((huntingBonus != null) ? huntingBonus.getLevel() : 0);
        this.writeByte((int)((huntingBonus != null) ? 1 : 0));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ExPledgeBonusOpen.class);
    }
}
