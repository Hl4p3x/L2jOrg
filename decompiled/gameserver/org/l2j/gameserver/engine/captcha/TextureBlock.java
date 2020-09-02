// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.captcha;

class TextureBlock
{
    private ARGB[] colors;
    private ARGB[] palette;
    private int minColorIndex;
    private int maxColorIndex;
    private short minColor;
    private short maxColor;
    
    TextureBlock() {
        this.colors = new ARGB[16];
        this.palette = new ARGB[4];
        for (int i = 0; i < 16; ++i) {
            this.colors[i] = new ARGB();
        }
        for (int i = 0; i < 4; ++i) {
            this.palette[i] = new ARGB();
        }
    }
    
    public void of(final int[] buffer) {
        int maxDistance = -1;
        for (int i = 0; i < 16; ++i) {
            this.colors[i].a = (0xFF & buffer[i] >> 24);
            this.colors[i].r = (0xFF & buffer[i] >> 16);
            this.colors[i].g = (0xFF & buffer[i] >> 8);
            this.colors[i].b = (0xFF & buffer[i]);
            for (int j = i - 1; j >= 0; --j) {
                final int distance = this.euclidianDistance(this.colors[i], this.colors[j]);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    this.minColorIndex = j;
                    this.maxColorIndex = i;
                }
            }
        }
        this.computMinMaxColor();
        this.computePalette();
    }
    
    private void computePalette() {
        this.palette[0] = this.colorAt(this.maxColorIndex);
        this.palette[1] = this.colorAt(this.minColorIndex);
        this.palette[2].a = 255;
        this.palette[2].r = (2 * this.palette[0].r + this.palette[1].r) / 3;
        this.palette[2].g = (2 * this.palette[0].g + this.palette[1].g) / 3;
        this.palette[2].b = (2 * this.palette[0].b + this.palette[1].b) / 3;
        this.palette[3].a = 255;
        this.palette[3].r = (2 * this.palette[1].r + this.palette[0].r) / 3;
        this.palette[3].g = (2 * this.palette[1].g + this.palette[0].g) / 3;
        this.palette[3].b = (2 * this.palette[1].b + this.palette[0].b) / 3;
    }
    
    private void computMinMaxColor() {
        this.maxColor = this.colors[this.maxColorIndex].toShortRGB565();
        this.minColor = this.colors[this.minColorIndex].toShortRGB565();
        if (this.maxColor < this.minColor) {
            final short tmp = this.maxColor;
            this.maxColor = this.minColor;
            this.minColor = tmp;
            final int tmp2 = this.maxColorIndex;
            this.maxColorIndex = this.minColorIndex;
            this.minColorIndex = tmp2;
        }
    }
    
    private int euclidianDistance(final ARGB c1, final ARGB c2) {
        return (c1.r - c2.r) * (c1.r - c2.r) + (c1.g - c2.g) * (c1.g - c2.g) + (c1.b - c2.b) * (c1.b - c2.b);
    }
    
    short getMaxColor() {
        return this.maxColor;
    }
    
    short getMinColor() {
        return this.minColor;
    }
    
    ARGB colorAt(final int index) {
        return this.colors[index];
    }
    
    ARGB[] getPalette() {
        return this.palette;
    }
    
    static class ARGB
    {
        int a;
        int r;
        int g;
        int b;
        
        short toShortRGB565() {
            return (short)((0xF8 & this.r) << 8 | (0xFC & this.g) << 3 | this.b >> 3);
        }
    }
}
