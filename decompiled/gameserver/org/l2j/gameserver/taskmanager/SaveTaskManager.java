// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager;

import org.l2j.commons.util.Util;
import java.time.Duration;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.TimeUnit;
import java.util.Objects;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Map;

public class SaveTaskManager
{
    private final Map<Player, Long> playerSaveStamp;
    private ScheduledFuture<?> scheduledTask;
    
    private SaveTaskManager() {
        this.playerSaveStamp = Collections.synchronizedMap(new WeakHashMap<Player, Long>());
    }
    
    public void registerPlayer(final Player player) {
        final int scheduleTime = ((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).autoSavePlayerTime();
        if (this.playerSaveStamp.isEmpty() && (Objects.isNull(this.scheduledTask) || this.scheduledTask.isDone())) {
            this.scheduledTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedDelay(this::saveTask, (long)scheduleTime, (long)scheduleTime, TimeUnit.MINUTES);
        }
        this.playerSaveStamp.put(player, this.nextSave(scheduleTime));
    }
    
    protected long nextSave(final int scheduleTime) {
        return System.currentTimeMillis() + Duration.ofMinutes(scheduleTime).toMillis();
    }
    
    private void saveTask() {
        final long now = System.currentTimeMillis();
        final long nextSave = this.nextSave(((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).autoSavePlayerTime());
        synchronized (this.playerSaveStamp) {
            this.playerSaveStamp.entrySet().stream().filter(entry -> Util.falseIfNullOrElse((Object)entry, e -> e.getValue() < now)).forEach(entry -> this.save(nextSave, entry));
        }
    }
    
    private void save(final long nextSave, final Map.Entry<Player, Long> entry) {
        entry.getKey().storeMe();
        entry.setValue(nextSave);
    }
    
    public void remove(final Player player) {
        this.playerSaveStamp.remove(player);
        if (this.playerSaveStamp.isEmpty() && Objects.nonNull(this.scheduledTask) && !this.scheduledTask.isDone()) {
            this.scheduledTask.cancel(false);
            this.scheduledTask = null;
        }
    }
    
    public static SaveTaskManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static final class Singleton
    {
        private static final SaveTaskManager INSTANCE;
        
        static {
            INSTANCE = new SaveTaskManager();
        }
    }
}
