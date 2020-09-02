// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.network.GameClient;

public class RequestAddExpandQuestAlarm extends ClientPacket
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
        final Quest quest = QuestManager.getInstance().getQuest(this._questId);
        if (quest != null) {
            quest.sendNpcLogList(activeChar);
        }
    }
}
