// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.math.BigInteger;
import java.util.Iterator;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import java.util.List;

public class AdminForgePacket extends ServerPacket
{
    private final List<Part> _parts;
    
    public AdminForgePacket() {
        this._parts = new ArrayList<Part>();
    }
    
    public void writeImpl(final GameClient client) {
        for (final Part p : this._parts) {
            this.generate(p.b, p.str);
        }
    }
    
    public boolean generate(final byte type, final String value) {
        boolean b = false;
        switch (type | 0x20) {
            case 99: {
                this.writeByte(Integer.decode(value).byteValue());
                b = true;
                break;
            }
            case 100: {
                this.writeInt((int)Integer.decode(value));
                b = true;
                break;
            }
            case 104: {
                this.writeShort(Integer.decode(value).shortValue());
                b = true;
                break;
            }
            case 102: {
                this.writeDouble(Double.parseDouble(value));
                b = true;
                break;
            }
            case 115: {
                this.writeString((CharSequence)value);
                b = true;
                break;
            }
            case 98:
            case 120: {
                this.writeBytes(new BigInteger(value).toByteArray());
                b = true;
                break;
            }
            case 113: {
                this.writeLong((long)Long.decode(value));
                b = true;
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public void addPart(final byte b, final String string) {
        this._parts.add(new Part(b, string));
    }
    
    private static class Part
    {
        public byte b;
        public String str;
        
        public Part(final byte bb, final String string) {
            this.b = bb;
            this.str = string;
        }
    }
}
