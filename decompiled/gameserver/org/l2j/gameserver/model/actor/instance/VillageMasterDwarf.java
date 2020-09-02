// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public final class VillageMasterDwarf extends VillageMaster
{
    public VillageMasterDwarf(final NpcTemplate template) {
        super(template);
    }
    
    @Override
    protected final boolean checkVillageMasterRace(final ClassId pclass) {
        return pclass != null && pclass.getRace() == Race.DWARF;
    }
}
