// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.tutorial;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTutorialEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.html.TutorialShowHtml;
import org.l2j.gameserver.network.serverpackets.html.TutorialWindowType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import org.l2j.gameserver.network.serverpackets.TutorialShowQuestionMark;
import org.l2j.gameserver.network.serverpackets.TutorialEnableClientEvent;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.NpcStringId;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public abstract class Tutorial extends Quest
{
    private static final int BLUE_GEM = 6353;
    private static final int QUESTION_MARK_ID_1 = 1;
    private static final int QUESTION_MARK_ID_2 = 5;
    private static final int QUESTION_MARK_ID_3 = 28;
    private static final String RADAR_HTM = "..\\L2text_Classic\\QT_001_Radar_01.htm";
    private static final ItemHolder SOULSHOT_REWARD;
    private static final ItemHolder SPIRITSHOT_REWARD;
    private static final ItemHolder ESCAPE_REWARD;
    private static final ItemHolder WIND_WALK_POTION;
    private static final int[] GREMLINS;
    private final String TUTORIAL_BYPASS;
    
    public Tutorial(final int questId, final ClassId... classIds) {
        super(questId);
        this.TUTORIAL_BYPASS = String.format("Quest %s ", this.getClass().getSimpleName());
        this.addCondClassIds(classIds);
        this.addFirstTalkId(this.newbieHelperId());
        this.addKillId(Tutorial.GREMLINS);
        this.registerQuestItems(new int[] { 6353 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState state = this.getQuestState(player, false);
        if (Objects.isNull(state)) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "start_newbie_tutorial": {
                if (state.getMemoState() == 0) {
                    state.startQuest();
                    showOnScreenMsg(player, NpcStringId.SPEAK_WITH_THE_NEWBIE_HELPER, 2, 5000, new String[0]);
                    final QuestSoundHtmlHolder startingEvent = this.startingVoiceHtml();
                    this.playTutorialVoice(player, startingEvent.getSound());
                    this.showTutorialHtml(player, startingEvent.getHtml());
                    break;
                }
                break;
            }
            case "tutorial_move.html": {
                player.sendPacket(new ServerPacket[] { (ServerPacket)new TutorialEnableClientEvent(1) });
                this.playTutorialVoice(player, "tutorial_voice_003");
                this.showTutorialHtml(player, event);
                break;
            }
            case "tutorial_exit.html": {
                player.sendPacket(new ServerPacket[] { (ServerPacket)new TutorialEnableClientEvent(0) });
                this.showTutorialHtml(player, event);
                break;
            }
            case "8":
            case "question_mark_1": {
                if (state.getMemoState() < 3) {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new TutorialShowQuestionMark(1, 0), (ServerPacket)TutorialCloseHtml.STATIC_PACKET });
                }
                player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
                break;
            }
            case "close_tutorial": {
                player.sendPacket(new ServerPacket[] { (ServerPacket)TutorialCloseHtml.STATIC_PACKET });
                player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
                break;
            }
            case "reward_2": {
                if (state.isMemoState(4)) {
                    state.setMemoState(5);
                    if (player.isMageClass() && player.getRace() != Race.ORC) {
                        giveItems(player, Tutorial.SPIRITSHOT_REWARD);
                        this.playTutorialVoice(player, "tutorial_voice_027");
                    }
                    else {
                        giveItems(player, Tutorial.SOULSHOT_REWARD);
                        this.playTutorialVoice(player, "tutorial_voice_026");
                    }
                    htmltext = "go_village.html";
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new TutorialShowQuestionMark(28, 0) });
                    break;
                }
                break;
            }
            case "1": {
                if (state.getMemoState() < 2) {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new TutorialEnableClientEvent(2) });
                    this.playTutorialVoice(player, "tutorial_voice_004");
                    this.showTutorialHtml(player, "tutorial_point_view.html");
                    break;
                }
                break;
            }
            case "2": {
                if (state.getMemoState() < 2) {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new TutorialEnableClientEvent(8) });
                    this.playTutorialVoice(player, "tutorial_voice_005");
                    this.showTutorialHtml(player, "tutorial_init_point_view.html");
                    break;
                }
                break;
            }
            case "go_to_newbie_helper": {
                player.teleToLocation(this.villageLocation());
                state.setState((byte)2);
                break;
            }
        }
        return htmltext;
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        final QuestState questState = this.getQuestState(player, false);
        if (Objects.isNull(questState)) {
            return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        if (npc.getId() == this.newbieHelperId()) {
            if (questState.isCompleted()) {
                return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            if (questState.getMemoState() < 3 && questState.getCond() == 3) {
                questState.setMemoState(3);
            }
            switch (questState.getMemoState()) {
                case 0: {
                    questState.setMemoState(1);
                    if (!player.isMageClass()) {
                        return "../kill_gremlins_fighter.html";
                    }
                    if (Race.ORC == player.getRace()) {
                        return "../kill_gremlins_orc_mystic.html";
                    }
                    return "../kill_gremlins_mystic.html";
                }
                case 1: {
                    if (!player.isMageClass()) {
                        return "../fighter_back.html";
                    }
                    if (Race.ORC == player.getRace()) {
                        return "../mystic_orc_back.html";
                    }
                    return "../mystic_back.html";
                }
                case 3: {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)TutorialCloseHtml.STATIC_PACKET });
                    player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
                    questState.setMemoState(4);
                    questState.setCond(3);
                    takeItems(player, 6353, -1L);
                    giveItems(player, Tutorial.ESCAPE_REWARD);
                    giveItems(player, Tutorial.WIND_WALK_POTION);
                    if (player.isMageClass() && player.getRace() != Race.ORC) {
                        giveItems(player, Tutorial.SPIRITSHOT_REWARD);
                        this.playTutorialVoice(player, "tutorial_voice_027");
                        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                    }
                    giveItems(player, Tutorial.SOULSHOT_REWARD);
                    this.playTutorialVoice(player, "tutorial_voice_026");
                    return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                case 4: {
                    return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                case 5:
                case 6: {
                    return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
        }
        else {
            if (questState.isCompleted()) {
                return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            switch (questState.getMemoState()) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                case 4: {
                    return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                case 5:
                case 6: {
                    return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
        }
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState questState = this.getQuestState(killer, false);
        if (Objects.nonNull(questState) && questState.getCond() < 2 && !hasQuestItems(killer, 6353) && getRandom(100) < 50) {
            killer.addItem("Quest", 6353, 1L, (WorldObject)killer, true);
            questState.setMemoState(3);
            questState.setCond(2);
            playSound(killer, "ItemSound.quest_tutorial");
            killer.sendPacket(new ServerPacket[] { (ServerPacket)new TutorialShowQuestionMark(5, 0) });
            killer.sendPacket(new ServerPacket[] { (ServerPacket)new TutorialShowHtml("..\\L2text_Classic\\QT_001_Radar_01.htm", TutorialWindowType.COMPOSITE) });
            this.playTutorialVoice(killer, "tutorial_voice_013");
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    protected void playTutorialVoice(final Player player, final String voice) {
        player.sendPacket(new ServerPacket[] { (ServerPacket)new PlaySound(2, voice, 0, 0, player.getX(), player.getY(), player.getZ()) });
    }
    
    protected void showTutorialHtml(final Player player, String fileName) {
        if (fileName.startsWith("tutorial_")) {
            fileName = fileName.replace("tutorial_", "../");
        }
        final String htm = this.getHtml(player, fileName).replace("%TutorialQuest%", this.getName());
        player.sendPacket(new ServerPacket[] { (ServerPacket)new TutorialShowHtml(htm) });
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogin(final OnPlayerLogin event) {
        if (Config.DISABLE_TUTORIAL) {
            return;
        }
        final Player player = event.getPlayer();
        if (player.getLevel() > 6) {
            return;
        }
        final QuestState qs = this.getQuestState(player, true);
        if (Objects.nonNull(qs) && qs.getMemoState() < 4) {
            this.startQuestTimer("start_newbie_tutorial", 2000L, (Npc)null, player);
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_BYPASS)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerBypass(final OnPlayerBypass event) {
        final String command = event.getCommand();
        if (command.startsWith(this.TUTORIAL_BYPASS)) {
            this.notifyEvent(command.replace(this.TUTORIAL_BYPASS, ""), (Npc)null, event.getPlayer());
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_TUTORIAL_EVENT)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnRequestTutorialEvent(final OnPlayerTutorialEvent event) {
        final Player player = event.getPlayer();
        final QuestState qs = this.getQuestState(player, false);
        if (Objects.isNull(qs)) {
            return;
        }
        this.notifyEvent(String.valueOf(event.getEventId()), (Npc)null, event.getPlayer());
    }
    
    @RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerPressTutorialMark(final OnPlayerPressTutorialMark event) {
        final Player player = event.getPlayer();
        final QuestState qs = this.getQuestState(player, false);
        if (Objects.isNull(qs)) {
            return;
        }
        switch (event.getMarkId()) {
            case 1: {
                if (qs.getCond() == 1) {
                    showOnScreenMsg(player, NpcStringId.SPEAK_WITH_THE_NEWBIE_HELPER, 2, 5000, new String[0]);
                    addRadar(player, (ILocational)this.helperLocation());
                    this.showTutorialHtml(player, "tutorial_newbie_helper.html");
                    this.playTutorialVoice(player, "tutorial_voice_007");
                    break;
                }
                break;
            }
            case 5: {
                if (qs.getCond() == 2) {
                    addRadar(event.getPlayer(), (ILocational)this.helperLocation());
                    this.showTutorialHtml(event.getPlayer(), "tutorial_gemstone_found.html");
                    break;
                }
                break;
            }
            case 28: {
                if (qs.getCond() == 3) {
                    addRadar(event.getPlayer(), this.villageLocation());
                    playSound(event.getPlayer(), "ItemSound.quest_tutorial");
                    qs.setState((byte)2);
                    break;
                }
                break;
            }
        }
    }
    
    protected abstract int newbieHelperId();
    
    protected abstract ILocational villageLocation();
    
    protected abstract QuestSoundHtmlHolder startingVoiceHtml();
    
    protected abstract Location helperLocation();
    
    static {
        SOULSHOT_REWARD = new ItemHolder(91927, 200L);
        SPIRITSHOT_REWARD = new ItemHolder(91928, 100L);
        ESCAPE_REWARD = new ItemHolder(10650, 5L);
        WIND_WALK_POTION = new ItemHolder(49036, 5L);
        GREMLINS = new int[] { 18342, 20001 };
    }
    
    protected static class QuestSoundHtmlHolder
    {
        private final String _sound;
        private final String _html;
        
        public QuestSoundHtmlHolder(final String sound, final String html) {
            this._sound = sound;
            this._html = html;
        }
        
        String getSound() {
            return this._sound;
        }
        
        String getHtml() {
            return this._html;
        }
    }
}
