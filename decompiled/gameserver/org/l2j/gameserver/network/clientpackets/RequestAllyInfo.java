// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.AllianceInfo;
import org.l2j.gameserver.network.GameClient;

public final class RequestAllyInfo extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final int allianceId = activeChar.getAllyId();
        if (allianceId > 0) {
            final AllianceInfo ai = new AllianceInfo(allianceId);
            ((GameClient)this.client).sendPacket(ai);
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_INFORMATION);
            ((GameClient)this.client).sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_NAME_S1);
            sm.addString(ai.getName());
            ((GameClient)this.client).sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_LEADER_S2_OF_S1);
            sm.addString(ai.getLeaderC());
            sm.addString(ai.getLeaderP());
            ((GameClient)this.client).sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.CONNECTION_S1_TOTAL_S2);
            sm.addInt(ai.getOnline());
            sm.addInt(ai.getTotal());
            ((GameClient)this.client).sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.AFFILIATED_CLANS_TOTAL_S1_CLAN_S);
            sm.addInt(ai.getAllies().length);
            ((GameClient)this.client).sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_INFORMATION);
            for (final ClanInfo aci : ai.getAllies()) {
                ((GameClient)this.client).sendPacket(sm);
                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_NAME_S1);
                sm.addString(aci.getClan().getName());
                ((GameClient)this.client).sendPacket(sm);
                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEADER_S1);
                sm.addString(aci.getClan().getLeaderName());
                ((GameClient)this.client).sendPacket(sm);
                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEVEL_S1);
                sm.addInt(aci.getClan().getLevel());
                ((GameClient)this.client).sendPacket(sm);
                sm = SystemMessage.getSystemMessage(SystemMessageId.CONNECTION_S1_TOTAL_S2);
                sm.addInt(aci.getOnline());
                sm.addInt(aci.getTotal());
                ((GameClient)this.client).sendPacket(sm);
                sm = SystemMessage.getSystemMessage(SystemMessageId.SEPARATOR_DASHES);
            }
            sm = SystemMessage.getSystemMessage(SystemMessageId.SEPARATOR_EQUALS);
            ((GameClient)this.client).sendPacket(sm);
        }
        else {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
        }
    }
}
