// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.l2j.gameserver.model.interfaces.ILocational;

public final class MathUtil
{
    private MathUtil() {
    }
    
    public static byte add(final byte oldValue, final byte value) {
        return (byte)(oldValue + value);
    }
    
    public static short add(final short oldValue, final short value) {
        return (short)(oldValue + value);
    }
    
    public static int add(final int oldValue, final int value) {
        return oldValue + value;
    }
    
    public static double add(final double oldValue, final double value) {
        return oldValue + value;
    }
    
    public static byte mul(final byte oldValue, final byte value) {
        return (byte)(oldValue * value);
    }
    
    public static short mul(final short oldValue, final short value) {
        return (short)(oldValue * value);
    }
    
    public static int mul(final int oldValue, final int value) {
        return oldValue * value;
    }
    
    public static double mul(final double oldValue, final double value) {
        return oldValue * value;
    }
    
    public static byte div(final byte oldValue, final byte value) {
        return (byte)(oldValue / value);
    }
    
    public static short div(final short oldValue, final short value) {
        return (short)(oldValue / value);
    }
    
    public static int div(final int oldValue, final int value) {
        return oldValue / value;
    }
    
    public static double div(final double oldValue, final double value) {
        return oldValue / value;
    }
    
    public static int limit(final int numToTest, final int min, final int max) {
        return (numToTest > max) ? max : Math.max(numToTest, min);
    }
    
    public static boolean isInsideRadius2D(final ILocational object, final ILocational other, final int radius) {
        return calculateDistanceSq2D(object, other) <= radius * radius;
    }
    
    public static boolean isInsideRadius2D(final ILocational object, final int x, final int y, final int radius) {
        return calculateDistanceSq2D(object.getX(), object.getY(), x, y) <= radius * radius;
    }
    
    public static double calculateDistanceSq2D(final ILocational object, final ILocational other) {
        return calculateDistanceSq2D(object.getX(), object.getY(), other.getX(), other.getY());
    }
    
    public static double calculateDistanceSq2D(final int x1, final int y1, final int x2, final int y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }
    
    public static double calculateDistance2D(final ILocational loc, final ILocational other) {
        return calculateDistance2D(loc.getX(), loc.getY(), other.getX(), other.getY());
    }
    
    public static double calculateDistance2D(final int x1, final int y1, final int x2, final int y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }
    
    public static double calculateDistance3D(final ILocational object, final ILocational other) {
        return calculateDistance3D(object.getX(), object.getY(), object.getZ(), other.getX(), other.getY(), other.getZ());
    }
    
    public static double calculateDistance3D(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0) + Math.pow(z1 - z2, 2.0));
    }
    
    public static double calculateDistanceSq3D(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
    }
    
    public static double calculateDistanceSq3D(final ILocational object, final ILocational other) {
        return calculateDistanceSq3D(object.getX(), object.getY(), object.getZ(), other.getX(), other.getY(), other.getZ());
    }
    
    public static boolean isInsideRadius3D(final ILocational object, final ILocational other, final int range) {
        return calculateDistanceSq3D(object, other) <= range * range;
    }
    
    public static boolean isInsideRadius3D(final ILocational object, final int x, final int y, final int z, final int range) {
        return calculateDistanceSq3D(object.getX(), object.getY(), object.getZ(), x, y, z) <= range * range;
    }
    
    public static boolean isInsideRadius3D(final int originX, final int originY, final int originZ, final int targetX, final int targetY, final int targetZ, final int range) {
        return calculateDistanceSq3D(originX, originY, originZ, targetX, targetY, targetZ) <= range * range;
    }
    
    public static int calculateHeadingFrom(final ILocational from, final ILocational to) {
        return calculateHeadingFrom(from.getX(), from.getY(), to.getX(), to.getY());
    }
    
    public static int calculateHeadingFrom(final int fromX, final int fromY, final int toX, final int toY) {
        double angleTarget = Math.toDegrees(Math.atan2(toY - fromY, toX - fromX));
        if (angleTarget < 0.0) {
            angleTarget += 360.0;
        }
        return (int)(angleTarget * 182.044444444);
    }
    
    public static int calculateHeadingFrom(final double dx, final double dy) {
        double angleTarget = Math.toDegrees(Math.atan2(dy, dx));
        if (angleTarget < 0.0) {
            angleTarget += 360.0;
        }
        return (int)(angleTarget * 182.044444444);
    }
    
    public static double convertHeadingToDegree(final int clientHeading) {
        return clientHeading / 182.044444444;
    }
    
    public static double calculateAngleFrom(final ILocational from, final ILocational to) {
        return calculateAngleFrom(from.getX(), from.getY(), to.getX(), to.getY());
    }
    
    public static double calculateAngleFrom(final int fromX, final int fromY, final int toX, final int toY) {
        double angleTarget = Math.toDegrees(Math.atan2(toY - fromY, toX - fromX));
        if (angleTarget < 0.0) {
            angleTarget += 360.0;
        }
        return angleTarget;
    }
    
    public static boolean checkAddOverFlow(final long x, final long y) {
        final long r = x + y;
        return ((x ^ r) & (y ^ r)) < 0L;
    }
}
