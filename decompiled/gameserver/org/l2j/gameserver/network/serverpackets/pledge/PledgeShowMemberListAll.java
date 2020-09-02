// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import java.util.Objects;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.database.data.SubPledgeData;

public class PledgeShowMemberListAll extends PledgeAbstractPacket
{
    private final SubPledgeData pledge;
    private final boolean isSubPledge;
    
    private PledgeShowMemberListAll(final Clan clan, final SubPledgeData pledge, final boolean isSubPledge) {
        super(clan);
        this.pledge = pledge;
        this.isSubPledge = isSubPledge;
    }
    
    public static void sendAllTo(final Player player) {
        final Clan clan = player.getClan();
        if (clan != null) {
            for (final SubPledgeData subPledge : clan.getAllSubPledges()) {
                player.sendPacket(new PledgeShowMemberListAll(clan, subPledge, false));
            }
            player.sendPacket(new PledgeShowMemberListAll(clan, null, true));
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_ALL);
        this.writeInt(!this.isSubPledge);
        this.writeInt(this.clan.getId());
        this.writeInt(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).serverId());
        final int pledgeId = Objects.isNull(this.pledge) ? 0 : this.pledge.getId();
        final String leaderName = Objects.isNull(this.pledge) ? this.clan.getLeaderName() : PlayerNameTable.getInstance().getNameById(this.pledge.getLeaderId());
        this.writeInt(pledgeId);
        this.writeString((CharSequence)(Objects.isNull(this.pledge) ? this.clan.getName() : this.pledge.getName()));
        this.writeString((CharSequence)leaderName);
        this.writeClanInfo(pledgeId);
        this.clan.forEachMember(this::writeMemberInfo, m -> m.getPledgeType() == pledgeId);
    }
    
    protected void writeMemberInfo(final ClanMember m) {
        this.writeString((CharSequence)m.getName());
        this.writeInt(m.getLevel());
        this.writeInt(m.getClassId());
        final Player player = m.getPlayerInstance();
        if (Objects.nonNull(player)) {
            this.writeInt(player.getAppearance().isFemale());
            this.writeInt(player.getRace().ordinal());
        }
        else {
            this.writeInt(1);
            this.writeInt(1);
        }
        this.writeInt(m.isOnline() ? m.getObjectId() : 0);
        this.writeInt(m.getSponsor() != 0);
        this.writeByte(m.getOnlineStatus());
    }
}
