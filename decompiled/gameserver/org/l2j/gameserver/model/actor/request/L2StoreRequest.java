// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;

public class L2StoreRequest extends AbstractRequest
{
    public L2StoreRequest(final Player activeChar) {
        super(activeChar);
    }
    
    @Override
    public boolean isUsingItem(final int objectId) {
        return false;
    }
}
