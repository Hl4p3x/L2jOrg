// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.util.MathUtil;
import java.util.function.Consumer;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.Iterator;
import java.util.function.Function;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;

public abstract class AbstractPlayerGroup
{
    public abstract List<Player> getMembers();
    
    public abstract Player getLeader();
    
    public abstract void setLeader(final Player leader);
    
    public int getLeaderObjectId() {
        return this.getLeader().getObjectId();
    }
    
    public boolean isLeader(final Player player) {
        return this.getLeader().getObjectId() == player.getObjectId();
    }
    
    public int getMemberCount() {
        return this.getMembers().size();
    }
    
    public abstract int getLevel();
    
    public void broadcastPacket(final ServerPacket packet) {
        this.checkEachMember(m -> {
            if (m != null) {
                m.sendPacket(packet);
            }
            return Boolean.valueOf(true);
        });
    }
    
    public void broadcastMessage(final SystemMessageId message) {
        this.broadcastPacket(SystemMessage.getSystemMessage(message));
    }
    
    public void broadcastCreatureSay(final CreatureSay msg, final Player broadcaster) {
        this.checkEachMember(m -> {
            if (m != null && !BlockList.isBlocked(m, broadcaster)) {
                m.sendPacket(msg);
            }
            return Boolean.valueOf(true);
        });
    }
    
    public boolean containsPlayer(final Player player) {
        return this.getMembers().contains(player);
    }
    
    public boolean checkEachMember(final Function<Player, Boolean> procedure) {
        for (final Player player : this.getMembers()) {
            if (!procedure.apply(player)) {
                return false;
            }
        }
        return true;
    }
    
    public void forEachMemberInRange(final ILocational loc, final int range, final Consumer<Player> action) {
        this.getMembers().stream().filter(member -> MathUtil.isInsideRadius3D(loc, member, range)).forEach((Consumer<? super Object>)action);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof AbstractPlayerGroup && this.getLeaderObjectId() == ((AbstractPlayerGroup)obj).getLeaderObjectId());
    }
}
