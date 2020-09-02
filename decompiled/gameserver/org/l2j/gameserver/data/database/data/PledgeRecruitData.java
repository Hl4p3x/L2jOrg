// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.gameserver.model.Clan;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("pledge_recruit")
public class PledgeRecruitData
{
    @Column("clan_id")
    private int clanId;
    private int karma;
    private String information;
    @Column("detailed_information")
    private String detailedInformation;
    @Column("application_type")
    private int applicationType;
    @Column("recruit_type")
    private int recruitType;
    @NonUpdatable
    private Clan clan;
    
    public PledgeRecruitData() {
    }
    
    public PledgeRecruitData(final int clanId, final int karma, final String information, final String detailedInformation, final int applicationType, final int recruitType) {
        this.clanId = clanId;
        this.karma = karma;
        this.information = information;
        this.detailedInformation = detailedInformation;
        this.clan = ClanTable.getInstance().getClan(clanId);
        this.applicationType = applicationType;
        this.recruitType = recruitType;
    }
    
    public int getClanId() {
        return this.clanId;
    }
    
    public void setClanId(final int clanId) {
        this.clanId = clanId;
    }
    
    public String getClanName() {
        return this.clan.getName();
    }
    
    public String getClanLeaderName() {
        return this.clan.getLeaderName();
    }
    
    public int getClanLevel() {
        return this.clan.getLevel();
    }
    
    public int getKarma() {
        return this.karma;
    }
    
    public void setKarma(final int karma) {
        this.karma = karma;
    }
    
    public String getInformation() {
        return this.information;
    }
    
    public void setInformation(final String information) {
        this.information = information;
    }
    
    public String getDetailedInformation() {
        return this.detailedInformation;
    }
    
    public void setDetailedInformation(final String detailedInformation) {
        this.detailedInformation = detailedInformation;
    }
    
    public int getApplicationType() {
        return this.applicationType;
    }
    
    public int getRecruitType() {
        return this.recruitType;
    }
    
    public Clan getClan() {
        return this.clan;
    }
    
    public void setClan(final Clan clan) {
        this.clan = clan;
    }
}
