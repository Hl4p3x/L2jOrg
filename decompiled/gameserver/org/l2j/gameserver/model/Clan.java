// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.database.data.PlayerData;
import java.sql.ResultSet;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeBonusMarkReset;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLeaderChange;
import java.util.LinkedList;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLvlUp;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.util.EnumIntBitmask;
import org.l2j.gameserver.network.serverpackets.PledgeReceiveSubPledgeCreated;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeSkillDelete;
import org.l2j.gameserver.network.serverpackets.ExSubPledgeSkillAdd;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeSkillAdd;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.data.database.data.ClanSkillData;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import java.util.stream.Stream;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.List;
import java.util.Collection;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLeft;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import java.util.Iterator;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanJoin;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.PledgeSkillList;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.spi.LoggingEventBuilder;
import java.util.function.Supplier;
import java.util.Objects;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.item.container.ClanWarehouse;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.data.database.data.ClanData;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.data.database.data.SubPledgeData;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.item.container.Warehouse;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public class Clan implements IIdentifiable, INamable
{
    private static final Logger LOGGER;
    public static final int PENALTY_TYPE_CLAN_LEAVED = 1;
    public static final int PENALTY_TYPE_CLAN_DISMISSED = 2;
    public static final int PENALTY_TYPE_DISMISS_CLAN = 3;
    public static final int PENALTY_TYPE_DISSOLVE_ALLY = 4;
    public static final int SUBUNIT_ACADEMY = -1;
    public static final int SUBUNIT_ROYAL1 = 100;
    public static final int SUBUNIT_ROYAL2 = 200;
    public static final int SUBUNIT_KNIGHT1 = 1001;
    public static final int SUBUNIT_KNIGHT2 = 1002;
    public static final int SUBUNIT_KNIGHT3 = 2001;
    public static final int SUBUNIT_KNIGHT4 = 2002;
    private static final int MAX_NOTICE_LENGTH = 8192;
    private final IntMap<ClanMember> members;
    private final Warehouse warehouse;
    private final IntMap<ClanWar> _atWarWith;
    private final IntMap<Skill> _skills;
    private final IntMap<RankPrivs> privs;
    private final IntMap<Skill> _subPledgeSkills;
    private final AtomicInteger _siegeKills;
    private final AtomicInteger _siegeDeaths;
    private ClanMember leader;
    private IntMap<SubPledgeData> subPledges;
    private int _hideoutId;
    private int _rank;
    private String notice;
    private boolean noticeEnabled;
    private ClanRewardBonus _lastMembersOnlineBonus;
    private ClanRewardBonus _lastHuntingBonus;
    private final ClanData data;
    
    public Clan(final ClanData data) {
        this.members = (IntMap<ClanMember>)new CHashIntMap();
        this.warehouse = new ClanWarehouse(this);
        this._atWarWith = (IntMap<ClanWar>)new CHashIntMap();
        this._skills = (IntMap<Skill>)new CHashIntMap();
        this.privs = (IntMap<RankPrivs>)new CHashIntMap();
        this._subPledgeSkills = (IntMap<Skill>)new CHashIntMap();
        this._siegeKills = new AtomicInteger();
        this._siegeDeaths = new AtomicInteger();
        this.subPledges = (IntMap<SubPledgeData>)new CHashIntMap();
        this._rank = 0;
        this.noticeEnabled = false;
        this._lastMembersOnlineBonus = null;
        this._lastHuntingBonus = null;
        this.data = data;
        this.initializePrivs();
        this.restore();
        this.warehouse.restore();
        final ClanRewardBonus availableOnlineBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(this);
        if (this._lastMembersOnlineBonus == null && availableOnlineBonus != null) {
            this._lastMembersOnlineBonus = availableOnlineBonus;
        }
        final ClanRewardBonus availableHuntingBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(this);
        if (this._lastHuntingBonus == null && availableHuntingBonus != null) {
            this._lastHuntingBonus = availableHuntingBonus;
        }
    }
    
    private void restore() {
        if (this.data.getAllyPenaltyExpiryTime() < System.currentTimeMillis()) {
            this.setAllyPenaltyExpiryTime(0L, 0);
        }
        if (this.data.getCharPenaltyExpiryTime() + Config.ALT_CLAN_JOIN_DAYS * 86400000 < System.currentTimeMillis()) {
            this.setCharPenaltyExpiryTime(0L);
        }
        final ClanMember member;
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findClanMembers(this.data.getId()).forEach(memberData -> {
            member = new ClanMember(this, memberData);
            if (member.getObjectId() == this.data.getLeaderId()) {
                this.setLeader(member);
            }
            else {
                this.addClanMember(member);
            }
            return;
        });
        this.restoreSubPledges();
        this.restoreRankPrivs();
        this.restoreSkills();
        this.restoreNotice();
        ClanRewardManager.getInstance().checkArenaProgress(this);
    }
    
    public Clan(final int clanId, final String clanName) {
        this.members = (IntMap<ClanMember>)new CHashIntMap();
        this.warehouse = new ClanWarehouse(this);
        this._atWarWith = (IntMap<ClanWar>)new CHashIntMap();
        this._skills = (IntMap<Skill>)new CHashIntMap();
        this.privs = (IntMap<RankPrivs>)new CHashIntMap();
        this._subPledgeSkills = (IntMap<Skill>)new CHashIntMap();
        this._siegeKills = new AtomicInteger();
        this._siegeDeaths = new AtomicInteger();
        this.subPledges = (IntMap<SubPledgeData>)new CHashIntMap();
        this._rank = 0;
        this.noticeEnabled = false;
        this._lastMembersOnlineBonus = null;
        this._lastHuntingBonus = null;
        (this.data = new ClanData()).setId(clanId);
        this.data.setName(clanName);
        this.initializePrivs();
    }
    
    @Override
    public int getId() {
        return this.data.getId();
    }
    
    public int getLeaderId() {
        return (this.leader != null) ? this.leader.getObjectId() : 0;
    }
    
    public ClanMember getLeader() {
        return this.leader;
    }
    
    public void setLeader(final ClanMember leader) {
        this.leader = leader;
        this.members.put(leader.getObjectId(), (Object)leader);
        this.data.setLeader(leader.getObjectId());
    }
    
    public String getLeaderName() {
        if (Objects.isNull(this.leader)) {
            final LoggingEventBuilder atWarn = Clan.LOGGER.atWarn();
            final ClanData data = this.data;
            Objects.requireNonNull(data);
            atWarn.addArgument((Supplier)data::getName).log("Clan {} without clan leader!");
            return "";
        }
        return this.leader.getName();
    }
    
    @Override
    public String getName() {
        return this.data.getName();
    }
    
    private void addClanMember(final ClanMember member) {
        this.members.put(member.getObjectId(), (Object)member);
    }
    
    public void addClanMember(final Player player) {
        final ClanMember member = new ClanMember(this, player);
        this.addClanMember(member);
        member.setPlayerInstance(player);
        player.setClan(this);
        player.setPledgeClass(ClanMember.calculatePledgeClass(player));
        player.sendPacket(new PledgeShowMemberListUpdate(player));
        player.sendPacket(new PledgeSkillList(this));
        this.addSkillEffects(player);
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanJoin(member, this), new ListenersContainer[0]);
    }
    
    public void updateClanMember(final Player player) {
        final ClanMember member = new ClanMember(player.getClan(), player);
        if (player.isClanLeader()) {
            this.setLeader(member);
        }
        this.addClanMember(member);
    }
    
    public ClanMember getClanMember(final String name) {
        for (final ClanMember temp : this.members.values()) {
            if (temp.getName().equals(name)) {
                return temp;
            }
        }
        return null;
    }
    
    public ClanMember getClanMember(final int objectId) {
        return (ClanMember)this.members.get(objectId);
    }
    
    public void removeClanMember(final int objectId, final long clanJoinExpiryTime) {
        final ClanMember exMember = (ClanMember)this.members.remove(objectId);
        if (exMember == null) {
            Clan.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, objectId));
            return;
        }
        final int leadssubpledge = this.getLeaderSubPledge(objectId);
        if (leadssubpledge != 0) {
            this.getSubPledge(leadssubpledge).setLeaderId(0);
            this.updateSubPledgeInDB(leadssubpledge);
        }
        if (exMember.getApprentice() != 0) {
            final ClanMember apprentice = this.getClanMember(exMember.getApprentice());
            if (apprentice != null) {
                if (apprentice.getPlayerInstance() != null) {
                    apprentice.getPlayerInstance().setSponsor(0);
                }
                else {
                    apprentice.setApprenticeAndSponsor(0, 0);
                }
                apprentice.saveApprenticeAndSponsor(0, 0);
            }
        }
        if (exMember.getSponsor() != 0) {
            final ClanMember sponsor = this.getClanMember(exMember.getSponsor());
            if (sponsor != null) {
                if (sponsor.getPlayerInstance() != null) {
                    sponsor.getPlayerInstance().setApprentice(0);
                }
                else {
                    sponsor.setApprenticeAndSponsor(0, 0);
                }
                sponsor.saveApprenticeAndSponsor(0, 0);
            }
        }
        exMember.saveApprenticeAndSponsor(0, 0);
        if (((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).removeCastleCirclets()) {
            CastleManager.getInstance().removeCirclet(exMember, this.getCastleId());
        }
        if (exMember.isOnline()) {
            final Player player = exMember.getPlayerInstance();
            if (!player.isNoble()) {
                player.setTitle("");
            }
            player.setApprentice(0);
            player.setSponsor(0);
            if (player.isClanLeader()) {
                SiegeManager.getInstance().removeSiegeSkills(player);
                player.setClanCreateExpiryTime(System.currentTimeMillis() + Config.ALT_CLAN_CREATE_DAYS * 86400000);
            }
            this.removeSkillEffects(player);
            player.getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());
            if (player.getClan().getCastleId() > 0) {
                CastleManager.getInstance().getCastleByOwner(player.getClan()).removeResidentialSkills(player);
            }
            player.sendSkillList();
            player.setClan(null);
            if (exMember.getPledgeType() != -1) {
                player.setClanJoinExpiryTime(clanJoinExpiryTime);
            }
            player.setPledgeClass(ClanMember.calculatePledgeClass(player));
            player.broadcastUserInfo();
            player.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
        }
        else {
            this.removeMemberInDatabase(exMember, clanJoinExpiryTime, (this.getLeaderId() == objectId) ? (System.currentTimeMillis() + Config.ALT_CLAN_CREATE_DAYS * 86400000) : 0L);
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLeft(exMember, this), new ListenersContainer[0]);
    }
    
    public Collection<ClanMember> getMembers() {
        return (Collection<ClanMember>)this.members.values();
    }
    
    public int getMembersCount() {
        return this.members.size();
    }
    
    public int getSubPledgeMembersCount(final int subpl) {
        int result = 0;
        for (final ClanMember temp : this.members.values()) {
            if (temp.getPledgeType() == subpl) {
                ++result;
            }
        }
        return result;
    }
    
    public int getMaxNrOfMembers(final int pledgeType) {
        int n = 0;
        Label_0207: {
            switch (pledgeType) {
                case 0: {
                    switch (this.data.getLevel()) {
                        case 3: {
                            n = 30;
                            break Label_0207;
                        }
                        case 2: {
                            n = 20;
                            break Label_0207;
                        }
                        case 1: {
                            n = 15;
                            break Label_0207;
                        }
                        case 0: {
                            n = 10;
                            break Label_0207;
                        }
                        default: {
                            n = 40;
                            break Label_0207;
                        }
                    }
                    break;
                }
                case -1: {
                    n = 20;
                    break;
                }
                case 100:
                case 200: {
                    n = ((this.data.getLevel() == 11) ? 30 : 20);
                    break;
                }
                case 1001:
                case 1002:
                case 2001:
                case 2002: {
                    switch (this.data.getLevel()) {
                        case 9:
                        case 10:
                        case 11: {
                            n = 25;
                            break Label_0207;
                        }
                        default: {
                            n = 10;
                            break Label_0207;
                        }
                    }
                    break;
                }
                default: {
                    n = 0;
                    break;
                }
            }
        }
        return n;
    }
    
    public List<Player> getOnlineMembers(final int exclude) {
        return this.members.values().stream().filter(member -> member.getObjectId() != exclude).filter(ClanMember::isOnline).map((Function<? super Object, ?>)ClanMember::getPlayerInstance).filter(Objects::nonNull).collect((Collector<? super Object, ?, List<Player>>)Collectors.toList());
    }
    
    public int getOnlineMembersCount() {
        return (int)this.members.values().stream().filter(ClanMember::isOnline).count();
    }
    
    public void forEachMember(final Consumer<ClanMember> action, final Predicate<ClanMember> filter) {
        this.members.values().stream().filter(filter).forEach(action);
    }
    
    public void forEachOnlineMember(final Consumer<Player> action) {
        this.onlineMembersStream().forEach(action);
    }
    
    public void forEachOnlineMember(final Consumer<Player> action, final Predicate<Player> filter) {
        this.onlineMembersStream().filter(filter).forEach(action);
    }
    
    private Stream<Player> onlineMembersStream() {
        return this.members.values().stream().filter(ClanMember::isOnline).map((Function<? super Object, ? extends Player>)ClanMember::getPlayerInstance);
    }
    
    public int getAllyId() {
        return this.data.getAllyId();
    }
    
    public void setAllyId(final int allyId) {
        this.data.setAllyId(allyId);
    }
    
    public String getAllyName() {
        return this.data.getAllyName();
    }
    
    public void setAllyName(final String allyName) {
        this.data.setAllyName(allyName);
    }
    
    public int getAllyCrestId() {
        return this.data.getAllyCrest();
    }
    
    public void setAllyCrestId(final int allyCrestId) {
        this.data.setAllyCrest(allyCrestId);
    }
    
    public int getLevel() {
        return this.data.getLevel();
    }
    
    public void setLevel(final int level) {
        this.data.setLevel(level);
    }
    
    public int getCastleId() {
        return this.data.getCastle();
    }
    
    public void setCastleId(final int castleId) {
        this.data.setCastle(castleId);
    }
    
    public int getHideoutId() {
        return this._hideoutId;
    }
    
    public void setHideoutId(final int hideoutId) {
        this._hideoutId = hideoutId;
    }
    
    public int getCrestId() {
        return this.data.getCrest();
    }
    
    public void setCrestId(final int crestId) {
        this.data.setCrest(crestId);
    }
    
    public int getCrestLargeId() {
        return this.data.getCrestLarge();
    }
    
    public void setCrestLargeId(final int crestLargeId) {
        this.data.setCrestLarge(crestLargeId);
    }
    
    public boolean isMember(final int id) {
        return id != 0 && this.members.containsKey(id);
    }
    
    public int getBloodAllianceCount() {
        return this.data.getBloodAllianceCount();
    }
    
    public void increaseBloodAllianceCount() {
        this.data.setBloodAllianceCount(this.data.getBloodAllianceCount() + SiegeManager.getInstance().getBloodAllianceReward());
    }
    
    public void resetBloodAllianceCount() {
        this.data.setBloodAllianceCount(0);
    }
    
    public void updateInDB() {
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).save((Object)this.data);
    }
    
    public void updateClanInDB() {
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).save((Object)this.data);
    }
    
    public void store() {
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).save((Object)this.data);
    }
    
    private void removeMemberInDatabase(final ClanMember member, final long clanJoinExpiryTime, final long clanCreateExpiryTime) {
        final PlayerDAO characterDAO = (PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class);
        characterDAO.deleteClanInfoOfMember(member.getObjectId(), clanJoinExpiryTime, clanCreateExpiryTime);
        characterDAO.deleteApprentice(member.getObjectId());
        characterDAO.deleteSponsor(member.getObjectId());
    }
    
    private void restoreNotice() {
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).withNoticesDo(this.data.getId(), resutSet -> {
            try {
                if (resutSet.next()) {
                    this.noticeEnabled = resutSet.getBoolean("enabled");
                    this.notice = resutSet.getString("notice");
                }
            }
            catch (Exception e) {
                Clan.LOGGER.error("Error restoring clan notice", (Throwable)e);
            }
        });
    }
    
    private void storeNotice(String notice, final boolean enabled) {
        if (Objects.isNull(notice)) {
            notice = "";
        }
        if (notice.length() > 8192) {
            notice = notice.substring(0, 8191);
        }
        this.notice = notice;
        this.noticeEnabled = enabled;
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).saveNotice(this.data.getId(), notice, enabled);
    }
    
    public boolean isNoticeEnabled() {
        return this.noticeEnabled;
    }
    
    public void setNoticeEnabled(final boolean enabled) {
        this.storeNotice(this.notice, enabled);
    }
    
    public String getNotice() {
        if (this.notice == null) {
            return "";
        }
        return this.notice;
    }
    
    public void setNotice(final String notice) {
        this.storeNotice(notice, this.noticeEnabled);
    }
    
    private void restoreSkills() {
        for (final ClanSkillData skillData : ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).findSkillsByClan(this.data.getId())) {
            final ClanSkillData clanSkillData;
            final SubPledgeData subPledge;
            Util.doIfNonNull((Object)SkillEngine.getInstance().getSkill(skillData.getId(), skillData.getLevel()), skill -> {
                switch (clanSkillData.getSubPledge()) {
                    case -2: {
                        this._skills.put(skill.getId(), (Object)skill);
                        break;
                    }
                    case 0: {
                        this._subPledgeSkills.put(skill.getId(), (Object)skill);
                        break;
                    }
                    default: {
                        subPledge = (SubPledgeData)this.subPledges.get(clanSkillData.getSubPledge());
                        if (Objects.nonNull(subPledge)) {
                            subPledge.addNewSkill(skill);
                            break;
                        }
                        else {
                            Clan.LOGGER.info("Missing sub pledge {} for clan {}, skill skipped.", (Object)subPledge, (Object)this);
                            break;
                        }
                        break;
                    }
                }
            });
        }
    }
    
    public final Skill[] getAllSkills() {
        return this._skills.values().toArray(Skill[]::new);
    }
    
    public IntMap<Skill> getSkills() {
        return this._skills;
    }
    
    public Skill addNewSkill(final Skill newSkill) {
        return this.addNewSkill(newSkill, -2);
    }
    
    public Skill addNewSkill(final Skill newSkill, final int subType) {
        Skill oldSkill = null;
        if (newSkill != null) {
            if (subType == -2) {
                oldSkill = (Skill)this._skills.put(newSkill.getId(), (Object)newSkill);
            }
            else if (subType == 0) {
                oldSkill = (Skill)this._subPledgeSkills.put(newSkill.getId(), (Object)newSkill);
            }
            else {
                final SubPledgeData subunit = this.getSubPledge(subType);
                if (subunit == null) {
                    Clan.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILorg/l2j/gameserver/model/Clan;)Ljava/lang/String;, subType, this));
                    return oldSkill;
                }
                oldSkill = subunit.addNewSkill(newSkill);
            }
            if (Objects.nonNull(oldSkill)) {
                ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).updateClanSkill(this.data.getId(), oldSkill.getId(), newSkill.getLevel());
            }
            else {
                ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).saveClanSkill(this.data.getId(), newSkill.getId(), newSkill.getLevel(), subType);
            }
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED);
            sm.addSkillName(newSkill.getId());
            for (final ClanMember temp : this.members.values()) {
                if (temp != null && temp.getPlayerInstance() != null && temp.isOnline()) {
                    if (subType == -2) {
                        temp.getPlayerInstance().addSkill(newSkill, false);
                        temp.getPlayerInstance().sendPacket(new PledgeSkillAdd(newSkill.getId(), newSkill.getLevel()));
                        temp.getPlayerInstance().sendPacket(sm);
                        temp.getPlayerInstance().sendSkillList();
                    }
                    else {
                        if (temp.getPledgeType() != subType) {
                            continue;
                        }
                        temp.getPlayerInstance().addSkill(newSkill, false);
                        temp.getPlayerInstance().sendPacket(new ExSubPledgeSkillAdd(subType, newSkill.getId(), newSkill.getLevel()));
                        temp.getPlayerInstance().sendPacket(sm);
                        temp.getPlayerInstance().sendSkillList();
                    }
                }
            }
        }
        return oldSkill;
    }
    
    public void removeSkill(final int skillId) {
        if (this._skills.containsKey(skillId)) {
            final Skill oldSkill = (Skill)this._skills.remove(skillId);
            ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).removeSkill(this.getId(), skillId);
            final PledgeSkillDelete skillDelete = new PledgeSkillDelete(oldSkill);
            final Skill skill;
            final ServerPacket serverPacket;
            this.forEachOnlineMember(player -> {
                player.removeSkill(skill, false);
                player.sendPacket(serverPacket);
            });
        }
    }
    
    public void addSkillEffects(final Player player) {
        if (player == null) {
            return;
        }
        final int playerSocialClass = player.getPledgeClass() + 1;
        for (final Skill skill : this._skills.values()) {
            final SkillLearn skillLearn = SkillTreesData.getInstance().getPledgeSkill(skill.getId(), skill.getLevel());
            if (skillLearn == null || skillLearn.getSocialClass() == null || playerSocialClass >= skillLearn.getSocialClass().ordinal()) {
                player.addSkill(skill, false);
            }
        }
        if (player.getPledgeType() == 0) {
            for (final Skill skill : this._subPledgeSkills.values()) {
                final SkillLearn skillLearn = SkillTreesData.getInstance().getSubPledgeSkill(skill.getId(), skill.getLevel());
                if (skillLearn == null || skillLearn.getSocialClass() == null || playerSocialClass >= skillLearn.getSocialClass().ordinal()) {
                    player.addSkill(skill, false);
                }
            }
        }
        else {
            final SubPledgeData subunit = this.getSubPledge(player.getPledgeType());
            if (subunit == null) {
                return;
            }
            for (final Skill skill2 : subunit.getSkills()) {
                player.addSkill(skill2, false);
            }
        }
        if (this.data.getReputation() < 0) {
            this.skillsStatus(player, true);
        }
    }
    
    public void removeSkillEffects(final Player player) {
        if (player == null) {
            return;
        }
        for (final Skill skill : this._skills.values()) {
            player.removeSkill(skill, false);
        }
        if (player.getPledgeType() == 0) {
            for (final Skill skill : this._subPledgeSkills.values()) {
                player.removeSkill(skill, false);
            }
        }
        else {
            final SubPledgeData subunit = this.getSubPledge(player.getPledgeType());
            if (subunit == null) {
                return;
            }
            for (final Skill skill2 : subunit.getSkills()) {
                player.removeSkill(skill2, false);
            }
        }
    }
    
    public void skillsStatus(final Player player, final boolean disable) {
        if (player == null) {
            return;
        }
        for (final Skill skill : this._skills.values()) {
            if (disable) {
                player.disableSkill(skill, -1L);
            }
            else {
                player.enableSkill(skill);
            }
        }
        if (player.getPledgeType() == 0) {
            for (final Skill skill : this._subPledgeSkills.values()) {
                if (disable) {
                    player.disableSkill(skill, -1L);
                }
                else {
                    player.enableSkill(skill);
                }
            }
        }
        else {
            final SubPledgeData subunit = this.getSubPledge(player.getPledgeType());
            if (subunit != null) {
                for (final Skill skill2 : subunit.getSkills()) {
                    if (disable) {
                        player.disableSkill(skill2, -1L);
                    }
                    else {
                        player.enableSkill(skill2);
                    }
                }
            }
        }
    }
    
    public void broadcastToOnlineAllyMembers(final ServerPacket packet) {
        ClanTable.getInstance().getClanAllies(this.getAllyId()).forEach(c -> c.broadcastToOnlineMembers(packet));
    }
    
    public void broadcastToOnlineMembers(final ServerPacket packet) {
        final Stream<Player> onlineMembersStream = this.onlineMembersStream();
        Objects.requireNonNull(packet);
        onlineMembersStream.forEach(packet::sendTo);
    }
    
    public void broadcastCSToOnlineMembers(final CreatureSay packet, final Player broadcaster) {
        for (final ClanMember member : this.members.values()) {
            if (member != null && member.isOnline() && !BlockList.isBlocked(member.getPlayerInstance(), broadcaster)) {
                member.getPlayerInstance().sendPacket(packet);
            }
        }
    }
    
    public void broadcastToOtherOnlineMembers(final ServerPacket packet, final Player player) {
        for (final ClanMember member : this.members.values()) {
            if (member != null && member.isOnline() && member.getPlayerInstance() != player) {
                member.getPlayerInstance().sendPacket(packet);
            }
        }
    }
    
    @Override
    public String toString() {
        return this.data.toString();
    }
    
    public Warehouse getWarehouse() {
        return this.warehouse;
    }
    
    public boolean isAtWarWith(final int clanId) {
        return this._atWarWith.containsKey(clanId);
    }
    
    public boolean isAtWarWith(final Clan clan) {
        return clan != null && this._atWarWith.containsKey(clan.getId());
    }
    
    public boolean isAtWar() {
        return !this._atWarWith.isEmpty();
    }
    
    public IntMap<ClanWar> getWarList() {
        return this._atWarWith;
    }
    
    public void broadcastClanStatus() {
        for (final Player member : this.getOnlineMembers(0)) {
            member.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
            PledgeShowMemberListAll.sendAllTo(member);
        }
    }
    
    private void restoreSubPledges() {
        this.subPledges = (IntMap<SubPledgeData>)((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).findClanSubPledges(this.data.getId());
    }
    
    public final SubPledgeData getSubPledge(final int pledgeType) {
        return (SubPledgeData)this.subPledges.get(pledgeType);
    }
    
    public final SubPledgeData getSubPledge(final String pledgeName) {
        for (final SubPledgeData sp : this.subPledges.values()) {
            if (sp.getName().equalsIgnoreCase(pledgeName)) {
                return sp;
            }
        }
        return null;
    }
    
    public final SubPledgeData[] getAllSubPledges() {
        if (this.subPledges == null) {
            return new SubPledgeData[0];
        }
        return this.subPledges.values().toArray(SubPledgeData[]::new);
    }
    
    public SubPledgeData createSubPledge(final Player player, int pledgeType, final int leaderId, final String subPledgeName) {
        pledgeType = this.getAvailablePledgeTypes(pledgeType);
        if (pledgeType == 0) {
            if (pledgeType == -1) {
                player.sendPacket(SystemMessageId.YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY);
            }
            else {
                player.sendMessage("You can't create any more sub-units of this type");
            }
            return null;
        }
        if (this.leader.getObjectId() == leaderId) {
            player.sendMessage("Leader is not correct");
            return null;
        }
        if (pledgeType != -1 && ((this.data.getReputation() < Config.ROYAL_GUARD_COST && pledgeType < 1001) || (this.data.getReputation() < Config.KNIGHT_UNIT_COST && pledgeType > 200))) {
            player.sendPacket(SystemMessageId.THE_CLAN_REPUTATION_IS_TOO_LOW);
            return null;
        }
        final SubPledgeData subPledgeData = new SubPledgeData();
        subPledgeData.setClanId(this.data.getId());
        subPledgeData.setId(pledgeType);
        subPledgeData.setName(subPledgeName);
        subPledgeData.setLeaderId((pledgeType != -1) ? leaderId : 0);
        this.subPledges.put(pledgeType, (Object)subPledgeData);
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).save(subPledgeData);
        if (pledgeType != -1) {
            if (pledgeType < 1001) {
                this.setReputationScore(this.data.getReputation() - Config.ROYAL_GUARD_COST, true);
            }
            else {
                this.setReputationScore(this.data.getReputation() - Config.KNIGHT_UNIT_COST, true);
            }
        }
        this.broadcastToOnlineMembers(new PledgeShowInfoUpdate(this.leader.getClan()));
        this.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(subPledgeData, this.leader.getClan()));
        return subPledgeData;
    }
    
    public int getAvailablePledgeTypes(int pledgeType) {
        if (this.subPledges.get(pledgeType) != null) {
            switch (pledgeType) {
                case -1:
                case 200:
                case 2002: {
                    return 0;
                }
                case 100: {
                    pledgeType = this.getAvailablePledgeTypes(200);
                    break;
                }
                case 1001: {
                    pledgeType = this.getAvailablePledgeTypes(1002);
                    break;
                }
                case 1002: {
                    pledgeType = this.getAvailablePledgeTypes(2001);
                    break;
                }
                case 2001: {
                    pledgeType = this.getAvailablePledgeTypes(2002);
                    break;
                }
            }
        }
        return pledgeType;
    }
    
    public void updateSubPledgeInDB(final int pledgeType) {
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).save(this.getSubPledge(pledgeType));
    }
    
    private void restoreRankPrivs() {
        int rank;
        int privileges;
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).withClanPrivs(this.data.getId(), resultSet -> {
            try {
                while (resultSet.next()) {
                    rank = resultSet.getInt("rank");
                    privileges = resultSet.getInt("privs");
                    if (rank == -1) {
                        continue;
                    }
                    else {
                        ((RankPrivs)this.privs.get(rank)).setPrivs(privileges);
                    }
                }
            }
            catch (Exception e) {
                Clan.LOGGER.error("Error restoring clan privs by rank", (Throwable)e);
            }
        });
    }
    
    public void initializePrivs() {
        for (int i = 1; i < 10; ++i) {
            this.privs.put(i, (Object)new RankPrivs(i, 0, new EnumIntBitmask<ClanPrivilege>(ClanPrivilege.class, false)));
        }
    }
    
    public EnumIntBitmask<ClanPrivilege> getRankPrivs(final int rank) {
        return (this.privs.get(rank) != null) ? ((RankPrivs)this.privs.get(rank)).getPrivs() : new EnumIntBitmask<ClanPrivilege>(ClanPrivilege.class, false);
    }
    
    public void setRankPrivs(final int rank, final int privs) {
        if (this.privs.get(rank) != null) {
            ((RankPrivs)this.privs.get(rank)).setPrivs(privs);
            ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).saveClanPrivs(this.data.getId(), rank, privs);
            for (final ClanMember cm : this.members.values()) {
                if (cm.isOnline() && cm.getPowerGrade() == rank && cm.getPlayerInstance() != null) {
                    cm.getPlayerInstance().getClanPrivileges().setBitmask(privs);
                    cm.getPlayerInstance().sendPacket(new UserInfo(cm.getPlayerInstance()));
                }
            }
            this.broadcastClanStatus();
        }
        else {
            this.privs.put(rank, (Object)new RankPrivs(rank, 0, privs));
            ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).saveClanPrivs(this.data.getId(), rank, privs);
        }
    }
    
    public final RankPrivs[] getAllRankPrivs() {
        return this.privs.values().toArray(RankPrivs[]::new);
    }
    
    public int getLeaderSubPledge(final int leaderId) {
        int id = 0;
        for (final SubPledgeData sp : this.subPledges.values()) {
            if (sp.getLeaderId() == 0) {
                continue;
            }
            if (sp.getLeaderId() != leaderId) {
                continue;
            }
            id = sp.getId();
        }
        return id;
    }
    
    public synchronized void addReputationScore(final int value, final boolean save) {
        this.setReputationScore(this.data.getReputation() + value, save);
    }
    
    public synchronized void takeReputationScore(final int value, final boolean save) {
        this.setReputationScore(this.data.getReputation() - value, save);
    }
    
    private void setReputationScore(int value, final boolean save) {
        if (this.data.getReputation() >= 0 && value < 0) {
            this.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.SINCE_THE_CLAN_REPUTATION_HAS_DROPPED_BELOW_0_YOUR_CLAN_SKILL_S_WILL_BE_DE_ACTIVATED));
            for (final ClanMember member : this.members.values()) {
                if (member.isOnline() && member.getPlayerInstance() != null) {
                    this.skillsStatus(member.getPlayerInstance(), true);
                }
            }
        }
        else if (this.data.getReputation() < 0 && value >= 0) {
            this.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_SKILLS_WILL_NOW_BE_ACTIVATED_SINCE_THE_CLAN_REPUTATION_IS_1_OR_HIGHER));
            for (final ClanMember member : this.members.values()) {
                if (member.isOnline() && member.getPlayerInstance() != null) {
                    this.skillsStatus(member.getPlayerInstance(), false);
                }
            }
        }
        if (value > 100000000) {
            value = 100000000;
        }
        else if (value < -100000000) {
            value = -100000000;
        }
        this.data.setReputation(value);
        this.broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
        if (save) {
            this.updateInDB();
        }
    }
    
    public int getReputationScore() {
        return this.data.getReputation();
    }
    
    public int getRank() {
        return this._rank;
    }
    
    public void setRank(final int rank) {
        this._rank = rank;
    }
    
    public boolean checkClanJoinCondition(final Player activeChar, final Player target, final int pledgeType) {
        if (activeChar == null) {
            return false;
        }
        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_JOIN_CLAN)) {
            activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }
        if (target == null) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return false;
        }
        if (activeChar.getObjectId() == target.getObjectId()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN);
            return false;
        }
        if (this.data.getCharPenaltyExpiryTime() > System.currentTimeMillis()) {
            activeChar.sendPacket(SystemMessageId.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
            return false;
        }
        if (target.getClanId() != 0) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ALREADY_A_MEMBER_OF_ANOTHER_CLAN);
            sm.addString(target.getName());
            activeChar.sendPacket(sm);
            return false;
        }
        if (target.getClanJoinExpiryTime() > System.currentTimeMillis()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN);
            sm.addString(target.getName());
            activeChar.sendPacket(sm);
            return false;
        }
        if ((target.getLevel() > 40 || target.getClassId().level() >= 2) && pledgeType == -1) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DOES_NOT_MEET_THE_REQUIREMENTS_TO_JOIN_A_CLAN_ACADEMY);
            sm.addString(target.getName());
            activeChar.sendPacket(sm);
            activeChar.sendPacket(SystemMessageId.IN_ORDER_TO_JOIN_THE_CLAN_ACADEMY_YOU_MUST_BE_UNAFFILIATED_WITH_A_CLAN_AND_BE_AN_UNAWAKENED_CHARACTER_LV_84_OR_BELOW_FPR_BOTH_MAIN_AND_SUBCLASS);
            return false;
        }
        if (this.getSubPledgeMembersCount(pledgeType) >= this.getMaxNrOfMembers(pledgeType)) {
            if (pledgeType == 0) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME);
                sm.addString(this.data.getName());
                activeChar.sendPacket(sm);
            }
            else {
                activeChar.sendPacket(SystemMessageId.THE_CLAN_IS_FULL);
            }
            return false;
        }
        return true;
    }
    
    public boolean checkAllyJoinCondition(final Player activeChar, final Player target) {
        if (activeChar == null) {
            return false;
        }
        if (activeChar.getAllyId() == 0 || !activeChar.isClanLeader() || activeChar.getClanId() != activeChar.getAllyId()) {
            activeChar.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return false;
        }
        final Clan leaderClan = activeChar.getClan();
        if (leaderClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis() && leaderClan.getAllyPenaltyType() == 3) {
            activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_ACCEPT_ANY_CLAN_WITHIN_A_DAY_AFTER_EXPELLING_ANOTHER_CLAN);
            return false;
        }
        if (target == null) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return false;
        }
        if (activeChar.getObjectId() == target.getObjectId()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN);
            return false;
        }
        if (target.getClan() == null) {
            activeChar.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
            return false;
        }
        if (!target.isClanLeader()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
            sm.addString(target.getName());
            activeChar.sendPacket(sm);
            return false;
        }
        final Clan targetClan = target.getClan();
        if (target.getAllyId() != 0) {
            final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE);
            sm2.addString(targetClan.getName());
            sm2.addString(targetClan.getAllyName());
            activeChar.sendPacket(sm2);
            return false;
        }
        if (targetClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis()) {
            if (targetClan.getAllyPenaltyType() == 1) {
                final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_ALLIANCE);
                sm2.addString(target.getClan().getName());
                sm2.addString(target.getClan().getAllyName());
                activeChar.sendPacket(sm2);
                return false;
            }
            if (targetClan.getAllyPenaltyType() == 2) {
                activeChar.sendPacket(SystemMessageId.A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION);
                return false;
            }
        }
        if (activeChar.isInsideZone(ZoneType.SIEGE) && target.isInsideZone(ZoneType.SIEGE)) {
            activeChar.sendPacket(SystemMessageId.THE_OPPOSING_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE);
            return false;
        }
        if (leaderClan.isAtWarWith(targetClan.getId())) {
            activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_CURRENTLY_AT_WAR_WITH_THAT_WOULD_BE_DIABOLICAL_AND_TREACHEROUS);
            return false;
        }
        if (ClanTable.getInstance().getClanAllies(activeChar.getAllyId()).size() >= Config.ALT_MAX_NUM_OF_CLANS_IN_ALLY) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_LIMIT);
            return false;
        }
        return true;
    }
    
    public long getAllyPenaltyExpiryTime() {
        return this.data.getAllyPenaltyExpiryTime();
    }
    
    public int getAllyPenaltyType() {
        return this.data.getAllyPenaltyType();
    }
    
    public void setAllyPenaltyExpiryTime(final long expiryTime, final int penaltyType) {
        this.data.setAllyPenaltyExpiryTime(expiryTime);
        this.data.setAllyPenaltyType(penaltyType);
    }
    
    public long getCharPenaltyExpiryTime() {
        return this.data.getCharPenaltyExpiryTime();
    }
    
    public void setCharPenaltyExpiryTime(final long time) {
        this.data.setCharPenaltyExpiryTime(time);
    }
    
    public long getDissolvingExpiryTime() {
        return this.data.getDissolvingExpiryTime();
    }
    
    public void setDissolvingExpiryTime(final long time) {
        this.data.setDissolvingExpiryTime(time);
    }
    
    public void createAlly(final Player player, final String allyName) {
        if (null == player) {
            return;
        }
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
            return;
        }
        if (this.data.getAllyId() != 0) {
            player.sendPacket(SystemMessageId.YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE);
            return;
        }
        if (this.getLevel() < 5) {
            player.sendPacket(SystemMessageId.TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
            return;
        }
        if (this.data.getAllyPenaltyExpiryTime() > System.currentTimeMillis() && this.getAllyPenaltyType() == 4) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_OF_DISSOLUTION);
            return;
        }
        if (this.data.getDissolvingExpiryTime() > System.currentTimeMillis()) {
            player.sendPacket(SystemMessageId.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_NO_ALLIANCE_CAN_BE_CREATED);
            return;
        }
        if (!Util.isAlphaNumeric(allyName)) {
            player.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME_PLEASE_TRY_AGAIN);
            return;
        }
        if (allyName.length() > 16 || allyName.length() < 2) {
            player.sendPacket(SystemMessageId.INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME);
            return;
        }
        if (ClanTable.getInstance().isAllyExists(allyName)) {
            player.sendPacket(SystemMessageId.THAT_ALLIANCE_NAME_ALREADY_EXISTS);
            return;
        }
        this.setAllyId(this.data.getId());
        this.setAllyName(allyName.trim());
        this.setAllyPenaltyExpiryTime(0L, 0);
        this.updateClanInDB();
        player.sendPacket(new UserInfo(player));
        player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, allyName));
    }
    
    public void dissolveAlly(final Player player) {
        if (this.data.getAllyId() == 0) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
            return;
        }
        if (!player.isClanLeader() || this.data.getId() != this.data.getAllyId()) {
            player.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }
        if (player.isInsideZone(ZoneType.SIEGE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_AN_ALLIANCE_WHILE_AN_AFFILIATED_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE);
            return;
        }
        this.broadcastToOnlineAllyMembers(SystemMessage.getSystemMessage(SystemMessageId.THE_ALLIANCE_HAS_BEEN_DISSOLVED));
        final long currentTime = System.currentTimeMillis();
        for (final Clan clan : ClanTable.getInstance().getClanAllies(this.getAllyId())) {
            if (clan.getId() != this.getId()) {
                clan.setAllyId(0);
                clan.setAllyName(null);
                clan.setAllyPenaltyExpiryTime(0L, 0);
                clan.updateClanInDB();
            }
        }
        this.setAllyId(0);
        this.setAllyName(null);
        this.changeAllyCrest(0, false);
        this.setAllyPenaltyExpiryTime(currentTime + Config.ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED * 86400000, 4);
        this.updateClanInDB();
    }
    
    public boolean levelUpClan(final Player player) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }
        if (System.currentTimeMillis() < this.data.getDissolvingExpiryTime()) {
            player.sendPacket(SystemMessageId.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOUR_CLAN_LEVEL_CANNOT_BE_INCREASED);
            return false;
        }
        boolean increaseClanLevel = false;
        switch (this.getLevel()) {
            case 0: {
                if (player.getSp() >= 1000L && player.getAdena() >= 150000L && this.members.size() >= 1 && player.reduceAdena("ClanLvl", 150000L, player.getTarget(), true)) {
                    player.setSp(player.getSp() - 1000L);
                    final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                    sp.addInt(1000);
                    player.sendPacket(sp);
                    increaseClanLevel = true;
                    break;
                }
                break;
            }
            case 1: {
                if (player.getSp() >= 15000L && player.getAdena() >= 300000L && this.members.size() >= 1 && player.reduceAdena("ClanLvl", 300000L, player.getTarget(), true)) {
                    player.setSp(player.getSp() - 15000L);
                    final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                    sp.addInt(15000);
                    player.sendPacket(sp);
                    increaseClanLevel = true;
                    break;
                }
                break;
            }
            case 2: {
                if (player.getSp() >= 100000L && player.getInventory().getItemByItemId(1419) != null && this.members.size() >= 1 && player.destroyItemByItemId("ClanLvl", 1419, 100L, player.getTarget(), true)) {
                    player.setSp(player.getSp() - 100000L);
                    final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                    sp.addInt(100000);
                    player.sendPacket(sp);
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                    sm.addItemName(1419);
                    player.sendPacket(sm);
                    increaseClanLevel = true;
                    break;
                }
                break;
            }
            case 3: {
                if (player.getSp() >= 1000000L && player.getInventory().getItemByItemId(1419) != null && this.members.size() >= 1 && player.destroyItemByItemId("ClanLvl", 1419, 5000L, player.getTarget(), true)) {
                    player.setSp(player.getSp() - 1000000L);
                    final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                    sp.addInt(1000000);
                    player.sendPacket(sp);
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                    sm.addItemName(1419);
                    player.sendPacket(sm);
                    increaseClanLevel = true;
                    break;
                }
                break;
            }
            case 4: {
                if (player.getSp() >= 5000000L && player.getInventory().getItemByItemId(1419) != null && this.members.size() >= 1 && player.destroyItemByItemId("ClanLvl", 1419, 10000L, player.getTarget(), true)) {
                    player.setSp(player.getSp() - 5000000L);
                    final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
                    sp.addInt(5000000);
                    player.sendPacket(sp);
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                    sm.addItemName(1419);
                    player.sendPacket(sm);
                    increaseClanLevel = true;
                    break;
                }
                break;
            }
            default: {
                return false;
            }
        }
        if (!increaseClanLevel) {
            player.sendPacket(SystemMessageId.THE_CONDITIONS_NECESSARY_TO_INCREASE_THE_CLAN_S_LEVEL_HAVE_NOT_BEEN_MET);
            return false;
        }
        final UserInfo ui = new UserInfo(player, false);
        ui.addComponentType(UserInfoType.CURRENT_HPMPCP_EXP_SP);
        player.sendPacket(ui);
        player.sendItemList();
        this.changeLevel(this.getLevel() + 1);
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLvlUp(player, this), new ListenersContainer[0]);
        return true;
    }
    
    public void changeLevel(final int level) {
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).updateClanLevel(this.data.getId(), level);
        this.setLevel(level);
        if (this.leader.isOnline()) {
            final Player leader = this.leader.getPlayerInstance();
            if (level > 4) {
                SiegeManager.getInstance().addSiegeSkills(leader);
                leader.sendPacket(SystemMessageId.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION);
            }
            else {
                SiegeManager.getInstance().removeSiegeSkills(leader);
            }
        }
        this.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_S_LEVEL_HAS_INCREASED));
        this.broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
    }
    
    public void changeClanCrest(final int crestId) {
        if (this.data.getCrest() != 0) {
            CrestTable.getInstance().removeCrest(this.getCrestId());
        }
        this.setCrestId(crestId);
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).updateClanCrest(this.data.getId(), crestId);
        this.forEachOnlineMember(Player::broadcastUserInfo);
    }
    
    public void changeAllyCrest(final int crestId, final boolean onlyThisClan) {
        if (!onlyThisClan) {
            if (this.data.getAllyCrest() != 0) {
                CrestTable.getInstance().removeCrest(this.getAllyCrestId());
            }
            ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).updateAllyCrestByAlly(this.data.getAllyId(), crestId);
        }
        else {
            ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).updateAllyCrest(this.data.getId(), crestId);
        }
        if (onlyThisClan) {
            this.setAllyCrestId(crestId);
            for (final Player member : this.getOnlineMembers(0)) {
                member.broadcastUserInfo();
            }
        }
        else {
            for (final Clan clan : ClanTable.getInstance().getClanAllies(this.getAllyId())) {
                clan.setAllyCrestId(crestId);
                for (final Player member2 : clan.getOnlineMembers(0)) {
                    member2.broadcastUserInfo();
                }
            }
        }
    }
    
    public void changeLargeCrest(final int crestId) {
        if (this.data.getCrestLarge() != 0) {
            CrestTable.getInstance().removeCrest(this.getCrestLargeId());
        }
        this.setCrestLargeId(crestId);
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).updateClanCrestLarge(this.data.getId(), crestId);
        this.forEachOnlineMember(Player::broadcastUserInfo);
    }
    
    public boolean isLearnableSubSkill(final int skillId, final int skillLevel) {
        Skill current = (Skill)this._subPledgeSkills.get(skillId);
        if (current != null && current.getLevel() + 1 == skillLevel) {
            return true;
        }
        if (current == null && skillLevel == 1) {
            return true;
        }
        for (final SubPledgeData subunit : this.subPledges.values()) {
            if (subunit.getId() == -1) {
                continue;
            }
            current = subunit.getSkill(skillId);
            if (current != null && current.getLevel() + 1 == skillLevel) {
                return true;
            }
            if (current == null && skillLevel == 1) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isLearnableSubPledgeSkill(final Skill skill, final int subType) {
        if (subType == -1) {
            return false;
        }
        final int id = skill.getId();
        Skill current;
        if (subType == 0) {
            current = (Skill)this._subPledgeSkills.get(id);
        }
        else {
            current = ((SubPledgeData)this.subPledges.get(subType)).getSkill(id);
        }
        return (current != null && current.getLevel() + 1 == skill.getLevel()) || (current == null && skill.getLevel() == 1);
    }
    
    public PledgeSkillList.SubPledgeSkill[] getAllSubSkills() {
        final List<PledgeSkillList.SubPledgeSkill> list = new LinkedList<PledgeSkillList.SubPledgeSkill>();
        for (final Skill skill : this._subPledgeSkills.values()) {
            list.add(new PledgeSkillList.SubPledgeSkill(0, skill.getId(), skill.getLevel()));
        }
        for (final SubPledgeData subunit : this.subPledges.values()) {
            for (final Skill skill2 : subunit.getSkills()) {
                list.add(new PledgeSkillList.SubPledgeSkill(subunit.getId(), skill2.getId(), skill2.getLevel()));
            }
        }
        return list.toArray(PledgeSkillList.SubPledgeSkill[]::new);
    }
    
    public void setNewLeaderId(final int objectId, final boolean storeInDb) {
        this.data.setNewLeader(objectId);
        if (storeInDb) {
            this.updateClanInDB();
        }
    }
    
    public int getNewLeaderId() {
        return this.data.getNewLeaderId();
    }
    
    public void setNewLeader(final ClanMember member) {
        final Player newLeader = member.getPlayerInstance();
        final ClanMember exMember = this.leader;
        final Player exLeader = exMember.getPlayerInstance();
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLeaderChange(exMember, member, this), new ListenersContainer[0]);
        if (exLeader != null) {
            if (exLeader.isFlying()) {
                exLeader.dismount();
            }
            if (this.getLevel() >= SiegeManager.getInstance().getSiegeClanMinLevel()) {
                SiegeManager.getInstance().removeSiegeSkills(exLeader);
            }
            exLeader.getClanPrivileges().clear();
            exLeader.broadcastUserInfo();
        }
        else {
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updateClanPrivs(this.getLeaderId(), 0);
        }
        this.setLeader(member);
        if (this.data.getNewLeaderId() != 0) {
            this.setNewLeaderId(0, true);
        }
        this.updateClanInDB();
        if (exLeader != null) {
            exLeader.setPledgeClass(ClanMember.calculatePledgeClass(exLeader));
            exLeader.broadcastUserInfo();
            exLeader.checkItemRestriction();
        }
        if (newLeader != null) {
            newLeader.setPledgeClass(ClanMember.calculatePledgeClass(newLeader));
            newLeader.getClanPrivileges().setAll();
            if (this.getLevel() >= SiegeManager.getInstance().getSiegeClanMinLevel()) {
                SiegeManager.getInstance().addSiegeSkills(newLeader);
            }
            newLeader.broadcastUserInfo();
        }
        else {
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updateClanPrivs(this.getLeaderId(), EnumIntBitmask.getAllBitmask(ClanPrivilege.class));
        }
        this.broadcastClanStatus();
        this.broadcastToOnlineMembers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEADER_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_C1)).addString(member.getName()));
        Clan.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getName(), member.getName(), exMember.getName()));
    }
    
    public String getNewLeaderName() {
        return PlayerNameTable.getInstance().getNameById(this.data.getNewLeaderId());
    }
    
    public int getSiegeKills() {
        return this._siegeKills.get();
    }
    
    public int getSiegeDeaths() {
        return this._siegeDeaths.get();
    }
    
    public int addSiegeKill() {
        return this._siegeKills.incrementAndGet();
    }
    
    public int addSiegeDeath() {
        return this._siegeDeaths.incrementAndGet();
    }
    
    public void clearSiegeKills() {
        this._siegeKills.set(0);
    }
    
    public void clearSiegeDeaths() {
        this._siegeDeaths.set(0);
    }
    
    public int getWarCount() {
        return this._atWarWith.size();
    }
    
    public void addWar(final int clanId, final ClanWar war) {
        this._atWarWith.put(clanId, (Object)war);
    }
    
    public void deleteWar(final int clanId) {
        this._atWarWith.remove(clanId);
    }
    
    public ClanWar getWarWith(final int clanId) {
        return (ClanWar)this._atWarWith.get(clanId);
    }
    
    public synchronized void addMemberOnlineTime(final Player player) {
        final ClanMember clanMember = this.getClanMember(player.getObjectId());
        if (clanMember != null) {
            clanMember.setOnlineTime(clanMember.getOnlineTime() + 60000L);
            if (clanMember.getOnlineTime() == 1800000L) {
                this.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(clanMember));
            }
        }
        final ClanRewardBonus availableBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(this);
        if (availableBonus != null) {
            if (this._lastMembersOnlineBonus == null) {
                this._lastMembersOnlineBonus = availableBonus;
                this.broadcastToOnlineMembers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_LOGIN_BONUS_LV_S1)).addByte(availableBonus.getLevel()));
            }
            else if (this._lastMembersOnlineBonus.getLevel() < availableBonus.getLevel()) {
                this._lastMembersOnlineBonus = availableBonus;
                this.broadcastToOnlineMembers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_LOGIN_BONUS_LV_S1)).addByte(availableBonus.getLevel()));
            }
        }
        final int currentMaxOnline = (int)this.members.values().stream().filter(member -> member.getOnlineTime() > Config.ALT_CLAN_MEMBERS_TIME_FOR_BONUS).count();
        if (this.getMaxOnlineMembers() < currentMaxOnline) {
            this.data.setMaxOnlineMembers(currentMaxOnline);
        }
    }
    
    public synchronized void addHuntingPoints(final Player activeChar, final Npc target, final double value) {
        final int points = (int)value / 2960;
        if (points > 0) {
            this.data.addHuntingPoints(points);
            final ClanRewardBonus availableBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(this);
            if (availableBonus != null) {
                if (this._lastHuntingBonus == null) {
                    this._lastHuntingBonus = availableBonus;
                    this.broadcastToOnlineMembers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_HUNTING_BONUS_LV_S1)).addByte(availableBonus.getLevel()));
                }
                else if (this._lastHuntingBonus.getLevel() < availableBonus.getLevel()) {
                    this._lastHuntingBonus = availableBonus;
                    this.broadcastToOnlineMembers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ACHIEVED_HUNTING_BONUS_LV_S1)).addByte(availableBonus.getLevel()));
                }
            }
        }
    }
    
    public int getMaxOnlineMembers() {
        return this.data.getMaxOnlineMember();
    }
    
    public int getHuntingPoints() {
        return this.data.getHuntingPoints();
    }
    
    public int getPreviousMaxOnlinePlayers() {
        return this.data.getPrevMaxOnlineMember();
    }
    
    public int getPreviousHuntingPoints() {
        return this.data.getPrevHuntingPoints();
    }
    
    public boolean canClaimBonusReward(final Player player, final ClanRewardType type) {
        final ClanMember clanMember = this.getClanMember(player.getObjectId());
        return clanMember != null && type.getAvailableBonus(this) != null && !clanMember.isRewardClaimed(type);
    }
    
    public void resetClanBonus() {
        this.data.setPrevMaxOnlineMember(this.data.getMaxOnlineMember());
        this.data.setPrevHuntingPoints(this.data.getHuntingPoints());
        this.data.setHuntingPoints(0);
        this.data.setMaxOnlineMembers(0);
        this.members.values().forEach(ClanMember::resetBonus);
        this.broadcastToOnlineMembers(ExPledgeBonusMarkReset.STATIC_PACKET);
    }
    
    public int getArenaProgress() {
        return GlobalVariablesManager.getInstance().getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getId()), 0);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Clan.class);
    }
    
    public static class RankPrivs
    {
        private final int _rankId;
        private final int _party;
        private final EnumIntBitmask<ClanPrivilege> _rankPrivs;
        
        public RankPrivs(final int rank, final int party, final int privs) {
            this._rankId = rank;
            this._party = party;
            this._rankPrivs = new EnumIntBitmask<ClanPrivilege>(ClanPrivilege.class, privs);
        }
        
        public RankPrivs(final int rank, final int party, final EnumIntBitmask<ClanPrivilege> rankPrivs) {
            this._rankId = rank;
            this._party = party;
            this._rankPrivs = rankPrivs;
        }
        
        public int getRank() {
            return this._rankId;
        }
        
        public int getParty() {
            return this._party;
        }
        
        public EnumIntBitmask<ClanPrivilege> getPrivs() {
            return this._rankPrivs;
        }
        
        public void setPrivs(final int privs) {
            this._rankPrivs.setBitmask(privs);
        }
    }
}
