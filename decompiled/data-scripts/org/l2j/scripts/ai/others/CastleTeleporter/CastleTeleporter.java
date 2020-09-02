// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.CastleTeleporter;

import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import java.util.Iterator;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.Location;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class CastleTeleporter extends AbstractNpcAI
{
    private static final int[] MASS_TELEPORTERS;
    private static final int[] SIEGE_TELEPORTERS;
    
    private CastleTeleporter() {
        this.addStartNpc(CastleTeleporter.MASS_TELEPORTERS);
        this.addStartNpc(CastleTeleporter.SIEGE_TELEPORTERS);
        this.addTalkId(CastleTeleporter.MASS_TELEPORTERS);
        this.addTalkId(CastleTeleporter.SIEGE_TELEPORTERS);
        this.addFirstTalkId(CastleTeleporter.MASS_TELEPORTERS);
        this.addFirstTalkId(CastleTeleporter.SIEGE_TELEPORTERS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final StringTokenizer st = new StringTokenizer(event, " ");
        final String nextToken;
        final String action = nextToken = st.nextToken();
        switch (nextToken) {
            case "CastleTeleporter-06.html": {
                if (npc.isScriptValue(0)) {
                    final Siege siege = npc.getCastle().getSiege();
                    final int time = (siege.isInProgress() && siege.getControlTowerCount() == 0) ? 180000 : 30000;
                    this.startQuestTimer("MASS_TELEPORT", (long)time, npc, (Player)null);
                    npc.setScriptValue(1);
                }
                return event;
            }
            case "teleportMe": {
                if (st.hasMoreTokens()) {
                    final int unknowInt = Integer.parseInt(st.nextToken());
                    final StatsSet npcParams = npc.getParameters();
                    Location teleLoc = null;
                    switch (unknowInt) {
                        case 0: {
                            teleLoc = this.getTeleportLocation(npcParams, "01", "02", "03");
                            break;
                        }
                        case 1: {
                            teleLoc = this.getTeleportLocation(npcParams, "11", "12", "13");
                            break;
                        }
                        case 2: {
                            teleLoc = this.getTeleportLocation(npcParams, "21", "22", "23");
                            break;
                        }
                        case 3: {
                            teleLoc = this.getTeleportLocation(npcParams, "31", "32", "33");
                            break;
                        }
                        case 4: {
                            teleLoc = this.getTeleportLocation(npcParams, "41", "42", "43");
                            break;
                        }
                        case 5: {
                            if (this.isOwner(player, npc)) {
                                teleLoc = new Location(npcParams.getInt("pos_x51"), npcParams.getInt("pos_y51"), npcParams.getInt("pos_z51"));
                                break;
                            }
                            return "CastleTeleporter-noAuthority.html";
                        }
                    }
                    if (teleLoc != null) {
                        player.teleToLocation((ILocational)teleLoc);
                    }
                    break;
                }
                break;
            }
            case "MASS_TELEPORT": {
                final int region = MapRegionManager.getInstance().getMapRegionLocId(npc.getX(), npc.getY());
                final NpcSay msg = new NpcSay(npc, ChatType.NPC_SHOUT, NpcStringId.THE_DEFENDERS_OF_S1_CASTLE_WILL_BE_TELEPORTED_TO_THE_INNER_CASTLE);
                msg.addStringParameter(npc.getCastle().getName());
                npc.getCastle().oustAllPlayers();
                npc.setScriptValue(0);
                for (final Player pl : World.getInstance().getPlayers()) {
                    if (region == MapRegionManager.getInstance().getMapRegionLocId((WorldObject)pl)) {
                        pl.sendPacket(new ServerPacket[] { (ServerPacket)msg });
                    }
                }
                break;
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (Util.contains(CastleTeleporter.MASS_TELEPORTERS, npc.getId())) {
            final Siege siege = npc.getCastle().getSiege();
            htmltext = (npc.isScriptValue(0) ? ((siege.isInProgress() && siege.getControlTowerCount() == 0) ? "CastleTeleporter-05.html" : "CastleTeleporter-04.html") : "CastleTeleporter-06.html");
        }
        else if (npc.getCastle().getOwnerId() == player.getClanId() && player.getClanId() != 0 && player.getSiegeState() == 2) {
            htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getHtmlName(npc));
        }
        else {
            htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getHtmlName(npc));
        }
        return htmltext;
    }
    
    private Location getTeleportLocation(final StatsSet npcParams, final String paramName1, final String paramName2, final String paramName3) {
        Location loc;
        if (Rnd.get(100) < 33) {
            loc = new Location(npcParams.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, paramName1)), npcParams.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, paramName1)), npcParams.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, paramName1)));
        }
        else if (Rnd.get(100) < 66) {
            loc = new Location(npcParams.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, paramName2)), npcParams.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, paramName2)), npcParams.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, paramName2)));
        }
        else {
            loc = new Location(npcParams.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, paramName3)), npcParams.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, paramName3)), npcParams.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, paramName3)));
        }
        return loc;
    }
    
    private String getHtmlName(final Npc npc) {
        switch (npc.getId()) {
            case 35092:
            case 35134:
            case 35176:
            case 35218:
            case 35308:
            case 35352:
            case 35544: {
                return "CastleTeleporter-01";
            }
            case 35093:
            case 35135:
            case 35177:
            case 35219:
            case 35309:
            case 35353:
            case 35545: {
                return "CastleTeleporter-02";
            }
            case 35094:
            case 35136:
            case 35178:
            case 35220:
            case 35310:
            case 35354:
            case 35546: {
                return "CastleTeleporter-03";
            }
            default: {
                return String.valueOf(npc.getId());
            }
        }
    }
    
    private boolean isOwner(final Player player, final Npc npc) {
        return player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || (player.getClan() != null && player.getClanId() == npc.getCastle().getOwnerId() && player.isClanLeader());
    }
    
    public static AbstractNpcAI provider() {
        return new CastleTeleporter();
    }
    
    static {
        MASS_TELEPORTERS = new int[] { 35095, 35137, 35179, 35221, 35266, 35311, 35355, 35502, 35547 };
        SIEGE_TELEPORTERS = new int[] { 35092, 35093, 35094, 35134, 35135, 35136, 35176, 35177, 35178, 35218, 35219, 35220, 35261, 35262, 35263, 35264, 35265, 35308, 35309, 35310, 35352, 35353, 35354, 35497, 35498, 35499, 35500, 35501, 35544, 35545, 35546 };
    }
}
