// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExListPartyMatchingWaitingRoom;
import org.l2j.gameserver.network.GameClient;
import java.util.LinkedList;
import org.l2j.gameserver.model.base.ClassId;
import java.util.List;

public class RequestListPartyMatchingWaitingRoom extends ClientPacket
{
    private int _page;
    private int _minLevel;
    private int _maxLevel;
    private List<ClassId> _classId;
    private String _query;
    
    public void readImpl() {
        this._page = this.readInt();
        this._minLevel = this.readInt();
        this._maxLevel = this.readInt();
        final int size = this.readInt();
        if (size > 0 && size < 128) {
            this._classId = new LinkedList<ClassId>();
            for (int i = 0; i < size; ++i) {
                this._classId.add(ClassId.getClassId(this.readInt()));
            }
        }
        if (this.available() > 0) {
            this._query = this.readString();
        }
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, this._page, this._minLevel, this._maxLevel, this._classId, this._query));
    }
}
