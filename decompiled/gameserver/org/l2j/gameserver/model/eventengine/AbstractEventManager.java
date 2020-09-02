// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

import java.nio.file.Path;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.eventengine.drop.IEventDrop;
import java.util.Map;
import org.l2j.gameserver.model.StatsSet;
import java.util.concurrent.atomic.AtomicReference;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Queue;
import java.util.Set;
import org.l2j.gameserver.model.events.AbstractScript;

public abstract class AbstractEventManager<T extends AbstractEvent<?>> extends AbstractScript
{
    private final Set<T> events;
    private final Queue<Player> registeredPlayers;
    private final AtomicReference<IEventState> state;
    private String name;
    private volatile StatsSet variables;
    private volatile Set<EventScheduler> schedulers;
    private volatile Set<IConditionalEventScheduler> conditionalSchedulers;
    private volatile Map<String, IEventDrop> rewards;
    
    public AbstractEventManager() {
        this.events = (Set<T>)ConcurrentHashMap.newKeySet();
        this.registeredPlayers = new ConcurrentLinkedDeque<Player>();
        this.state = new AtomicReference<IEventState>();
        this.variables = StatsSet.EMPTY_STATSET;
        this.schedulers = Collections.emptySet();
        this.conditionalSchedulers = Collections.emptySet();
        this.rewards = Collections.emptyMap();
    }
    
    public abstract void onInitialized();
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public StatsSet getVariables() {
        return this.variables;
    }
    
    public void setVariables(final StatsSet variables) {
        this.variables = new StatsSet(Collections.unmodifiableMap((Map<? extends String, ?>)variables.getSet()));
    }
    
    public EventScheduler getScheduler(final String name) {
        return this.schedulers.stream().filter(scheduler -> scheduler.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    
    public void setSchedulers(final Set<EventScheduler> schedulers) {
        this.schedulers = Collections.unmodifiableSet((Set<? extends EventScheduler>)schedulers);
    }
    
    public Set<IConditionalEventScheduler> getConditionalSchedulers() {
        return this.conditionalSchedulers;
    }
    
    public void setConditionalSchedulers(final Set<IConditionalEventScheduler> schedulers) {
        this.conditionalSchedulers = Collections.unmodifiableSet((Set<? extends IConditionalEventScheduler>)schedulers);
    }
    
    public IEventDrop getRewards(final String name) {
        return this.rewards.get(name);
    }
    
    public void setRewards(final Map<String, IEventDrop> rewards) {
        this.rewards = Collections.unmodifiableMap((Map<? extends String, ? extends IEventDrop>)rewards);
    }
    
    public Set<T> getEvents() {
        return this.events;
    }
    
    public void startScheduler() {
        this.schedulers.forEach(EventScheduler::startScheduler);
    }
    
    public void stopScheduler() {
        this.schedulers.forEach(EventScheduler::stopScheduler);
    }
    
    public void startConditionalSchedulers() {
        this.conditionalSchedulers.stream().filter(IConditionalEventScheduler::test).forEach(IConditionalEventScheduler::run);
    }
    
    public IEventState getState() {
        return this.state.get();
    }
    
    public void setState(final IEventState newState) {
        final IEventState previousState = this.state.get();
        this.state.set(newState);
        this.onStateChange(previousState, newState);
    }
    
    public boolean setState(final IEventState previousState, final IEventState newState) {
        if (this.state.compareAndSet(previousState, newState)) {
            this.onStateChange(previousState, newState);
            return true;
        }
        return false;
    }
    
    public final boolean registerPlayer(final Player player) {
        return this.canRegister(player, true) && this.registeredPlayers.offer(player);
    }
    
    public final boolean unregisterPlayer(final Player player) {
        return this.registeredPlayers.remove(player);
    }
    
    public final boolean isRegistered(final Player player) {
        return this.registeredPlayers.contains(player);
    }
    
    public boolean canRegister(final Player player, final boolean sendMessage) {
        return !this.registeredPlayers.contains(player);
    }
    
    public final Queue<Player> getRegisteredPlayers() {
        return this.registeredPlayers;
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGOUT)
    @RegisterType(ListenerRegisterType.GLOBAL)
    public void OnPlayerLogout(final OnPlayerLogout event) {
        final Player player = event.getActiveChar();
        if (this.registeredPlayers.remove(player)) {
            this.onUnregisteredPlayer(player);
        }
    }
    
    protected void onUnregisteredPlayer(final Player player) {
    }
    
    protected void onStateChange(final IEventState previousState, final IEventState newState) {
    }
    
    @Override
    public String getScriptName() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public Path getScriptPath() {
        return null;
    }
}
