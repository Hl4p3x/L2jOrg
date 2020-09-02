// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager.events;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.quest.Event;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;

public class RaceManager extends AbstractEventManager<AbstractEvent>
{
    private RaceManager() {
    }
    
    @Override
    public void onInitialized() {
    }
    
    protected void startEvent() {
        final Event event = (Event)QuestManager.getInstance().getQuest("Race");
        if (event != null) {
            event.eventStart(null);
        }
    }
    
    public static RaceManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final RaceManager INSTANCE;
        
        static {
            INSTANCE = new RaceManager();
        }
    }
}
