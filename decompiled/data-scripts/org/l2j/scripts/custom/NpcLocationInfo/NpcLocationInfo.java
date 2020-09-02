// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.custom.NpcLocationInfo;

import java.util.ArrayList;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import java.util.List;
import io.github.joealisson.primitive.IntSet;
import org.l2j.scripts.ai.AbstractNpcAI;

public class NpcLocationInfo extends AbstractNpcAI
{
    private static final IntSet NPC;
    private static final List<Integer> NPCRADAR;
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = event;
        if (Util.isInteger(event)) {
            htmltext = null;
            final int npcId = Integer.parseInt(event);
            if (NpcLocationInfo.NPCRADAR.contains(npcId)) {
                int x = 0;
                int y = 0;
                int z = 0;
                final Spawn spawn = SpawnTable.getInstance().getAnySpawn(npcId);
                if (spawn != null) {
                    x = spawn.getX();
                    y = spawn.getY();
                    z = spawn.getZ();
                }
                addRadar(player, x, y, z);
                htmltext = "MoveToLoc.htm";
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = getNoQuestMsg(player);
        final int npcId = npc.getId();
        if (NpcLocationInfo.NPC.contains(npcId)) {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId);
        }
        return htmltext;
    }
    
    private NpcLocationInfo() {
        NpcLocationInfo.NPCRADAR.add(30006);
        NpcLocationInfo.NPCRADAR.add(30039);
        NpcLocationInfo.NPCRADAR.add(30040);
        NpcLocationInfo.NPCRADAR.add(30041);
        NpcLocationInfo.NPCRADAR.add(30042);
        NpcLocationInfo.NPCRADAR.add(30043);
        NpcLocationInfo.NPCRADAR.add(30044);
        NpcLocationInfo.NPCRADAR.add(30045);
        NpcLocationInfo.NPCRADAR.add(30046);
        NpcLocationInfo.NPCRADAR.add(30283);
        NpcLocationInfo.NPCRADAR.add(30003);
        NpcLocationInfo.NPCRADAR.add(30004);
        NpcLocationInfo.NPCRADAR.add(30001);
        NpcLocationInfo.NPCRADAR.add(30002);
        NpcLocationInfo.NPCRADAR.add(30031);
        NpcLocationInfo.NPCRADAR.add(30033);
        NpcLocationInfo.NPCRADAR.add(30035);
        NpcLocationInfo.NPCRADAR.add(30032);
        NpcLocationInfo.NPCRADAR.add(30036);
        NpcLocationInfo.NPCRADAR.add(30026);
        NpcLocationInfo.NPCRADAR.add(30027);
        NpcLocationInfo.NPCRADAR.add(30029);
        NpcLocationInfo.NPCRADAR.add(30028);
        NpcLocationInfo.NPCRADAR.add(30054);
        NpcLocationInfo.NPCRADAR.add(30055);
        NpcLocationInfo.NPCRADAR.add(30005);
        NpcLocationInfo.NPCRADAR.add(30048);
        NpcLocationInfo.NPCRADAR.add(30312);
        NpcLocationInfo.NPCRADAR.add(30368);
        NpcLocationInfo.NPCRADAR.add(30049);
        NpcLocationInfo.NPCRADAR.add(30047);
        NpcLocationInfo.NPCRADAR.add(30497);
        NpcLocationInfo.NPCRADAR.add(30050);
        NpcLocationInfo.NPCRADAR.add(30311);
        NpcLocationInfo.NPCRADAR.add(30051);
        NpcLocationInfo.NPCRADAR.add(30134);
        NpcLocationInfo.NPCRADAR.add(30224);
        NpcLocationInfo.NPCRADAR.add(30348);
        NpcLocationInfo.NPCRADAR.add(30355);
        NpcLocationInfo.NPCRADAR.add(30347);
        NpcLocationInfo.NPCRADAR.add(30432);
        NpcLocationInfo.NPCRADAR.add(30356);
        NpcLocationInfo.NPCRADAR.add(30349);
        NpcLocationInfo.NPCRADAR.add(30346);
        NpcLocationInfo.NPCRADAR.add(30433);
        NpcLocationInfo.NPCRADAR.add(30357);
        NpcLocationInfo.NPCRADAR.add(30431);
        NpcLocationInfo.NPCRADAR.add(30430);
        NpcLocationInfo.NPCRADAR.add(30307);
        NpcLocationInfo.NPCRADAR.add(30138);
        NpcLocationInfo.NPCRADAR.add(30137);
        NpcLocationInfo.NPCRADAR.add(30135);
        NpcLocationInfo.NPCRADAR.add(30136);
        NpcLocationInfo.NPCRADAR.add(30143);
        NpcLocationInfo.NPCRADAR.add(30360);
        NpcLocationInfo.NPCRADAR.add(30145);
        NpcLocationInfo.NPCRADAR.add(30135);
        NpcLocationInfo.NPCRADAR.add(30144);
        NpcLocationInfo.NPCRADAR.add(30358);
        NpcLocationInfo.NPCRADAR.add(30359);
        NpcLocationInfo.NPCRADAR.add(30141);
        NpcLocationInfo.NPCRADAR.add(30139);
        NpcLocationInfo.NPCRADAR.add(30140);
        NpcLocationInfo.NPCRADAR.add(30350);
        NpcLocationInfo.NPCRADAR.add(30421);
        NpcLocationInfo.NPCRADAR.add(30419);
        NpcLocationInfo.NPCRADAR.add(30130);
        NpcLocationInfo.NPCRADAR.add(30351);
        NpcLocationInfo.NPCRADAR.add(30353);
        NpcLocationInfo.NPCRADAR.add(30354);
        NpcLocationInfo.NPCRADAR.add(30146);
        NpcLocationInfo.NPCRADAR.add(30285);
        NpcLocationInfo.NPCRADAR.add(30284);
        NpcLocationInfo.NPCRADAR.add(30221);
        NpcLocationInfo.NPCRADAR.add(30217);
        NpcLocationInfo.NPCRADAR.add(30219);
        NpcLocationInfo.NPCRADAR.add(30220);
        NpcLocationInfo.NPCRADAR.add(30218);
        NpcLocationInfo.NPCRADAR.add(30216);
        NpcLocationInfo.NPCRADAR.add(30363);
        NpcLocationInfo.NPCRADAR.add(30149);
        NpcLocationInfo.NPCRADAR.add(30150);
        NpcLocationInfo.NPCRADAR.add(30148);
        NpcLocationInfo.NPCRADAR.add(30147);
        NpcLocationInfo.NPCRADAR.add(30155);
        NpcLocationInfo.NPCRADAR.add(30156);
        NpcLocationInfo.NPCRADAR.add(30157);
        NpcLocationInfo.NPCRADAR.add(30158);
        NpcLocationInfo.NPCRADAR.add(30154);
        NpcLocationInfo.NPCRADAR.add(30153);
        NpcLocationInfo.NPCRADAR.add(30152);
        NpcLocationInfo.NPCRADAR.add(30151);
        NpcLocationInfo.NPCRADAR.add(30423);
        NpcLocationInfo.NPCRADAR.add(30414);
        NpcLocationInfo.NPCRADAR.add(31853);
        NpcLocationInfo.NPCRADAR.add(30223);
        NpcLocationInfo.NPCRADAR.add(30362);
        NpcLocationInfo.NPCRADAR.add(30222);
        NpcLocationInfo.NPCRADAR.add(30371);
        NpcLocationInfo.NPCRADAR.add(31852);
        NpcLocationInfo.NPCRADAR.add(30540);
        NpcLocationInfo.NPCRADAR.add(30541);
        NpcLocationInfo.NPCRADAR.add(30542);
        NpcLocationInfo.NPCRADAR.add(30543);
        NpcLocationInfo.NPCRADAR.add(30544);
        NpcLocationInfo.NPCRADAR.add(30545);
        NpcLocationInfo.NPCRADAR.add(30546);
        NpcLocationInfo.NPCRADAR.add(30547);
        NpcLocationInfo.NPCRADAR.add(30548);
        NpcLocationInfo.NPCRADAR.add(30531);
        NpcLocationInfo.NPCRADAR.add(30532);
        NpcLocationInfo.NPCRADAR.add(30533);
        NpcLocationInfo.NPCRADAR.add(30534);
        NpcLocationInfo.NPCRADAR.add(30535);
        NpcLocationInfo.NPCRADAR.add(30536);
        NpcLocationInfo.NPCRADAR.add(30525);
        NpcLocationInfo.NPCRADAR.add(30526);
        NpcLocationInfo.NPCRADAR.add(30527);
        NpcLocationInfo.NPCRADAR.add(30518);
        NpcLocationInfo.NPCRADAR.add(30519);
        NpcLocationInfo.NPCRADAR.add(30516);
        NpcLocationInfo.NPCRADAR.add(30517);
        NpcLocationInfo.NPCRADAR.add(30520);
        NpcLocationInfo.NPCRADAR.add(30521);
        NpcLocationInfo.NPCRADAR.add(30522);
        NpcLocationInfo.NPCRADAR.add(30523);
        NpcLocationInfo.NPCRADAR.add(30524);
        NpcLocationInfo.NPCRADAR.add(30537);
        NpcLocationInfo.NPCRADAR.add(30650);
        NpcLocationInfo.NPCRADAR.add(30538);
        NpcLocationInfo.NPCRADAR.add(30539);
        NpcLocationInfo.NPCRADAR.add(30671);
        NpcLocationInfo.NPCRADAR.add(30651);
        NpcLocationInfo.NPCRADAR.add(30550);
        NpcLocationInfo.NPCRADAR.add(30554);
        NpcLocationInfo.NPCRADAR.add(30553);
        NpcLocationInfo.NPCRADAR.add(30576);
        NpcLocationInfo.NPCRADAR.add(30577);
        NpcLocationInfo.NPCRADAR.add(30578);
        NpcLocationInfo.NPCRADAR.add(30579);
        NpcLocationInfo.NPCRADAR.add(30580);
        NpcLocationInfo.NPCRADAR.add(30581);
        NpcLocationInfo.NPCRADAR.add(30582);
        NpcLocationInfo.NPCRADAR.add(30583);
        NpcLocationInfo.NPCRADAR.add(30584);
        NpcLocationInfo.NPCRADAR.add(30569);
        NpcLocationInfo.NPCRADAR.add(30570);
        NpcLocationInfo.NPCRADAR.add(30571);
        NpcLocationInfo.NPCRADAR.add(30572);
        NpcLocationInfo.NPCRADAR.add(30564);
        NpcLocationInfo.NPCRADAR.add(30560);
        NpcLocationInfo.NPCRADAR.add(30561);
        NpcLocationInfo.NPCRADAR.add(30558);
        NpcLocationInfo.NPCRADAR.add(30559);
        NpcLocationInfo.NPCRADAR.add(30562);
        NpcLocationInfo.NPCRADAR.add(30563);
        NpcLocationInfo.NPCRADAR.add(30565);
        NpcLocationInfo.NPCRADAR.add(30566);
        NpcLocationInfo.NPCRADAR.add(30567);
        NpcLocationInfo.NPCRADAR.add(30568);
        NpcLocationInfo.NPCRADAR.add(30585);
        NpcLocationInfo.NPCRADAR.add(30587);
        this.addStartNpc((IntCollection)NpcLocationInfo.NPC);
        this.addTalkId((IntCollection)NpcLocationInfo.NPC);
    }
    
    public static AbstractNpcAI provider() {
        return new NpcLocationInfo();
    }
    
    static {
        NPC = IntSet.of(30598, 30599, new int[] { 30600, 30601, 30602 });
        NPCRADAR = new ArrayList<Integer>();
    }
}
