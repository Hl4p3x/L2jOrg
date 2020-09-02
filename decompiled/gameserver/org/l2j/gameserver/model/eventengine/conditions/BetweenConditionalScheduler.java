// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine.conditions;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import java.util.Objects;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.slf4j.Logger;
import org.l2j.gameserver.model.eventengine.IConditionalEventScheduler;

public class BetweenConditionalScheduler implements IConditionalEventScheduler
{
    private static final Logger LOGGER;
    private final AbstractEventManager<?> _eventManager;
    private final String _name;
    private final String _scheduler1;
    private final String _scheduler2;
    
    public BetweenConditionalScheduler(final AbstractEventManager<?> eventManager, final String name, final String scheduler1, final String scheduler2) {
        Objects.requireNonNull(eventManager);
        Objects.requireNonNull(name);
        Objects.requireNonNull(scheduler1);
        Objects.requireNonNull(scheduler2);
        this._eventManager = eventManager;
        this._name = name;
        this._scheduler1 = scheduler1;
        this._scheduler2 = scheduler2;
    }
    
    @Override
    public boolean test() {
        final EventScheduler scheduler1 = this._eventManager.getScheduler(this._scheduler1);
        final EventScheduler scheduler2 = this._eventManager.getScheduler(this._scheduler2);
        if (scheduler1 == null) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._scheduler1));
        }
        if (scheduler2 == null) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._scheduler2));
        }
        final long previousStart = scheduler1.getPrevSchedule();
        final long previousEnd = scheduler2.getPrevSchedule();
        return previousStart > previousEnd;
    }
    
    @Override
    public void run() {
        final EventScheduler mainScheduler = this._eventManager.getScheduler(this._name);
        if (mainScheduler == null) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._name));
        }
        mainScheduler.run();
        BetweenConditionalScheduler.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._eventManager.getClass().getSimpleName()));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)BetweenConditionalScheduler.class);
    }
}
