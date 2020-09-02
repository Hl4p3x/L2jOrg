// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

public final class EventShrineManager
{
    private static boolean ENABLE_SHRINES;
    
    private EventShrineManager() {
    }
    
    public static EventShrineManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    public boolean areShrinesEnabled() {
        return EventShrineManager.ENABLE_SHRINES;
    }
    
    public void setEnabled(final boolean enabled) {
        EventShrineManager.ENABLE_SHRINES = enabled;
    }
    
    static {
        EventShrineManager.ENABLE_SHRINES = false;
    }
    
    private static class Singleton
    {
        private static final EventShrineManager INSTANCE;
        
        static {
            INSTANCE = new EventShrineManager();
        }
    }
}
