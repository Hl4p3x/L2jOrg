// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerQuestAbort;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.QuestList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.network.GameClient;

public final class RequestQuestAbort extends ClientPacket
{
    private int _questId;
    
    public void readImpl() {
        this._questId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Quest qe = QuestManager.getInstance().getQuest(this._questId);
        if (qe != null) {
            final QuestState qs = activeChar.getQuestState(qe.getName());
            if (qs != null) {
                qs.exitQuest(QuestType.REPEATABLE);
                activeChar.sendPacket(new QuestList(activeChar));
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestAbort(activeChar, this._questId), activeChar, Listeners.players());
                qe.onQuestAborted(activeChar);
            }
        }
    }
}
