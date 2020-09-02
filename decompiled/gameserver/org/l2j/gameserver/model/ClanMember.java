// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.Config;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public class ClanMember
{
    private static final Logger LOGGER;
    private final Clan clan;
    private int _objectId;
    private String _name;
    private String _title;
    private int _powerGrade;
    private int _level;
    private int _classId;
    private boolean _sex;
    private int _raceOrdinal;
    private Player _player;
    private int _pledgeType;
    private int _apprentice;
    private int _sponsor;
    private long _onlineTime;
    
    public ClanMember(final Clan clan, final PlayerData memberData) {
        this.clan = clan;
        this._name = memberData.getName();
        this._level = memberData.getLevel();
        this._classId = memberData.getClassId();
        this._objectId = memberData.getCharId();
        this._pledgeType = memberData.getSubPledge();
        this._title = memberData.getTitle();
        this._powerGrade = memberData.getPowerGrade();
        this._apprentice = memberData.getApprentice();
        this._sponsor = memberData.getSponsor();
        this._sex = memberData.isFemale();
        this._raceOrdinal = memberData.getRace();
    }
    
    public ClanMember(final Clan clan, final Player player) {
        if (clan == null) {
            throw new IllegalArgumentException("Cannot create a Clan Member if player has a null clan.");
        }
        this._player = player;
        this.clan = clan;
        this._name = player.getName();
        this._level = player.getLevel();
        this._classId = player.getClassId().getId();
        this._objectId = player.getObjectId();
        this._pledgeType = player.getPledgeType();
        this._powerGrade = player.getPowerGrade();
        this._title = player.getTitle();
        this._sponsor = 0;
        this._apprentice = 0;
        this._sex = player.getAppearance().isFemale();
        this._raceOrdinal = player.getRace().ordinal();
    }
    
    public static int calculatePledgeClass(final Player player) {
        int pledgeClass = 0;
        if (player == null) {
            return pledgeClass;
        }
        final Clan clan = player.getClan();
        if (clan != null) {
            switch (clan.getLevel()) {
                case 4: {
                    if (player.isClanLeader()) {
                        pledgeClass = 3;
                        break;
                    }
                    break;
                }
                case 5: {
                    if (player.isClanLeader()) {
                        pledgeClass = 4;
                        break;
                    }
                    pledgeClass = 2;
                    break;
                }
                case 6: {
                    Label_0215: {
                        switch (player.getPledgeType()) {
                            case -1: {
                                pledgeClass = 1;
                                break;
                            }
                            case 100:
                            case 200: {
                                pledgeClass = 2;
                                break;
                            }
                            case 0: {
                                if (player.isClanLeader()) {
                                    pledgeClass = 5;
                                    break;
                                }
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 4;
                                        break Label_0215;
                                    }
                                    default: {
                                        pledgeClass = 3;
                                        break Label_0215;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 7: {
                    Label_0413: {
                        switch (player.getPledgeType()) {
                            case -1: {
                                pledgeClass = 1;
                                break;
                            }
                            case 100:
                            case 200: {
                                pledgeClass = 3;
                                break;
                            }
                            case 1001:
                            case 1002:
                            case 2001:
                            case 2002: {
                                pledgeClass = 2;
                                break;
                            }
                            case 0: {
                                if (player.isClanLeader()) {
                                    pledgeClass = 7;
                                    break;
                                }
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 6;
                                        break Label_0413;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 5;
                                        break Label_0413;
                                    }
                                    default: {
                                        pledgeClass = 4;
                                        break Label_0413;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 8: {
                    Label_0614: {
                        switch (player.getPledgeType()) {
                            case -1: {
                                pledgeClass = 1;
                                break;
                            }
                            case 100:
                            case 200: {
                                pledgeClass = 4;
                                break;
                            }
                            case 1001:
                            case 1002:
                            case 2001:
                            case 2002: {
                                pledgeClass = 3;
                                break;
                            }
                            case 0: {
                                if (player.isClanLeader()) {
                                    pledgeClass = 8;
                                    break;
                                }
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 7;
                                        break Label_0614;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 6;
                                        break Label_0614;
                                    }
                                    default: {
                                        pledgeClass = 5;
                                        break Label_0614;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 9: {
                    Label_0815: {
                        switch (player.getPledgeType()) {
                            case -1: {
                                pledgeClass = 1;
                                break;
                            }
                            case 100:
                            case 200: {
                                pledgeClass = 5;
                                break;
                            }
                            case 1001:
                            case 1002:
                            case 2001:
                            case 2002: {
                                pledgeClass = 4;
                                break;
                            }
                            case 0: {
                                if (player.isClanLeader()) {
                                    pledgeClass = 9;
                                    break;
                                }
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 8;
                                        break Label_0815;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 7;
                                        break Label_0815;
                                    }
                                    default: {
                                        pledgeClass = 6;
                                        break Label_0815;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 10: {
                    Label_1015: {
                        switch (player.getPledgeType()) {
                            case -1: {
                                pledgeClass = 1;
                                break;
                            }
                            case 100:
                            case 200: {
                                pledgeClass = 6;
                                break;
                            }
                            case 1001:
                            case 1002:
                            case 2001:
                            case 2002: {
                                pledgeClass = 5;
                                break;
                            }
                            case 0: {
                                if (player.isClanLeader()) {
                                    pledgeClass = 10;
                                    break;
                                }
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 9;
                                        break Label_1015;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 8;
                                        break Label_1015;
                                    }
                                    default: {
                                        pledgeClass = 7;
                                        break Label_1015;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 11: {
                    Label_1215: {
                        switch (player.getPledgeType()) {
                            case -1: {
                                pledgeClass = 1;
                                break;
                            }
                            case 100:
                            case 200: {
                                pledgeClass = 7;
                                break;
                            }
                            case 1001:
                            case 1002:
                            case 2001:
                            case 2002: {
                                pledgeClass = 6;
                                break;
                            }
                            case 0: {
                                if (player.isClanLeader()) {
                                    pledgeClass = 11;
                                    break;
                                }
                                switch (clan.getLeaderSubPledge(player.getObjectId())) {
                                    case 100:
                                    case 200: {
                                        pledgeClass = 10;
                                        break Label_1215;
                                    }
                                    case 1001:
                                    case 1002:
                                    case 2001:
                                    case 2002: {
                                        pledgeClass = 9;
                                        break Label_1215;
                                    }
                                    default: {
                                        pledgeClass = 8;
                                        break Label_1215;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                default: {
                    pledgeClass = 1;
                    break;
                }
            }
        }
        if (player.isNoble() && pledgeClass < 5) {
            pledgeClass = 5;
        }
        if (player.isHero() && pledgeClass < 8) {
            pledgeClass = 8;
        }
        return pledgeClass;
    }
    
    public Player getPlayerInstance() {
        return this._player;
    }
    
    public void setPlayerInstance(final Player player) {
        if (player == null && this._player != null) {
            this._name = this._player.getName();
            this._level = this._player.getLevel();
            this._classId = this._player.getClassId().getId();
            this._objectId = this._player.getObjectId();
            this._powerGrade = this._player.getPowerGrade();
            this._pledgeType = this._player.getPledgeType();
            this._title = this._player.getTitle();
            this._apprentice = this._player.getApprentice();
            this._sponsor = this._player.getSponsor();
            this._sex = this._player.getAppearance().isFemale();
            this._raceOrdinal = this._player.getRace().ordinal();
        }
        if (player != null) {
            this.clan.addSkillEffects(player);
            if (this.clan.getLevel() > 3 && player.isClanLeader()) {
                SiegeManager.getInstance().addSiegeSkills(player);
            }
            if (player.isClanLeader()) {
                this.clan.setLeader(this);
            }
        }
        this._player = player;
    }
    
    public boolean isOnline() {
        return this._player != null && this._player.isOnline() && this._player.getClient() != null && !this._player.getClient().isDetached();
    }
    
    public int getClassId() {
        return (this._player != null) ? this._player.getClassId().getId() : this._classId;
    }
    
    public int getLevel() {
        return (this._player != null) ? this._player.getLevel() : this._level;
    }
    
    public String getName() {
        return (this._player != null) ? this._player.getName() : this._name;
    }
    
    public int getObjectId() {
        return (this._player != null) ? this._player.getObjectId() : this._objectId;
    }
    
    public String getTitle() {
        return (this._player != null) ? this._player.getTitle() : this._title;
    }
    
    public int getPledgeType() {
        return (this._player != null) ? this._player.getPledgeType() : this._pledgeType;
    }
    
    public void setPledgeType(final int pledgeType) {
        this._pledgeType = pledgeType;
        if (this._player != null) {
            this._player.setPledgeType(pledgeType);
        }
        else {
            this.updatePledgeType();
        }
    }
    
    public void updatePledgeType() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("UPDATE characters SET subpledge=? WHERE charId=?");
                try {
                    ps.setLong(1, this._pledgeType);
                    ps.setInt(2, this.getObjectId());
                    ps.execute();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
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
            ClanMember.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public int getPowerGrade() {
        return (this._player != null) ? this._player.getPowerGrade() : this._powerGrade;
    }
    
    public void setPowerGrade(final int powerGrade) {
        this._powerGrade = powerGrade;
        if (this._player != null) {
            this._player.setPowerGrade(powerGrade);
        }
        else {
            this.updatePowerGrade();
        }
    }
    
    public void updatePowerGrade() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("UPDATE characters SET power_grade=? WHERE charId=?");
                try {
                    ps.setLong(1, this._powerGrade);
                    ps.setInt(2, this.getObjectId());
                    ps.execute();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
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
            ClanMember.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public void setApprenticeAndSponsor(final int apprenticeID, final int sponsorID) {
        this._apprentice = apprenticeID;
        this._sponsor = sponsorID;
    }
    
    public int getRaceOrdinal() {
        return (this._player != null) ? this._player.getRace().ordinal() : this._raceOrdinal;
    }
    
    public boolean getSex() {
        return (this._player != null) ? this._player.getAppearance().isFemale() : this._sex;
    }
    
    public int getSponsor() {
        return (this._player != null) ? this._player.getSponsor() : this._sponsor;
    }
    
    public int getApprentice() {
        return (this._player != null) ? this._player.getApprentice() : this._apprentice;
    }
    
    public String getApprenticeOrSponsorName() {
        if (this._player != null) {
            this._apprentice = this._player.getApprentice();
            this._sponsor = this._player.getSponsor();
        }
        if (this._apprentice != 0) {
            final ClanMember apprentice = this.clan.getClanMember(this._apprentice);
            if (apprentice != null) {
                return apprentice.getName();
            }
            return "Error";
        }
        else {
            if (this._sponsor == 0) {
                return "";
            }
            final ClanMember sponsor = this.clan.getClanMember(this._sponsor);
            if (sponsor != null) {
                return sponsor.getName();
            }
            return "Error";
        }
    }
    
    public Clan getClan() {
        return this.clan;
    }
    
    public void saveApprenticeAndSponsor(final int apprentice, final int sponsor) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("UPDATE characters SET apprentice=?,sponsor=? WHERE charId=?");
                try {
                    ps.setInt(1, apprentice);
                    ps.setInt(2, sponsor);
                    ps.setInt(3, this.getObjectId());
                    ps.execute();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
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
        catch (SQLException e) {
            ClanMember.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public long getOnlineTime() {
        return this._onlineTime;
    }
    
    public void setOnlineTime(final long onlineTime) {
        this._onlineTime = onlineTime;
    }
    
    public void resetBonus() {
        this._onlineTime = 0L;
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).resetClaimedClanReward();
    }
    
    public int getOnlineStatus() {
        return this.isOnline() ? ((this._onlineTime >= Config.ALT_CLAN_MEMBERS_TIME_FOR_BONUS) ? 2 : 1) : 0;
    }
    
    public boolean isRewardClaimed(final ClanRewardType type) {
        final int claimedRewards = this._player.getClaimedClanRewards(ClanRewardType.getDefaultMask());
        return (claimedRewards & type.getMask()) == type.getMask();
    }
    
    public void setRewardClaimed(final ClanRewardType type) {
        int claimedRewards = this._player.getClaimedClanRewards(ClanRewardType.getDefaultMask());
        claimedRewards |= type.getMask();
        this._player.setClaimedClanRewards(claimedRewards);
        this._player.storeVariables();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanMember.class);
    }
}
