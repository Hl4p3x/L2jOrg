// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10673_SagaOfLegend;

import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionChange;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.network.NpcStringId;
import java.util.HashSet;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import java.util.Set;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.CategoryManager;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.quest.Quest;

public class Q10673_SagaOfLegend extends Quest
{
    private static final int ORVEN = 30857;
    private static final int[] MOBS;
    private static final int MAGICAL_TABLET = 90045;
    private static final int SPELLBOOK_HUMAN = 90038;
    private static final int SPELLBOOK_ELF = 90039;
    private static final int SPELLBOOK_DELF = 90040;
    private static final int SPELLBOOK_ORC = 90042;
    private static final int SPELLBOOK_DWARF = 90041;
    private static final int SPELLBOOK_KAMAEL = 91946;
    private static final int MIN_LEVEL = 76;
    private static final String KILL_COUNT_VAR = "KillCount";
    
    public Q10673_SagaOfLegend() {
        super(10673);
        this.addStartNpc(30857);
        this.addTalkId(30857);
        this.addKillId(Q10673_SagaOfLegend.MOBS);
        this.addCondMinLevel(76, "30857-00.htm");
        this.addCondInCategory(CategoryType.THIRD_CLASS_GROUP, "30857-00.htm");
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        final QuestState qs = this.getQuestState(player, false);
        if (Objects.isNull(qs)) {
            return htmltext;
        }
        switch (event) {
            case "30857-02.htm":
            case "30857-03.htm":
            case "30857-04.htm":
            case "30857-06.html": {
                htmltext = event;
                break;
            }
            case "30857-05.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "30857-07.html": {
                qs.setCond(2, true);
                htmltext = event;
                break;
            }
            case "30857-07a.html": {
                qs.setCond(3, true);
                htmltext = event;
                break;
            }
            case "30857-10.html": {
                if (qs.isCond(4)) {
                    giveItems(player, 90045, 10L);
                    qs.exitQuest(false, true);
                    if (CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, player.getClassId().getId())) {
                        player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
                    }
                    htmltext = event;
                    break;
                }
                break;
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (Objects.isNull(qs)) {
            return htmltext;
        }
        switch (qs.getState()) {
            case 0: {
                htmltext = "30857-01.htm";
            }
            case 1: {
                switch (qs.getCond()) {
                    case 1: {
                        htmltext = "30857-05.htm";
                        break;
                    }
                    case 2: {
                        htmltext = "30857-08.html";
                        break;
                    }
                    case 3: {
                        htmltext = "30857-08a.html";
                        break;
                    }
                    case 4: {
                        htmltext = "30857-09.html";
                        break;
                    }
                }
                break;
            }
            case 2: {
                htmltext = getAlreadyCompletedMsg(player);
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState qs = this.getQuestState(killer, false);
        if (qs != null && qs.getCond() > 1) {
            final int killCount = qs.getInt("KillCount") + 1;
            if (killCount < 700) {
                qs.set("KillCount", killCount);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                this.sendNpcLogList(killer);
            }
            else {
                qs.setCond(4, true);
                qs.unset("KillCount");
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.getCond() > 1) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.ORVEN_S_REQUEST.getId(), true, qs.getInt("KillCount")));
            return holder;
        }
        return (Set<NpcLogListHolder>)super.getNpcLogList(player);
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogin(final OnPlayerLogin event) {
        if (Config.DISABLE_TUTORIAL) {
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (!CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, player.getClassId().getId())) {
            return;
        }
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCompleted()) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onProfessionChange(final OnPlayerProfessionChange event) {
        final Player player = event.getActiveChar();
        if (player == null) {
            return;
        }
        if (!CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, player.getClassId().getId())) {
            return;
        }
        if (player.isItemsRewarded()) {
            return;
        }
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCompleted()) {
            player.setItemsRewarded(true);
            switch (player.getRace()) {
                case ELF: {
                    giveItems(player, 90039, 1L);
                    break;
                }
                case DARK_ELF: {
                    giveItems(player, 90040, 1L);
                    break;
                }
                case ORC: {
                    giveItems(player, 90042, 1L);
                    break;
                }
                case DWARF: {
                    giveItems(player, 90041, 1L);
                    break;
                }
                case JIN_KAMAEL: {
                    giveItems(player, 91946, 1L);
                    break;
                }
                case HUMAN: {
                    giveItems(player, 90038, 1L);
                    break;
                }
            }
        }
    }
    
    static {
        MOBS = new int[] { 20965, 20970, 20966, 20971, 20967, 20973, 20968, 20969, 20972, 24025, 24046, 24032, 24041, 24026, 24042, 24047, 24033, 24048, 24043, 24050, 24049, 24034, 24027, 24052, 24051, 24035, 24028, 24053, 24054, 24036, 24037, 24055, 24030, 24029, 24044, 24045, 24031, 24040, 24039, 24038 };
    }
}
