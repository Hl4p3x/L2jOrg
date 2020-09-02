// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.sql.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.database.data.ClanData;
import org.l2j.gameserver.data.database.data.ClanWarData;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import org.l2j.gameserver.model.events.impl.clan.OnClanWarFinish;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanDestroy;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanCreate;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.util.EnumIntBitmask;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Collection;
import org.l2j.gameserver.model.ClanWar;
import java.util.Objects;
import java.util.Iterator;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.model.Clan;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public class ClanTable
{
    private static final Logger LOGGER;
    private final IntMap<Clan> clans;
    
    private ClanTable() {
        this.clans = (IntMap<Clan>)new CHashIntMap();
    }
    
    private void load() {
        final Clan clan;
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).findAll().forEach(data -> {
            clan = new Clan(data);
            this.clans.put(data.getId(), (Object)clan);
            if (data.getDissolvingExpiryTime() != 0L) {
                this.scheduleRemoveClan(clan);
            }
            return;
        });
        this.allianceCheck();
        this.restoreClanWars();
    }
    
    private void allianceCheck() {
        for (final Clan clan : this.clans.values()) {
            final int allyId = clan.getAllyId();
            if (allyId != 0 && clan.getId() != allyId && !this.clans.containsKey(allyId)) {
                clan.setAllyId(0);
                clan.setAllyName(null);
                clan.changeAllyCrest(0, true);
                clan.updateClanInDB();
                ClanTable.LOGGER.info("Removed alliance from clan: {}", (Object)clan);
            }
        }
    }
    
    private void restoreClanWars() {
        final Clan attacker;
        final Clan attacked;
        ClanWar clanWar;
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).findAllWars().forEach(warData -> {
            attacker = this.getClan(warData.getAttacker());
            attacked = this.getClan(warData.getAttacked());
            if (Objects.nonNull(attacker) && Objects.nonNull(attacked)) {
                clanWar = new ClanWar(warData);
                attacker.addWar(attacked.getId(), clanWar);
                attacked.addWar(attacker.getId(), clanWar);
            }
            else {
                ClanTable.LOGGER.warn("Restore wars one of clans is null attacker: {} attacked: {}", (Object)attacker, (Object)attacked);
            }
        });
    }
    
    public Collection<Clan> getClans() {
        return (Collection<Clan>)this.clans.values();
    }
    
    public int getClanCount() {
        return this.clans.size();
    }
    
    public Clan getClan(final int clanId) {
        return (Clan)this.clans.get(clanId);
    }
    
    public Clan getClanByName(final String clanName) {
        return this.clans.values().stream().filter(c -> c.getName().equalsIgnoreCase(clanName)).findFirst().orElse(null);
    }
    
    public Clan createClan(final Player player, final String clanName) {
        if (null == player) {
            return null;
        }
        if (10 > player.getLevel()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN);
            return null;
        }
        if (0 != player.getClanId()) {
            player.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
            return null;
        }
        if (System.currentTimeMillis() < player.getClanCreateExpiryTime()) {
            player.sendPacket(SystemMessageId.YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN);
            return null;
        }
        if (!Util.isAlphaNumeric(clanName) || 2 > clanName.length()) {
            player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
            return null;
        }
        if (16 < clanName.length()) {
            player.sendPacket(SystemMessageId.CLAN_NAME_S_LENGTH_IS_INCORRECT);
            return null;
        }
        if (null != this.getClanByName(clanName)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_EXISTS);
            sm.addString(clanName);
            player.sendPacket(sm);
            return null;
        }
        final Clan clan = new Clan(IdFactory.getInstance().getNextId(), clanName);
        final ClanMember leader = new ClanMember(clan, player);
        clan.setLeader(leader);
        leader.setPlayerInstance(player);
        clan.store();
        player.setClan(clan);
        player.setPledgeClass(ClanMember.calculatePledgeClass(player));
        player.setClanPrivileges(new EnumIntBitmask<ClanPrivilege>(ClanPrivilege.class, true));
        this.clans.put(clan.getId(), (Object)clan);
        player.sendPacket(new PledgeShowInfoUpdate(clan));
        PledgeShowMemberListAll.sendAllTo(player);
        player.sendPacket(new PledgeShowMemberListUpdate(player));
        player.sendPacket(SystemMessageId.YOUR_CLAN_HAS_BEEN_CREATED);
        player.broadcastUserInfo(UserInfoType.RELATION, UserInfoType.CLAN);
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanCreate(player, clan), new ListenersContainer[0]);
        return clan;
    }
    
    public synchronized void destroyClan(final Clan clan) {
        clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_HAS_DISPERSED));
        ClanEntryManager.getInstance().removeFromClanList(clan.getId());
        final int castleId = clan.getCastleId();
        if (castleId == 0) {
            for (final Siege siege : SiegeManager.getInstance().getSieges()) {
                siege.removeSiegeClan(clan);
            }
        }
        final ClanHall hall = ClanHallManager.getInstance().getClanHallByClan(clan);
        if (hall != null) {
            hall.setOwner(null);
        }
        final ClanMember leaderMember = clan.getLeader();
        if (leaderMember == null) {
            clan.getWarehouse().destroyAllItems("ClanRemove", null, null);
        }
        else {
            clan.getWarehouse().destroyAllItems("ClanRemove", clan.getLeader().getPlayerInstance(), null);
        }
        for (final ClanMember member : clan.getMembers()) {
            clan.removeClanMember(member.getObjectId(), 0L);
        }
        final int clanId = clan.getId();
        this.clans.remove(clanId);
        IdFactory.getInstance().releaseId(clanId);
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).deleteClan(clanId);
        CrestTable.getInstance().removeCrests(clan);
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanDestroy(leaderMember, clan), new ListenersContainer[0]);
    }
    
    public void scheduleRemoveClan(final Clan clan) {
        ThreadPool.schedule(() -> {
            if (clan.getDissolvingExpiryTime() != 0L) {
                this.destroyClan(clan);
            }
        }, Math.max(clan.getDissolvingExpiryTime() - System.currentTimeMillis(), 300000L));
    }
    
    public boolean isAllyExists(final String allyName) {
        for (final Clan clan : this.clans.values()) {
            if (clan.getAllyName() != null && clan.getAllyName().equalsIgnoreCase(allyName)) {
                return true;
            }
        }
        return false;
    }
    
    public void deleteClanWars(final int clanId1, final int clanId2) {
        final Clan clan1 = getInstance().getClan(clanId1);
        final Clan clan2 = getInstance().getClan(clanId2);
        EventDispatcher.getInstance().notifyEventAsync(new OnClanWarFinish(clan1, clan2), new ListenersContainer[0]);
        clan1.deleteWar(clan2.getId());
        clan2.deleteWar(clan1.getId());
        clan1.broadcastClanStatus();
        clan2.broadcastClanStatus();
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).deleteClanWar(clanId1, clanId2);
    }
    
    public List<Clan> getClanAllies(final int allianceId) {
        final List<Clan> clanAllies = new ArrayList<Clan>();
        if (allianceId != 0) {
            for (final Clan clan : this.clans.values()) {
                if (clan != null && clan.getAllyId() == allianceId) {
                    clanAllies.add(clan);
                }
            }
        }
        return clanAllies;
    }
    
    public void forEachClan(final Consumer<Clan> action) {
    }
    
    public void shutdown() {
        for (final Clan clan : this.clans.values()) {
            clan.updateInDB();
            clan.getWarList().values().forEach(ClanWar::save);
        }
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static ClanTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanTable.class);
    }
    
    private static class Singleton
    {
        private static final ClanTable INSTANCE;
        
        static {
            INSTANCE = new ClanTable();
        }
    }
}
