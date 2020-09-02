// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.announce.manager;

import org.l2j.gameserver.util.Broadcast;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.threading.ThreadPool;
import java.util.Objects;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.data.database.announce.AnnouncementType;
import org.l2j.gameserver.data.database.data.AnnounceData;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.AnnounceDAO;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.data.database.announce.Announce;
import io.github.joealisson.primitive.IntMap;

public final class AnnouncementsManager
{
    private final IntMap<Announce> announcements;
    private static final IntMap<ScheduledFuture<?>> schedules;
    
    private AnnouncementsManager() {
        this.announcements = (IntMap<Announce>)new CHashIntMap();
    }
    
    private void load() {
        this.announcements.clear();
        final List<AnnounceData> announces = ((AnnounceDAO)DatabaseAccess.getDAO((Class)AnnounceDAO.class)).findAll();
        for (final AnnounceData announce : announces) {
            this.announcements.put(announce.getId(), (Object)announce);
            if (AnnouncementType.isAutoAnnounce(announce)) {
                this.scheduleAnnounce(announce);
            }
        }
    }
    
    public void scheduleAnnounce(final AnnounceData announce) {
        final ScheduledFuture<?> task = (ScheduledFuture<?>)AnnouncementsManager.schedules.get(announce.getId());
        if (Objects.nonNull(task) && !task.isCancelled()) {
            task.cancel(false);
        }
        AnnouncementsManager.schedules.put(announce.getId(), (Object)ThreadPool.schedule((Runnable)new AutoAnnounce(announce), announce.getInitial()));
    }
    
    public void showAnnouncements(final Player player) {
        this.sendAnnouncements(player, AnnouncementType.NORMAL);
        this.sendAnnouncements(player, AnnouncementType.CRITICAL);
        this.sendAnnouncements(player, AnnouncementType.EVENT);
    }
    
    private void sendAnnouncements(final Player player, final AnnouncementType type) {
        final CreatureSay creatureSay;
        final Stream map = this.announcements.values().stream().filter(a -> a.getType() == type && a.isValid()).map(a -> {
            new CreatureSay(0, (type == AnnouncementType.CRITICAL) ? ChatType.CRITICAL_ANNOUNCE : ChatType.ANNOUNCEMENT, player.getName(), a.getContent());
            return creatureSay;
        });
        Objects.requireNonNull(player);
        map.forEach(xva$0 -> player.sendPacket(xva$0));
    }
    
    public void addAnnouncement(final Announce announce) {
        if (announce instanceof AnnounceData) {
            ((AnnounceDAO)DatabaseAccess.getDAO((Class)AnnounceDAO.class)).save((Object)announce);
        }
        this.announcements.put(announce.getId(), (Object)announce);
    }
    
    public boolean deleteAnnouncement(final int id) {
        final Announce announce = (Announce)this.announcements.remove(id);
        if (announce instanceof AnnounceData) {
            ((AnnounceDAO)DatabaseAccess.getDAO((Class)AnnounceDAO.class)).deleteById(id);
            return true;
        }
        return false;
    }
    
    public void updateAnnouncement(final Announce announce) {
        if (announce instanceof AnnounceData) {
            ((AnnounceDAO)DatabaseAccess.getDAO((Class)AnnounceDAO.class)).save((Object)announce);
        }
        this.announcements.putIfAbsent(announce.getId(), (Object)announce);
    }
    
    public Announce getAnnounce(final int id) {
        return (Announce)this.announcements.get(id);
    }
    
    public void restartAutoAnnounce() {
        final Stream filter = this.announcements.values().stream().filter(AnnouncementType::isAutoAnnounce);
        final Class<AnnounceData> obj = AnnounceData.class;
        Objects.requireNonNull(obj);
        filter.map(obj::cast).forEach(this::scheduleAnnounce);
    }
    
    public Collection<Announce> getAllAnnouncements() {
        return (Collection<Announce>)this.announcements.values();
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static AnnouncementsManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        schedules = (IntMap)new CHashIntMap();
    }
    
    private static class Singleton
    {
        protected static final AnnouncementsManager INSTANCE;
        
        static {
            INSTANCE = new AnnouncementsManager();
        }
    }
    
    private static class AutoAnnounce implements Runnable
    {
        private final AnnounceData data;
        private int runs;
        
        private AutoAnnounce(final AnnounceData data) {
            this.data = data;
            this.runs = Math.max(1, data.getRepeat());
        }
        
        @Override
        public void run() {
            ScheduledFuture task;
            if (this.data.getRepeat() == -1 || this.runs > 0) {
                for (final String content : this.data.getContent().split(System.lineSeparator())) {
                    Broadcast.toAllOnlinePlayers(content, this.data.getType() == AnnouncementType.AUTO_CRITICAL);
                }
                if (this.data.getRepeat() != -1) {
                    --this.runs;
                }
                if (this.data.getDelay() > 0L) {
                    task = (ScheduledFuture)AnnouncementsManager.schedules.put(this.data.getId(), (Object)ThreadPool.schedule((Runnable)this, this.data.getDelay()));
                }
                else {
                    task = (ScheduledFuture)AnnouncementsManager.schedules.remove(this.data.getId());
                }
            }
            else {
                task = (ScheduledFuture)AnnouncementsManager.schedules.remove(this.data.getId());
            }
            if (Objects.nonNull(task)) {
                task.cancel(false);
            }
        }
    }
}
