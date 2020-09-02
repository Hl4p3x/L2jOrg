// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import java.util.Iterator;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class Tower extends FriendlyNpc
{
    private static int[] ITEM_REWARD;
    private Player _talkingPlayer;
    
    public Tower(final NpcTemplate template) {
        super(template);
    }
    
    @Override
    public void onBypassFeedback(final Player player, final String command) {
        this._talkingPlayer = player;
        final StringTokenizer st = new StringTokenizer(command, "_");
        final String cmd = st.nextToken();
        if (cmd.equals("getreward")) {
            if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_reward", 0) == 1) {
                GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 0);
                if (player.isGM()) {
                    for (final int r : Tower.ITEM_REWARD) {
                        player.addItem("TowerInstance", r, 1L, this, true);
                    }
                }
                else {
                    for (final Player partyMember : player.getParty().getMembers()) {
                        for (final int r2 : Tower.ITEM_REWARD) {
                            partyMember.addItem("TowerInstance", r2, 1L, this, true);
                        }
                    }
                }
                this.showChatWindow(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getId()));
            }
        }
        else {
            super.onBypassFeedback(player, command);
        }
    }
    
    @Override
    public int getHateBaseAmount() {
        return 1000;
    }
    
    @Override
    public void showChatWindow(final Player player, final int val) {
        super.showChatWindow(this._talkingPlayer = player, val);
    }
    
    @Override
    public String getHtmlPath(final int npcId, final int val) {
        String filename = "data/html/default/";
        if (!this._talkingPlayer.isGM() && (!this._talkingPlayer.isInParty() || !this._talkingPlayer.getParty().isLeader(this._talkingPlayer))) {
            filename = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, filename, npcId);
        }
        else if (!this.isDead() && GlobalVariablesManager.getInstance().getInt("heavenly_rift_complete", 0) == 2) {
            if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_reward", 0) == 1) {
                filename = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, filename, npcId);
            }
            else {
                filename = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, filename, npcId);
            }
        }
        else {
            filename = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, filename, npcId);
        }
        return filename;
    }
    
    static {
        Tower.ITEM_REWARD = new int[] { 49764, 49765 };
    }
}
