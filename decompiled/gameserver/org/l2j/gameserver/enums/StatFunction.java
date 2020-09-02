// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum StatFunction
{
    ADD("Add", 30), 
    DIV("Div", 20), 
    ENCHANT("Enchant", 0), 
    ENCHANTHP("EnchantHp", 40), 
    MUL("Mul", 20), 
    SET("Set", 0), 
    SUB("Sub", 30);
    
    String name;
    int order;
    
    private StatFunction(final String name, final int order) {
        this.name = name;
        this.order = order;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getOrder() {
        return this.order;
    }
}
