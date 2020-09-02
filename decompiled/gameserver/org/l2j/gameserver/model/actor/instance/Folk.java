// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.actor.status.NpcStatus;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.status.FolkStatus;
import io.github.joealisson.primitive.LongMap;
import org.l2j.gameserver.model.SkillLearn;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ExAcquirableSkillListByClass;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Npc;

public class Folk extends Npc
{
    public Folk(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2NpcInstance);
        this.setIsInvul(false);
    }
    
    public static void showSkillList(final Player player, final Npc npc, final ClassId classId) {
        final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableSkills(player, classId, false, false);
        if (skills.isEmpty()) {
            final LongMap<SkillLearn> skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(classId);
            final int minLevel = SkillTreesData.getInstance().getMinLevelForNewSkill(player, skillTree);
            if (minLevel > 0) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN_COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
                sm.addInt(minLevel);
                player.sendPacket(sm);
            }
            else if (player.getClassId().level() == 1) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN_PLEASE_COME_BACK_AFTER_S1ND_CLASS_CHANGE);
                sm.addInt(2);
                player.sendPacket(sm);
            }
            else {
                player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
            }
        }
        else {
            player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.CLASS));
        }
    }
    
    @Override
    public FolkStatus getStatus() {
        return (FolkStatus)super.getStatus();
    }
    
    @Override
    public void initCharStatus() {
        this.setStatus(new FolkStatus(this));
    }
}
