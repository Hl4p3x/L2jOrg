// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.captcha;

public class Captcha
{
    private final int code;
    private final byte[] data;
    private final int id;
    
    Captcha(final int id, final int code, final byte[] data) {
        this.id = id;
        this.code = code;
        this.data = data;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public int getId() {
        return this.id;
    }
}
