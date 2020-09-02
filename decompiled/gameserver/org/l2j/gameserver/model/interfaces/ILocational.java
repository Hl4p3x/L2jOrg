// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.interfaces;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.util.MathUtil;

public interface ILocational
{
    int getX();
    
    int getY();
    
    int getZ();
    
    int getHeading();
    
    ILocational getLocation();
    
    @Deprecated
    default int calculateHeadingTo(final ILocational to) {
        return MathUtil.calculateHeadingFrom(this, to);
    }
    
    default boolean isInFrontOf(final ILocational target) {
        return Util.falseIfNullOrElse((Object)target, t -> Position.getPosition(this, target) == Position.FRONT);
    }
    
    default boolean isOnSideOf(final ILocational target) {
        return Util.falseIfNullOrElse((Object)target, t -> Position.getPosition(this, target) == Position.SIDE);
    }
    
    default boolean isBehind(final ILocational target) {
        return Util.falseIfNullOrElse((Object)target, t -> Position.getPosition(this, target) == Position.BACK);
    }
}
