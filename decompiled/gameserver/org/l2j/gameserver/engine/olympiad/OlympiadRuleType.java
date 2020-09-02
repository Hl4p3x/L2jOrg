// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.olympiad;

public enum OlympiadRuleType
{
    TEAM, 
    CLASSLESS, 
    CLASS, 
    MAX;
    
    public int participantCount() {
        return (this == OlympiadRuleType.TEAM) ? 6 : 2;
    }
    
    public static OlympiadRuleType of(final byte ruleType) {
        OlympiadRuleType olympiadRuleType = null;
        switch (ruleType) {
            case 0: {
                olympiadRuleType = OlympiadRuleType.TEAM;
                break;
            }
            case 1: {
                olympiadRuleType = OlympiadRuleType.CLASSLESS;
                break;
            }
            case 2: {
                olympiadRuleType = OlympiadRuleType.CLASS;
                break;
            }
            default: {
                olympiadRuleType = OlympiadRuleType.MAX;
                break;
            }
        }
        return olympiadRuleType;
    }
}
