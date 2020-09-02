// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.captcha;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;

class DXT1ImageCompressor
{
    private static final int MAGIC = 542327876;
    private static final int HEADER_SIZE = 124;
    private static final int DDSD_PIXEL_FORMAT = 4096;
    private static final int DDSD_CAPS = 1;
    private static final int DDSD_HEIGHT = 2;
    private static final int DDSD_WIDTH = 4;
    private static final int DDSCAPS_TEXTURE = 4096;
    private static final int DDPF_FOURCC = 4;
    private static final int DXT1 = 827611204;
    private static final int PIXEL_FORMAT_SIZE = 32;
    
    byte[] compress(final BufferedImage image) {
        final int height = image.getHeight();
        final int width = image.getWidth();
        final int compressedSize = Math.max(width, 4) * Math.max(height, 4) / 2;
        final ByteBuffer buffer = ByteBuffer.allocate(128 + compressedSize).order(ByteOrder.LITTLE_ENDIAN);
        this.writeHeader(image, buffer);
        final int[] texelBuffer = new int[16];
        final TextureBlock block = new TextureBlock();
        for (int i = 0; i < height; i += 4) {
            for (int j = 0; j < width; j += 4) {
                this.extractBlock(image, j, i, texelBuffer, block);
                buffer.putShort(block.getMaxColor());
                buffer.putShort(block.getMinColor());
                buffer.putInt(this.computColorIndexes(block));
            }
        }
        return buffer.array();
    }
    
    private int computColorIndexes(final TextureBlock block) {
        final TextureBlock.ARGB[] palette = block.getPalette();
        long encodedColors = 0L;
        for (int i = 15; i >= 0; --i) {
            final TextureBlock.ARGB color = block.colorAt(i);
            final int d0 = Math.abs(palette[0].r - color.r) + Math.abs(palette[0].g - color.g) + Math.abs(palette[0].b - color.b);
            final int d2 = Math.abs(palette[1].r - color.r) + Math.abs(palette[1].g - color.g) + Math.abs(palette[1].b - color.b);
            final int d3 = Math.abs(palette[2].r - color.r) + Math.abs(palette[2].g - color.g) + Math.abs(palette[2].b - color.b);
            final int d4 = Math.abs(palette[3].r - color.r) + Math.abs(palette[3].g - color.g) + Math.abs(palette[3].b - color.b);
            final int b0 = this.compare(d0, d4);
            final int b2 = this.compare(d2, d3);
            final int b3 = this.compare(d0, d3);
            final int b4 = this.compare(d2, d4);
            final int b5 = this.compare(d3, d4);
            final int x0 = b2 & b3;
            final int x2 = b0 & b4;
            final int x3 = b0 & b5;
            final long index = x3 | (x0 | x2) << 1;
            encodedColors |= index << (i << 1);
        }
        return (int)encodedColors;
    }
    
    private int compare(final int a, final int b) {
        return b - a >>> 31;
    }
    
    private void writeHeader(final BufferedImage image, final ByteBuffer buffer) {
        buffer.putInt(542327876);
        buffer.putInt(124);
        buffer.putInt(4103);
        buffer.putInt(image.getHeight());
        buffer.putInt(image.getWidth());
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.put(new byte[44]);
        buffer.putInt(32);
        buffer.putInt(4);
        buffer.putInt(827611204);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(4096);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
    }
    
    private void extractBlock(final BufferedImage image, final int x, final int y, final int[] buffer, final TextureBlock block) {
        final int blockWidth = Math.min(image.getWidth() - x, 4);
        final int blockHeight = Math.min(image.getHeight() - y, 4);
        image.getRGB(x, y, blockWidth, blockHeight, buffer, 0, 4);
        block.of(buffer);
    }
}
