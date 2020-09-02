// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.util.Evolve;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class PetManager extends Merchant
{
    public PetManager(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2PetManagerInstance);
    }
    
    @Override
    public String getHtmlPath(final int npcId, final int val) {
        String pom = "";
        if (val == 0) {
            pom = Integer.toString(npcId);
        }
        else {
            pom = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npcId, val);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pom);
    }
    
    @Override
    public void showChatWindow(final Player player) {
        String filename = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getId());
        if (this.getId() == 36478 && player.hasSummon()) {
            filename = "data/html/petmanager/restore-unsummonpet.htm";
        }
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        html.setFile(player, filename);
        html.replace("%objectId%", String.valueOf(this.getObjectId()));
        html.replace("%npcname%", this.getName());
        player.sendPacket(html);
    }
    
    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (command.startsWith("exchange")) {
            final String[] params = command.split(" ");
            final int val = Integer.parseInt(params[1]);
            switch (val) {
                case 1: {
                    this.exchange(player, 7585, 6650);
                    break;
                }
                case 2: {
                    this.exchange(player, 7583, 6648);
                    break;
                }
                case 3: {
                    this.exchange(player, 7584, 6649);
                    break;
                }
            }
            return;
        }
        if (command.startsWith("evolve")) {
            final String[] params = command.split(" ");
            final int val = Integer.parseInt(params[1]);
            boolean ok = false;
            switch (val) {
                case 1: {
                    ok = Evolve.doEvolve(player, this, 2375, 9882, 55);
                    break;
                }
                case 2: {
                    ok = Evolve.doEvolve(player, this, 9882, 10426, 70);
                    break;
                }
                case 3: {
                    ok = Evolve.doEvolve(player, this, 6648, 10311, 55);
                    break;
                }
                case 4: {
                    ok = Evolve.doEvolve(player, this, 6650, 10313, 55);
                    break;
                }
                case 5: {
                    ok = Evolve.doEvolve(player, this, 6649, 10312, 55);
                    break;
                }
            }
            if (!ok) {
                final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
                html.setFile(player, "data/html/petmanager/evolve_no.htm");
                player.sendPacket(html);
            }
            return;
        }
        if (command.startsWith("restore")) {
            final String[] params = command.split(" ");
            final int val = Integer.parseInt(params[1]);
            boolean ok = false;
            switch (val) {
                case 1: {
                    ok = Evolve.doRestore(player, this, 10307, 9882, 55);
                    break;
                }
                case 2: {
                    ok = Evolve.doRestore(player, this, 10611, 10426, 70);
                    break;
                }
                case 3: {
                    ok = Evolve.doRestore(player, this, 10308, 4422, 55);
                    break;
                }
                case 4: {
                    ok = Evolve.doRestore(player, this, 10309, 4423, 55);
                    break;
                }
                case 5: {
                    ok = Evolve.doRestore(player, this, 10310, 4424, 55);
                    break;
                }
            }
            if (!ok) {
                final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
                html.setFile(player, "data/html/petmanager/restore_no.htm");
                player.sendPacket(html);
            }
            return;
        }
        super.onBypassFeedback(player, command);
    }
    
    public final void exchange(final Player player, final int itemIdtake, final int itemIdgive) {
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        if (player.destroyItemByItemId("Consume", itemIdtake, 1L, this, true)) {
            player.addItem("", itemIdgive, 1L, this, true);
            html.setFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getId()));
            player.sendPacket(html);
        }
        else {
            html.setFile(player, "data/html/petmanager/exchange_no.htm");
            player.sendPacket(html);
        }
    }
}
