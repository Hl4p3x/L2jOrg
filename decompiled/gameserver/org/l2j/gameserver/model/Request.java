// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.model.actor.instance.Player;

public class Request
{
    private static final int REQUEST_TIMEOUT = 15;
    protected Player _player;
    protected Player _partner;
    protected boolean _isRequestor;
    protected boolean _isAnswerer;
    protected ClientPacket _requestPacket;
    
    public Request(final Player player) {
        this._player = player;
    }
    
    protected void clear() {
        this._partner = null;
        this._requestPacket = null;
        this._isRequestor = false;
        this._isAnswerer = false;
    }
    
    public Player getPartner() {
        return this._partner;
    }
    
    private synchronized void setPartner(final Player partner) {
        this._partner = partner;
    }
    
    public ClientPacket getRequestPacket() {
        return this._requestPacket;
    }
    
    private synchronized void setRequestPacket(final ClientPacket packet) {
        this._requestPacket = packet;
    }
    
    public synchronized boolean setRequest(final Player partner, final ClientPacket packet) {
        if (partner == null) {
            this._player.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return false;
        }
        if (partner.getRequest().isProcessingRequest()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
            sm.addString(partner.getName());
            this._player.sendPacket(sm);
            return false;
        }
        if (this.isProcessingRequest()) {
            this._player.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
            return false;
        }
        this._partner = partner;
        this._requestPacket = packet;
        this.setOnRequestTimer(true);
        this._partner.getRequest().setPartner(this._player);
        this._partner.getRequest().setRequestPacket(packet);
        this._partner.getRequest().setOnRequestTimer(false);
        return true;
    }
    
    private void setOnRequestTimer(final boolean isRequestor) {
        this._isRequestor = isRequestor;
        this._isAnswerer = !isRequestor;
        ThreadPool.schedule(() -> this.clear(), 15000L);
    }
    
    public void onRequestResponse() {
        if (this._partner != null) {
            this._partner.getRequest().clear();
        }
        this.clear();
    }
    
    public boolean isProcessingRequest() {
        return this._partner != null;
    }
}
