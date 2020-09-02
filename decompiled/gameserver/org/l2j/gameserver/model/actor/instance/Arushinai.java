// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.Iterator;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class Arushinai extends Folk
{
    public Arushinai(final NpcTemplate template) {
        super(template);
    }
    
    @Override
    public void onBypassFeedback(final Player player, final String command) {
        final StringTokenizer st = new StringTokenizer(command, "_");
        final String cmd = st.nextToken();
        if (cmd.equals("proceed")) {
            if (!player.isGM()) {
                final Party party = player.getParty();
                if (party == null) {
                    player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                    player.teleToLocation(114264, 13352, -5104);
                    return;
                }
                if (!party.isLeader(player)) {
                    player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
                    return;
                }
            }
            if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_complete", 0) == 0) {
                final int riftLevel = Rnd.get(1, 3);
                GlobalVariablesManager.getInstance().set("heavenly_rift_level", riftLevel);
                GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 4);
                switch (riftLevel) {
                    case 1: {
                        HeavenlyRift.startEvent20Bomb(player);
                        break;
                    }
                    case 2: {
                        HeavenlyRift.startEventTower(player);
                        break;
                    }
                    case 3: {
                        HeavenlyRift.startEvent40Angels(player);
                        break;
                    }
                }
            }
            else {
                showBusyWindow(player, this);
            }
        }
        else if (cmd.equals("finish")) {
            if (player.isInParty()) {
                final Party party = player.getParty();
                if (party.isLeader(player)) {
                    for (final Player partyMember : party.getMembers()) {
                        if (!GameUtils.checkIfInRange(1000, player, partyMember, false)) {
                            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED);
                            sm.addPcName(partyMember);
                            player.sendPacket(sm);
                            party.broadcastToPartyMembers(player, sm);
                            return;
                        }
                    }
                    GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 0);
                    for (final Player partyMember : party.getMembers()) {
                        partyMember.teleToLocation(114264, 13352, -5104);
                    }
                }
                else {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_A_PARTY_LEADER);
                }
            }
            else {
                if (!player.isGM()) {
                    player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_A_PARTY);
                    return;
                }
                GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
                player.teleToLocation(114264, 13352, -5104);
            }
        }
        else {
            super.onBypassFeedback(player, command);
        }
    }
    
    public static void showBusyWindow(final Player player, final Npc npc) {
        player.sendPacket(new NpcHtmlMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npc.getName())));
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    @Override
    public String getHtmlPath(final int npcId, final int val) {
        String filename = "data/html/default/";
        if (val == 1) {
            filename = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, filename, npcId);
        }
        else if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_complete", 0) > 0) {
            filename = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, filename, npcId);
        }
        else {
            filename = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, filename, npcId);
        }
        return filename;
    }
}
