// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.awt.Color;
import org.l2j.gameserver.network.serverpackets.ExServerPrimitive;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.actor.instance.Player;

public final class GeoUtils
{
    public static void debug2DLine(final Player player, final int x, final int y, final int tx, final int ty, final int z) {
        final int gx = GeoEngine.getGeoX(x);
        final int gy = GeoEngine.getGeoY(y);
        final int tgx = GeoEngine.getGeoX(tx);
        final int tgy = GeoEngine.getGeoY(ty);
        final ExServerPrimitive prim = new ExServerPrimitive("Debug2DLine", x, y, z);
        prim.addLine(Color.BLUE, GeoEngine.getWorldX(gx), GeoEngine.getWorldY(gy), z, GeoEngine.getWorldX(tgx), GeoEngine.getWorldY(tgy), z);
        final LinePointIterator iter = new LinePointIterator(gx, gy, tgx, tgy);
        while (iter.next()) {
            final int wx = GeoEngine.getWorldX(iter.x());
            final int wy = GeoEngine.getWorldY(iter.y());
            prim.addPoint(Color.RED, wx, wy, z);
        }
        player.sendPacket(prim);
    }
    
    public static void debug3DLine(final Player player, final int x, final int y, final int z, final int tx, final int ty, final int tz) {
        final int gx = GeoEngine.getGeoX(x);
        final int gy = GeoEngine.getGeoY(y);
        final int tgx = GeoEngine.getGeoX(tx);
        final int tgy = GeoEngine.getGeoY(ty);
        final ExServerPrimitive prim = new ExServerPrimitive("Debug3DLine", x, y, z);
        prim.addLine(Color.BLUE, GeoEngine.getWorldX(gx), GeoEngine.getWorldY(gy), z, GeoEngine.getWorldX(tgx), GeoEngine.getWorldY(tgy), tz);
        final LinePointIterator3D iter = new LinePointIterator3D(gx, gy, z, tgx, tgy, tz);
        iter.next();
        int prevX = iter.x();
        int prevY = iter.y();
        int wx = GeoEngine.getWorldX(prevX);
        int wy = GeoEngine.getWorldY(prevY);
        int wz = iter.z();
        prim.addPoint(Color.RED, wx, wy, wz);
        while (iter.next()) {
            final int curX = iter.x();
            final int curY = iter.y();
            if (curX != prevX || curY != prevY) {
                wx = GeoEngine.getWorldX(curX);
                wy = GeoEngine.getWorldY(curY);
                wz = iter.z();
                prim.addPoint(Color.RED, wx, wy, wz);
                prevX = curX;
                prevY = curY;
            }
        }
        player.sendPacket(prim);
    }
    
    private static Color getDirectionColor(final int x, final int y, final int z, final int nswe) {
        if ((GeoEngine.getInstance().getNsweNearest(x, y, z) & nswe) == nswe) {
            return Color.GREEN;
        }
        return Color.RED;
    }
    
    public static void debugGrid(final Player player) {
        final int geoRadius = 20;
        final int blocksPerPacket = 40;
        int iBlock = 40;
        int iPacket = 0;
        ExServerPrimitive exsp = null;
        final int playerGx = GeoEngine.getGeoX(player.getX());
        final int playerGy = GeoEngine.getGeoY(player.getY());
        for (int dx = -20; dx <= 20; ++dx) {
            for (int dy = -20; dy <= 20; ++dy) {
                if (iBlock >= 40) {
                    iBlock = 0;
                    if (exsp != null) {
                        ++iPacket;
                        player.sendPacket(exsp);
                    }
                    exsp = new ExServerPrimitive(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, iPacket), player.getX(), player.getY(), -16000);
                }
                if (exsp == null) {
                    throw new IllegalStateException();
                }
                final int gx = playerGx + dx;
                final int gy = playerGy + dy;
                final int x = GeoEngine.getWorldX(gx);
                final int y = GeoEngine.getWorldY(gy);
                final int z = GeoEngine.getInstance().getHeightNearest(gx, gy, player.getZ());
                Color col = getDirectionColor(gx, gy, z, 8);
                exsp.addLine(col, x - 1, y - 7, z, x + 1, y - 7, z);
                exsp.addLine(col, x - 2, y - 6, z, x + 2, y - 6, z);
                exsp.addLine(col, x - 3, y - 5, z, x + 3, y - 5, z);
                exsp.addLine(col, x - 4, y - 4, z, x + 4, y - 4, z);
                col = getDirectionColor(gx, gy, z, 1);
                exsp.addLine(col, x + 7, y - 1, z, x + 7, y + 1, z);
                exsp.addLine(col, x + 6, y - 2, z, x + 6, y + 2, z);
                exsp.addLine(col, x + 5, y - 3, z, x + 5, y + 3, z);
                exsp.addLine(col, x + 4, y - 4, z, x + 4, y + 4, z);
                col = getDirectionColor(gx, gy, z, 4);
                exsp.addLine(col, x - 1, y + 7, z, x + 1, y + 7, z);
                exsp.addLine(col, x - 2, y + 6, z, x + 2, y + 6, z);
                exsp.addLine(col, x - 3, y + 5, z, x + 3, y + 5, z);
                exsp.addLine(col, x - 4, y + 4, z, x + 4, y + 4, z);
                col = getDirectionColor(gx, gy, z, 2);
                exsp.addLine(col, x - 7, y - 1, z, x - 7, y + 1, z);
                exsp.addLine(col, x - 6, y - 2, z, x - 6, y + 2, z);
                exsp.addLine(col, x - 5, y - 3, z, x - 5, y + 3, z);
                exsp.addLine(col, x - 4, y - 4, z, x - 4, y + 4, z);
                ++iBlock;
            }
        }
        player.sendPacket(exsp);
    }
    
    public static int computeNswe(final int lastX, final int lastY, final int x, final int y) {
        if (x > lastX) {
            if (y > lastY) {
                return 16;
            }
            if (y < lastY) {
                return 64;
            }
            return 1;
        }
        else if (x < lastX) {
            if (y > lastY) {
                return 32;
            }
            if (y < lastY) {
                return -128;
            }
            return 2;
        }
        else {
            if (y > lastY) {
                return 4;
            }
            if (y < lastY) {
                return 8;
            }
            throw new RuntimeException();
        }
    }
}
