// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10984_CollectSpiderweb;

import io.github.joealisson.primitive.HashIntIntMap;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import java.util.Objects;
import java.util.HashSet;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import java.util.Set;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.ItemHolder;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.model.quest.Quest;

public class Q10984_CollectSpiderweb extends Quest
{
    private static final int HERBIEL = 30150;
    private static final int CAPTAIN_BATHIS = 30332;
    private static final int HOOK_SPIDER = 20308;
    private static final int CRIMSON_SPIDER = 20460;
    private static final int PINCER_SPIDER = 20466;
    private static final int BIGSPIDERWEB = 91652;
    private static final IntIntMap MONSTER_DROP_CHANCES;
    private static final ItemHolder SOE_TO_CAPTAIN_BATHIS;
    private static final ItemHolder SOE_NOVICE;
    private static final ItemHolder SPIRIT_ORE;
    private static final ItemHolder HP_POTS;
    private static final ItemHolder RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT;
    private static final ItemHolder MOON_HELMET;
    private static final ItemHolder MOON_ARMOR;
    private static final ItemHolder MOON_GAUNTLETS;
    private static final ItemHolder MOON_BOOTS;
    private static final ItemHolder MOON_SHELL;
    private static final ItemHolder MOON_LEATHER_GLOVES;
    private static final ItemHolder MOON_SHOES;
    private static final ItemHolder MOON_CAPE;
    private static final ItemHolder MOON_SILK;
    private static final ItemHolder MOON_SANDALS;
    private static final String KILL_COUNT_VAR = "KillCount";
    
    public Q10984_CollectSpiderweb() {
        super(10984);
        this.addCondClassIds(new ClassId[] { ClassId.ELVEN_FIGHTER, ClassId.ELVEN_MAGE });
        this.addStartNpc(30150);
        this.addTalkId(new int[] { 30150, 30332 });
        this.addKillId((IntCollection)Q10984_CollectSpiderweb.MONSTER_DROP_CHANCES.keySet());
        this.registerQuestItems(new int[] { 91652 });
        this.addCondLevel(15, 20, "no_lvl.html");
        this.setQuestNameNpcStringId(NpcStringId.LV_15_20_SPIDER_WEB);
    }
    
