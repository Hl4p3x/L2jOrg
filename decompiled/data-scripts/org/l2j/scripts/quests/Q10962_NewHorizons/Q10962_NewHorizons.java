// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10962_NewHorizons;

import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import java.util.HashSet;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import java.util.Set;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.data.xml.CategoryManager;
import java.util.Objects;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public final class Q10962_NewHorizons extends Quest
{
    private static final int LEAHEN = 34111;
    private static final int CAPTAIN_BATHIS = 30332;
    private static final int MOUNTAIN_WEREWORLF = 21985;
    private static final int MOUNTAIN_FUNGUES = 21986;
    private static final int MUERTOS_WARRIOR = 21987;
    private static final int MUERTOS_CAPTAIN = 21988;
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
    
    public Q10962_NewHorizons() {
        super(10962);
        this.addCondClassIds(new ClassId[] { ClassId.JIN_KAMAEL_SOLDIER });
        this.addStartNpc(34111);
        this.addTalkId(new int[] { 34111, 30332 });
        this.addKillId(new int[] { 21985, 21986, 21987, 21988 });
        this.addCondLevel(15, 20, "no_lvl.html");
        this.setQuestNameNpcStringId(NpcStringId.LV_15_20_NEW_HORIZONS);
    }
    
    public boolean checkPartyMember(final Player player, final Npc npc) {
        return Util.falseIfNullOrElse((Object)this.getQuestState(player, false), QuestState::isStarted);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState state = this.getQuestState(player, false);
        if (Objects.isNull(state)) {
            return null;
        }
        String s = null;
        switch (event) {
            case "30332-01.html":
            case "30332-02.html":
            case "30332-03.html":
            case "30332.html": {
                s = event;
                break;
            }
            case "NEXT_QUEST": {
                s = "34111.htm";
                break;
            }
            case "34111-01.html": {
                state.startQuest();
                s = event;
                break;
            }
            case "TELEPORT_TO_HUNTING_GROUND": {
                player.teleToLocation(-107827, 47535, -1448);
                s = null;
                break;
            }
            case "HeavyArmor.html": {
                if (state.isStarted()) {
                    this.giveHeavyRewards(player);
                    state.exitQuest(false, true);
                    s = event;
                    break;
                }
                s = null;
                break;
            }
            case "LightArmor.html": {
                if (state.isStarted()) {
                    this.giveLightRewards(player);
                    state.exitQuest(false, true);
                    s = event;
                    break;
                }
                s = null;
                break;
            }
            case "Robe.html": {
                if (state.isStarted()) {
                    this.giveRobeRewards(player);
                    state.exitQuest(false, true);
                    s = event;
                    break;
                }
                s = null;
                break;
            }
            default: {
                s = null;
                break;
            }
        }
        return s;
    }
    
    private void giveRobeRewards(final Player player) {
        this.giveCommonRewards(player);
        giveItems(player, Q10962_NewHorizons.MOON_CAPE);
        giveItems(player, Q10962_NewHorizons.MOON_SILK);
        giveItems(player, Q10962_NewHorizons.MOON_SANDALS);
        this.checkClassChange(player);
    }
    
    private void checkClassChange(final Player player) {
        if (CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId())) {
            showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, 2, 10000, new String[0]);
            player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
        }
    }
    
    private void giveLightRewards(final Player player) {
        this.giveCommonRewards(player);
        giveItems(player, Q10962_NewHorizons.MOON_SHELL);
        giveItems(player, Q10962_NewHorizons.MOON_LEATHER_GLOVES);
        giveItems(player, Q10962_NewHorizons.MOON_SHOES);
        this.checkClassChange(player);
    }
    
    private void giveHeavyRewards(final Player player) {
        this.giveCommonRewards(player);
        giveItems(player, Q10962_NewHorizons.MOON_ARMOR);
        giveItems(player, Q10962_NewHorizons.MOON_GAUNTLETS);
        giveItems(player, Q10962_NewHorizons.MOON_BOOTS);
        this.checkClassChange(player);
    }
    
    private void giveCommonRewards(final Player player) {
        addExpAndSp(player, 600000L, 13500);
        giveItems(player, Q10962_NewHorizons.SOE_NOVICE);
        giveItems(player, Q10962_NewHorizons.SPIRIT_ORE);
        giveItems(player, Q10962_NewHorizons.HP_POTS);
        giveItems(player, Q10962_NewHorizons.RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT);
        giveItems(player, Q10962_NewHorizons.MOON_HELMET);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState state = this.getQuestState(killer, false);
        if (Objects.nonNull(state) && state.isCond(1)) {
            final int killCount = state.getInt("KillCount") + 1;
            if (killCount < 30) {
                state.set("KillCount", killCount);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                this.sendNpcLogList(killer);
            }
            else {
                state.setCond(2, true);
                state.unset("KillCount");
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("You hunted all monsters.#Use the Scroll of Escape in you inventory to go to Captain Bathis in the Town of Gludio.", 5000) });
                giveItems(killer, Q10962_NewHorizons.SOE_TO_CAPTAIN_BATHIS);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_GOLDEN_HILLS.getId(), true, qs.getInt("KillCount")));
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
            htmltext = "34111.htm";
        }
        else if (qs.isStarted()) {
            switch (npc.getId()) {
                case 34111: {
                    if (qs.isCond(1)) {
                        htmltext = "34111-01.html";
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
        else if (qs.isCompleted() && npc.getId() == 34111) {
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
