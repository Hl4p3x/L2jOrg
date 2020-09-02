// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.character;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.actor.Creature;

public final class NotifyAITask implements Runnable
{
    private final Creature _character;
    private final CtrlEvent _event;
    
    public NotifyAITask(final Creature character, final CtrlEvent event) {
        this._character = character;
        this._event = event;
    }
    
    @Override
    public void run() {
        if (this._character != null) {
            this._character.getAI().notifyEvent(this._event, null);
        }
    }
}
