// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

import java.util.ArrayList;
import java.util.List;

public class MountEnabledSkillList
{
    private static final List<Integer> ENABLED_SKILLS;
    
    public MountEnabledSkillList() {
        MountEnabledSkillList.ENABLED_SKILLS.add(4289);
        MountEnabledSkillList.ENABLED_SKILLS.add(325);
    }
    
    public static boolean contains(final int skillId) {
        return MountEnabledSkillList.ENABLED_SKILLS.contains(skillId);
    }
    
    static {
        ENABLED_SKILLS = new ArrayList<Integer>(2);
    }
}
