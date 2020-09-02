// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public class BuffFinishTask
{
    private final Map<BuffInfo, AtomicInteger> _buffInfos;
    private final ScheduledFuture<?> _task;
    
    public BuffFinishTask() {
        this._buffInfos = new ConcurrentHashMap<BuffInfo, AtomicInteger>();
        final Iterator<Map.Entry<BuffInfo, AtomicInteger>> iterator;
        Map.Entry<BuffInfo, AtomicInteger> entry;
        BuffInfo info;
        this._task = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate(() -> {
            this._buffInfos.entrySet().iterator();
            while (iterator.hasNext()) {
                entry = iterator.next();
                info = entry.getKey();
                if (info.getEffected() != null && entry.getValue().incrementAndGet() > info.getAbnormalTime()) {
                    info.getEffected().getEffectList().stopSkillEffects(false, info.getSkill().getId());
                }
            }
        }, 0L, 1000L);
    }
    
    public ScheduledFuture<?> getTask() {
        return this._task;
    }
    
    public void addBuffInfo(final BuffInfo info) {
        this._buffInfos.put(info, new AtomicInteger());
    }
    
    public void removeBuffInfo(final BuffInfo info) {
        this._buffInfos.remove(info);
    }
}