    public boolean checkPartyMember(final Player member, final Npc npc) {
        final QuestState qs = this.getQuestState(member, false);
        return qs != null && qs.isStarted();
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30150-01.html": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "30332-01.html": {
                htmltext = event;
                break;
            }
            case "30332-02.html": {
                htmltext = event;
                break;
            }
            case "30332-03.html": {
                htmltext = event;
                break;
            }
            case "30332.html": {
                htmltext = event;
                break;
            }
            case "TELEPORT_TO_HUNTING_GROUND": {
                player.teleToLocation(5135, 68148, -3256);
                break;
            }
            case "NEXT_QUEST": {
                htmltext = "30150.htm";
                break;
            }
            case "HeavyArmor.html": {
                if (qs.isStarted()) {
                    addExpAndSp(player, 600000L, 13500);
                    giveItems(player, Q10984_CollectSpiderweb.SOE_NOVICE);
                    giveItems(player, Q10984_CollectSpiderweb.SPIRIT_ORE);
                    giveItems(player, Q10984_CollectSpiderweb.HP_POTS);
                    giveItems(player, Q10984_CollectSpiderweb.RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_HELMET);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_ARMOR);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_GAUNTLETS);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_BOOTS);
                    if (CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId())) {
                        showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, 2, 10000, new String[0]);
                        player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
                    }
                    qs.exitQuest(false, true);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "LightArmor.html": {
                if (qs.isStarted()) {
                    addExpAndSp(player, 600000L, 13500);
                    giveItems(player, Q10984_CollectSpiderweb.SOE_NOVICE);
                    giveItems(player, Q10984_CollectSpiderweb.SPIRIT_ORE);
                    giveItems(player, Q10984_CollectSpiderweb.HP_POTS);
                    giveItems(player, Q10984_CollectSpiderweb.RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_HELMET);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_SHELL);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_LEATHER_GLOVES);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_SHOES);
                    if (CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId())) {
                        showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, 2, 10000, new String[0]);
                        player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
                    }
                    qs.exitQuest(false, true);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "Robe.html": {
                if (qs.isStarted()) {
                    addExpAndSp(player, 600000L, 13500);
                    giveItems(player, Q10984_CollectSpiderweb.SOE_NOVICE);
                    giveItems(player, Q10984_CollectSpiderweb.SPIRIT_ORE);
                    giveItems(player, Q10984_CollectSpiderweb.HP_POTS);
                    giveItems(player, Q10984_CollectSpiderweb.RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_HELMET);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_CAPE);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_SILK);
                    giveItems(player, Q10984_CollectSpiderweb.MOON_SANDALS);
                    if (CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId())) {
                        showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, 2, 10000, new String[0]);
                        player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
                    }
                    qs.exitQuest(false, true);
                    htmltext = event;
                    break;
                }
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState qs = this.getQuestState(killer, false);
        if (qs != null && qs.isCond(1)) {
            final int killCount = qs.getInt("KillCount") + 1;
            if (Rnd.get(100) < Q10984_CollectSpiderweb.MONSTER_DROP_CHANCES.get(npc.getId())) {
                giveItems(killer, 91652, 1L);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
            }
            if (killCount < 30) {
                qs.set("KillCount", killCount);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                this.sendNpcLogList(killer);
            }
            else {
                qs.setCond(2, true);
                qs.unset("KillCount");
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("You hunted all monsters.#Use the Scroll of Escape in you inventory to go to Captain Bathis in the Town of Gludio.", 5000) });
                giveItems(killer, Q10984_CollectSpiderweb.SOE_TO_CAPTAIN_BATHIS);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.LV_15_20_SPIDER_WEB_IN_PROGRESS.getId(), true, qs.getInt("KillCount")));
            return holder;
        }
        return (Set<NpcLogListHolder>)super.getNpcLogList(player);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (Objects.isNull(qs)) {
            return htmltext;
        }
        if (qs.isCreated()) {
            htmltext = "30150.htm";
        }
        else if (qs.isStarted()) {
            switch (npc.getId()) {
                case 30150: {
                    if (qs.isCond(1)) {
                        htmltext = "30150-01.html";
                        break;
                    }
                    break;
                }
                case 30332: {
                    if (qs.isCond(2)) {
                        htmltext = "30332.html";
                        break;
                    }
                    break;
                }
            }
        }
        else if (qs.isCompleted() && npc.getId() == 30150) {
            htmltext = getAlreadyCompletedMsg(player);
        }
        return htmltext;
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
        if (!CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId())) {
            return;
        }
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCompleted()) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
        }
    }
    
    static {
        (MONSTER_DROP_CHANCES = (IntIntMap)new HashIntIntMap()).put(20308, 100);
        Q10984_CollectSpiderweb.MONSTER_DROP_CHANCES.put(20460, 100);
        Q10984_CollectSpiderweb.MONSTER_DROP_CHANCES.put(20466, 100);
        SOE_TO_CAPTAIN_BATHIS = new ItemHolder(91918, 1L);
        SOE_NOVICE = new ItemHolder(10650, 20L);
        SPIRIT_ORE = new ItemHolder(3031, 50L);
        HP_POTS = new ItemHolder(91912, 50L);
        RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT = new ItemHolder(91840, 1L);
        MOON_HELMET = new ItemHolder(7850, 1L);
        MOON_ARMOR = new ItemHolder(7851, 1L);
        MOON_GAUNTLETS = new ItemHolder(7852, 1L);
        MOON_BOOTS = new ItemHolder(7853, 1L);
        MOON_SHELL = new ItemHolder(7854, 1L);
        MOON_LEATHER_GLOVES = new ItemHolder(7855, 1L);
        MOON_SHOES = new ItemHolder(7856, 1L);
        MOON_CAPE = new ItemHolder(7857, 1L);
        MOON_SILK = new ItemHolder(7858, 1L);
        MOON_SANDALS = new ItemHolder(7859, 1L);
    }
}
