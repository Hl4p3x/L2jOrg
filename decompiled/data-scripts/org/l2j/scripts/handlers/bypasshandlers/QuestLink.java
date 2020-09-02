// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.quest.QuestState;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.stream.Stream;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.model.quest.Quest;
import java.util.function.Function;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IBypassHandler;

public class QuestLink implements IBypassHandler
{
    private static final Logger LOGGER;
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        String quest = "";
        try {
            quest = command.substring(5).trim();
        }
        catch (IndexOutOfBoundsException e) {
            QuestLink.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        if (Util.isNullOrEmpty((CharSequence)quest)) {
            this.showQuestsWindow(player, (Npc)target);
        }
        else {
            final int questNameEnd = quest.indexOf(" ");
            if (questNameEnd == -1) {
                showQuestWindow(player, (Npc)target, quest);
            }
            else {
                player.processQuestEvent(quest.substring(0, questNameEnd), quest.substring(questNameEnd).trim());
            }
        }
        return true;
    }
    
    private void showQuestsWindow(final Player player, final Npc npc) {
        final Stream map = npc.getListeners(EventType.ON_NPC_TALK).stream().map(AbstractEventListener::getOwner);
        final Class<Quest> obj = Quest.class;
        Objects.requireNonNull(obj);
        final Stream<Object> filter = map.filter(obj::isInstance);
        final Class<Quest> obj2 = Quest.class;
        Objects.requireNonNull(obj2);
        final Set<Quest> quests = filter.map((Function<? super Object, ?>)obj2::cast).filter(quest -> quest.getId() > 0).collect((Collector<? super Object, ?, Set<Quest>>)Collectors.toSet());
        if (quests.size() > 1) {
            this.showQuestChooseWindow(player, npc, quests);
        }
        else if (quests.size() == 1) {
            showQuestWindow(player, npc, quests.iterator().next().getName());
        }
        else {
            showQuestWindow(player, npc, "");
        }
    }
    
    private void showQuestChooseWindow(final Player player, final Npc npc, final Collection<Quest> quests) {
        final StringBuilder sbStarted = new StringBuilder(128);
        final StringBuilder sbCanStart = new StringBuilder(128);
        final StringBuilder sbCompleted = new StringBuilder(128);
        for (final Quest quest : quests) {
            final QuestState questState = quest.getQuestState(player, false);
            if (Objects.isNull(questState) || questState.isCreated() || (questState.isCompleted() && questState.isNowAvailable())) {
                if (!quest.canStartQuest(player)) {
                    continue;
                }
                sbCanStart.append("<font color=\"bbaa88\">");
                this.createQuestButton(npc, quest, sbCanStart, "", "01</fstring>");
            }
            else if (questState.isStarted()) {
                sbStarted.append("<font color=\"ffdd66\">");
                this.createQuestButton(npc, quest, sbStarted, " (In Progress)", "02</fstring>");
            }
            else {
                if (!questState.isCompleted()) {
                    continue;
                }
                sbCompleted.append("<font color=\"787878\">").append("<button icon=\"quest\" align=\"left\">").append(quest.isCustomQuest() ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, quest.getPath()) : invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, quest.getNpcStringId())).append("</button></font>");
            }
        }
        String content;
        if (sbStarted.length() > 0 || sbCanStart.length() > 0 || sbCompleted.length() > 0) {
            content = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, sbStarted.toString(), sbCanStart.toString(), sbCompleted.toString());
        }
        else {
            content = Quest.getNoQuestMsg(player);
        }
        content = content.replaceAll("%objectId%", String.valueOf(npc.getObjectId()));
        player.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(npc.getObjectId(), content) });
    }
    
    private void createQuestButton(final Npc npc, final Quest quest, final StringBuilder stringBuilder, final String customSuffix, final String npcStringIdSuffix) {
        stringBuilder.append("<button icon=\"quest\" align=\"left\" action=\"bypass -h npc_").append(npc.getObjectId()).append("_Quest ").append(quest.getName()).append("\">").append(quest.isCustomQuest() ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, quest.getPath(), customSuffix) : invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, quest.getNpcStringId(), npcStringIdSuffix)).append("</button></font>");
    }
    
    private static void showQuestWindow(final Player player, final Npc npc, final String questId) {
        String content = null;
        final Quest quest = QuestManager.getInstance().getQuest(questId);
        if (Objects.nonNull(quest)) {
            if (player.getWeightPenalty() >= 3 || !player.isInventoryUnder80(true)) {
                player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            }
            final QuestState qs = player.getQuestState(questId);
            if (Objects.isNull(qs) && quest.getId() >= 1 && player.getAllActiveQuests().size() > 40) {
                final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
                html.setFile(player, "data/html/fullquest.html");
                player.sendPacket(new ServerPacket[] { (ServerPacket)html });
                return;
            }
            quest.notifyTalk(npc, player);
        }
        else {
            content = Quest.getNoQuestMsg(player);
        }
        if (Objects.nonNull(content)) {
            content = content.replaceAll("%objectId%", String.valueOf(npc.getObjectId()));
            player.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(npc.getObjectId(), content) });
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
    }
    
    public String[] getBypassList() {
        return QuestLink.COMMANDS;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)QuestLink.class);
        COMMANDS = new String[] { "Quest" };
    }
}
