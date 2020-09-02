// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00300_HuntingLetoLizardman;

import io.github.joealisson.primitive.HashIntIntMap;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.quest.QuestState;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00300_HuntingLetoLizardman extends Quest
{
    private static final int RATH = 30126;
    private static final int BRACELET_OF_LIZARDMAN = 7139;
    private static final ItemHolder REWARD_ADENA;
    private static final ItemHolder REWARD_ANIMAL_BONE;
    private static final ItemHolder REWARD_ANIMAL_SKIN;
    private static final int MIN_LEVEL = 34;
    private static final int REQUIRED_BRACELET_COUNT = 60;
    private static final IntIntMap MOBS_SAC;
    
    public Q00300_HuntingLetoLizardman() {
        super(300);
        this.addStartNpc(30126);
        this.addTalkId(30126);
        this.addKillId((IntCollection)Q00300_HuntingLetoLizardman.MOBS_SAC.keySet());
        this.registerQuestItems(new int[] { 7139 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        if (Objects.isNull(st)) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30126-03.htm": {
                if (st.isCreated()) {
                    st.startQuest();
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30126-06.html": {
                if (getQuestItemsCount(player, 7139) >= 60L) {
                    takeItems(player, 7139, -1L);
                    final int rand = getRandom(1000);
                    if (rand < 500) {
                        giveItems(player, Q00300_HuntingLetoLizardman.REWARD_ADENA);
                    }
                    else if (rand < 750) {
                        giveItems(player, Q00300_HuntingLetoLizardman.REWARD_ANIMAL_SKIN);
                    }
                    else if (rand < 1000) {
                        giveItems(player, Q00300_HuntingLetoLizardman.REWARD_ANIMAL_BONE);
                    }
                    st.exitQuest(true, true);
                    htmltext = event;
                    break;
                }
                htmltext = "30126-07.html";
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        final Player partyMember = this.getRandomPartyMember(player, 1);
        if (partyMember != null) {
            final QuestState st = this.getQuestState(partyMember, false);
            if (st.isCond(1) && getRandom(1000) < Q00300_HuntingLetoLizardman.MOBS_SAC.get(npc.getId())) {
                giveItems(player, 7139, 1L);
                if (getQuestItemsCount(player, 7139) == 60L) {
                    st.setCond(2, true);
                }
                else {
                    playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                }
            }
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        Label_0121: {
            switch (st.getState()) {
                case 0: {
                    htmltext = ((player.getLevel() >= 34) ? "30126-01.htm" : "30126-02.htm");
                    break;
                }
                case 1: {
                    switch (st.getCond()) {
                        case 1: {
                            htmltext = "30126-04.html";
                            break Label_0121;
                        }
                        case 2: {
                            if (getQuestItemsCount(player, 7139) >= 60L) {
                                htmltext = "30126-05.html";
                                break Label_0121;
                            }
                            break Label_0121;
                        }
                    }
                    break;
                }
            }
        }
        return htmltext;
    }
    
    static {
        REWARD_ADENA = new ItemHolder(57, 5000L);
        REWARD_ANIMAL_BONE = new ItemHolder(1872, 50L);
        REWARD_ANIMAL_SKIN = new ItemHolder(1867, 50L);
        (MOBS_SAC = (IntIntMap)new HashIntIntMap()).put(20577, 360);
        Q00300_HuntingLetoLizardman.MOBS_SAC.put(20578, 390);
        Q00300_HuntingLetoLizardman.MOBS_SAC.put(20579, 410);
        Q00300_HuntingLetoLizardman.MOBS_SAC.put(20580, 790);
        Q00300_HuntingLetoLizardman.MOBS_SAC.put(20582, 890);
    }
}
