// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Creature;
import java.util.concurrent.ScheduledFuture;

public class MpRewardTask
{
    private int _count;
    private final double _value;
    private final ScheduledFuture<?> _task;
    private final Creature _creature;
    
    public MpRewardTask(final Creature creature, final Npc npc) {
        final NpcTemplate template = npc.getTemplate();
        this._creature = creature;
        this._count = template.getMpRewardTicks();
        this._value = this.calculateBaseValue(npc, creature);
        this._task = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate(this::run, Config.EFFECT_TICK_RATIO, Config.EFFECT_TICK_RATIO);
    }
    
    private double calculateBaseValue(final Npc npc, final Creature creature) {
        final NpcTemplate template = npc.getTemplate();
        switch (template.getMpRewardType()) {
            case PER: {
                return creature.getMaxMp() * (template.getMpRewardValue() / 100.0) / template.getMpRewardTicks();
            }
            default: {
                return template.getMpRewardValue() / (double)template.getMpRewardTicks();
            }
        }
    }
    
    private void run() {
        final int count = this._count - 1;
        this._count = count;
        if (count <= 0 || (GameUtils.isPlayer(this._creature) && !this._creature.getActingPlayer().isOnline())) {
            this._task.cancel(false);
            return;
        }
        this._creature.setCurrentMp(this._creature.getCurrentMp() + this._value);
    }
}
