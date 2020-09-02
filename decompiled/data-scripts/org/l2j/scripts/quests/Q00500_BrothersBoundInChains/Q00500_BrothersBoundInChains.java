// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00500_BrothersBoundInChains;

import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerUnsummonAgathion;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonAgathion;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.quest.Quest;

public class Q00500_BrothersBoundInChains extends Quest
{
    private static final int DARK_JUDGE = 30981;
    private static final int GEMSTONE_B = 2132;
    private static final int PENITENT_MANACLES = 70806;
    private static final int CRUMBS_OF_PENITENCE = 70807;
    private static final int HOUR_OF_PENITENCE = 55702;
    private static final int SIN_EATER = 9021;
    private static final String KILL_COUNT_VAR = "killCount";
    
    public Q00500_BrothersBoundInChains() {
        super(500);
        this.addStartNpc(30981);
        this.addTalkId(30981);
        this.registerQuestItems(new int[] { 70806, 70807 });
        Listeners.Global().addListener((AbstractEventListener)new ConsumerEventListener(Listeners.Global(), EventType.ON_PLAYER_SUMMON_AGATHION, event -> this.OnPlayerSummonAgathion(event), (Object)this));
        Listeners.Global().addListener((AbstractEventListener)new ConsumerEventListener(Listeners.Global(), EventType.ON_PLAYER_UNSUMMON_AGATHION, event -> this.OnPlayerUnsummonAgathion(event), (Object)this));
    }
    
    public String onAdvEvent(String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return getNoQuestMsg(player);
        }
        final String s = event;
        switch (s) {
            case "buff": {
                if (player != null && player.getAgathionId() == 9021) {
                    final Skill skill = SkillEngine.getInstance().getSkill(55702, 1);
                    skill.activateSkill((Creature)player, new WorldObject[] { (WorldObject)player });
                    this.startQuestTimer("buff", 270000L, (Npc)null, player);
                }
                return null;
            }
            case "30981-02.htm":
            case "30981-03.htm": {
                break;
            }
            case "30981-04.htm": {
                if (getQuestItemsCount(player, 2132) >= 30L) {
                    takeItems(player, 2132, 30L);
                    giveItems(player, 70806, 1L);
                    break;
                }
                event = "30981-05.html";
                break;
            }
            case "30981-06.htm": {
                qs.startQuest();
                break;
            }
            case "30981-09.html": {
                if (getQuestItemsCount(player, 70807) >= 35L) {
                    takeItems(player, 70807, -1L);
                    takeItems(player, 70806, -1L);
                    player.setPkKills(Math.max(0, player.getPkKills() - getRandom(1, 3)));
                    qs.unset("killCount");
                    qs.exitQuest(QuestType.DAILY, true);
                    break;
                }
                qs.setCond(1);
                event = "30981-07.html";
                break;
            }
            default: {
                event = getNoQuestMsg(player);
                break;
            }
        }
        return event;
    }
    
    public String onTalk(final Npc npc, final Player talker) {
        final QuestState qs = this.getQuestState(talker, true);
        String htmltext = getNoQuestMsg(talker);
        switch (qs.getState()) {
            case 0: {
                htmltext = ((talker.getPkKills() > 0 && talker.getReputation() >= 0) ? "30981-01.htm" : "30981-nopk.htm");
                break;
            }
            case 1: {
                switch (qs.getCond()) {
                    case 1: {
                        htmltext = "30981-07.html";
                        break;
                    }
                    case 2: {
                        htmltext = "30981-08.html";
                        break;
                    }
                }
                break;
            }
            case 2: {
                if (qs.isNowAvailable()) {
                    qs.setState((byte)0);
                    htmltext = "30981-01.htm";
                    break;
                }
                break;
            }
        }
        return htmltext;
    }
    
    private void OnPlayerSummonAgathion(final OnPlayerSummonAgathion event) {
        if (event.getAgathionId() != 9021) {
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return;
        }
        this.startQuestTimer("buff", 2500L, (Npc)null, player);
    }
    
    private void OnPlayerUnsummonAgathion(final OnPlayerUnsummonAgathion event) {
        if (event.getAgathionId() != 9021) {
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return;
        }
        this.cancelQuestTimer("buff", (Npc)null, player);
        player.getEffectList().stopSkillEffects(true, 55702);
    }
    
    @RegisterEvent(EventType.ON_ATTACKABLE_KILL)
    @RegisterType(ListenerRegisterType.GLOBAL_MONSTERS)
    public void onAttackableKill(final OnAttackableKill event) {
        final Player player = event.getAttacker();
        if (player == null || player.getAgathionId() != 9021 || !player.getEffectList().isAffectedBySkill(55702)) {
            return;
        }
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return;
        }
        final Attackable target = event.getTarget();
        if (target == null) {
            return;
        }
        if (target.getLevel() - player.getLevel() < -6) {
            return;
        }
        if (target.isRaid() || target.isRaidMinion()) {
            return;
        }
        if (player.getCommandChannel() != null) {
            return;
        }
        final int killCount = qs.getInt("killCount");
        if (killCount >= 20) {
            giveItems(player, 70807, 1L);
            qs.set("killCount", 0);
            if (!qs.isCond(2) && getQuestItemsCount(player, 70807) >= 35L) {
                qs.setCond(2, true);
            }
        }
        else {
            qs.set("killCount", killCount + 1);
        }
    }
}
