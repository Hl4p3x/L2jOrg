// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.network.serverpackets.attendance.ExVipAttendanceItemList;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.Clan;
import java.util.Iterator;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.ExAutoSoulShot;
import org.l2j.gameserver.network.serverpackets.mission.ExConnectedTimeAndGettableReward;
import org.l2j.gameserver.settings.AttendanceSettings;
import org.l2j.gameserver.network.serverpackets.ExWorldChatCnt;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.StatusUpdate;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;
import org.l2j.gameserver.network.serverpackets.ExBeautyItemList;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.network.serverpackets.ExNotifyPremiumItem;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.instancemanager.ServerRestartManager;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.data.database.announce.manager.AnnouncementsManager;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.IntFunction;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.friend.FriendListPacket;
import org.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import org.l2j.gameserver.network.serverpackets.ExPCCafePointInfo;
import org.l2j.gameserver.model.entity.Event;
import org.l2j.gameserver.network.serverpackets.ExRotation;
import org.l2j.gameserver.network.serverpackets.ExDressRoomUiOpen;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;
import org.l2j.gameserver.network.serverpackets.ShortCutInit;
import org.l2j.gameserver.network.serverpackets.ExBloodyCoinCount;
import org.l2j.gameserver.network.serverpackets.ExAdenaInvenCount;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeWaitingListAlarm;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.network.serverpackets.PledgeSkillList;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeCount;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.HennaInfo;
import org.l2j.gameserver.network.serverpackets.ExCastleState;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.serverpackets.ExQuestItemList;
import org.l2j.gameserver.network.serverpackets.item.ItemList;
import org.l2j.gameserver.network.serverpackets.ExGetBookMarkInfoPacket;
import org.l2j.gameserver.network.serverpackets.ExEnterWorld;
import org.l2j.gameserver.network.serverpackets.ExVitalityEffectInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class EnterWorld extends ClientPacket
{
    private static final Logger LOGGER;
    private final int[][] tracert;
    
    public EnterWorld() {
        this.tracert = new int[5][4];
    }
    
    public void readImpl() {
        for (int i = 0; i < 5; ++i) {
            for (int o = 0; o < 4; ++o) {
                this.tracert[i][o] = this.readByte();
            }
        }
        this.readInt();
        this.readInt();
        this.readInt();
        this.readInt();
        this.readBytes(new byte[64]);
        this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            EnterWorld.LOGGER.warn("EnterWorld failed! player returned 'null'.");
            Disconnection.of((GameClient)this.client).defaultSequence(false);
            return;
        }
        ((GameClient)this.client).setConnectionState(ConnectionState.IN_GAME);
        ((GameClient)this.client).setClientTracert(this.tracert);
        if (Config.RESTORE_PLAYER_INSTANCE) {
            final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
            if (instance != null && instance.getId() == player.getInstanceRestore()) {
                player.setInstance(instance);
            }
            player.setInstanceRestore(-1);
        }
        player.updatePvpTitleAndColor(false);
        if (player.isGM()) {
            this.onGameMasterEnter(player);
        }
        if (player.isChatBanned()) {
            player.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.NO_CHAT);
        }
        if (player.getCurrentHp() < 0.5) {
            player.setIsDead(true);
        }
        if (Config.ENABLE_VITALITY) {
            player.sendPacket(new ExVitalityEffectInfo(player));
        }
        ((GameClient)this.client).sendPacket(new ExEnterWorld());
        player.getMacros().sendAllMacros();
        ((GameClient)this.client).sendPacket(new ExGetBookMarkInfoPacket(player));
        ItemList.sendList(player);
        ((GameClient)this.client).sendPacket(new ExQuestItemList(1, player));
        ((GameClient)this.client).sendPacket(new ExQuestItemList(2, player));
        player.sendPacket(ExBasicActionList.STATIC_PACKET);
        for (final Castle castle : CastleManager.getInstance().getCastles()) {
            player.sendPacket(new ExCastleState(castle));
        }
        player.sendPacket(new HennaInfo(player));
        player.sendSkillList();
        player.sendPacket(new EtcStatusUpdate(player));
        boolean showClanNotice = false;
        final Clan clan = player.getClan();
        if (clan != null) {
            this.notifyClanMembers(player);
            this.notifySponsorOrApprentice(player);
            for (final Siege siege : SiegeManager.getInstance().getSieges()) {
                if (!siege.isInProgress()) {
                    continue;
                }
                if (siege.checkIsAttacker(clan)) {
                    player.setSiegeState((byte)1);
                    player.setSiegeSide(siege.getCastle().getId());
                }
                else {
                    if (!siege.checkIsDefender(clan)) {
                        continue;
                    }
                    player.setSiegeState((byte)2);
                    player.setSiegeSide(siege.getCastle().getId());
                }
            }
            if (player.getClan().getCastleId() > 0) {
                CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(player);
            }
            showClanNotice = clan.isNoticeEnabled();
            clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(player));
            PledgeShowMemberListAll.sendAllTo(player);
            clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
            player.sendPacket(new PledgeSkillList(clan));
            final ClanHall ch = ClanHallManager.getInstance().getClanHallByClan(clan);
            if (ch != null && ch.getCostFailDay() > 0) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
                sm.addInt(ch.getLease());
                player.sendPacket(sm);
            }
        }
        else {
            player.sendPacket(ExPledgeWaitingListAlarm.STATIC_PACKET);
        }
        ((GameClient)this.client).sendPacket(new ExSubjobInfo(player, SubclassInfoType.NO_CHANGES));
        ((GameClient)this.client).sendPacket(new ExUserInfoInvenWeight(player));
        ((GameClient)this.client).sendPacket(new ExAdenaInvenCount(player));
        ((GameClient)this.client).sendPacket(new ExBloodyCoinCount());
        ((GameClient)this.client).sendPacket(new ShortCutInit());
        player.forEachShortcut(s -> {
            if (s.isActive()) {
                ((GameClient)this.client).sendPacket(new ExActivateAutoShortcut(s.getClientId(), true));
            }
            return;
        });
        ((GameClient)this.client).sendPacket(new ExDressRoomUiOpen());
        if (Config.PLAYER_SPAWN_PROTECTION > 0) {
            player.setSpawnProtection(true);
        }
        player.spawnMe();
        player.sendPacket(new ExRotation(player.getObjectId(), player.getHeading()));
        if (Event.isParticipant(player)) {
            Event.restorePlayerEventStatus(player);
        }
        if (Config.PC_CAFE_ENABLED) {
            if (player.getPcCafePoints() > 0) {
                player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), 0, 1));
            }
            else {
                player.sendPacket(new ExPCCafePointInfo());
            }
        }
        player.sendPacket(new ExStorageMaxCount(player));
        ((GameClient)this.client).sendPacket(new FriendListPacket(player));
        final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOUR_FRIEND_S1_JUST_LOGGED_IN).addString(player.getName());
        final World world = World.getInstance();
        final IntStream stream = player.getFriendList().stream();
        final World obj = world;
        Objects.requireNonNull(obj);
        final Stream<Object> filter = stream.mapToObj((IntFunction<?>)obj::findPlayer).filter(Objects::nonNull);
        final SystemMessage obj2 = sm2;
        Objects.requireNonNull(obj2);
        filter.forEach((Consumer<? super Object>)obj2::sendTo);
        player.sendPacket(SystemMessageId.WELCOME_TO_THE_WORLD);
        AnnouncementsManager.getInstance().showAnnouncements(player);
        if (((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).scheduleRestart() && Config.SERVER_RESTART_SCHEDULE_MESSAGE) {
            player.sendPacket(new CreatureSay(2, ChatType.BATTLEFIELD, "[SERVER]", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ServerRestartManager.getInstance().getNextRestartTime())));
        }
        if (showClanNotice) {
            final NpcHtmlMessage notice = new NpcHtmlMessage();
            notice.setFile(player, "data/html/clanNotice.htm");
            notice.replace("%clan_name%", player.getClan().getName());
            notice.replace("%notice_text%", player.getClan().getNotice().replaceAll("\r\n", "<br>"));
            notice.disableValidation();
            ((GameClient)this.client).sendPacket(notice);
        }
        else if (Config.SERVER_NEWS) {
            final String serverNews = HtmCache.getInstance().getHtm(player, "data/html/servnews.htm");
            if (serverNews != null) {
                ((GameClient)this.client).sendPacket(new NpcHtmlMessage(serverNews));
            }
        }
        if (Config.PETITIONING_ALLOWED) {
            PetitionManager.getInstance().checkPetitionMessages(player);
        }
        ((GameClient)this.client).sendPacket(new SkillCoolTime(player));
        ((GameClient)this.client).sendPacket(new ExVoteSystemInfo(player));
        for (final Item item : player.getInventory().getItems()) {
            if (item.isTimeLimitedItem()) {
                item.scheduleLifeTimeTask();
            }
        }
        for (final Item whItem : player.getWarehouse().getItems()) {
            if (whItem.isTimeLimitedItem()) {
                whItem.scheduleLifeTimeTask();
            }
        }
        if (player.getClanJoinExpiryTime() > System.currentTimeMillis()) {
            player.sendPacket(SystemMessageId.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS);
        }
        if (!player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && player.isInsideZone(ZoneType.SIEGE) && (!player.isInSiege() || player.getSiegeState() < 2)) {
            player.teleToLocation(TeleportWhereType.TOWN);
        }
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail()) {
            MailEngine.getInstance().sendUnreadCount(player);
        }
        if (Config.WELCOME_MESSAGE_ENABLED) {
            player.sendPacket(new ExShowScreenMessage(Config.WELCOME_MESSAGE_TEXT, Config.WELCOME_MESSAGE_TIME));
        }
        if (!player.getPremiumItemList().isEmpty()) {
            player.sendPacket(ExNotifyPremiumItem.STATIC_PACKET);
        }
        if (BeautyShopData.getInstance().hasBeautyData(player.getRace(), player.getAppearance().getSexType())) {
            player.sendPacket(new ExBeautyItemList(player));
        }
        if (player.getActiveElementalSpiritType() >= 0) {
            ((GameClient)this.client).sendPacket(new ElementalSpiritInfo(player.getActiveElementalSpiritType(), (byte)2));
        }
        player.broadcastUserInfo();
        player.sendPacket(StatusUpdate.of(player, StatusUpdateType.CUR_HP, (int)player.getCurrentHp()).addUpdate(StatusUpdateType.MAX_HP, player.getMaxHp()));
        player.sendPacket(new ExUserInfoEquipSlot(player));
        if (((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).worldChatEnabled()) {
            player.sendPacket(new ExWorldChatCnt(player));
        }
        if (!player.getEffectList().getCurrentAbnormalVisualEffects().isEmpty()) {
            player.updateAbnormalVisualEffects();
        }
        if (((AttendanceSettings)Configurator.getSettings((Class)AttendanceSettings.class)).enabled()) {
            this.sendAttendanceInfo(player);
        }
        player.sendPacket(new ExConnectedTimeAndGettableReward(player));
        player.sendPacket(new ExAutoSoulShot(0, true, 0));
        player.sendPacket(new ExAutoSoulShot(0, true, 1));
        player.sendPacket(new ExAutoSoulShot(0, true, 2));
        player.sendPacket(new ExAutoSoulShot(0, true, 3));
        if (Config.HARDWARE_INFO_ENABLED) {
            ThreadPool.schedule(() -> {
                if (((GameClient)this.client).getHardwareInfo() == null) {
                    Disconnection.of((GameClient)this.client).defaultSequence(false);
                }
                return;
            }, 5000L);
        }
        if (player.isInTimedHuntingZone()) {
            final long currentTime = System.currentTimeMillis();
            final long pirateTombExitTime = player.getHuntingZoneResetTime(2);
            if (pirateTombExitTime > currentTime && player.isInTimedHuntingZone(2)) {
                player.startTimedHuntingZone(1, pirateTombExitTime - currentTime);
            }
            else {
                player.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN));
            }
        }
        player.onEnter();
        Quest.playerEnter(player);
    }
    
    private void sendAttendanceInfo(final Player player) {
        final AttendanceSettings attendanceSettings = (AttendanceSettings)Configurator.getSettings((Class)AttendanceSettings.class);
        int lastRewardIndex;
        final AttendanceSettings attendanceSettings2;
        ThreadPool.schedule(() -> {
            if (player.canReceiveAttendance()) {
                lastRewardIndex = player.lastAttendanceReward() + 1;
                player.sendPacket(new ExShowScreenMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, lastRewardIndex), 2, 7000, 0, true, true));
                player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, lastRewardIndex));
                player.sendMessage("Click on General Menu -> Attendance Check.");
                if (attendanceSettings2.popUpWindow()) {
                    player.sendPacket(new ExVipAttendanceItemList(player));
                }
            }
        }, (long)attendanceSettings.delay(), TimeUnit.MINUTES);
    }
    
    private void onGameMasterEnter(final Player activeChar) {
        if (Config.GM_GIVE_SPECIAL_SKILLS) {
            SkillTreesData.getInstance().addSkills(activeChar, false);
        }
        if (Config.GM_GIVE_SPECIAL_AURA_SKILLS) {
            SkillTreesData.getInstance().addSkills(activeChar, true);
        }
        AdminData.getInstance().addGm(activeChar, !Config.GM_STARTUP_AUTO_LIST || !AdminData.getInstance().hasAccess("admin_gmliston", activeChar.getAccessLevel()));
        if (Config.GM_STARTUP_BUILDER_HIDE && AdminData.getInstance().hasAccess("admin_hide", activeChar.getAccessLevel())) {
            BuilderUtil.setHiding(activeChar, true);
            BuilderUtil.sendSysMessage(activeChar, "hide is default for builder.");
            BuilderUtil.sendSysMessage(activeChar, "FriendAddOff is default for builder.");
            BuilderUtil.sendSysMessage(activeChar, "whisperoff is default for builder.");
            return;
        }
        if (Config.GM_STARTUP_INVULNERABLE && AdminData.getInstance().hasAccess("admin_invul", activeChar.getAccessLevel())) {
            activeChar.setIsInvul(true);
        }
        if (Config.GM_STARTUP_INVISIBLE && AdminData.getInstance().hasAccess("admin_invisible", activeChar.getAccessLevel())) {
            activeChar.setInvisible(true);
            activeChar.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.STEALTH);
        }
        if (Config.GM_STARTUP_SILENCE && AdminData.getInstance().hasAccess("admin_silence", activeChar.getAccessLevel())) {
            activeChar.setSilenceMode(true);
        }
        if (Config.GM_STARTUP_DIET_MODE && AdminData.getInstance().hasAccess("admin_diet", activeChar.getAccessLevel())) {
            activeChar.setDietMode(true);
            activeChar.refreshOverloaded(true);
        }
    }
    
    private void notifyClanMembers(final Player activeChar) {
        final Clan clan = activeChar.getClan();
        if (clan != null) {
            clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME);
            msg.addString(activeChar.getName());
            clan.broadcastToOtherOnlineMembers(msg, activeChar);
            clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(activeChar), activeChar);
        }
    }
    
    private void notifySponsorOrApprentice(final Player activeChar) {
        if (activeChar.getSponsor() != 0) {
            final Player sponsor = World.getInstance().findPlayer(activeChar.getSponsor());
            if (sponsor != null) {
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN);
                msg.addString(activeChar.getName());
                sponsor.sendPacket(msg);
            }
        }
        else if (activeChar.getApprentice() != 0) {
            final Player apprentice = World.getInstance().findPlayer(activeChar.getApprentice());
            if (apprentice != null) {
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_C1_HAS_LOGGED_IN);
                msg.addString(activeChar.getName());
                apprentice.sendPacket(msg);
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EnterWorld.class);
    }
}
