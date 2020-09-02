// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager.events;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.quest.Event;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;

public class ElpiesManager extends AbstractEventManager<AbstractEvent>
{
    private ElpiesManager() {
    }
    
    @Override
    public void onInitialized() {
    }
    
    protected void startEvent() {
        final Event event = (Event)QuestManager.getInstance().getQuest("Elpies");
        if (event != null) {
            event.eventStart(null);
        }
    }
    
    public static ElpiesManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final ElpiesManager INSTANCE;
        
        static {
            INSTANCE = new ElpiesManager();
        }
    }
}
