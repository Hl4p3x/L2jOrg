// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.RadarControl;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.l2j.gameserver.model.actor.instance.Player;

public final class Radar
{
    private final Player _player;
    private final Set<RadarMarker> _markers;
    
    public Radar(final Player player) {
        this._markers = (Set<RadarMarker>)ConcurrentHashMap.newKeySet();
        this._player = player;
    }
    
    public void addMarker(final int x, final int y, final int z) {
        final RadarMarker newMarker = new RadarMarker(x, y, z);
        this._markers.add(newMarker);
        this._player.sendPacket(new RadarControl(2, 2, x, y, z));
        this._player.sendPacket(new RadarControl(0, 1, x, y, z));
    }
    
    public void removeMarker(final int x, final int y, final int z) {
        for (final RadarMarker rm : this._markers) {
            if (rm._x == x && rm._y == y && rm._z == z) {
                this._markers.remove(rm);
            }
        }
        this._player.sendPacket(new RadarControl(1, 1, x, y, z));
    }
    
    public void removeAllMarkers() {
        for (final RadarMarker tempMarker : this._markers) {
            this._player.sendPacket(new RadarControl(2, 2, tempMarker._x, tempMarker._y, tempMarker._z));
        }
        this._markers.clear();
    }
    
    public void loadMarkers() {
        this._player.sendPacket(new RadarControl(2, 2, this._player.getX(), this._player.getY(), this._player.getZ()));
        for (final RadarMarker tempMarker : this._markers) {
            this._player.sendPacket(new RadarControl(0, 1, tempMarker._x, tempMarker._y, tempMarker._z));
        }
    }
    
    public static class RadarMarker
    {
        public int _type;
        public int _x;
        public int _y;
        public int _z;
        
        public RadarMarker(final int type, final int x, final int y, final int z) {
            this._type = type;
            this._x = x;
            this._y = y;
            this._z = z;
        }
        
        public RadarMarker(final int x, final int y, final int z) {
            this._type = 1;
            this._x = x;
            this._y = y;
            this._z = z;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + this._type;
            result = 31 * result + this._x;
            result = 31 * result + this._y;
            result = 31 * result + this._z;
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RadarMarker)) {
                return false;
            }
            final RadarMarker other = (RadarMarker)obj;
            return this._type == other._type && this._x == other._x && this._y == other._y && this._z == other._z;
        }
    }
}
