// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances.MonsterArena;

import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.Clan;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Collection;
import org.l2j.scripts.instances.AbstractInstance;

public class MonsterArena extends AbstractInstance
{
    private static final int LEO = 30202;
    private static final int MACHINE = 30203;
    private static final int SUPPLIES = 30204;
    private static final int[] BOSSES;
    private static final int BATTLE_BOX_1 = 70917;
    private static final int BATTLE_BOX_2 = 70918;
    private static final int BATTLE_BOX_3 = 70919;
    private static final int BATTLE_BOX_4 = 70920;
    private static final int TICKET_L = 90945;
    private static final int TICKET_M = 90946;
    private static final int TICKET_H = 90947;
    private static final Collection<Player> REWARDED_PLAYERS;
    private static final int TEMPLATE_ID = 192;
    
    private MonsterArena() {
        super(new int[] { 192 });
        this.addStartNpc(new int[] { 30202, 30203, 30204 });
        this.addFirstTalkId(new int[] { 30202, 30203, 30204 });
        this.addTalkId(new int[] { 30202, 30203, 30204 });
        this.addKillId(MonsterArena.BOSSES);
        this.addInstanceLeaveId(new int[] { 192 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "30202-01.htm":
            case "30202-02.htm":
            case "30202-03.htm":
            case "30203-01.htm": {
                return event;
            }
            case "enter_monster_arena": {
                if (player.getClan() != null && player.getCommandChannel() != null) {
                    for (final Player member : player.getCommandChannel().getMembers()) {
                        final Instance world = member.getInstanceWorld();
                        if (world != null && world.getTemplateId() == 192 && world.getPlayersCount() < 40 && player.getClanId() == member.getClanId()) {
                            player.teleToLocation((ILocational)world.getNpc(30203), true, world);
                            if (world.getStatus() > 0 && world.getStatus() < 5) {
                                player.sendPacket(new ServerPacket[] { (ServerPacket)new ExSendUIEvent(player, false, false, (int)(world.getRemainingTime() / 1000L), 0, NpcStringId.REMAINING_TIME, new String[0]) });
                            }
                            return null;
                        }
                    }
                }
                if (player.getClan() == null || player.getClan().getLeaderId() != player.getObjectId() || player.getCommandChannel() == null) {
                    return "30202-03.htm";
                }
                if (player.getClan().getLevel() < 3) {
                    player.sendMessage("Your clan must be at least level 3.");
                    return null;
                }
                for (final Player member : player.getCommandChannel().getMembers()) {
                    if (member.getClan() == null || member.getClanId() != player.getClanId()) {
                        player.sendMessage("Your command channel must be consisted only by clan members.");
                        return null;
                    }
                }
                this.enterInstance(player, npc, 192);
                final Instance world2 = player.getInstanceWorld();
                if (world2 != null) {
                    final Npc machine = world2.getNpc(30203);
                    machine.setScriptValue(player.getClanId());
                    if (GlobalVariablesManager.getInstance().getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, machine.getScriptValue()), -1) == -1) {
                        GlobalVariablesManager.getInstance().set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, machine.getScriptValue()), 1);
                    }
                    final int progress = GlobalVariablesManager.getInstance().getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, machine.getScriptValue()));
                    if (progress > 17) {
                        GlobalVariablesManager.getInstance().set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, machine.getScriptValue()), 17);
                    }
                    this.startQuestTimer("machine_talk", 10000L, machine, (Player)null);
                    this.startQuestTimer("start_countdown", 60000L, machine, (Player)null);
                    this.startQuestTimer("next_spawn", 60000L, machine, (Player)null);
                    break;
                }
                break;
            }
            case "machine_talk": {
                final Instance world2 = npc.getInstanceWorld();
                if (world2 != null) {
                    npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WELCOME_TO_THE_ARENA_TEST_YOUR_CLAN_S_STRENGTH, new String[0]);
                    break;
                }
                break;
            }
            case "start_countdown": {
                final Instance world2 = npc.getInstanceWorld();
                if (world2 != null) {
                    world2.setStatus(1);
                    for (final Player plr : world2.getPlayers()) {
                        plr.sendPacket(new ServerPacket[] { (ServerPacket)new ExSendUIEvent(plr, false, false, 1800, 0, NpcStringId.REMAINING_TIME, new String[0]) });
                    }
                    break;
                }
                break;
            }
            case "next_spawn": {
                final Instance world2 = npc.getInstanceWorld();
                if (world2 != null) {
                    world2.spawnGroup(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GlobalVariablesManager.getInstance().getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getScriptValue()))));
                    break;
                }
                break;
            }
            case "supply_reward": {
                final Instance world2 = npc.getInstanceWorld();
                if (world2 != null && npc.getId() == 30204 && player.getLevel() > 39 && !MonsterArena.REWARDED_PLAYERS.contains(player) && npc.isScriptValue(0)) {
                    npc.setScriptValue(1);
                    npc.doDie((Creature)npc);
                    MonsterArena.REWARDED_PLAYERS.add(player);
                    ThreadPool.schedule(() -> MonsterArena.REWARDED_PLAYERS.remove(player), 60000L);
                    final Npc machine = world2.getNpc(30203);
                    final int progress = GlobalVariablesManager.getInstance().getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, machine.getScriptValue()));
                    if (progress > 16) {
                        giveItems(player, 70920, 1L);
                    }
                    else if (progress > 11) {
                        giveItems(player, 70919, 1L);
                    }
                    else if (progress > 6) {
                        giveItems(player, 70918, 1L);
                    }
                    else {
                        giveItems(player, 70917, 1L);
                    }
                    if (getRandom(100) < 1) {
                        giveItems(player, 90945, 1L);
                    }
                    else if (getRandom(100) < 1) {
                        giveItems(player, 90946, 1L);
                    }
                    else if (getRandom(100) < 1) {
                        giveItems(player, 90947, 1L);
                    }
                    break;
                }
                break;
            }
            case "remove_supplies": {
                final Instance world2 = npc.getInstanceWorld();
                if (world2 != null) {
                    for (final Npc aliveNpc : world2.getAliveNpcs()) {
                        if (aliveNpc != null && aliveNpc.getId() == 30204) {
                            aliveNpc.deleteMe();
                        }
                    }
                    break;
                }
                break;
            }
        }
        return null;
    }
    
    public void onInstanceLeave(final Player player, final Instance instance) {
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExSendUIEvent(player, false, false, 0, 0, NpcStringId.REMAINING_TIME, new String[0]) });
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        final Instance world = npc.getInstanceWorld();
        if (world != null) {
            world.incStatus();
            final Npc machine = world.getNpc(30203);
            machine.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HA_NOT_BAD, new String[0]);
            GlobalVariablesManager.getInstance().increaseInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, machine.getScriptValue()), 1);
            Util.doIfNonNull((Object)player.getClan(), clan -> ClanRewardManager.getInstance().checkArenaProgress(clan));
            world.spawnGroup("supplies");
            this.startQuestTimer("remove_supplies", 60000L, machine, (Player)null);
            if (world.getStatus() < 5) {
                this.startQuestTimer("next_spawn", 60000L, machine, (Player)null);
            }
            else {
                for (final Player plr : world.getPlayers()) {
                    plr.sendPacket(new ServerPacket[] { (ServerPacket)new ExSendUIEvent(plr, false, false, 0, 0, NpcStringId.REMAINING_TIME, new String[0]) });
                }
                world.finishInstance();
            }
        }
        return super.onKill(npc, player, isSummon);
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    public static AbstractInstance provider() {
        return new MonsterArena();
    }
    
    static {
        BOSSES = new int[] { 25794, 25795, 25796, 25797, 25798, 25799, 25800, 25801, 25802, 25803, 25804, 25805, 25806, 25807, 25808, 25809, 25810, 25811, 25812, 25813 };
        REWARDED_PLAYERS = ConcurrentHashMap.newKeySet();
    }
}
