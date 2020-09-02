// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class RequestExChangeName extends ClientPacket
{
    private static final Logger LOGGER;
    private String _newName;
    private int _type;
    private int _charSlot;
    
    public void readImpl() {
        this._type = this.readInt();
        this._newName = this.readString();
        this._charSlot = this.readInt();
    }
    
    public void runImpl() {
        RequestExChangeName.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), this._newName, this._type, this._charSlot));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestExChangeName.class);
    }
}
