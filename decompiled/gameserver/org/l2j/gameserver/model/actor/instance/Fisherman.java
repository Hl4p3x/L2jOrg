// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.SkillLearn;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ExAcquirableSkillListByClass;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public final class Fisherman extends Merchant
{
    public Fisherman(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2FishermanInstance);
    }
    
    public static void showFishSkillList(final Player player) {
        final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableFishingSkills(player);
        if (skills.isEmpty()) {
            final int minlLevel = SkillTreesData.getInstance().getMinLevelForNewSkill(player, SkillTreesData.getInstance().getFishingSkillTree());
            if (minlLevel > 0) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN_COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
                sm.addInt(minlLevel);
                player.sendPacket(sm);
            }
            else {
                player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
            }
        }
        else {
            player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.FISHING));
        }
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
    public void onBypassFeedback(final Player player, final String command) {
        if (command.equalsIgnoreCase("FishSkillList")) {
            showFishSkillList(player);
        }
        else {
            super.onBypassFeedback(player, command);
        }
    }
}
