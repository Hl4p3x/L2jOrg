// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.Config;
import org.l2j.commons.util.Rnd;
import java.util.Set;
import java.util.List;

public class OlympiadGameClassed extends OlympiadGameNormal
{
    private OlympiadGameClassed(final int id, final Participant[] opponents) {
        super(id, opponents);
    }
    
    protected static OlympiadGameClassed createGame(final int id, final List<Set<Integer>> classList) {
        if (classList == null || classList.isEmpty()) {
            return null;
        }
        while (!classList.isEmpty()) {
            final Set<Integer> list = classList.get(Rnd.get(classList.size()));
            if (list == null || list.size() < 2) {
                classList.remove(list);
            }
            else {
                final Participant[] opponents = OlympiadGameNormal.createListOfParticipants(list);
                if (opponents != null) {
                    return new OlympiadGameClassed(id, opponents);
                }
                classList.remove(list);
            }
        }
        return null;
    }
    
    @Override
    public final CompetitionType getType() {
        return CompetitionType.CLASSED;
    }
    
    @Override
    protected final int getDivider() {
        return Config.ALT_OLY_DIVIDER_CLASSED;
    }
}
