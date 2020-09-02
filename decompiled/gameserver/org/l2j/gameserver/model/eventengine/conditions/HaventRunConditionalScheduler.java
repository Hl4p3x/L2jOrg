// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine.conditions;

import org.l2j.gameserver.model.eventengine.EventScheduler;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.EventDAO;
import java.util.Objects;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.IConditionalEventScheduler;

public class HaventRunConditionalScheduler implements IConditionalEventScheduler
{
    private final AbstractEventManager<?> eventManager;
    private final String name;
    
    public HaventRunConditionalScheduler(final AbstractEventManager<?> eventManager, final String name) {
        this.eventManager = eventManager;
        this.name = name;
    }
    
    @Override
    public boolean test() {
        final EventScheduler mainScheduler = this.eventManager.getScheduler(this.name);
        if (Objects.isNull(mainScheduler)) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        final long lastRun = ((EventDAO)DatabaseAccess.getDAO((Class)EventDAO.class)).findLastRun(this.eventManager.getName(), mainScheduler.getName());
        final long lastPossibleRun = mainScheduler.getPrevSchedule();
        return lastPossibleRun > lastRun && Math.abs(lastPossibleRun - lastRun) > 1000L;
    }
    
    @Override
    public void run() {
        final EventScheduler mainScheduler = this.eventManager.getScheduler(this.name);
        if (mainScheduler == null) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        if (mainScheduler.updateLastRun()) {
            mainScheduler.run();
        }
    }
}
