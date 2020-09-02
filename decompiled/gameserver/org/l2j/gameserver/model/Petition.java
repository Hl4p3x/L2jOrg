// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.network.serverpackets.PetitionVotePacket;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.idfactory.IdFactory;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.enums.PetitionState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import java.util.Collection;
import org.l2j.gameserver.enums.PetitionType;

public final class Petition
{
    private final long _submitTime;
    private final int _id;
    private final PetitionType _type;
    private final String _content;
    private final Collection<CreatureSay> _messageLog;
    private final Player _petitioner;
    private PetitionState _state;
    private Player _responder;
    
    public Petition(final Player petitioner, final String petitionText, int petitionType) {
        this._submitTime = System.currentTimeMillis();
        this._messageLog = (Collection<CreatureSay>)ConcurrentHashMap.newKeySet();
        this._state = PetitionState.PENDING;
        this._id = IdFactory.getInstance().getNextId();
        this._type = PetitionType.values()[--petitionType];
        this._content = petitionText;
        this._petitioner = petitioner;
    }
    
    public boolean addLogMessage(final CreatureSay cs) {
        return this._messageLog.add(cs);
    }
    
    public Collection<CreatureSay> getLogMessages() {
        return this._messageLog;
    }
    
    public boolean endPetitionConsultation(final PetitionState endState) {
        this.setState(endState);
        if (this._responder != null && this._responder.isOnline()) {
            if (endState == PetitionState.RESPONDER_REJECT) {
                this._petitioner.sendMessage("Your petition was rejected. Please try again later.");
            }
            else {
                SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ENDED_THE_CUSTOMER_CONSULTATION_WITH_C1);
                sm.addString(this._petitioner.getName());
                this._responder.sendPacket(sm);
                if (endState == PetitionState.PETITIONER_CANCEL) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.CANCELED_THE_QUERY_NO_S1);
                    sm.addInt(this._id);
                    this._responder.sendPacket(sm);
                }
            }
        }
        if (this._petitioner != null && this._petitioner.isOnline()) {
            this._petitioner.sendPacket(SystemMessageId.GM_HAS_REPLIED_TO_YOUR_QUESTION_PLEASE_LEAVE_A_REVIEW_ON_OUR_CUSTOMER_QUERY_SERVICE);
            this._petitioner.sendPacket(PetitionVotePacket.STATIC_PACKET);
        }
        PetitionManager.getInstance().getCompletedPetitions().put(this.getId(), this);
        return PetitionManager.getInstance().getPendingPetitions().remove(this.getId()) != null;
    }
    
    public String getContent() {
        return this._content;
    }
    
    public int getId() {
        return this._id;
    }
    
    public Player getPetitioner() {
        return this._petitioner;
    }
    
    public Player getResponder() {
        return this._responder;
    }
    
    public void setResponder(final Player respondingAdmin) {
        if (this._responder != null) {
            return;
        }
        this._responder = respondingAdmin;
    }
    
    public long getSubmitTime() {
        return this._submitTime;
    }
    
    public PetitionState getState() {
        return this._state;
    }
    
    public void setState(final PetitionState state) {
        this._state = state;
    }
    
    public String getTypeAsString() {
        return this._type.toString().replace("_", " ");
    }
    
    public void sendPetitionerPacket(final ServerPacket responsePacket) {
        if (this._petitioner == null || !this._petitioner.isOnline()) {
            return;
        }
        this._petitioner.sendPacket(responsePacket);
    }
    
    public void sendResponderPacket(final ServerPacket responsePacket) {
        if (this._responder == null || !this._responder.isOnline()) {
            this.endPetitionConsultation(PetitionState.RESPONDER_MISSING);
            return;
        }
        this._responder.sendPacket(responsePacket);
    }
}
