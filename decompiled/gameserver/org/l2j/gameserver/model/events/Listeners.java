// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events;

public class Listeners
{
    private static final ListenersContainer _globalContainer;
    private static final ListenersContainer _globalNpcsContainer;
    private static final ListenersContainer _globalMonstersContainer;
    private static final ListenersContainer _globalPlayersContainer;
    
    protected Listeners() {
    }
    
    public static ListenersContainer Global() {
        return Listeners._globalContainer;
    }
    
    public static ListenersContainer Npcs() {
        return Listeners._globalNpcsContainer;
    }
    
    public static ListenersContainer Monsters() {
        return Listeners._globalMonstersContainer;
    }
    
    public static ListenersContainer players() {
        return Listeners._globalPlayersContainer;
    }
    
    static {
        _globalContainer = new ListenersContainer();
        _globalNpcsContainer = new ListenersContainer();
        _globalMonstersContainer = new ListenersContainer();
        _globalPlayersContainer = new ListenersContainer();
    }
}
