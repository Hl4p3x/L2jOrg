// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00327_RecoverTheFarmland;

import java.util.HashMap;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Map;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00327_RecoverTheFarmland extends Quest
{
    private static final int IRIS = 30034;
    private static final int ASHA = 30313;
    private static final int NESTLE = 30314;
    private static final int LEIKAN = 30382;
    private static final int PIOTUR = 30597;
    private static final int TUREK_ORK_WARLORD = 20495;
    private static final int TUREK_ORK_ARCHER = 20496;
    private static final int TUREK_ORK_SKIRMISHER = 20497;
    private static final int TUREK_ORK_SUPPLIER = 20498;
    private static final int TUREK_ORK_FOOTMAN = 20499;
    private static final int TUREK_ORK_SENTINEL = 20500;
    private static final int TUREK_ORK_SHAMAN = 20501;
    private static final int TUREK_DOG_TAG = 1846;
    private static final int TUREK_MEDALLION = 1847;
    private static final int LEIKANS_LETTER = 5012;
    private static final int CLAY_URN_FRAGMENT = 1848;
    private static final int BRASS_TRINKET_PIECE = 1849;
    private static final int BRONZE_MIRROR_PIECE = 1850;
    private static final int JADE_NECKLACE_BEAD = 1851;
    private static final int ANCIENT_CLAY_URN = 1852;
    private static final int ANCIENT_BRASS_TIARA = 1853;
    private static final int ANCIENT_BRONZE_MIRROR = 1854;
    private static final int ANCIENT_JADE_NECKLACE = 1855;
    private static final int QUICK_STEP_POTION = 734;
    private static final int SWIFT_ATTACK_POTION = 735;
    private static final int SCROLL_OF_ESCAPE = 736;
    private static final int SCROLL_OF_RESURRECTION = 737;
    private static final int HEALING_POTION = 1061;
    private static final int SOULSHOT_D = 1463;
    private static final int SPIRITSHOT_D = 2510;
    private static final int MIN_LVL = 25;
    private static final Map<String, ItemHolder> FRAGMENTS_REWARD_DATA;
    private static final Map<Integer, Integer> FRAGMENTS_DROP_PROB;
    private static final ItemHolder[] FULL_REWARD_DATA;
    
    public Q00327_RecoverTheFarmland() {
        super(327);
        this.addStartNpc(new int[] { 30382, 30597 });
        this.addTalkId(new int[] { 30382, 30597, 30034, 30313, 30314 });
        this.addKillId(new int[] { 20495, 20496, 20497, 20498, 20499, 20500, 20501 });
        this.registerQuestItems(new int[] { 1846, 1847, 5012 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        if (st == null) {
            return null;
        }
        String html = null;
        switch (event) {
            case "30034-01.html":
            case "30313-01.html":
            case "30314-02.html":
            case "30314-08.html":
            case "30314-09.html":
            case "30382-05a.html":
            case "30382-05b.html":
            case "30597-03.html":
            case "30597-07.html": {
                html = event;
                break;
            }
            case "30382-03.htm": {
                st.startQuest();
                giveItems(player, 5012, 1L);
                st.setCond(2);
                html = event;
                break;
            }
            case "30597-03.htm": {
                st.startQuest();
                html = event;
                break;
            }
            case "30597-06.html": {
                st.exitQuest(true, true);
                html = event;
                break;
            }
            case "30034-03.html":
            case "30034-04.html":
            case "30034-05.html":
            case "30034-06.html": {
                final ItemHolder item = Q00327_RecoverTheFarmland.FRAGMENTS_REWARD_DATA.get(event);
                if (!hasQuestItems(player, item.getId())) {
                    html = "30034-02.html";
                    break;
                }
                addExpAndSp(player, getQuestItemsCount(player, item.getId()) * item.getCount(), 0);
                takeItems(player, item.getId(), -1L);
                playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                html = event;
                break;
            }
            case "30034-07.html": {
                boolean rewarded = false;
                for (final ItemHolder it : Q00327_RecoverTheFarmland.FULL_REWARD_DATA) {
                    if (hasQuestItems(player, it.getId())) {
                        addExpAndSp(player, getQuestItemsCount(player, it.getId()) * it.getCount(), 0);
                        takeItems(player, it.getId(), -1L);
                        playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                        rewarded = true;
                    }
                }
                html = (rewarded ? event : "30034-02.html");
                break;
            }
            case "30313-03.html": {
                if (getQuestItemsCount(player, 1848) < 5L) {
                    html = "30313-02.html";
                    break;
                }
                takeItems(player, 1848, 5L);
                if (getRandom(6) < 5) {
                    giveItems(player, 1852, 1L);
                    html = event;
                    break;
                }
                html = "30313-10.html";
                break;
            }
            case "30313-05.html": {
                if (getQuestItemsCount(player, 1849) < 5L) {
                    html = "30313-04.html";
                    break;
                }
                takeItems(player, 1849, 5L);
                if (getRandom(7) < 6) {
                    giveItems(player, 1853, 1L);
                    html = event;
                    break;
                }
                html = "30313-10.html";
                break;
            }
            case "30313-07.html": {
                if (getQuestItemsCount(player, 1850) < 5L) {
                    html = "30313-06.html";
                    break;
                }
                takeItems(player, 1850, 5L);
                if (getRandom(7) < 6) {
                    giveItems(player, 1854, 1L);
                    html = event;
                    break;
                }
                html = "30313-10.html";
                break;
            }
            case "30313-09.html": {
                if (getQuestItemsCount(player, 1851) < 5L) {
                    html = "30313-08.html";
                    break;
                }
                takeItems(player, 1851, 5L);
                if (getRandom(8) < 7) {
                    giveItems(player, 1855, 1L);
                    html = event;
                    break;
                }
                html = "30313-10.html";
                break;
            }
            case "30314-03.html": {
                if (!hasQuestItems(player, 1852)) {
                    html = "30314-07.html";
                    break;
                }
                rewardItems(player, 1463, (long)getRandom(70, 110));
                takeItems(player, 1852, 1L);
                html = event;
                break;
            }
            case "30314-04.html": {
                if (!hasQuestItems(player, 1853)) {
                    html = "30314-07.html";
                    break;
                }
                final int rnd = getRandom(100);
                if (rnd < 40) {
                    rewardItems(player, 1061, 1L);
                }
                else if (rnd < 84) {
                    rewardItems(player, 734, 1L);
                }
                else {
                    rewardItems(player, 735, 1L);
                }
                takeItems(player, 1853, 1L);
                html = event;
                break;
            }
            case "30314-05.html": {
                if (!hasQuestItems(player, 1854)) {
                    html = "30314-07.html";
                    break;
                }
                rewardItems(player, (getRandom(100) < 59) ? 736 : 737, 1L);
                takeItems(player, 1854, 1L);
                html = event;
                break;
            }
            case "30314-06.html": {
                if (!hasQuestItems(player, 1855)) {
                    html = "30314-07.html";
                    break;
                }
                rewardItems(player, 2510, (long)getRandom(50, 90));
                takeItems(player, 1855, 1L);
                html = event;
                break;
            }
        }
        return html;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState st = this.getQuestState(killer, false);
        if (st != null) {
            if (npc.getId() == 20501 || npc.getId() == 20495) {
                giveItems(killer, 1847, 1L);
            }
            else {
                giveItems(killer, 1846, 1L);
            }
            if (getRandom(100) < Q00327_RecoverTheFarmland.FRAGMENTS_DROP_PROB.get(npc.getId())) {
                giveItems(killer, getRandom(1848, 1851), 1L);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String html = getNoQuestMsg(player);
        switch (npc.getId()) {
            case 30382: {
                if (st.isCreated()) {
                    html = ((player.getLevel() >= 25) ? "30382-02.htm" : "30382-01.htm");
                    break;
                }
                if (!st.isStarted()) {
                    break;
                }
                if (hasQuestItems(player, 5012)) {
                    html = "30382-04.html";
                    break;
                }
                html = "30382-05.html";
                st.setCond(5, true);
                break;
            }
            case 30597: {
                if (st.isCreated()) {
                    html = ((player.getLevel() >= 25) ? "30597-02.htm" : "30597-01.htm");
                    break;
                }
                if (!st.isStarted()) {
                    break;
                }
                if (hasQuestItems(player, 5012)) {
                    html = "30597-03a.htm";
                    takeItems(player, 5012, -1L);
                    st.setCond(3, true);
                    break;
                }
                if (!hasQuestItems(player, 1846) && !hasQuestItems(player, 1847)) {
                    html = "30597-04.html";
                    break;
                }
                html = "30597-05.html";
                final long dogTags = getQuestItemsCount(player, 1846);
                final long medallions = getQuestItemsCount(player, 1847);
                final long rewardCount = dogTags * 8L + medallions * 8L + ((dogTags + medallions >= 10L) ? 1000 : 0);
                this.giveAdena(player, rewardCount, true);
                takeItems(player, 1846, -1L);
                takeItems(player, 1847, -1L);
                st.setCond(4, true);
                break;
            }
            case 30034: {
                if (st.isStarted()) {
                    html = "30034-01.html";
                    break;
                }
                break;
            }
            case 30313: {
                if (st.isStarted()) {
                    html = "30313-01.html";
                    break;
                }
                break;
            }
            case 30314: {
                if (st.isStarted()) {
                    html = "30314-01.html";
                    break;
                }
                break;
            }
        }
        return html;
    }
    
    static {
        FRAGMENTS_REWARD_DATA = new HashMap<String, ItemHolder>(4);
        FRAGMENTS_DROP_PROB = new HashMap<Integer, Integer>(7);
        FULL_REWARD_DATA = new ItemHolder[] { new ItemHolder(1852, 2766L), new ItemHolder(1853, 3227L), new ItemHolder(1854, 3227L), new ItemHolder(1855, 3919L) };
        Q00327_RecoverTheFarmland.FRAGMENTS_REWARD_DATA.put("30034-03.html", new ItemHolder(1848, 307L));
        Q00327_RecoverTheFarmland.FRAGMENTS_REWARD_DATA.put("30034-04.html", new ItemHolder(1849, 368L));
        Q00327_RecoverTheFarmland.FRAGMENTS_REWARD_DATA.put("30034-05.html", new ItemHolder(1850, 368L));
        Q00327_RecoverTheFarmland.FRAGMENTS_REWARD_DATA.put("30034-06.html", new ItemHolder(1851, 430L));
        Q00327_RecoverTheFarmland.FRAGMENTS_DROP_PROB.put(20496, 21);
        Q00327_RecoverTheFarmland.FRAGMENTS_DROP_PROB.put(20499, 19);
        Q00327_RecoverTheFarmland.FRAGMENTS_DROP_PROB.put(20500, 18);
        Q00327_RecoverTheFarmland.FRAGMENTS_DROP_PROB.put(20501, 22);
        Q00327_RecoverTheFarmland.FRAGMENTS_DROP_PROB.put(20497, 21);
        Q00327_RecoverTheFarmland.FRAGMENTS_DROP_PROB.put(20498, 20);
        Q00327_RecoverTheFarmland.FRAGMENTS_DROP_PROB.put(20495, 26);
    }
}
