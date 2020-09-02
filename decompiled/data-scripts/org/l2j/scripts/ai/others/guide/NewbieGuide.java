// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.guide;

import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public class NewbieGuide extends AbstractNpcAI
{
    private static final int[] NEWBIE_GUIDES;
    private static final ItemHolder SOULSHOT_REWARD;
    private static final ItemHolder SPIRITSHOT_REWARD;
    private static final String TUTORIAL_QUEST = "Q00255_Tutorial";
    private static final String SUPPORT_MAGIC_STRING = "<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h Link default/SupportMagic.htm\">Receive help from beneficial magic.</Button>";
    
    private NewbieGuide() {
        this.addStartNpc(NewbieGuide.NEWBIE_GUIDES);
        this.addTalkId(NewbieGuide.NEWBIE_GUIDES);
        this.addFirstTalkId(NewbieGuide.NEWBIE_GUIDES);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmlText;
        if (event.equals("0")) {
            if (Config.MAX_NEWBIE_BUFF_LEVEL <= 0) {
                final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId(), this.getHtml(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId())));
                html.replace("<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h Link default/SupportMagic.htm\">Receive help from beneficial magic.</Button>", "");
                player.sendPacket(new ServerPacket[] { (ServerPacket)html });
                return null;
            }
            htmlText = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        else {
            htmlText = invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, npc.getId(), event, player.isMageClass() ? "m" : "f");
        }
        return htmlText;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        if (npc.getRace() != player.getTemplate().getRace()) {
            return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        final QuestState qs = player.getQuestState("Q00255_Tutorial");
        if (qs != null && !Config.DISABLE_TUTORIAL && qs.isMemoState(5)) {
            qs.setMemoState(6);
            if (player.isMageClass() && player.getRace() != Race.ORC) {
                giveItems(player, NewbieGuide.SPIRITSHOT_REWARD);
                this.playTutorialVoice(player, "tutorial_voice_027");
            }
            else {
                giveItems(player, NewbieGuide.SOULSHOT_REWARD);
                this.playTutorialVoice(player, "tutorial_voice_026");
            }
        }
        if (Config.MAX_NEWBIE_BUFF_LEVEL > 0) {
            return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId(), this.getHtml(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId())));
        html.replace("<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h Link default/SupportMagic.htm\">Receive help from beneficial magic.</Button>", "");
        player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        return null;
    }
    
    public void playTutorialVoice(final Player player, final String voice) {
        player.sendPacket(new ServerPacket[] { (ServerPacket)new PlaySound(2, voice, 0, 0, player.getX(), player.getY(), player.getZ()) });
    }
    
    public static AbstractNpcAI provider() {
        return new NewbieGuide();
    }
    
    static {
        NEWBIE_GUIDES = new int[] { 30598, 30599, 30600, 30601, 30602, 34110 };
        SOULSHOT_REWARD = new ItemHolder(91927, 200L);
        SPIRITSHOT_REWARD = new ItemHolder(91927, 100L);
    }
}
