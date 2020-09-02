// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

import java.util.Iterator;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Map;

public final class SkillChannelized
{
    private final Map<Integer, Map<Integer, Creature>> _channelizers;
    
    public SkillChannelized() {
        this._channelizers = new ConcurrentHashMap<Integer, Map<Integer, Creature>>();
    }
    
    public void addChannelizer(final int skillId, final Creature channelizer) {
        this._channelizers.computeIfAbsent(Integer.valueOf(skillId), k -> new ConcurrentHashMap()).put(channelizer.getObjectId(), channelizer);
    }
    
    public void removeChannelizer(final int skillId, final Creature channelizer) {
        this.getChannelizers(skillId).remove(channelizer.getObjectId());
    }
    
    public int getChannerlizersSize(final int skillId) {
        return this.getChannelizers(skillId).size();
    }
    
    public Map<Integer, Creature> getChannelizers(final int skillId) {
        return this._channelizers.getOrDefault(skillId, Collections.emptyMap());
    }
    
    public void abortChannelization() {
        for (final Map<Integer, Creature> map : this._channelizers.values()) {
            for (final Creature channelizer : map.values()) {
                channelizer.abortCast();
            }
        }
        this._channelizers.clear();
    }
    
    public boolean isChannelized() {
        for (final Map<Integer, Creature> map : this._channelizers.values()) {
            if (!map.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
