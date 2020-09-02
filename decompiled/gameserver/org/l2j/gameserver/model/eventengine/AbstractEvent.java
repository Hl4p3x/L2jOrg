// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

import org.l2j.gameserver.model.actor.instance.Player;
import java.nio.file.Path;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.l2j.gameserver.model.events.AbstractScript;

public abstract class AbstractEvent<T extends AbstractEventMember<?>> extends AbstractScript
{
    private final Map<Integer, T> _members;
    private IEventState _state;
    
    public AbstractEvent() {
        this._members = new ConcurrentHashMap<Integer, T>();
    }
    
    public final Map<Integer, T> getMembers() {
        return this._members;
    }
    
    public final T getMember(final int objectId) {
        return this._members.get(objectId);
    }
    
    public final void addMember(final T member) {
        this._members.put(member.getObjectId(), member);
    }
    
    public final void broadcastPacket(final ServerPacket... packets) {
        this._members.values().forEach(member -> member.sendPacket(packets));
    }
    
    public final IEventState getState() {
        return this._state;
    }
    
    public final void setState(final IEventState state) {
        this._state = state;
    }
    
    @Override
    public final String getScriptName() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public final Path getScriptPath() {
        return null;
    }
    
    public boolean isOnEvent(final Player player) {
        return this._members.containsKey(player.getObjectId());
    }
    
    public boolean isBlockingExit(final Player player) {
        return false;
    }
    
    public boolean isBlockingDeathPenalty(final Player player) {
        return false;
    }
    
    public boolean canRevive(final Player player) {
        return true;
    }
}
