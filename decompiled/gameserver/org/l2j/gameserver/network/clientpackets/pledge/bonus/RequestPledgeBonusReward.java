// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledge.bonus;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeBonusReward extends ClientPacket
{
    private static final Logger LOGGER;
    private int _type;
    
    public void readImpl() {
        this._type = this.readByte();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getClan() == null) {
            return;
        }
        if (this._type < 0 || this._type > ClanRewardType.values().length) {
            return;
        }
        final Clan clan = player.getClan();
        final ClanRewardType type = ClanRewardType.values()[this._type];
        final ClanMember member = clan.getClanMember(player.getObjectId());
        if (clan.canClaimBonusReward(player, type)) {
            final ClanRewardBonus bonus = type.getAvailableBonus(player.getClan());
            if (bonus != null) {
                final ItemHolder itemReward = bonus.getItemReward();
                final SkillHolder skillReward = bonus.getSkillReward();
                if (itemReward != null) {
                    player.addItem("ClanReward", itemReward.getId(), itemReward.getCount(), player, true);
                }
                else if (skillReward != null) {
                    skillReward.getSkill().activateSkill(player, player);
                }
                member.setRewardClaimed(type);
            }
            else {
                RequestPledgeBonusReward.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/Clan;)Ljava/lang/String;, player, clan));
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestPledgeBonusReward.class);
    }
}
