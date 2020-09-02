// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.Location;
import java.util.HashMap;
import java.util.Map;

public final class NpcRoutesHolder
{
    private final Map<String, String> _correspondences;
    
    public NpcRoutesHolder() {
        this._correspondences = new HashMap<String, String>();
    }
    
    public void addRoute(final String routeName, final Location loc) {
        this._correspondences.put(this.getUniqueKey(loc), routeName);
    }
    
    public String getRouteName(final Npc npc) {
        if (npc.getSpawn() != null) {
            final String key = this.getUniqueKey(npc.getSpawn().getLocation());
            return this._correspondences.getOrDefault(key, "");
        }
        return "";
    }
    
    private String getUniqueKey(final ILocational loc) {
        return invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, loc.getX(), loc.getY(), loc.getZ());
    }
}
