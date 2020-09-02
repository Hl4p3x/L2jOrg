// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.Buffer;

public class Crypt
{
    private final byte[] inKey;
    private final byte[] outKey;
    private boolean enabled;
    
    public Crypt() {
        this.inKey = new byte[16];
        this.outKey = new byte[16];
    }
    
    public void setKey(final byte[] key) {
        System.arraycopy(key, 0, this.inKey, 0, 16);
        System.arraycopy(key, 0, this.outKey, 0, 16);
    }
    
    public boolean encrypt(final Buffer data, final int offset, final int size) {
        if (!this.enabled) {
            this.enabled = true;
        }
        else {
            int encrypted = 0;
            for (int i = 0; i < size; ++i) {
                final int raw = Byte.toUnsignedInt(data.readByte(offset + i));
                encrypted ^= (raw ^ this.outKey[i & 0xF]);
                data.writeByte(offset + i, (byte)encrypted);
            }
            this.shiftKey(this.outKey, size);
        }
        return true;
    }
    
    public boolean decrypt(final Buffer data, final int offset, final int size) {
        if (this.enabled) {
            int xOr = 0;
            for (int i = 0; i < size; ++i) {
                final int encrypted = Byte.toUnsignedInt(data.readByte(offset + i));
                data.writeByte(offset + i, (byte)(encrypted ^ this.inKey[i & 0xF] ^ xOr));
                xOr = encrypted;
            }
            this.shiftKey(this.inKey, size);
        }
        return true;
    }
    
    private void shiftKey(final byte[] key, final int size) {
        int old = key[8] & 0xFF;
        old |= (key[9] << 8 & 0xFF00);
        old |= (key[10] << 16 & 0xFF0000);
        old |= (key[11] << 24 & 0xFF000000);
        old += size;
        key[8] = (byte)(old & 0xFF);
        key[9] = (byte)(old >> 8 & 0xFF);
        key[10] = (byte)(old >> 16 & 0xFF);
        key[11] = (byte)(old >> 24 & 0xFF);
    }
}
