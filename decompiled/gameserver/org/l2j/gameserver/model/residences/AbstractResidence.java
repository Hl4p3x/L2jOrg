// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.residences;

import java.util.function.Function;
import java.util.function.Consumer;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ResidenceDAO;
import org.l2j.gameserver.model.base.SocialClass;
import java.util.Objects;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.model.SkillLearn;
import java.util.List;
import org.l2j.gameserver.world.zone.type.ResidenceZone;
import org.l2j.gameserver.data.database.data.ResidenceFunctionData;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.events.ListenersContainer;

public abstract class AbstractResidence extends ListenersContainer implements INamable
{
    private final IntMap<ResidenceFunctionData> functions;
    private final int id;
    private String name;
    private ResidenceZone zone;
    private List<SkillLearn> residentialSkills;
    
    public AbstractResidence(final int id) {
        this.functions = (IntMap<ResidenceFunctionData>)new CHashIntMap();
        this.zone = null;
        this.id = id;
        this.initResidentialSkills();
    }
    
    protected void initResidentialSkills() {
        this.residentialSkills = SkillTreesData.getInstance().getAvailableResidentialSkills(this.id);
    }
    
    public void giveResidentialSkills(final Player player) {
        if (!Util.isNullOrEmpty((Collection)this.residentialSkills)) {
            final int socialClass = player.getPledgeClass() + 1;
            this.residentialSkills.stream().filter(s -> this.checkSocialClass(socialClass, s)).forEach(s -> player.addSkill(s.getSkill()));
        }
    }
    
    private boolean checkSocialClass(final int socialClass, final SkillLearn skillLearn) {
        final SocialClass skillSocialClass = skillLearn.getSocialClass();
        return Objects.isNull(skillSocialClass) || socialClass >= skillSocialClass.ordinal();
    }
    
    public void removeResidentialSkills(final Player player) {
        if (!Util.isNullOrEmpty((Collection)this.residentialSkills)) {
            this.residentialSkills.forEach(skill -> player.removeSkill(skill.getSkillId(), false));
        }
    }
    
    protected void initFunctions() {
        ((ResidenceDAO)DatabaseAccess.getDAO((Class)ResidenceDAO.class)).findFunctionsByResidence(this.id).forEach(function -> {
            function.initResidence(this);
            if (function.getExpiration() <= System.currentTimeMillis() && !function.reactivate()) {
                this.removeFunction(function);
            }
            else {
                this.functions.put(function.getId(), (Object)function);
            }
        });
    }
    
    public void addFunction(final int id, final int level) {
        this.addFunction(new ResidenceFunctionData(id, level, this));
    }
    
    private void addFunction(final ResidenceFunctionData function) {
        final int functionId = function.getId();
        ((ResidenceDAO)DatabaseAccess.getDAO((Class)ResidenceDAO.class)).saveFunction(functionId, function.getLevel(), function.getExpiration(), this.id);
        final ResidenceFunctionData old = (ResidenceFunctionData)this.functions.remove(functionId);
        if (old != null) {
            this.removeFunction(old);
        }
        this.functions.put(functionId, (Object)function);
    }
    
    public void removeFunction(final ResidenceFunctionData function) {
        ((ResidenceDAO)DatabaseAccess.getDAO((Class)ResidenceDAO.class)).deleteFunction(function.getId(), this.id);
        function.cancelExpiration();
        this.functions.remove(function.getId());
    }
    
    public void removeFunctions() {
        ((ResidenceDAO)DatabaseAccess.getDAO((Class)ResidenceDAO.class)).deleteFunctionsByResidence(this.id);
        this.functions.values().forEach(ResidenceFunctionData::cancelExpiration);
        this.functions.clear();
    }
    
    public boolean hasFunction(final ResidenceFunctionType type) {
        return this.functions.values().stream().map(ResidenceFunctionData::getTemplate).anyMatch(func -> func.getType() == type);
    }
    
    public ResidenceFunctionData getFunction(final ResidenceFunctionType type) {
        return this.functions.values().stream().filter(func -> func.getType() == type).findFirst().orElse(null);
    }
    
    public ResidenceFunctionData getFunction(final int id, final int level) {
        return (ResidenceFunctionData)Util.computeIfNonNull((Object)this.functions.get(id), f -> (f.getLevel() == level) ? f : null);
    }
    
    public ResidenceFunctionData getFunction(final int id) {
        return (ResidenceFunctionData)this.functions.get(id);
    }
    
    public int getFunctionLevel(final ResidenceFunctionType type) {
        return Util.zeroIfNullOrElse((Object)this.getFunction(type), ResidenceFunctionData::getLevel);
    }
    
    public final int getId() {
        return this.id;
    }
    
    @Override
    public final String getName() {
        return this.name;
    }
    
    public final void setName(final String name) {
        this.name = name;
    }
    
    public ResidenceZone getResidenceZone() {
        return this.zone;
    }
    
    protected void setResidenceZone(final ResidenceZone zone) {
        this.zone = zone;
    }
    
    public Collection<ResidenceFunctionData> getFunctions() {
        return (Collection<ResidenceFunctionData>)this.functions.values();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof AbstractResidence && ((AbstractResidence)obj).getId() == this.getId();
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.name, this.id);
    }
    
    protected abstract void load();
    
    protected abstract void initResidenceZone();
    
    public abstract int getOwnerId();
}
