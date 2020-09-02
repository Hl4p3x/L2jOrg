// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.LinkedList;
import org.l2j.gameserver.model.actor.Playable;
import java.util.List;

public final class RelationChanged extends ServerPacket
{
    public static final int RELATION_PARTY1 = 1;
    public static final int RELATION_PARTY2 = 2;
    public static final int RELATION_PARTY3 = 4;
    public static final int RELATION_PARTY4 = 8;
    public static final int RELATION_PARTYLEADER = 16;
    public static final int RELATION_HAS_PARTY = 32;
    public static final int RELATION_CLAN_MEMBER = 64;
    public static final int RELATION_LEADER = 128;
    public static final int RELATION_CLAN_MATE = 256;
    public static final int RELATION_INSIEGE = 512;
    public static final int RELATION_ATTACKER = 1024;
    public static final int RELATION_ALLY = 2048;
    public static final int RELATION_ENEMY = 4096;
    public static final int RELATION_DECLARED_WAR = 16384;
    public static final int RELATION_MUTUAL_WAR = 32768;
    public static final int RELATION_ALLY_MEMBER = 65536;
    public static final int RELATION_TERRITORY_WAR = 524288;
    public static final byte SEND_ONE = 0;
    public static final byte SEND_DEFAULT = 1;
    public static final byte SEND_MULTI = 4;
    private final List<Relation> _multi;
    private Relation _singled;
    private byte _mask;
    
    public RelationChanged(final Playable activeChar, final int relation, final boolean autoattackable) {
        this._mask = 0;
        this._mask |= 0x0;
        this._singled = new Relation();
        this._singled._objId = activeChar.getObjectId();
        this._singled._relation = relation;
        this._singled._autoAttackable = (autoattackable ? 1 : 0);
        this._singled._reputation = activeChar.getReputation();
        this._singled._pvpFlag = activeChar.getPvpFlag();
        this._multi = null;
    }
    
    public RelationChanged() {
        this._mask = 0;
        this._mask |= 0x4;
        this._multi = new LinkedList<Relation>();
    }
    
    public void addRelation(final Playable activeChar, final int relation, final boolean autoattackable) {
        if (activeChar.isInvisible()) {
            return;
        }
        final Relation r = new Relation();
        r._objId = activeChar.getObjectId();
        r._relation = relation;
        r._autoAttackable = (autoattackable ? 1 : 0);
        r._reputation = activeChar.getReputation();
        r._pvpFlag = activeChar.getPvpFlag();
        this._multi.add(r);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.RELATION_CHANGED);
        this.writeByte(this._mask);
        if (this._multi == null) {
            this.writeRelation(this._singled);
        }
        else {
            this.writeShort((short)this._multi.size());
            for (final Relation r : this._multi) {
                this.writeRelation(r);
            }
        }
    }
    
    private void writeRelation(final Relation relation) {
        this.writeInt(relation._objId);
        if ((this._mask & 0x1) == 0x0) {
            this.writeInt(relation._relation);
            this.writeByte((byte)relation._autoAttackable);
            this.writeInt(relation._reputation);
            this.writeByte((byte)relation._pvpFlag);
        }
    }
    
    protected static class Relation
    {
        int _objId;
        int _relation;
        int _autoAttackable;
        int _reputation;
        int _pvpFlag;
    }
}
