// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.OnDayNightChange;
import org.l2j.gameserver.model.events.EventDispatcher;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Set;
import org.slf4j.Logger;

public final class WorldTimeController extends Thread
{
    private static final Logger LOGGER;
    public static final int TICKS_PER_SECOND = 10;
    public static final int MILLIS_IN_TICK = 100;
    private static final int IN_GAME_DAYS_PER_DAY = 6;
    private static final int MILLIS_PER_IN_GAME_DAY = 14400000;
    private static final int SECONDS_PER_IN_GAME_DAY = 14400;
    private static final int TICKS_PER_IN_GAME_DAY = 144000;
    private final Set<Creature> movingObjects;
    private final Set<Creature> shadowSenseCharacters;
    private final long referenceTime;
    private volatile boolean shutdown;
    
    private WorldTimeController() {
        super("World Time Controller");
        this.movingObjects = (Set<Creature>)ConcurrentHashMap.newKeySet();
        this.shadowSenseCharacters = (Set<Creature>)ConcurrentHashMap.newKeySet();
        this.shutdown = false;
        this.setDaemon(true);
        this.setPriority(10);
        this.referenceTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    public static void init() {
        getInstance().start();
    }
    
    public final int getGameTime() {
        return this.getGameTicks() % 144000 / 100;
    }
    
    public final int getGameHour() {
        return this.getGameTime() / 60;
    }
    
    public final int getGameMinute() {
        return this.getGameTime() % 60;
    }
    
    public final boolean isNight() {
        return this.getGameHour() < 6;
    }
    
    public final int getGameTicks() {
        return (int)((System.currentTimeMillis() - this.referenceTime) / 100L);
    }
    
    public final void registerMovingObject(final Creature cha) {
        if (cha == null) {
            return;
        }
        this.movingObjects.add(cha);
    }
    
    private void moveObjects() {
        this.movingObjects.removeIf(Creature::updatePosition);
    }
    
    public final void stopTimer() {
        this.shutdown = true;
    }
    
    @Override
    public final void run() {
        boolean isNight = this.isNight();
        EventDispatcher.getInstance().notifyEventAsync(OnDayNightChange.of(isNight), new ListenersContainer[0]);
        while (!this.shutdown) {
            final long nextTickTime = System.currentTimeMillis() / 100L * 100L + 100L;
            try {
                this.moveObjects();
            }
            catch (Throwable e) {
                WorldTimeController.LOGGER.warn(e.getLocalizedMessage(), e);
            }
            final long sleepTime = nextTickTime - System.currentTimeMillis();
            if (sleepTime > 0L) {
                try {
                    Thread.sleep(sleepTime);
                }
                catch (InterruptedException ex) {}
            }
            if (this.isNight() != isNight) {
                isNight = !isNight;
                EventDispatcher.getInstance().notifyEventAsync(OnDayNightChange.of(isNight), new ListenersContainer[0]);
                this.notifyShadowSense();
            }
        }
    }
    
    public synchronized void addShadowSenseCharacter(final Creature character) {
        if (!this.shadowSenseCharacters.contains(character)) {
            this.shadowSenseCharacters.add(character);
            if (this.isNight()) {
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT);
                msg.addSkillName(CommonSkill.SHADOW_SENSE_ID.getId());
                character.sendPacket(msg);
            }
        }
    }
    
    public void removeShadowSenseCharacter(final Creature character) {
        this.shadowSenseCharacters.remove(character);
    }
    
    private void notifyShadowSense() {
        final SystemMessage msg = SystemMessage.getSystemMessage(this.isNight() ? SystemMessageId.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT : SystemMessageId.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR);
        msg.addSkillName(CommonSkill.SHADOW_SENSE_ID.getId());
        for (final Creature character : this.shadowSenseCharacters) {
            character.getStats().recalculateStats(true);
            character.sendPacket(msg);
        }
    }
    
    public static WorldTimeController getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)WorldTimeController.class);
    }
    
    private static class Singleton
    {
        private static final WorldTimeController INSTANCE;
        
        static {
            INSTANCE = new WorldTimeController();
        }
    }
}
