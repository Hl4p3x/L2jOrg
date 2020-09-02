// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.skill.api;

import org.w3c.dom.Node;
import org.l2j.commons.xml.XmlParser;

public abstract class SkillConditionFactory extends XmlParser
{
    public abstract SkillCondition create(final Node xmlNode);
    
    public abstract String conditionName();
}
