// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances.GolbergRoom;

import java.util.Iterator;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.scripts.instances.AbstractInstance;

public class GolbergRoom extends AbstractInstance
{
    private static final int SORA = 34091;
    private static final int GOLBERG = 18359;
    private static final int GOLBERG_TREASURE_CHEST = 18357;
    private static final int GOLBERG_KEY_ROOM = 91636;
    private static final int TEMPLATE_ID = 207;
    private Monster _golberg;
    private int _treasureCounter;
    
    public GolbergRoom() {
        super(new int[] { 207 });
        this.addStartNpc(34091);
        this.addKillId(new int[] { 18359, 18357 });
        this.addInstanceLeaveId(new int[] { 207 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        int n = -1;
        switch (event.hashCode()) {
            case 66129592: {
                if (event.equals("ENTER")) {
                    n = 0;
                    break;
                }
                break;
            }
            case 1145130516: {
                if (event.equals("GOLBERG_MOVE")) {
                    n = 1;
                    break;
                }
                break;
            }
            case -261598471: {
                if (event.equals("NEXT_TEXT")) {
                    n = 2;
                    break;
                }
                break;
            }
            case 2006942828: {
                if (event.equals("NEXT_TEXT_2")) {
                    n = 3;
                    break;
                }
                break;
            }
            case 870008120: {
                if (event.equals("SPAWN_TRESURE")) {
                    n = 4;
                    break;
                }
                break;
            }
        }
        Label_1262: {
            switch (n) {
                case 0: {
                    if (player.isGM()) {
                        this.enterInstance(player, npc, 207);
                        player.sendMessage("SYS: You have entered as GM/Admin to Goldberg instance");
                        break;
                    }
                    if (player.isGM()) {
                        break;
                    }
                    final Party party = player.getParty();
                    if (party == null) {
                        return "no_party.htm";
                    }
                    if (!hasQuestItems(player, 91636)) {
                        return "no_item.htm";
                    }
                    takeItems(player, 91636, 1L);
                    this.enterInstance(player, npc, 207);
                    final Instance world = player.getInstanceWorld();
                    if (world != null) {
                        for (final Player member : party.getMembers()) {
                            if (member == player) {
                                continue;
                            }
                            member.teleToLocation((ILocational)player, 10, world);
                        }
                        this.startQuestTimer("GOLBERG_MOVE", 5000L, (Npc)(this._golberg = (Monster)player.getInstanceWorld().getNpc(18359)), player);
                    }
                    break;
                }
                case 1: {
                    final Instance world2 = player.getInstanceWorld();
                    if (world2 != null) {
                        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("Rats have become kings while I've been dormant.", 5000) });
                        this.startQuestTimer("NEXT_TEXT", 7000L, (Npc)this._golberg, player);
                    }
                    npc.moveToLocation(11711, -86508, -10928, 0);
                    break;
                }
                case 2: {
                    final Instance world2 = player.getInstanceWorld();
                    if (world2 != null) {
                        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("Zaken or whatever is going wild all over the southern sea.", 5000) });
                        this.startQuestTimer("NEXT_TEXT_2", 7000L, (Npc)this._golberg, player);
                        break;
                    }
                    break;
                }
                case 3: {
                    final Instance world2 = player.getInstanceWorld();
                    if (world2 != null) {
                        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("Who dare enter my place? Zaken sent you?", 5000) });
                        break;
                    }
                    break;
                }
                case 4: {
                    if (player.isGM()) {
                        if (this._treasureCounter <= 27) {
                            addSpawn(18357, 11708 + getRandom(-1000, 1000), -86505 + getRandom(-1000, 1000), -10928, 0, true, -1L, true, player.getInstanceId());
                            this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                            ++this._treasureCounter;
                            break;
                        }
                        break;
                    }
                    else {
                        if (player.getParty() == null) {
                            break;
                        }
                        switch (player.getParty().getMemberCount()) {
                            case 2: {
                                if (this._treasureCounter <= 1) {
                                    addSpawn(18357, 11708 + getRandom(-1000, 1000), -86505 + getRandom(-1000, 1000), -10928, 0, true, -1L, true, player.getInstanceId());
                                    this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                                    ++this._treasureCounter;
                                    break Label_1262;
                                }
                                break Label_1262;
                            }
                            case 3: {
                                if (this._treasureCounter <= 2) {
                                    addSpawn(18357, 11708 + getRandom(-1000, 1000), -86505 + getRandom(-1000, 1000), -10928, 0, true, -1L, true, player.getInstanceId());
                                    this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                                    ++this._treasureCounter;
                                    break Label_1262;
                                }
                                break Label_1262;
                            }
                            case 4: {
                                if (this._treasureCounter <= 4) {
                                    addSpawn(18357, 11708 + getRandom(-1000, 1000), -86505 + getRandom(-1000, 1000), -10928, 0, true, -1L, true, player.getInstanceId());
                                    this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                                    ++this._treasureCounter;
                                    break Label_1262;
                                }
                                break Label_1262;
                            }
                            case 5: {
                                if (this._treasureCounter <= 7) {
                                    addSpawn(18357, 11708 + getRandom(-1000, 1000), -86505 + getRandom(-1000, 1000), -10928, 0, true, -1L, true, player.getInstanceId());
                                    this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                                    ++this._treasureCounter;
                                    break Label_1262;
                                }
                                break Label_1262;
                            }
                            case 6: {
                                if (this._treasureCounter <= 10) {
                                    addSpawn(18357, 11708 + getRandom(-1000, 1000), -86505 + getRandom(-1000, 1000), -10928, 0, true, -1L, true, player.getInstanceId());
                                    this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                                    ++this._treasureCounter;
                                    break Label_1262;
                                }
                                break Label_1262;
                            }
                            case 7: {
                                if (this._treasureCounter <= 13) {
                                    addSpawn(18357, 11708 + getRandom(-1000, 1000), -86505 + getRandom(-1000, 1000), -10928, 0, true, -1L, true, player.getInstanceId());
                                    this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                                    ++this._treasureCounter;
                                    break Label_1262;
                                }
                                break Label_1262;
                            }
                            case 8: {
                                if (this._treasureCounter <= 16) {
                                    addSpawn(18357, 11708 + getRandom(-1000, 1000), -86505 + getRandom(-1000, 1000), -10928, 0, true, -1L, true, player.getInstanceId());
                                    this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                                    ++this._treasureCounter;
                                    break Label_1262;
                                }
                                break Label_1262;
                            }
                            case 9: {
                                if (this._treasureCounter <= 27) {
                                    addSpawn(18357, 11708 + getRandom(-1000, 1000), -86505 + getRandom(-1000, 1000), -10928, 0, true, -1L, true, player.getInstanceId());
                                    this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                                    ++this._treasureCounter;
                                    break Label_1262;
                                }
                                break Label_1262;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        switch (npc.getId()) {
            case 18359: {
                this.startQuestTimer("SPAWN_TRESURE", 1000L, npc, player);
                final Instance world = npc.getInstanceWorld();
                if (world != null) {
                    world.finishInstance();
                    break;
                }
                break;
            }
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public static void main(final String[] args) {
        new GolbergRoom();
    }
}
