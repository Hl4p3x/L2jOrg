// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.SubclassType;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.model.base.SubClass;
import java.util.ArrayList;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;

public class ExSubjobInfo extends ServerPacket
{
    private final int _currClassId;
    private final int _currRace;
    private final int _type;
    private final List<SubInfo> _subs;
    
    public ExSubjobInfo(final Player player, final SubclassInfoType type) {
        this._currClassId = player.getClassId().getId();
        this._currRace = player.getRace().ordinal();
        this._type = type.ordinal();
        (this._subs = new ArrayList<SubInfo>()).add(0, new SubInfo(player));
        for (final SubClass sub : player.getSubClasses().values()) {
            this._subs.add(new SubInfo(sub));
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SUBJOB_INFO);
        this.writeByte((byte)this._type);
        this.writeInt(this._currClassId);
        this.writeInt(this._currRace);
        this.writeInt(this._subs.size());
        for (final SubInfo sub : this._subs) {
            this.writeInt(sub.getIndex());
            this.writeInt(sub.getClassId());
            this.writeInt(sub.getLevel());
            this.writeByte((byte)sub.getType());
        }
    }
    
    private final class SubInfo
    {
        private final int _index;
        private final int _classId;
        private final int _level;
        private final int _type;
        
        public SubInfo(final SubClass sub) {
            this._index = sub.getClassIndex();
            this._classId = sub.getClassId();
            this._level = sub.getLevel();
            this._type = (sub.isDualClass() ? SubclassType.DUALCLASS.ordinal() : SubclassType.SUBCLASS.ordinal());
        }
        
        public SubInfo(final Player player) {
            this._index = 0;
            this._classId = player.getBaseClass();
            this._level = player.getStats().getBaseLevel();
            this._type = SubclassType.BASECLASS.ordinal();
        }
        
        public int getIndex() {
            return this._index;
        }
        
        public int getClassId() {
            return this._classId;
        }
        
        public int getLevel() {
            return this._level;
        }
        
        public int getType() {
            return this._type;
        }
    }
}
