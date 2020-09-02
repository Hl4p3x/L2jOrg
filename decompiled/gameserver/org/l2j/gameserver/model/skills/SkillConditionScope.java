// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import java.util.Map;

public enum SkillConditionScope
{
    GENERAL("conditions"), 
    TARGET("targetConditions"), 
    PASSIVE("passiveConditions");
    
    private static final Map<String, SkillConditionScope> XML_NODE_NAME_TO_SKILL_CONDITION_SCOPE;
    private final String _xmlNodeName;
    
    private SkillConditionScope(final String xmlNodeName) {
        this._xmlNodeName = xmlNodeName;
    }
    
    public static SkillConditionScope findByXmlNodeName(final String xmlNodeName) {
        return SkillConditionScope.XML_NODE_NAME_TO_SKILL_CONDITION_SCOPE.get(xmlNodeName);
    }
    
    public String getXmlNodeName() {
        return this._xmlNodeName;
    }
    
    static {
        XML_NODE_NAME_TO_SKILL_CONDITION_SCOPE = Arrays.stream(values()).collect(Collectors.toMap((Function<? super SkillConditionScope, ? extends String>)SkillConditionScope::getXmlNodeName, (Function<? super SkillConditionScope, ? extends SkillConditionScope>)Function.identity()));
    }
}
