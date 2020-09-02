// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.GameClient;

public class RequestTutorialQuestionMark extends ClientPacket
{
    private int _number;
    
    public RequestTutorialQuestionMark() {
        this._number = 0;
    }
    
    public void readImpl() {
        this.readByte();
        this._number = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPressTutorialMark(player, this._number), player);
    }
}
