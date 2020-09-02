// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.interfaces;

import org.l2j.gameserver.model.Location;

public interface IPositionable extends ILocational
{
    void setXYZ(final int x, final int y, final int z);
    
    void setXYZ(final ILocational loc);
    
    void setHeading(final int heading);
    
    void setLocation(final Location loc);
}
