// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import org.l2j.commons.util.Rnd;

public class BlowFishKeygen
{
    private static final int CRYPT_KEYS_SIZE = 20;
    private static final byte[][] CRYPT_KEYS;
    
    private BlowFishKeygen() {
    }
    
    public static byte[] getRandomKey() {
        return BlowFishKeygen.CRYPT_KEYS[Rnd.get(20)];
    }
    
    static {
        CRYPT_KEYS = new byte[20][16];
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < BlowFishKeygen.CRYPT_KEYS[i].length; ++j) {
                BlowFishKeygen.CRYPT_KEYS[i][j] = (byte)Rnd.get(255);
            }
            BlowFishKeygen.CRYPT_KEYS[i][8] = -56;
            BlowFishKeygen.CRYPT_KEYS[i][9] = 39;
            BlowFishKeygen.CRYPT_KEYS[i][10] = -109;
            BlowFishKeygen.CRYPT_KEYS[i][11] = 1;
            BlowFishKeygen.CRYPT_KEYS[i][12] = -95;
            BlowFishKeygen.CRYPT_KEYS[i][13] = 108;
            BlowFishKeygen.CRYPT_KEYS[i][14] = 49;
            BlowFishKeygen.CRYPT_KEYS[i][15] = -105;
        }
    }
}
