// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.util.Objects;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;

@Table("global_tasks")
public class TaskData
{
    @NonUpdatable
    private int id;
    private String name;
    private String type;
    @Column("last_activation")
    private long lastActivation;
    private String param1;
    private String param2;
    private String param3;
    
    public String getName() {
        return this.name;
    }
    
    public String geType() {
        return this.type;
    }
    
    public void setLastActivation(final long lastActivation) {
        this.lastActivation = lastActivation;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getParam1() {
        return this.param1;
    }
    
    public String getParam2() {
        return this.param2;
    }
    
    public String getparam3() {
        return this.param3;
    }
    
    public long getLastActivation() {
        return this.lastActivation;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TaskData taskData = (TaskData)o;
        return this.id == taskData.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public void setParam1(final String param1) {
        this.param1 = param1;
    }
    
    public void setParam2(final String param2) {
        this.param2 = param2;
    }
    
    public void setParam3(final String param3) {
        this.param3 = param3;
    }
}
