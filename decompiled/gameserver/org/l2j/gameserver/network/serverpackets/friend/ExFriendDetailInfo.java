// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import java.time.LocalDate;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExFriendDetailInfo extends ServerPacket
{
    private final int _objectId;
    private Player friend;
    private FriendInfo info;
    private final String _name;
    
    public ExFriendDetailInfo(final Player player, final String name) {
        this._objectId = player.getObjectId();
        this._name = name;
        final int friendId = PlayerNameTable.getInstance().getIdByName(name);
        this.friend = World.getInstance().findPlayer(friendId);
        if (Objects.isNull(this.friend)) {
            this.info = new FriendInfo(friendId, ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findFriendData(friendId));
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_FRIEND_DETAIL_INFO);
        this.writeInt(this._objectId);
        if (Objects.isNull(this.friend)) {
            this.WriteFriendInfo();
        }
        else {
            this.writePlayerInfo();
        }
    }
    
    private void writePlayerInfo() {
        this.writeString((CharSequence)this.friend.getName());
        this.writeInt(this.friend.isOnline());
        this.writeInt(this.friend.isOnline() ? this.friend.getObjectId() : 0);
        this.writeShort(this.friend.getLevel());
        this.writeShort(this.friend.getClassId().getId());
        this.writeInt(this.friend.getClanId());
        this.writeInt(this.friend.getClanCrestId());
        this.writeString((CharSequence)((this.friend.getClan() != null) ? this.friend.getClan().getName() : ""));
        this.writeInt(this.friend.getAllyId());
        this.writeInt(this.friend.getAllyCrestId());
        this.writeString((CharSequence)((this.friend.getClan() != null) ? this.friend.getClan().getAllyName() : ""));
        final LocalDate createDate = this.friend.getCreateDate();
        this.writeByte(createDate.getMonthValue());
        this.writeByte(createDate.getDayOfMonth());
        this.writeInt(-1);
        this.writeString((CharSequence)"");
    }
    
    private void WriteFriendInfo() {
        this.writeString((CharSequence)this._name);
        this.writeInt(this.info.online);
        this.writeInt(this.info.online ? this.info.objectId : 0);
        this.writeShort(this.info.level);
        this.writeShort(this.info.classId);
        this.writeInt(this.info.clanId);
        final Clan clan;
        if (this.info.clanId > 0 && Objects.nonNull(clan = ClanTable.getInstance().getClan(this.info.clanId))) {
            this.writeInt(clan.getCrestId());
            this.writeString((CharSequence)clan.getName());
            this.writeInt(clan.getAllyId());
            this.writeInt(clan.getAllyCrestId());
            this.writeString((CharSequence)clan.getAllyName());
        }
        else {
            this.writeInt(0);
            this.writeString((CharSequence)"");
            this.writeInt(0);
            this.writeInt(0);
            this.writeString((CharSequence)"");
        }
        if (Objects.nonNull(this.info.createDate)) {
            this.writeByte(this.info.createDate.getMonthValue());
            this.writeByte(this.info.createDate.getDayOfMonth());
        }
        else {
            this.writeByte(0);
            this.writeByte(0);
        }
        this.writeInt((int)((System.currentTimeMillis() - this.info.lastAccess) / 1000L));
        this.writeString((CharSequence)"");
    }
}
