// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.PetitionState;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.HashMap;
import org.l2j.gameserver.model.Petition;
import java.util.Map;
import org.slf4j.Logger;

public final class PetitionManager
{
    protected static final Logger LOGGER;
    private final Map<Integer, Petition> _pendingPetitions;
    private final Map<Integer, Petition> _completedPetitions;
    
    private PetitionManager() {
        this._pendingPetitions = new HashMap<Integer, Petition>();
        this._completedPetitions = new HashMap<Integer, Petition>();
    }
    
    public void clearCompletedPetitions() {
        final int numPetitions = this._pendingPetitions.size();
        this._completedPetitions.clear();
        PetitionManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), numPetitions));
    }
    
    public void clearPendingPetitions() {
        final int numPetitions = this._pendingPetitions.size();
        this._pendingPetitions.clear();
        PetitionManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), numPetitions));
    }
    
    public boolean acceptPetition(final Player respondingAdmin, final int petitionId) {
        if (!this.isValidPetition(petitionId)) {
            return false;
        }
        final Petition currPetition = this._pendingPetitions.get(petitionId);
        if (currPetition.getResponder() != null) {
            return false;
        }
        currPetition.setResponder(respondingAdmin);
        currPetition.setState(PetitionState.IN_PROCESS);
        currPetition.sendPetitionerPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_APPLICATION_ACCEPTED));
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PETITION_APPLICATION_HAS_BEEN_ACCEPTED_NRECEIPT_NO_IS_S1);
        sm.addInt(currPetition.getId());
        currPetition.sendResponderPacket(sm);
        sm = SystemMessage.getSystemMessage(SystemMessageId.STARTING_PETITION_CONSULTATION_WITH_C1);
        sm.addString(currPetition.getPetitioner().getName());
        currPetition.sendResponderPacket(sm);
        currPetition.getPetitioner().setLastPetitionGmName(currPetition.getResponder().getName());
        return true;
    }
    
    public boolean cancelActivePetition(final Player player) {
        for (final Petition currPetition : this._pendingPetitions.values()) {
            if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
                return currPetition.endPetitionConsultation(PetitionState.PETITIONER_CANCEL);
            }
            if (currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId()) {
                return currPetition.endPetitionConsultation(PetitionState.RESPONDER_CANCEL);
            }
        }
        return false;
    }
    
    public void checkPetitionMessages(final Player petitioner) {
        if (petitioner != null) {
            for (final Petition currPetition : this._pendingPetitions.values()) {
                if (currPetition == null) {
                    continue;
                }
                if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == petitioner.getObjectId()) {
                    for (final CreatureSay logMessage : currPetition.getLogMessages()) {
                        petitioner.sendPacket(logMessage);
                    }
                }
            }
        }
    }
    
    public boolean endActivePetition(final Player player) {
        if (!player.isGM()) {
            return false;
        }
        for (final Petition currPetition : this._pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }
            if (currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId()) {
                return currPetition.endPetitionConsultation(PetitionState.COMPLETED);
            }
        }
        return false;
    }
    
    public Map<Integer, Petition> getCompletedPetitions() {
        return this._completedPetitions;
    }
    
    public Map<Integer, Petition> getPendingPetitions() {
        return this._pendingPetitions;
    }
    
    public int getPendingPetitionCount() {
        return this._pendingPetitions.size();
    }
    
    public int getPlayerTotalPetitionCount(final Player player) {
        if (player == null) {
            return 0;
        }
        int petitionCount = 0;
        for (final Petition currPetition : this._pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }
            if (currPetition.getPetitioner() == null || currPetition.getPetitioner().getObjectId() != player.getObjectId()) {
                continue;
            }
            ++petitionCount;
        }
        for (final Petition currPetition : this._completedPetitions.values()) {
            if (currPetition == null) {
                continue;
            }
            if (currPetition.getPetitioner() == null || currPetition.getPetitioner().getObjectId() != player.getObjectId()) {
                continue;
            }
            ++petitionCount;
        }
        return petitionCount;
    }
    
    public boolean isPetitionInProcess() {
        for (final Petition currPetition : this._pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }
            if (currPetition.getState() == PetitionState.IN_PROCESS) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isPetitionInProcess(final int petitionId) {
        if (!this.isValidPetition(petitionId)) {
            return false;
        }
        final Petition currPetition = this._pendingPetitions.get(petitionId);
        return currPetition.getState() == PetitionState.IN_PROCESS;
    }
    
    public boolean isPlayerInConsultation(final Player player) {
        if (player != null) {
            for (final Petition currPetition : this._pendingPetitions.values()) {
                if (currPetition == null) {
                    continue;
                }
                if (currPetition.getState() != PetitionState.IN_PROCESS) {
                    continue;
                }
                if ((currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) || (currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isPetitioningAllowed() {
        return Config.PETITIONING_ALLOWED;
    }
    
    public boolean isPlayerPetitionPending(final Player petitioner) {
        if (petitioner != null) {
            for (final Petition currPetition : this._pendingPetitions.values()) {
                if (currPetition == null) {
                    continue;
                }
                if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == petitioner.getObjectId()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isValidPetition(final int petitionId) {
        return this._pendingPetitions.containsKey(petitionId);
    }
    
    public boolean rejectPetition(final Player respondingAdmin, final int petitionId) {
        if (!this.isValidPetition(petitionId)) {
            return false;
        }
        final Petition currPetition = this._pendingPetitions.get(petitionId);
        if (currPetition.getResponder() != null) {
            return false;
        }
        currPetition.setResponder(respondingAdmin);
        return currPetition.endPetitionConsultation(PetitionState.RESPONDER_REJECT);
    }
    
    public boolean sendActivePetitionMessage(final Player player, final String messageText) {
        for (final Petition currPetition : this._pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }
            if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
                final CreatureSay cs = new CreatureSay(player, ChatType.PETITION_PLAYER, messageText);
                currPetition.addLogMessage(cs);
                currPetition.sendResponderPacket(cs);
                currPetition.sendPetitionerPacket(cs);
                return true;
            }
            if (currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId()) {
                final CreatureSay cs = new CreatureSay(player, ChatType.PETITION_GM, messageText);
                currPetition.addLogMessage(cs);
                currPetition.sendResponderPacket(cs);
                currPetition.sendPetitionerPacket(cs);
                return true;
            }
        }
        return false;
    }
    
    public void sendPendingPetitionList(final Player activeChar) {
        final StringBuilder htmlContent = new StringBuilder(600 + this._pendingPetitions.size() * 300);
        htmlContent.append("<html><body><center><table width=270><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=180><center>Petition Menu</center></td><td width=45><button value=\"Back\" action=\"bypass -h admin_admin7\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br><table width=\"270\"><tr><td><table width=\"270\"><tr><td><button value=\"Reset\" action=\"bypass -h admin_reset_petitions\" width=\"80\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td align=right><button value=\"Refresh\" action=\"bypass -h admin_view_petitions\" width=\"80\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br></td></tr>");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (this._pendingPetitions.isEmpty()) {
            htmlContent.append("<tr><td>There are no currently pending petitions.</td></tr>");
        }
        else {
            htmlContent.append("<tr><td><font color=\"LEVEL\">Current Petitions:</font><br></td></tr>");
        }
        boolean color = true;
        int petcount = 0;
        for (final Petition currPetition : this._pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }
            htmlContent.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, color ? "131210" : "444444", dateFormat.format(new Date(currPetition.getSubmitTime()))));
            htmlContent.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, currPetition.getPetitioner().isOnline() ? "00FF00" : "999999", currPetition.getPetitioner().getName()));
            htmlContent.append("<tr><td width=\"130\">");
            if (currPetition.getState() != PetitionState.IN_PROCESS) {
                htmlContent.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, currPetition.getId(), currPetition.getId()));
            }
            else {
                htmlContent.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, currPetition.getResponder().isOnline() ? "00FF00" : "999999", currPetition.getResponder().getName()));
            }
            htmlContent.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, currPetition.getTypeAsString(), currPetition.getTypeAsString()));
            color = !color;
            if (++petcount > 10) {
                htmlContent.append("<tr><td><font color=\"LEVEL\">There is more pending petition...</font><br></td></tr>");
                break;
            }
        }
        htmlContent.append("</table></center></body></html>");
        final NpcHtmlMessage htmlMsg = new NpcHtmlMessage();
        htmlMsg.setHtml(htmlContent.toString());
        activeChar.sendPacket(htmlMsg);
    }
    
    public int submitPetition(final Player petitioner, final String petitionText, final int petitionType) {
        final Petition newPetition = new Petition(petitioner, petitionText, petitionType);
        final int newPetitionId = newPetition.getId();
        this._pendingPetitions.put(newPetitionId, newPetition);
        final String msgContent = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, petitioner.getName());
        AdminData.getInstance().broadcastToGMs(new CreatureSay(petitioner.getObjectId(), ChatType.HERO_VOICE, "Petition System", msgContent));
        return newPetitionId;
    }
    
    public void viewPetition(final Player activeChar, final int petitionId) {
        if (!activeChar.isGM()) {
            return;
        }
        if (!this.isValidPetition(petitionId)) {
            return;
        }
        final Petition currPetition = this._pendingPetitions.get(petitionId);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile(activeChar, "data/html/admin/petition.htm");
        html.replace("%petition%", String.valueOf(currPetition.getId()));
        html.replace("%time%", dateFormat.format(new Date(currPetition.getSubmitTime())));
        html.replace("%type%", currPetition.getTypeAsString());
        html.replace("%petitioner%", currPetition.getPetitioner().getName());
        html.replace("%online%", currPetition.getPetitioner().isOnline() ? "00FF00" : "999999");
        html.replace("%text%", currPetition.getContent());
        activeChar.sendPacket(html);
    }
    
    public static PetitionManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PetitionManager.class);
    }
    
    private static class Singleton
    {
        private static final PetitionManager INSTANCE;
        
        static {
            INSTANCE = new PetitionManager();
        }
    }
}
