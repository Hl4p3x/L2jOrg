// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.util.Collection;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.gameserver.engine.skill.api.Skill;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.annotation.Table;

@Table("clan_subpledges")
public class SubPledgeData
{
    @NonUpdatable
    private final IntMap<Skill> skills;
    @Column("clan_id")
    private int clanId;
    @Column("sub_pledge_id")
    private int id;
    private String name;
    @Column("leader_id")
    private int leaderId;
    
    public SubPledgeData() {
        this.skills = (IntMap<Skill>)new CHashIntMap();
    }
    
    public int getClanId() {
        return this.clanId;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getLeaderId() {
        return this.leaderId;
    }
    
    public void setClanId(final int clanId) {
        this.clanId = clanId;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setLeaderId(final int leaderId) {
        this.leaderId = leaderId;
    }
    
    public Skill addNewSkill(final Skill skill) {
        return (Skill)this.skills.put(skill.getId(), (Object)skill);
    }
    
    public Skill getSkill(final int skillId) {
        return (Skill)this.skills.get(skillId);
    }
    
    public Collection<Skill> getSkills() {
        return (Collection<Skill>)this.skills.values();
    }
}
