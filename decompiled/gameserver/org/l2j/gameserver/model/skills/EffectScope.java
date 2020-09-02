// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;

public enum EffectScope
{
    GENERAL("effects"), 
    START("startEffects"), 
    SELF("selfEffects"), 
    CHANNELING("channelingEffects"), 
    PVP("pvpEffects"), 
    PVE("pveEffects"), 
    END("endEffects");
    
    private static final Map<String, EffectScope> XML_NODE_NAME_TO_EFFECT_SCOPE;
    private final String _xmlNodeName;
    
    private EffectScope(final String xmlNodeName) {
        this._xmlNodeName = xmlNodeName;
    }
    
    public static EffectScope findByXmlNodeName(final String xmlNodeName) {
        return EffectScope.XML_NODE_NAME_TO_EFFECT_SCOPE.get(xmlNodeName);
    }
    
    public String getXmlNodeName() {
        return this._xmlNodeName;
    }
    
    static {
        XML_NODE_NAME_TO_EFFECT_SCOPE = Arrays.stream(values()).collect(Collectors.toMap((Function<? super EffectScope, ? extends String>)EffectScope::getXmlNodeName, e -> e));
    }
}
