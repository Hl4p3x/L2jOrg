// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.attackable;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.actor.Attackable;

public final class OnKillNotifyTask implements Runnable
{
    private final Attackable _attackable;
    private final Quest _quest;
    private final Player _killer;
    private final boolean _isSummon;
    
    public OnKillNotifyTask(final Attackable attackable, final Quest quest, final Player killer, final boolean isSummon) {
        this._attackable = attackable;
        this._quest = quest;
        this._killer = killer;
        this._isSummon = isSummon;
    }
    
    @Override
    public void run() {
        if (this._quest != null && this._attackable != null && this._killer != null) {
            this._quest.notifyKill(this._attackable, this._killer, this._isSummon);
        }
    }
}
