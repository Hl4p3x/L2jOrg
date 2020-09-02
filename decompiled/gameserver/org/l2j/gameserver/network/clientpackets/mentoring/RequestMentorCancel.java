// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.Mentee;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeLeft;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeRemove;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestMentorCancel extends ClientPacket
{
    private int _confirmed;
    private String _name;
    
    public void readImpl() {
        this._confirmed = this.readInt();
        this._name = this.readString();
    }
    
    public void runImpl() {
        if (this._confirmed != 1) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        final int objectId = PlayerNameTable.getInstance().getIdByName(this._name);
        if (player != null) {
            if (player.isMentor()) {
                final Mentee mentee = MentorManager.getInstance().getMentee(player.getObjectId(), objectId);
                if (mentee != null) {
                    MentorManager.getInstance().cancelAllMentoringBuffs(mentee.getPlayerInstance());
                    if (MentorManager.getInstance().isAllMenteesOffline(player.getObjectId(), mentee.getObjectId())) {
                        MentorManager.getInstance().cancelAllMentoringBuffs(player);
                    }
                    player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_MENTORING_RELATIONSHIP_WITH_S1_HAS_BEEN_CANCELED_THE_MENTOR_CANNOT_OBTAIN_ANOTHER_MENTEE_FOR_TWO_DAYS)).addString(this._name));
                    MentorManager.getInstance().setPenalty(player.getObjectId(), Config.MENTOR_PENALTY_FOR_MENTEE_LEAVE);
                    MentorManager.getInstance().deleteMentor(player.getObjectId(), mentee.getObjectId());
                    EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeRemove(player, mentee), player);
                }
            }
            else if (player.isMentee()) {
                final Mentee mentor = MentorManager.getInstance().getMentor(player.getObjectId());
                if (mentor != null && mentor.getObjectId() == objectId) {
                    MentorManager.getInstance().cancelAllMentoringBuffs(player);
                    if (MentorManager.getInstance().isAllMenteesOffline(mentor.getObjectId(), player.getObjectId())) {
                        MentorManager.getInstance().cancelAllMentoringBuffs(mentor.getPlayerInstance());
                    }
                    MentorManager.getInstance().setPenalty(mentor.getObjectId(), Config.MENTOR_PENALTY_FOR_MENTEE_LEAVE);
                    MentorManager.getInstance().deleteMentor(mentor.getObjectId(), player.getObjectId());
                    EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeLeft(mentor, player), player);
                    mentor.getPlayerInstance().sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_MENTORING_RELATIONSHIP_WITH_S1_HAS_BEEN_CANCELED_THE_MENTOR_CANNOT_OBTAIN_ANOTHER_MENTEE_FOR_TWO_DAYS)).addString(this._name));
                }
            }
        }
    }
}
