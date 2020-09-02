// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

import java.util.EmptyStackException;

class ArrayStack
{
    private double[] data;
    private int idx;
    
    ArrayStack() {
        this(5);
    }
    
    ArrayStack(final int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Stack's capacity must be positive");
        }
        this.data = new double[initialCapacity];
        this.idx = -1;
    }
    
    void push(final double value) {
        if (this.idx + 1 == this.data.length) {
            final double[] temp = new double[(int)(this.data.length * 1.2) + 1];
            System.arraycopy(this.data, 0, temp, 0, this.data.length);
            this.data = temp;
        }
        this.data[++this.idx] = value;
    }
    
    double peek() {
        if (this.idx == -1) {
            throw new EmptyStackException();
        }
        return this.data[this.idx];
    }
    
    double pop() {
        if (this.idx == -1) {
            throw new EmptyStackException();
        }
        return this.data[this.idx--];
    }
    
    boolean isEmpty() {
        return this.idx == -1;
    }
    
    int size() {
        return this.idx + 1;
    }
}
