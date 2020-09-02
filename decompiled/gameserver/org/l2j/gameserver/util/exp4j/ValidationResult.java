// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

import java.util.List;

public class ValidationResult
{
    public static final ValidationResult SUCCESS;
    private final boolean valid;
    private final List<String> errors;
    
    public ValidationResult(final boolean valid, final List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }
    
    public boolean isValid() {
        return this.valid;
    }
    
    public List<String> getErrors() {
        return this.errors;
    }
    
    static {
        SUCCESS = new ValidationResult(true, null);
    }
}
