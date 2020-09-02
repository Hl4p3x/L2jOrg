// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;

public final class PartySmallWindowAdd extends ServerPacket
{
    private final Player member;
    private final Party party;
    
    public PartySmallWindowAdd(final Player member, final Party party) {
        this.member = member;
        this.party = party;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PARTY_SMALL_WINDOW_ADD);
        this.writeInt(this.party.getLeaderObjectId());
        this.writeInt(this.party.getDistributionType().getId());
        this.writeInt(this.member.getObjectId());
        this.writeString((CharSequence)this.member.getName());
        this.writeInt((int)this.member.getCurrentCp());
        this.writeInt(this.member.getMaxCp());
        this.writeInt((int)this.member.getCurrentHp());
        this.writeInt(this.member.getMaxHp());
        this.writeInt((int)this.member.getCurrentMp());
        this.writeInt(this.member.getMaxMp());
        this.writeInt(this.member.getVitalityPoints());
        this.writeByte(this.member.getLevel());
        this.writeShort(this.member.getClassId().getId());
        this.writeByte(0);
        this.writeShort(this.member.getRace().ordinal());
        this.writeInt(0);
    }
}
