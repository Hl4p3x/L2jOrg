// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager.events;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.quest.Event;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;

public class RabbitsManager extends AbstractEventManager<AbstractEvent<?>>
{
    private RabbitsManager() {
    }
    
    @Override
    public void onInitialized() {
    }
    
    protected void startEvent() {
        final Event event = (Event)QuestManager.getInstance().getQuest("Rabbits");
        if (event != null) {
            event.eventStart(null);
        }
    }
    
    public static RabbitsManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final RabbitsManager INSTANCE;
        
        static {
            INSTANCE = new RabbitsManager();
        }
    }
}
