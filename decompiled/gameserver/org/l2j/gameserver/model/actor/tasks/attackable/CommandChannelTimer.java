// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.attackable;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.model.actor.Attackable;

public final class CommandChannelTimer implements Runnable
{
    private final Attackable _attackable;
    
    public CommandChannelTimer(final Attackable attackable) {
        this._attackable = attackable;
    }
    
    @Override
    public void run() {
        if (this._attackable == null) {
            return;
        }
        if (System.currentTimeMillis() - this._attackable.getCommandChannelLastAttack() > ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).raidLootPrivilegeTime()) {
            this._attackable.setCommandChannelTimer(null);
            this._attackable.setFirstCommandChannelAttacked(null);
            this._attackable.setCommandChannelLastAttack(0L);
        }
        else {
            ThreadPool.schedule((Runnable)this, 10000L);
        }
    }
}
