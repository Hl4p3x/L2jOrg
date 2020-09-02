// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.function.Function;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ExCloseMPCC;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ExMPCCPartyInfoUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExOpenMPCC;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Collection;

public class CommandChannel extends AbstractPlayerGroup
{
    private final Collection<Party> _parties;
    private Player _commandLeader;
    private int _channelLvl;
    
    public CommandChannel(final Player leader) {
        this._parties = (Collection<Party>)ConcurrentHashMap.newKeySet();
        this._commandLeader = leader;
        final Party party = leader.getParty();
        this._parties.add(party);
        this._channelLvl = party.getLevel();
        party.setCommandChannel(this);
        party.broadcastMessage(SystemMessageId.THE_COMMAND_CHANNEL_HAS_BEEN_FORMED);
        party.broadcastPacket(ExOpenMPCC.STATIC_PACKET);
    }
    
    public void addParty(final Party party) {
        if (party == null) {
            return;
        }
        this.broadcastPacket(new ExMPCCPartyInfoUpdate(party, 1));
        this._parties.add(party);
        if (party.getLevel() > this._channelLvl) {
            this._channelLvl = party.getLevel();
        }
        party.setCommandChannel(this);
        party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_JOINED_THE_COMMAND_CHANNEL));
        party.broadcastPacket(ExOpenMPCC.STATIC_PACKET);
    }
    
    public void removeParty(final Party party) {
        if (party == null) {
            return;
        }
        this._parties.remove(party);
        this._channelLvl = 0;
        for (final Party pty : this._parties) {
            if (pty.getLevel() > this._channelLvl) {
                this._channelLvl = pty.getLevel();
            }
        }
        party.setCommandChannel(null);
        party.broadcastPacket(ExCloseMPCC.STATIC_PACKET);
        if (this._parties.size() < 2) {
            this.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED));
            this.disbandChannel();
        }
        else {
            this.broadcastPacket(new ExMPCCPartyInfoUpdate(party, 0));
        }
    }
    
    public void disbandChannel() {
        if (this._parties != null) {
            for (final Party party : this._parties) {
                if (party != null) {
                    this.removeParty(party);
                }
            }
            this._parties.clear();
        }
    }
    
    @Override
    public int getMemberCount() {
        int count = 0;
        for (final Party party : this._parties) {
            if (party != null) {
                count += party.getMemberCount();
            }
        }
        return count;
    }
    
    public Collection<Party> getPartys() {
        return this._parties;
    }
    
    @Override
    public List<Player> getMembers() {
        final List<Player> members = new LinkedList<Player>();
        for (final Party party : this._parties) {
            members.addAll(party.getMembers());
        }
        return members;
    }
    
    @Override
    public int getLevel() {
        return this._channelLvl;
    }
    
    public boolean meetRaidWarCondition(final WorldObject obj) {
        return GameUtils.isCreature(obj) && ((Creature)obj).isRaid() && this.getMemberCount() >= Config.LOOT_RAIDS_PRIVILEGE_CC_SIZE;
    }
    
    @Override
    public Player getLeader() {
        return this._commandLeader;
    }
    
    @Override
    public void setLeader(final Player leader) {
        this._commandLeader = leader;
        if (leader.getLevel() > this._channelLvl) {
            this._channelLvl = leader.getLevel();
        }
    }
    
    @Override
    public boolean containsPlayer(final Player player) {
        if (this._parties != null && !this._parties.isEmpty()) {
            for (final Party party : this._parties) {
                if (party.containsPlayer(player)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean checkEachMember(final Function<Player, Boolean> function) {
        if (this._parties != null && !this._parties.isEmpty()) {
            for (final Party party : this._parties) {
                if (!party.checkEachMember(function)) {
                    return false;
                }
            }
        }
        return true;
    }
}
