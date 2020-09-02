// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import java.util.Iterator;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class DimensionalVortex extends Folk
{
    private static final int ITEM_ID = 49759;
    
    public DimensionalVortex(final NpcTemplate template) {
        super(template);
    }
    
    @Override
    public void onBypassFeedback(final Player player, final String command) {
        final StringTokenizer st = new StringTokenizer(command, "_");
        final String cmd = st.nextToken();
        if (cmd.equals("tryenter")) {
            if (player.getInventory().getInventoryItemCount(49759, -1) >= 1L) {
                if (this.isBusy()) {
                    Arushinai.showBusyWindow(player, this);
                    return;
                }
                if (player.isGM()) {
                    this.setBusy(true);
                    player.destroyItemByItemId("Rift", 49759, 1L, this, true);
                    GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
                    GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
                    player.teleToLocation(112685, 13362, 10966);
                    ThreadPool.schedule((Runnable)new HeavenlyRift.ClearZoneTask(this), 180000L);
                    return;
                }
                if (!player.isInParty()) {
                    player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                    return;
                }
                final Party party = player.getParty();
                if (!party.isLeader(player)) {
                    player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
                    return;
                }
                for (final Player partyMember : party.getMembers()) {
                    if (!GameUtils.checkIfInRange(1000, player, partyMember, false)) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED);
                        sm.addPcName(partyMember);
                        player.sendPacket(sm);
                        party.broadcastToPartyMembers(player, sm);
                        return;
                    }
                }
                this.setBusy(true);
                player.destroyItemByItemId("Rift", 49759, 1L, this, true);
                GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
                GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
                for (final Player partyMember : party.getMembers()) {
                    partyMember.teleToLocation(112685, 13362, 10966);
                }
                ThreadPool.schedule((Runnable)new HeavenlyRift.ClearZoneTask(this), 1200000L);
            }
            else {
                this.showChatWindow(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getId()));
            }
        }
        else if (cmd.equals("exchange")) {
            long count_have = player.getInventory().getInventoryItemCount(49767, -1);
            if (count_have < 10L) {
                this.showChatWindow(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getId()));
                return;
            }
            if (count_have % 10L != 0L) {
                count_have -= count_have % 10L;
            }
            final long to_give = count_have / 10L;
            player.destroyItemByItemId("Rift", 49767, count_have, this, true);
            player.addItem("Rift", 49759, to_give, this, true);
        }
        else {
            super.onBypassFeedback(player, command);
        }
    }
}
