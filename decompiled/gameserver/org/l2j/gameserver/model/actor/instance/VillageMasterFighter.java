// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public final class VillageMasterFighter extends VillageMaster
{
    public VillageMasterFighter(final NpcTemplate template) {
        super(template);
    }
    
    @Override
    protected final boolean checkVillageMasterRace(final ClassId pclass) {
        return pclass != null && (pclass.getRace() == Race.HUMAN || pclass.getRace() == Race.ELF);
    }
    
    @Override
    protected final boolean checkVillageMasterTeachType(final ClassId pclass) {
        return pclass != null && CategoryManager.getInstance().isInCategory(CategoryType.FIGHTER_GROUP, pclass.getId());
    }
}
