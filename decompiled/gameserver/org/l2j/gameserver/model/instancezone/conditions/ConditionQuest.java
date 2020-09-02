// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public final class ConditionQuest extends Condition
{
    public ConditionQuest(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        super(template, parameters, onlyLeader, showMessageAndHtml);
        this.setSystemMessage(SystemMessageId.C1_S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED, (message, player) -> message.addString(player.getName()));
    }
    
    @Override
    protected boolean test(final Player player, final Npc npc) {
        final int id = this.getParameters().getInt("id");
        final Quest q = QuestManager.getInstance().getQuest(id);
        if (q == null) {
            return false;
        }
        final QuestState qs = player.getQuestState(q.getName());
        if (qs == null) {
            return false;
        }
        final int cond = this.getParameters().getInt("cond", -1);
        return cond == -1 || qs.isCond(cond);
    }
}
