// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

import java.nio.file.Path;
import org.w3c.dom.Node;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import java.util.Collections;
import java.util.Set;
import org.l2j.gameserver.model.events.AbstractScript;

public abstract class AbstractEventManager<T extends AbstractEvent> extends AbstractScript
{
    private String name;
    private Set<EventScheduler> schedulers;
    private Set<IConditionalEventScheduler> conditionalSchedulers;
    
    public AbstractEventManager() {
        this.schedulers = Collections.emptySet();
        this.conditionalSchedulers = Collections.emptySet();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
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
    
    public void startScheduler() {
        this.schedulers.forEach(EventScheduler::startScheduler);
    }
    
    public void stopScheduler() {
        this.schedulers.forEach(EventScheduler::stopScheduler);
    }
    
    public void startConditionalSchedulers() {
        this.conditionalSchedulers.stream().filter(IConditionalEventScheduler::test).forEach(IConditionalEventScheduler::run);
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGOUT)
    @RegisterType(ListenerRegisterType.GLOBAL)
    public void playerLogout(final OnPlayerLogout event) {
        this.onPlayerLogout(event.getPlayer());
    }
    
    protected void onPlayerLogout(final Player player) {
    }
    
    public void config(final GameXmlReader reader, final Node configNode) {
    }
    
    @Override
    public String getScriptName() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public Path getScriptPath() {
        return null;
    }
    
    public abstract void onInitialized();
}
