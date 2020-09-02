// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Summon;
import java.util.Iterator;
import java.util.function.Consumer;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Party;

public final class PartySmallWindowAll extends ServerPacket
{
    private final Party _party;
    private final Player _exclude;
    
    public PartySmallWindowAll(final Player exclude, final Party party) {
        this._exclude = exclude;
        this._party = party;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PARTY_SMALL_WINDOW_ALL);
        this.writeInt(this._party.getLeaderObjectId());
        this.writeByte((byte)this._party.getDistributionType().getId());
        this.writeByte((byte)(this._party.getMemberCount() - 1));
        for (final Player member : this._party.getMembers()) {
            if (member != null && member != this._exclude) {
                this.writeInt(member.getObjectId());
                this.writeString((CharSequence)member.getName());
                this.writeInt((int)member.getCurrentCp());
                this.writeInt(member.getMaxCp());
                this.writeInt((int)member.getCurrentHp());
                this.writeInt(member.getMaxHp());
                this.writeInt((int)member.getCurrentMp());
                this.writeInt(member.getMaxMp());
                this.writeInt(member.getVitalityPoints());
                this.writeByte(member.getLevel());
                this.writeShort(member.getClassId().getId());
                this.writeByte(1);
                this.writeShort(member.getRace().ordinal());
                this.writeInt(0);
                final Summon pet = member.getPet();
                this.writeInt(member.getServitors().size() + ((pet != null) ? 1 : 0));
                Util.doIfNonNull((Object)pet, (Consumer)this::writeSummonStatus);
                member.getServitors().values().forEach(this::writeSummonStatus);
            }
        }
    }
    
    private void writeSummonStatus(final Summon summon) {
        this.writeInt(summon.getObjectId());
        this.writeInt(summon.getId() + 1000000);
        this.writeByte(summon.getSummonType());
        this.writeString((CharSequence)summon.getName());
        this.writeInt((int)summon.getCurrentHp());
        this.writeInt(summon.getMaxHp());
        this.writeInt((int)summon.getCurrentMp());
        this.writeInt(summon.getMaxMp());
        this.writeByte(summon.getLevel());
    }
}
