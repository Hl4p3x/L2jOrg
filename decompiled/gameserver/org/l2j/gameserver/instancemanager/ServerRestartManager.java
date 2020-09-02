// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.Shutdown;
import org.slf4j.LoggerFactory;
import org.l2j.commons.threading.ThreadPool;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.Config;
import java.util.Calendar;
import org.slf4j.Logger;

public class ServerRestartManager
{
    static final Logger LOGGER;
    private String nextRestartTime;
    
    private ServerRestartManager() {
        this.nextRestartTime = "unknown";
        try {
            final Calendar currentTime = Calendar.getInstance();
            final Calendar restartTime = Calendar.getInstance();
            Calendar lastRestart = null;
            long delay = 0L;
            long lastDelay = 0L;
            for (final String scheduledTime : Config.SERVER_RESTART_SCHEDULE) {
                final String[] splitTime = scheduledTime.trim().split(":");
                restartTime.set(11, Integer.parseInt(splitTime[0]));
                restartTime.set(12, Integer.parseInt(splitTime[1]));
                restartTime.set(13, 0);
                if (restartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                    restartTime.add(5, 1);
                }
                delay = restartTime.getTimeInMillis() - currentTime.getTimeInMillis();
                if (lastDelay == 0L) {
                    lastDelay = delay;
                    lastRestart = restartTime;
                }
                if (delay < lastDelay) {
                    lastDelay = delay;
                    lastRestart = restartTime;
                }
            }
            if (lastRestart != null) {
                this.nextRestartTime = new SimpleDateFormat("HH:mm").format(lastRestart.getTime());
                ThreadPool.schedule((Runnable)new ServerRestartTask(), lastDelay - Config.SERVER_RESTART_SCHEDULE_COUNTDOWN * 1000);
                ServerRestartManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/util/Date;)Ljava/lang/String;, lastRestart.getTime()));
            }
        }
        catch (Exception e) {
            ServerRestartManager.LOGGER.info("The scheduled server restart config is not set properly, please correct it!");
        }
    }
    
    public String getNextRestartTime() {
        return this.nextRestartTime;
    }
    
    public static ServerRestartManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(ServerRestartManager.class.getName());
    }
    
    private static class Singleton
    {
        private static final ServerRestartManager INSTANCE;
        
        static {
            INSTANCE = new ServerRestartManager();
        }
    }
    
    class ServerRestartTask implements Runnable
    {
        @Override
        public void run() {
            Shutdown.getInstance().startShutdown(null, Config.SERVER_RESTART_SCHEDULE_COUNTDOWN, true);
        }
    }
}
