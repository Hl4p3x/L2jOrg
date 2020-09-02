// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.datatables;

import org.xml.sax.Attributes;
import java.util.PrimitiveIterator;
import org.l2j.gameserver.model.Clan;
import io.github.joealisson.primitive.HashIntMap;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.network.serverpackets.captcha.ReceiveBotCaptchaResult;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.commons.threading.ThreadPool;
import java.util.Calendar;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.captcha.Captcha;
import org.l2j.gameserver.network.serverpackets.captcha.ReceiveBotCaptchaImage;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.impl.CaptchaRequest;
import org.l2j.gameserver.engine.captcha.CaptchaEngine;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.database.DAO;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.data.database.data.BotReportData;
import java.time.LocalDateTime;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.BotReportDAO;
import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.Config;
import java.util.Map;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public final class ReportTable
{
    public static final int ATTACK_ACTION_BLOCK_ID = -1;
    public static final int TRADE_ACTION_BLOCK_ID = -2;
    public static final int PARTY_ACTION_BLOCK_ID = -3;
    public static final int ACTION_BLOCK_ID = -4;
    public static final int CHAT_BLOCK_ID = -5;
    private static final Logger LOGGER;
    private static final String REPORT_TYPE_ADENA_ADS = "ADENA_ADS";
    private IntMap<BotReportedCharData> reports;
    private IntMap<ReporterCharData> reporters;
    private Map<Integer, Long> ipRegistry;
    private Map<Integer, PunishHolder> _punishments;
    
    private ReportTable() {
        if (Config.BOTREPORT_ENABLE) {
            this.reports = (IntMap<BotReportedCharData>)new CHashIntMap();
            this.ipRegistry = new HashMap<Integer, Long>();
            this.reporters = (IntMap<ReporterCharData>)new CHashIntMap();
            this._punishments = new ConcurrentHashMap<Integer, PunishHolder>();
            try {
                final File punishments = new File("./config/BotReportPunishments.xml");
                if (!punishments.exists()) {
                    throw new FileNotFoundException(punishments.getName());
                }
                final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(punishments, new PunishmentsLoader());
            }
            catch (Exception e) {
                ReportTable.LOGGER.warn("Could not load punishments from /config/BotReportPunishments.xml", (Throwable)e);
            }
            this.loadReportedCharData();
            this.scheduleResetPointTask();
        }
    }
    
    private static boolean timeHasPassed(final Map<Integer, Long> map, final int objectId) {
        return !map.containsKey(objectId) || System.currentTimeMillis() - map.get(objectId) > Config.BOTREPORT_REPORT_DELAY;
    }
    
    private void loadReportedCharData() {
        final BotReportDAO botReportDao = (BotReportDAO)DatabaseAccess.getDAO((Class)BotReportDAO.class);
        final LocalDateTime yesterday = LocalDateTime.now().minusDays(1L);
        botReportDao.removeExpiredReports(yesterday);
        final List<BotReportData> reportsData = botReportDao.findAll();
        for (final BotReportData report : reportsData) {
            ((BotReportedCharData)this.reports.computeIfAbsent(report.getBotId(), bot -> new BotReportedCharData())).addReporter(report);
            ((ReporterCharData)this.reporters.computeIfAbsent(report.getReporterId(), reporter -> new ReporterCharData())).decreaseReportPoints();
        }
        ReportTable.LOGGER.info("Loaded {} bot reports", (Object)this.reports.size());
    }
    
    public void saveReportedCharData() {
        final BotReportDAO botReportDao = (BotReportDAO)DatabaseAccess.getDAO((Class)BotReportDAO.class);
        final DAO dao;
        final Iterable iterable;
        final Iterable iterable2;
        this.reports.values().forEach(r -> {
            r.adsReporters.values();
            Objects.requireNonNull((BotReportDAO)dao);
            iterable.forEach(dao::save);
            r.reporters.values();
            Objects.requireNonNull(dao);
            iterable2.forEach(dao::save);
        });
    }
    
    public boolean reportBot(final Player reporter) {
        final WorldObject target = reporter.getTarget();
        if (!GameUtils.isPlayer(target) || target.getObjectId() == reporter.getObjectId()) {
            return false;
        }
        final Player bot = (Player)target;
        if (bot.isInsideZone(ZoneType.PEACE) || bot.isInsideZone(ZoneType.PVP)) {
            reporter.sendPacket(SystemMessageId.YOU_CANNOT_REPORT_A_CHARACTER_WHO_IS_IN_A_PEACE_ZONE_OR_A_BATTLEGROUND);
            return false;
        }
        if (bot.isInOlympiadMode()) {
            reporter.sendPacket(SystemMessageId.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_YOU_CANNOT_MAKE_A_REPORT_WHILE_LOCATED_INSIDE_A_PEACE_ZONE_OR_A_BATTLEGROUND_WHILE_YOU_ARE_AN_OPPOSING_CLAN_MEMBER_DURING_A_CLAN_WAR_OR_WHILE_PARTICIPATING_IN_THE_OLYMPIAD);
            return false;
        }
        if (Objects.nonNull(bot.getClan()) && bot.getClan().isAtWarWith(reporter.getClan())) {
            reporter.sendPacket(SystemMessageId.YOU_CANNOT_REPORT_WHEN_A_CLAN_WAR_HAS_BEEN_DECLARED);
            return false;
        }
        if (bot.getExp() == bot.getStats().getStartingExp()) {
            reporter.sendPacket(SystemMessageId.YOU_CANNOT_REPORT_A_CHARACTER_WHO_HAS_NOT_ACQUIRED_ANY_XP_AFTER_CONNECTING);
            return false;
        }
        BotReportedCharData rcd = (BotReportedCharData)this.reports.get(bot.getObjectId());
        ReporterCharData rcdRep = (ReporterCharData)this.reporters.get(reporter.getObjectId());
        final int reporterId = reporter.getObjectId();
        final int ip = GameUtils.hashIp(reporter);
        if (!timeHasPassed(this.ipRegistry, ip)) {
            reporter.sendPacket(SystemMessageId.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_THE_TARGET_HAS_ALREADY_BEEN_REPORTED_BY_EITHER_YOUR_CLAN_OR_HAS_ALREADY_BEEN_REPORTED_FROM_YOUR_CURRENT_IP);
            return false;
        }
        if (Objects.nonNull(rcd)) {
            if (rcd.alredyReportedBy(reporterId)) {
                reporter.sendPacket(SystemMessageId.YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME);
                return false;
            }
            if (!Config.BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS && rcd.reportedBySameClan(reporter.getClan())) {
                reporter.sendPacket(SystemMessageId.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_THE_TARGET_HAS_ALREADY_BEEN_REPORTED_BY_EITHER_YOUR_CLAN_OR_HAS_ALREADY_BEEN_REPORTED_FROM_YOUR_CURRENT_IP);
                return false;
            }
        }
        if (Objects.nonNull(rcdRep)) {
            if (rcdRep.getPointsLeft() == 0) {
                reporter.sendPacket(SystemMessageId.YOU_VE_SPENT_ALL_POINTS_THE_POINTS_WILL_BE_RESET_AT_06_30_SO_THAT_YOU_CAN_USE_THEM_AGAIN);
                return false;
            }
            final long reuse = System.currentTimeMillis() - rcdRep.getLastReporTime();
            if (reuse < Config.BOTREPORT_REPORT_DELAY) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_MAKE_ANOTHER_REPORT_IN_S1_MINUTE_S_YOU_HAVE_S2_POINT_S_REMAINING_ON_THIS_ACCOUNT);
                sm.addInt((int)(reuse / 60000L));
                sm.addInt(rcdRep.getPointsLeft());
                reporter.sendPacket(sm);
                return false;
            }
        }
        final long curTime = System.currentTimeMillis();
        synchronized (this) {
            if (Objects.isNull(rcd)) {
                rcd = new BotReportedCharData();
                this.reports.put(bot.getObjectId(), (Object)rcd);
            }
            rcd.addReporter(bot.getObjectId(), reporterId, "BOT");
            if (Objects.isNull(rcdRep)) {
                rcdRep = new ReporterCharData();
            }
            rcdRep.registerReport(curTime);
            this.ipRegistry.put(ip, curTime);
            this.reporters.put(reporterId, (Object)rcdRep);
        }
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_WAS_REPORTED_AS_A_BOT);
        sm.addString(bot.getName());
        reporter.sendPacket(sm);
        sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_USED_A_REPORT_POINT_ON_C1_YOU_HAVE_S2_POINTS_REMAINING_ON_THIS_ACCOUNT);
        sm.addString(bot.getName());
        sm.addInt(rcdRep.getPointsLeft());
        reporter.sendPacket(sm);
        this.handleReport(bot, rcd);
        return true;
    }
    
    private void handleReport(final Player bot, final BotReportedCharData rcd) {
        this.punishBot(bot, this._punishments.get(rcd.getReportCount()));
        for (final int key : this._punishments.keySet()) {
            if (key < 0 && Math.abs(key) <= rcd.getReportCount()) {
                this.punishBot(bot, this._punishments.get(key));
            }
        }
        if (rcd.getReportCount() % 3 == 0) {
            final Captcha captcha = CaptchaEngine.getInstance().next();
            if (!bot.hasRequest(CaptchaRequest.class, (Class<? extends AbstractRequest>[])new Class[0])) {
                final CaptchaRequest request = new CaptchaRequest(bot, captcha);
                bot.addRequest(request);
                bot.sendPacket(new ReceiveBotCaptchaImage(captcha, request.getRemainingTime()));
                bot.sendPacket(SystemMessageId.PLEASE_ENTER_THE_AUTHENTICATION_CODE_IN_TIME_TO_CONTINUE_PLAYING);
            }
        }
    }
    
    private void punishBot(final Player bot, final PunishHolder ph) {
        if (Objects.nonNull(ph)) {
            ph._punish.applyEffects(bot, bot);
            if (ph._systemMessageId > -1) {
                final SystemMessageId id = SystemMessageId.getSystemMessageId(ph._systemMessageId);
                if (Objects.nonNull(id)) {
                    bot.sendPacket(id);
                }
            }
        }
    }
    
    private void addPunishment(final int neededReports, final int skillId, final int skillLevel, final int sysMsg) {
        final Skill sk = SkillEngine.getInstance().getSkill(skillId, skillLevel);
        if (Objects.nonNull(sk)) {
            this._punishments.put(neededReports, new PunishHolder(sk, sysMsg));
        }
        else {
            ReportTable.LOGGER.warn("Could not add punishment for {} report(s): Skill {} - {} does not exist!", new Object[] { neededReports, skillId, skillLevel });
        }
    }
    
    private void resetPointsAndSchedule() {
        this.reporters.values().forEach(r -> r.setPoints(7));
        this.scheduleResetPointTask();
    }
    
    private void scheduleResetPointTask() {
        try {
            final String[] hour = Config.BOTREPORT_RESETPOINT_HOUR;
            final Calendar c = Calendar.getInstance();
            c.set(11, Integer.parseInt(hour[0]));
            c.set(12, Integer.parseInt(hour[1]));
            if (System.currentTimeMillis() > c.getTimeInMillis()) {
                c.set(6, c.get(6) + 1);
            }
            ThreadPool.schedule((Runnable)new ResetPointTask(), c.getTimeInMillis() - System.currentTimeMillis());
        }
        catch (Exception e) {
            ThreadPool.schedule((Runnable)new ResetPointTask(), 86400000L);
            ReportTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
    }
    
    public void punishBotDueUnsolvedCaptcha(final Player bot) {
        CommonSkill.BOT_REPORT_STATUS.getSkill().applyEffects(bot, bot);
        bot.removeRequest(CaptchaRequest.class);
        final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.IF_A_USER_ENTERS_A_WRONG_AUTHENTICATION_CODE_3_TIMES_IN_A_ROW_OR_DOES_NOT_ENTER_THE_CODE_IN_TIME_THE_SYSTEM_WILL_QUALIFY_HIM_AS_A_RULE_BREAKER_AND_CHARGE_HIS_ACCOUNT_WITH_A_PENALTY_S1);
        msg.addSkillName(CommonSkill.BOT_REPORT_STATUS.getId());
        bot.sendPacket(msg);
        bot.sendPacket(ReceiveBotCaptchaResult.FAILED);
    }
    
    public void reportAdenaADS(final int reporterId, final int reportedId) {
        BotReportedCharData reportedData = (BotReportedCharData)this.reports.get(reportedId);
        if (Objects.isNull(reportedData)) {
            reportedData = new BotReportedCharData();
        }
        else if (reportedData.alreadyReportedAdenaADS(reporterId)) {
            return;
        }
        reportedData.addReporter(reporterId, reportedId, "ADENA_ADS");
        final int reportedCount = reportedData.getADSReportedCount();
        if (reportedCount >= ((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).banChatAdenaAdsReportCount()) {
            final PunishmentManager manager = PunishmentManager.getInstance();
            if (manager.hasPunishment(reportedId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN)) {
                manager.stopPunishment(reportedId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
            }
            manager.startPunishment(new PunishmentTask(0, reportedId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, Instant.now().plus(14L, (TemporalUnit)ChronoUnit.HOURS).toEpochMilli(), "Chat banned bot report", "system", false));
        }
    }
    
    public static ReportTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ReportTable.class);
    }
    
    private static final class Singleton
    {
        private static final ReportTable INSTANCE;
        
        static {
            INSTANCE = new ReportTable();
        }
    }
    
    private final class ReporterCharData
    {
        private long _lastReport;
        private byte _reportPoints;
        
        ReporterCharData() {
            this._reportPoints = 7;
            this._lastReport = 0L;
        }
        
        void registerReport(final long time) {
            --this._reportPoints;
            this._lastReport = time;
        }
        
        long getLastReporTime() {
            return this._lastReport;
        }
        
        byte getPointsLeft() {
            return this._reportPoints;
        }
        
        void setPoints(final int points) {
            this._reportPoints = (byte)points;
        }
        
        void decreaseReportPoints() {
            --this._reportPoints;
        }
    }
    
    private final class BotReportedCharData
    {
        IntMap<BotReportData> reporters;
        IntMap<BotReportData> adsReporters;
        
        BotReportedCharData() {
            this.reporters = (IntMap<BotReportData>)new HashIntMap();
            this.adsReporters = (IntMap<BotReportData>)new HashIntMap();
        }
        
        int getReportCount() {
            return this.reporters.size();
        }
        
        boolean alredyReportedBy(final int objectId) {
            return this.reporters.containsKey(objectId);
        }
        
        boolean reportedBySameClan(final Clan clan) {
            if (Objects.isNull(clan)) {
                return false;
            }
            final PrimitiveIterator.OfInt it = this.reporters.keySet().iterator();
            while (it.hasNext()) {
                if (clan.isMember(it.nextInt())) {
                    return true;
                }
            }
            return false;
        }
        
        void addReporter(final BotReportData report) {
            if ("ADENA_ADS".equalsIgnoreCase(report.getType())) {
                this.adsReporters.put(report.getReporterId(), (Object)report);
            }
            else {
                this.reporters.put(report.getReporterId(), (Object)report);
            }
        }
        
        void addReporter(final int botId, final int reporterId, final String type) {
            this.addReporter(new BotReportData(botId, reporterId, type));
        }
        
        boolean alreadyReportedAdenaADS(final int reporterId) {
            return this.adsReporters.containsKey(reporterId);
        }
        
        int getADSReportedCount() {
            return this.adsReporters.size();
        }
    }
    
    private final class PunishmentsLoader extends DefaultHandler
    {
        PunishmentsLoader() {
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attr) {
            if (qName.equals("punishment")) {
                int reportCount = -1;
                int skillId = -1;
                int skillLevel = 1;
                int sysMessage = -1;
                try {
                    reportCount = Integer.parseInt(attr.getValue("neededReportCount"));
                    skillId = Integer.parseInt(attr.getValue("skillId"));
                    final String level = attr.getValue("skillLevel");
                    final String systemMessageId = attr.getValue("sysMessageId");
                    if (level != null) {
                        skillLevel = Integer.parseInt(level);
                    }
                    if (systemMessageId != null) {
                        sysMessage = Integer.parseInt(systemMessageId);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                ReportTable.this.addPunishment(reportCount, skillId, skillLevel, sysMessage);
            }
        }
    }
    
    private class PunishHolder
    {
        final Skill _punish;
        final int _systemMessageId;
        
        PunishHolder(final Skill sk, final int sysMsg) {
            this._punish = sk;
            this._systemMessageId = sysMsg;
        }
    }
    
    private class ResetPointTask implements Runnable
    {
        @Override
        public void run() {
            ReportTable.this.resetPointsAndSchedule();
        }
    }
}
