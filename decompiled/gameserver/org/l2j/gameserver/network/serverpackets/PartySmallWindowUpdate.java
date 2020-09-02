// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.PartySmallWindowUpdateType;

public final class PartySmallWindowUpdate extends AbstractMaskPacket<PartySmallWindowUpdateType>
{
    private final Player _member;
    private int _flags;
    
    public PartySmallWindowUpdate(final Player member, final boolean addAllFlags) {
        this._flags = 0;
        this._member = member;
        if (addAllFlags) {
            for (final PartySmallWindowUpdateType type : PartySmallWindowUpdateType.values()) {
                this.addComponentType(type);
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PARTY_SMALL_WINDOW_UPDATE);
        this.writeInt(this._member.getObjectId());
        this.writeShort((short)this._flags);
        if (this.containsMask(PartySmallWindowUpdateType.CURRENT_CP)) {
            this.writeInt((int)this._member.getCurrentCp());
        }
        if (this.containsMask(PartySmallWindowUpdateType.MAX_CP)) {
            this.writeInt(this._member.getMaxCp());
        }
        if (this.containsMask(PartySmallWindowUpdateType.CURRENT_HP)) {
            this.writeInt((int)this._member.getCurrentHp());
        }
        if (this.containsMask(PartySmallWindowUpdateType.MAX_HP)) {
            this.writeInt(this._member.getMaxHp());
        }
        if (this.containsMask(PartySmallWindowUpdateType.CURRENT_MP)) {
            this.writeInt((int)this._member.getCurrentMp());
        }
        if (this.containsMask(PartySmallWindowUpdateType.MAX_MP)) {
            this.writeInt(this._member.getMaxMp());
        }
        if (this.containsMask(PartySmallWindowUpdateType.LEVEL)) {
            this.writeByte((byte)this._member.getLevel());
        }
        if (this.containsMask(PartySmallWindowUpdateType.CLASS_ID)) {
            this.writeShort((short)this._member.getClassId().getId());
        }
        if (this.containsMask(PartySmallWindowUpdateType.PARTY_SUBSTITUTE)) {
            this.writeByte((byte)0);
        }
        if (this.containsMask(PartySmallWindowUpdateType.VITALITY_POINTS)) {
            this.writeInt(this._member.getVitalityPoints());
        }
    }
    
    @Override
    protected void addMask(final int mask) {
        this._flags |= mask;
    }
    
    @Override
    public boolean containsMask(final PartySmallWindowUpdateType component) {
        return this.containsMask(this._flags, component);
    }
    
    @Override
    protected byte[] getMasks() {
        return new byte[0];
    }
}
