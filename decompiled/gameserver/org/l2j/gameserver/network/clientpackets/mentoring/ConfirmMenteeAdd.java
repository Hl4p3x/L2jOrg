// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.network.serverpackets.mentoring.ExMentorList;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeAdd;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ConfirmMenteeAdd extends ClientPacket
{
    private static final Logger LOGGER;
    private int _confirmed;
    private String _mentor;
    
    public static boolean validate(final Player mentor, final Player mentee) {
        if (mentor == null || mentee == null) {
            return false;
        }
        if (!mentee.isOnline()) {
            mentor.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
            return false;
        }
        if (MentorManager.getInstance().getMentorPenalty(mentor.getObjectId()) > System.currentTimeMillis()) {
            long remainingTime = (MentorManager.getInstance().getMentorPenalty(mentor.getObjectId()) - System.currentTimeMillis()) / 1000L;
            final int days = (int)(remainingTime / 86400L);
            remainingTime %= 86400L;
            final int hours = (int)(remainingTime / 3600L);
            remainingTime %= 3600L;
            final int minutes = (int)(remainingTime / 60L);
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_BOND_WITH_A_NEW_MENTEE_IN_S1_DAY_S_S2_HOUR_S_S3_MINUTE_S);
            msg.addInt(days);
            msg.addInt(hours);
            msg.addInt(minutes);
            mentor.sendPacket(msg);
            return false;
        }
        if (mentor.getObjectId() == mentee.getObjectId()) {
            mentor.sendPacket(SystemMessageId.YOU_CANNOT_BECOME_YOUR_OWN_MENTEE);
            return false;
        }
        if (mentee.getLevel() >= 86) {
            mentor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ABOVE_LEVEL_85_AND_CANNOT_BECOME_A_MENTEE)).addString(mentee.getName()));
            return false;
        }
        if (mentee.isSubClassActive()) {
            mentor.sendPacket(SystemMessageId.INVITATION_CAN_OCCUR_ONLY_WHEN_THE_MENTEE_IS_IN_MAIN_CLASS_STATUS);
            return false;
        }
        if (MentorManager.getInstance().getMentees(mentor.getObjectId()) != null && MentorManager.getInstance().getMentees(mentor.getObjectId()).size() >= 3) {
            mentor.sendPacket(SystemMessageId.A_MENTOR_CAN_HAVE_UP_TO_3_MENTEES_AT_THE_SAME_TIME);
            return false;
        }
        if (mentee.isMentee()) {
            mentor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_HAS_A_MENTOR)).addString(mentee.getName()));
            return false;
        }
        return true;
    }
    
    public void readImpl() {
        this._confirmed = this.readInt();
        this._mentor = this.readString();
    }
    
    public void runImpl() {
        final Player mentee = ((GameClient)this.client).getPlayer();
        if (mentee == null) {
            return;
        }
        final Player mentor = World.getInstance().findPlayer(this._mentor);
        if (mentor == null) {
            return;
        }
        if (this._confirmed == 0) {
            mentee.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_DECLINED_S1_S_MENTORING_OFFER)).addString(mentor.getName()));
            mentor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_DECLINED_BECOMING_YOUR_MENTEE)).addString(mentee.getName()));
        }
        else if (validate(mentor, mentee)) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("INSERT INTO character_mentees (charId, mentorId) VALUES (?, ?)");
                    try {
                        statement.setInt(1, mentee.getObjectId());
                        statement.setInt(2, mentor.getObjectId());
                        statement.execute();
                        MentorManager.getInstance().addMentor(mentor.getObjectId(), mentee.getObjectId());
                        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeAdd(mentor, mentee), mentor);
                        mentor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.FROM_NOW_ON_S1_WILL_BE_YOUR_MENTEE)).addString(mentee.getName()));
                        mentor.sendPacket(new ExMentorList(mentor));
                        mentee.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.FROM_NOW_ON_S1_WILL_BE_YOUR_MENTOR)).addString(mentor.getName()));
                        mentee.sendPacket(new ExMentorList(mentee));
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                catch (Throwable t2) {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
            }
            catch (Exception e) {
                ConfirmMenteeAdd.LOGGER.warn(e.getLocalizedMessage(), (Throwable)e);
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ConfirmMenteeAdd.class);
    }
}
