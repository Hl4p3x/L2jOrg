// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public final class RequestPCCafeCouponUse extends ClientPacket
{
    private static final Logger LOGGER;
    private String _str;
    
    public void readImpl() {
        this._str = this.readString();
    }
    
    public void runImpl() {
        RequestPCCafeCouponUse.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._str));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestPCCafeCouponUse.class);
    }
}
