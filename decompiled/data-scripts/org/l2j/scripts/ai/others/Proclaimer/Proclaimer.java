// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.Proclaimer;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Proclaimer extends AbstractNpcAI
{
    private static final int[] PROCLAIMER;
    private static final SkillHolder XP_BUFF;
    
    private Proclaimer() {
        this.addStartNpc(Proclaimer.PROCLAIMER);
        this.addFirstTalkId(Proclaimer.PROCLAIMER);
        this.addTalkId(Proclaimer.PROCLAIMER);
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (!player.isOnDarkSide()) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)new NpcSay(npc.getObjectId(), ChatType.WHISPER, npc.getId(), NpcStringId.WHEN_THE_WORLD_PLUNGES_INTO_CHAOS_WE_WILL_NEED_YOUR_HELP_WE_HOPE_YOU_JOIN_US_WHEN_THE_TIME_COMES) });
            final Clan ownerClan = npc.getCastle().getOwner();
            if (ownerClan != null) {
                final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
                packet.setHtml(this.getHtml(player, "proclaimer.html"));
                packet.replace("%leaderName%", ownerClan.getLeaderName());
                packet.replace("%clanName%", ownerClan.getName());
                packet.replace("%castleName%", npc.getCastle().getName());
                player.sendPacket(new ServerPacket[] { (ServerPacket)packet });
            }
        }
        else {
            htmltext = "proclaimer-01.html";
        }
        return htmltext;
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        if (event.equals("giveBuff")) {
            if (!player.isOnDarkSide()) {
                SkillCaster.triggerCast((Creature)npc, (Creature)player, Proclaimer.XP_BUFF.getSkill());
            }
            else {
                htmltext = "proclaimer-01.html";
            }
        }
        return htmltext;
    }
    
    public static AbstractNpcAI provider() {
        return new Proclaimer();
    }
    
    static {
        PROCLAIMER = new int[] { 36609, 36610, 36611, 36612, 36613, 36614, 36615, 36616, 36617 };
        XP_BUFF = new SkillHolder(19036, 1);
    }
}
