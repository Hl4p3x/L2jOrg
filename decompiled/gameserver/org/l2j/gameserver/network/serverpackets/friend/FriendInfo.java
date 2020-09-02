// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.database.data.PlayerData;
import java.time.LocalDate;

class FriendInfo
{
    int objectId;
    String name;
    int level;
    int classId;
    boolean online;
    int clanId;
    LocalDate createDate;
    long lastAccess;
    
    FriendInfo(final int friendId, final PlayerData friendData) {
        this(friendId, friendData.getName(), false, friendData.getLevel(), friendData.getClassId());
        this.clanId = friendData.getClanId();
        this.createDate = friendData.getCreateDate();
        this.lastAccess = friendData.getLastAccess();
    }
    
    FriendInfo(final int objectId, final String name, final boolean online, final int level, final int classId) {
        this.objectId = objectId;
        this.name = name;
        this.online = online;
        this.level = level;
        this.classId = classId;
    }
}
