// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import java.util.Iterator;
import java.util.Optional;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class BypassBuilder
{
    private final String _bypass;
    private final List<BypassParam> _params;
    
    public BypassBuilder(final String bypass) {
        this._params = new ArrayList<BypassParam>();
        this._bypass = bypass;
    }
    
    public void addParam(final BypassParam param) {
        Objects.requireNonNull(param, "param cannot be null!");
        this._params.add(param);
    }
    
    public void addParam(final String name, final String separator, final Object value) {
        Objects.requireNonNull(name, "name cannot be null!");
        this.addParam(new BypassParam(name, Optional.ofNullable(separator), Optional.ofNullable(value)));
    }
    
    public void addParam(final String name, final Object value) {
        this.addParam(name, "=", value);
    }
    
    public void addParam(final String name) {
        this.addParam(name, null, null);
    }
    
    public StringBuilder toStringBuilder() {
        final StringBuilder sb = new StringBuilder(this._bypass);
        for (final BypassParam param : this._params) {
            sb.append(" ").append(param.getName().trim());
            if (param.getSeparator().isPresent() && param.getValue().isPresent()) {
                sb.append(param.getSeparator().get().trim());
                final Object value = param.getValue().get();
                if (value instanceof String) {
                    sb.append('\"');
                }
                sb.append(String.valueOf(value).trim());
                if (!(value instanceof String)) {
                    continue;
                }
                sb.append('\"');
            }
        }
        return sb;
    }
    
    @Override
    public String toString() {
        return this.toStringBuilder().toString();
    }
    
    private static class BypassParam
    {
        private final String _name;
        private final Optional<String> _separator;
        private final Optional<Object> _value;
        
        public BypassParam(final String name, final Optional<String> separator, final Optional<Object> value) {
            this._name = name;
            this._separator = separator;
            this._value = value;
        }
        
        public String getName() {
            return this._name;
        }
        
        public Optional<String> getSeparator() {
            return this._separator;
        }
        
        public Optional<Object> getValue() {
            return this._value;
        }
    }
}
