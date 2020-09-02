// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.interfaces.ILocational;

public enum Position
{
    FRONT, 
    SIDE, 
    BACK;
    
    public static Position getPosition(final ILocational from, final ILocational to) {
        final int heading = Math.abs(to.getHeading() - MathUtil.calculateHeadingFrom(from, to));
        if ((heading >= 8192 && heading <= 24576) || Integer.toUnsignedLong(heading - 40960) <= 16384L) {
            return Position.SIDE;
        }
        if (Integer.toUnsignedLong(heading - 8192) <= 49152L) {
            return Position.FRONT;
        }
        return Position.BACK;
    }
}
