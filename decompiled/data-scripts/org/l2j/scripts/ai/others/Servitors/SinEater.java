// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.Servitors;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttacked;
import org.l2j.gameserver.model.events.annotations.Id;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class SinEater extends AbstractNpcAI
{
    private static final int SIN_EATER = 12564;
    
    private SinEater() {
        this.addSummonSpawnId(new int[] { 12564 });
        this.addSummonTalkId(new int[] { 12564 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equals("TALK") && player != null && player.getPet() != null) {
            if (Rnd.get(100) < 30) {
                final int random = Rnd.get(100);
                final Summon summon = (Summon)player.getPet();
                if (random < 20) {
                    this.broadcastSummonSay(summon, NpcStringId.YAWWWWN_IT_S_SO_BORING_HERE_WE_SHOULD_GO_AND_FIND_SOME_ACTION);
                }
                else if (random < 40) {
                    this.broadcastSummonSay(summon, NpcStringId.HEY_IF_YOU_CONTINUE_TO_WASTE_TIME_YOU_WILL_NEVER_FINISH_YOUR_PENANCE);
                }
                else if (random < 60) {
                    this.broadcastSummonSay(summon, NpcStringId.I_KNOW_YOU_DON_T_LIKE_ME_THE_FEELING_IS_MUTUAL);
                }
                else if (random < 80) {
                    this.broadcastSummonSay(summon, NpcStringId.I_NEED_A_DRINK);
                }
                else {
                    this.broadcastSummonSay(summon, NpcStringId.OH_THIS_IS_DRAGGING_ON_TOO_LONG_AT_THIS_RATE_I_WON_T_MAKE_IT_HOME_BEFORE_THE_SEVEN_SEALS_ARE_BROKEN);
                }
            }
            this.startQuestTimer("TALK", 60000L, (Npc)null, player);
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    @RegisterEvent(EventType.ON_CREATURE_DEATH)
    @RegisterType(ListenerRegisterType.NPC)
    @Id({ 12564 })
    public void onCreatureKill(final OnCreatureDeath event) {
        final int random = Rnd.get(100);
        final Summon summon = (Summon)event.getTarget();
        if (random < 30) {
            this.broadcastSummonSay(summon, NpcStringId.OH_THIS_IS_JUST_GREAT_WHAT_ARE_YOU_GOING_TO_DO_NOW);
        }
        else if (random < 70) {
            this.broadcastSummonSay(summon, NpcStringId.YOU_INCONSIDERATE_MORON_CAN_T_YOU_EVEN_TAKE_CARE_OF_LITTLE_OLD_ME);
        }
        else {
            this.broadcastSummonSay(summon, NpcStringId.OH_NO_THE_MAN_WHO_EATS_ONE_S_SINS_HAS_DIED_PENITENCE_IS_FURTHER_AWAY);
        }
    }
    
    @RegisterEvent(EventType.ON_CREATURE_ATTACKED)
    @RegisterType(ListenerRegisterType.NPC)
    @Id({ 12564 })
    public void onCreatureAttacked(final OnCreatureAttacked event) {
        if (Rnd.get(100) < 30) {
            final int random = Rnd.get(100);
            final Summon summon = (Summon)event.getTarget();
            if (random < 35) {
                this.broadcastSummonSay(summon, NpcStringId.OH_THAT_SMARTS);
            }
            else if (random < 70) {
                this.broadcastSummonSay(summon, NpcStringId.HEY_MASTER_PAY_ATTENTION_I_M_DYING_OVER_HERE);
            }
            else {
                this.broadcastSummonSay(summon, NpcStringId.WHAT_HAVE_I_DONE_TO_DESERVE_THIS);
            }
        }
    }
    
    public void onSummonSpawn(final Summon summon) {
        this.broadcastSummonSay(summon, Rnd.nextBoolean() ? NpcStringId.HEY_IT_SEEMS_LIKE_YOU_NEED_MY_HELP_DOESN_T_IT : NpcStringId.ALMOST_GOT_IT_OUCH_STOP_DAMN_THESE_BLOODY_MANACLES);
        this.startQuestTimer("TALK", 60000L, (Npc)null, summon.getOwner());
    }
    
    public void onSummonTalk(final Summon summon) {
        if (Rnd.get(100) < 10) {
            final int random = Rnd.get(100);
            if (random < 25) {
                this.broadcastSummonSay(summon, NpcStringId.USING_A_SPECIAL_SKILL_HERE_COULD_TRIGGER_A_BLOODBATH);
            }
            else if (random < 50) {
                this.broadcastSummonSay(summon, NpcStringId.HEY_WHAT_DO_YOU_EXPECT_OF_ME);
            }
            else if (random < 75) {
                this.broadcastSummonSay(summon, NpcStringId.UGGGGGH_PUSH_IT_S_NOT_COMING_OUT);
            }
            else {
                this.broadcastSummonSay(summon, NpcStringId.AH_I_MISSED_THE_MARK);
            }
        }
    }
    
    private void broadcastSummonSay(final Summon summon, final NpcStringId npcstringId) {
        summon.broadcastPacket((ServerPacket)new NpcSay(summon.getObjectId(), ChatType.NPC_GENERAL, summon.getId(), npcstringId));
    }
    
    public static AbstractNpcAI provider() {
        return new SinEater();
    }
}
