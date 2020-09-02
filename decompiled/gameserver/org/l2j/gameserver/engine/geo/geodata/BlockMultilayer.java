// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.geodata;

import java.io.IOException;
import java.io.BufferedOutputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.nio.ByteBuffer;

public class BlockMultilayer extends ABlock
{
    private static final int MAX_LAYERS = 127;
    private static ByteBuffer temp;
    protected byte[] _buffer;
    
    protected BlockMultilayer() {
        this._buffer = null;
    }
    
    public BlockMultilayer(final ByteBuffer bb, final GeoFormat format) {
        for (int cell = 0; cell < 64; ++cell) {
            final byte layers = (format != GeoFormat.L2OFF) ? bb.get() : ((byte)bb.getShort());
            if (layers <= 0 || layers > 127) {
                throw new RuntimeException("Invalid layer count for MultilayerBlock");
            }
            BlockMultilayer.temp.put(layers);
            for (byte layer = 0; layer < layers; ++layer) {
                if (format != GeoFormat.L2D) {
                    final short data = bb.getShort();
                    BlockMultilayer.temp.put((byte)(data & 0xF));
                    BlockMultilayer.temp.putShort((short)((short)(data & 0xFFF0) >> 1));
                }
                else {
                    BlockMultilayer.temp.put(bb.get());
                    BlockMultilayer.temp.putShort(bb.getShort());
                }
            }
        }
        this._buffer = Arrays.copyOf(BlockMultilayer.temp.array(), BlockMultilayer.temp.position());
        BlockMultilayer.temp.clear();
    }
    
    public static void initialize() {
        BlockMultilayer.temp = ByteBuffer.allocate(24384).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    public static void release() {
        BlockMultilayer.temp = null;
    }
    
    @Override
    public final boolean hasGeoPos() {
        return true;
    }
    
    @Override
    public final short getHeightNearest(final int geoX, final int geoY, final int worldZ) {
        final int index = this.getIndexNearest(geoX, geoY, worldZ);
        return (short)((this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8);
    }
    
    @Override
    public short getHeightNearestOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getHeightNearest(geoX, geoY, worldZ);
    }
    
    @Override
    public final short getHeightAbove(final int geoX, final int geoY, final int worldZ) {
        int index = 0;
        for (int i = 0; i < geoX % 8 * 8 + geoY % 8; ++i) {
            index += this._buffer[index] * 3 + 1;
        }
        byte layers = this._buffer[index++];
        index += (layers - 1) * 3;
        while (true) {
            final byte b = layers;
            --layers;
            if (b <= 0) {
                return -32768;
            }
            final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
            if (height > worldZ) {
                return (short)height;
            }
            index -= 3;
        }
    }
    
    @Override
    public final short getHeightBelow(final int geoX, final int geoY, final int worldZ) {
        int index = 0;
        for (int i = 0; i < geoX % 8 * 8 + geoY % 8; ++i) {
            index += this._buffer[index] * 3 + 1;
        }
        byte layers = this._buffer[index++];
        while (true) {
            final byte b = layers;
            --layers;
            if (b <= 0) {
                return 32767;
            }
            final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
            if (height < worldZ) {
                return (short)height;
            }
            index += 3;
        }
    }
    
    @Override
    public final byte getNsweNearest(final int geoX, final int geoY, final int worldZ) {
        final int index = this.getIndexNearest(geoX, geoY, worldZ);
        return this._buffer[index];
    }
    
    @Override
    public byte getNsweNearestOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getNsweNearest(geoX, geoY, worldZ);
    }
    
    @Override
    public final byte getNsweAbove(final int geoX, final int geoY, final int worldZ) {
        int index = 0;
        for (int i = 0; i < geoX % 8 * 8 + geoY % 8; ++i) {
            index += this._buffer[index] * 3 + 1;
        }
        byte layers = this._buffer[index++];
        index += (layers - 1) * 3;
        while (true) {
            final byte b = layers;
            --layers;
            if (b <= 0) {
                return 0;
            }
            final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
            if (height > worldZ) {
                return this._buffer[index];
            }
            index -= 3;
        }
    }
    
    @Override
    public final byte getNsweBelow(final int geoX, final int geoY, final int worldZ) {
        int index = 0;
        for (int i = 0; i < geoX % 8 * 8 + geoY % 8; ++i) {
            index += this._buffer[index] * 3 + 1;
        }
        byte layers = this._buffer[index++];
        while (true) {
            final byte b = layers;
            --layers;
            if (b <= 0) {
                return 0;
            }
            final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
            if (height < worldZ) {
                return this._buffer[index];
            }
            index += 3;
        }
    }
    
    @Override
    public final int getIndexNearest(final int geoX, final int geoY, final int worldZ) {
        int index = 0;
        for (int i = 0; i < geoX % 8 * 8 + geoY % 8; ++i) {
            index += this._buffer[index] * 3 + 1;
        }
        byte layers = this._buffer[index++];
        int limit = Integer.MAX_VALUE;
        while (true) {
            final byte b = layers;
            --layers;
            if (b <= 0) {
                break;
            }
            final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
            final int distance = Math.abs(height - worldZ);
            if (distance > limit) {
                break;
            }
            limit = distance;
            index += 3;
        }
        return index - 3;
    }
    
    @Override
    public final int getIndexAbove(final int geoX, final int geoY, final int worldZ) {
        int index = 0;
        for (int i = 0; i < geoX % 8 * 8 + geoY % 8; ++i) {
            index += this._buffer[index] * 3 + 1;
        }
        byte layers = this._buffer[index++];
        index += (layers - 1) * 3;
        while (true) {
            final byte b = layers;
            --layers;
            if (b <= 0) {
                return -1;
            }
            final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
            if (height > worldZ) {
                return index;
            }
            index -= 3;
        }
    }
    
    @Override
    public int getIndexAboveOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getIndexAbove(geoX, geoY, worldZ);
    }
    
    @Override
    public final int getIndexBelow(final int geoX, final int geoY, final int worldZ) {
        int index = 0;
        for (int i = 0; i < geoX % 8 * 8 + geoY % 8; ++i) {
            index += this._buffer[index] * 3 + 1;
        }
        byte layers = this._buffer[index++];
        while (true) {
            final byte b = layers;
            --layers;
            if (b <= 0) {
                return -1;
            }
            final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
            if (height < worldZ) {
                return index;
            }
            index += 3;
        }
    }
    
    @Override
    public int getIndexBelowOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getIndexBelow(geoX, geoY, worldZ);
    }
    
    @Override
    public final short getHeight(final int index) {
        return (short)((this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8);
    }
    
    @Override
    public short getHeightOriginal(final int index) {
        return (short)((this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8);
    }
    
    @Override
    public final byte getNswe(final int index) {
        return this._buffer[index];
    }
    
    @Override
    public byte getNsweOriginal(final int index) {
        return this._buffer[index];
    }
    
    @Override
    public final void setNswe(final int index, final byte nswe) {
        this._buffer[index] = nswe;
    }
    
    @Override
    public final void saveBlock(final BufferedOutputStream stream) throws IOException {
        stream.write(-46);
        int index = 0;
        for (int i = 0; i < 64; ++i) {
            final byte layers = this._buffer[index++];
            stream.write(layers);
            stream.write(this._buffer, index, layers * 3);
            index += layers * 3;
        }
    }
}
