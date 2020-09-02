// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.stream.Stream;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import java.util.Map;

public final class PunishmentHolder
{
    private final Map<String, Map<PunishmentType, PunishmentTask>> _holder;
    
    public PunishmentHolder() {
        this._holder = new ConcurrentHashMap<String, Map<PunishmentType, PunishmentTask>>();
    }
    
    public void addPunishment(final PunishmentTask task) {
        if (!task.isExpired()) {
            final String key = String.valueOf(task.getKey());
            this._holder.computeIfAbsent(key, k -> new ConcurrentHashMap()).put(task.getType(), task);
        }
    }
    
    public void stopPunishment(final PunishmentTask task) {
        final String key = String.valueOf(task.getKey());
        if (this._holder.containsKey(key)) {
            task.stopPunishment();
            final Map<PunishmentType, PunishmentTask> punishments = this._holder.get(key);
            punishments.remove(task.getType());
            if (punishments.isEmpty()) {
                this._holder.remove(key);
            }
        }
    }
    
    public void stopPunishment(final PunishmentType type) {
        final String key;
        final Map<PunishmentType, PunishmentTask> punishments;
        this._holder.values().stream().flatMap(p -> p.values().stream()).filter(p -> p.getType() == type).forEach(t -> {
            t.stopPunishment();
            key = String.valueOf(t.getKey());
            punishments = this._holder.get(key);
            punishments.remove(t.getType());
            if (punishments.isEmpty()) {
                this._holder.remove(key);
            }
        });
    }
    
    public boolean hasPunishment(final String key, final PunishmentType type) {
        return this.getPunishment(key, type) != null;
    }
    
    public PunishmentTask getPunishment(final String key, final PunishmentType type) {
        if (this._holder.containsKey(key)) {
            return this._holder.get(key).get(type);
        }
        return null;
    }
}
