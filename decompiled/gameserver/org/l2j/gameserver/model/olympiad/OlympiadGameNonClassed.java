// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.Config;
import java.util.Set;

public class OlympiadGameNonClassed extends OlympiadGameNormal
{
    public OlympiadGameNonClassed(final int id, final Participant[] opponents) {
        super(id, opponents);
    }
    
    protected static OlympiadGameNonClassed createGame(final int id, final Set<Integer> list) {
        final Participant[] opponents = OlympiadGameNormal.createListOfParticipants(list);
        if (opponents == null) {
            return null;
        }
        return new OlympiadGameNonClassed(id, opponents);
    }
    
    @Override
    public final CompetitionType getType() {
        return CompetitionType.NON_CLASSED;
    }
    
    @Override
    protected final int getDivider() {
        return Config.ALT_OLY_DIVIDER_NON_CLASSED;
    }
}
