// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import org.l2j.gameserver.world.World;
import java.util.stream.Stream;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.function.Function;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.model.Mentee;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public class MentorManager
{
    private static final Logger LOGGER;
    private final IntMap<IntMap<Mentee>> _menteeData;
    private final IntMap<Mentee> _mentors;
    
    private MentorManager() {
        this._menteeData = (IntMap<IntMap<Mentee>>)new CHashIntMap();
        this._mentors = (IntMap<Mentee>)new CHashIntMap();
        this.load();
    }
    
    private void load() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement statement = con.createStatement();
                try {
                    final ResultSet rset = statement.executeQuery("SELECT * FROM character_mentees");
                    try {
                        while (rset.next()) {
                            this.addMentor(rset.getInt("mentorId"), rset.getInt("charId"));
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            MentorManager.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    public void deleteMentee(final int mentorId, final int menteeId) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("DELETE FROM character_mentees WHERE mentorId = ? AND charId = ?");
                try {
                    statement.setInt(1, mentorId);
                    statement.setInt(2, menteeId);
                    statement.execute();
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
            MentorManager.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    public void deleteMentor(final int mentorId, final int menteeId) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("DELETE FROM character_mentees WHERE mentorId = ? AND charId = ?");
                try {
                    statement.setInt(1, mentorId);
                    statement.setInt(2, menteeId);
                    statement.execute();
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
            MentorManager.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        finally {
            this.removeMentor(mentorId, menteeId);
        }
    }
    
    public boolean isMentor(final int objectId) {
        return this._menteeData.containsKey(objectId);
    }
    
    public boolean isMentee(final int objectId) {
        return this._menteeData.values().stream().anyMatch(map -> map.containsKey(objectId));
    }
    
    public void cancelAllMentoringBuffs(final Player player) {
        if (player == null) {
            return;
        }
        final Stream<Object> map = player.getEffectList().getEffects().stream().map((Function<? super BuffInfo, ?>)BuffInfo::getSkill);
        Objects.requireNonNull(player);
        map.forEach((Consumer<? super Object>)player::stopSkillEffects);
    }
    
    public void setPenalty(final int mentorId, final long penalty) {
        final Player player = World.getInstance().findPlayer(mentorId);
        player.setMentorPenaltyId(mentorId);
        player.setMentorPenaltyTime(System.currentTimeMillis() + penalty);
    }
    
    public long getMentorPenalty(final int mentorId) {
        final Player player = World.getInstance().findPlayer(mentorId);
        return player.getMentorPenaltyTime();
    }
    
    public void addMentor(final int mentorId, final int menteeId) {
        final IntMap<Mentee> mentees = (IntMap<Mentee>)this._menteeData.computeIfAbsent(mentorId, map -> new CHashIntMap());
        if (mentees.containsKey(menteeId)) {
            ((Mentee)mentees.get(menteeId)).load();
        }
        else {
            mentees.put(menteeId, (Object)new Mentee(menteeId));
        }
    }
    
    public void removeMentor(final int mentorId, final int menteeId) {
        if (this._menteeData.containsKey(mentorId)) {
            ((IntMap)this._menteeData.get(mentorId)).remove(menteeId);
            if (((IntMap)this._menteeData.get(mentorId)).isEmpty()) {
                this._menteeData.remove(mentorId);
                this._mentors.remove(mentorId);
            }
        }
    }
    
    public Mentee getMentor(final int menteeId) {
        for (final IntMap.Entry<IntMap<Mentee>> map : this._menteeData.entrySet()) {
            if (((IntMap)map.getValue()).containsKey(menteeId)) {
                if (!this._mentors.containsKey(map.getKey())) {
                    this._mentors.put(map.getKey(), (Object)new Mentee(map.getKey()));
                }
                return (Mentee)this._mentors.get(map.getKey());
            }
        }
        return null;
    }
    
    public Collection<Mentee> getMentees(final int mentorId) {
        if (this._menteeData.containsKey(mentorId)) {
            return (Collection<Mentee>)((IntMap)this._menteeData.get(mentorId)).values();
        }
        return (Collection<Mentee>)Collections.emptyList();
    }
    
    public Mentee getMentee(final int mentorId, final int menteeId) {
        if (this._menteeData.containsKey(mentorId)) {
            return (Mentee)((IntMap)this._menteeData.get(mentorId)).get(menteeId);
        }
        return null;
    }
    
    public boolean isAllMenteesOffline(final int menteorId, final int menteeId) {
        boolean isAllMenteesOffline = true;
        for (final Mentee men : this.getMentees(menteorId)) {
            if (men.isOnline() && men.getObjectId() != menteeId && isAllMenteesOffline) {
                isAllMenteesOffline = false;
                break;
            }
        }
        return isAllMenteesOffline;
    }
    
    public static MentorManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)MentorManager.class);
    }
    
    private static class Singleton
    {
        private static final MentorManager INSTANCE;
        
        static {
            INSTANCE = new MentorManager();
        }
    }
}
