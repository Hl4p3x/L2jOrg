// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.util.StreamUtil;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Stream;
import java.util.function.Predicate;
import java.util.Collections;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.commons.util.TimeUtil;
import java.time.Duration;
import org.l2j.commons.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.IParserAdvUtils;

public class StatsSet implements IParserAdvUtils
{
    public static final StatsSet EMPTY_STATSET;
    private static final Logger LOGGER;
    private final Map<String, Object> _set;
    
    public StatsSet() {
        this((Supplier<Map<String, Object>>)ConcurrentHashMap::new);
    }
    
    public StatsSet(final Supplier<Map<String, Object>> mapFactory) {
        this(mapFactory.get());
    }
    
    public StatsSet(final Map<String, Object> map) {
        this._set = map;
    }
    
    public StatsSet(final StatsSet other) {
        this();
        this.merge(other);
    }
    
    public static StatsSet valueOf(final String key, final Object value) {
        final StatsSet set = new StatsSet();
        set.set(key, value);
        return set;
    }
    
    public final Map<String, Object> getSet() {
        return this._set;
    }
    
    public void merge(final StatsSet newSet) {
        this._set.putAll(newSet.getSet());
    }
    
    public void merge(final Map<String, Object> map) {
        this._set.putAll(map);
    }
    
    public boolean isEmpty() {
        return this._set.isEmpty();
    }
    
    @Override
    public boolean getBoolean(final String key) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Boolean value required, but not specified");
        }
        if (val instanceof Boolean) {
            return (boolean)val;
        }
        try {
            return Boolean.parseBoolean((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Boolean) {
            return (boolean)val;
        }
        try {
            return Boolean.parseBoolean((String)val);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    @Override
    public byte getByte(final String key) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Byte value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number)val).byteValue();
        }
        try {
            return Byte.parseByte((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    @Override
    public byte getByte(final String key, final byte defaultValue) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number)val).byteValue();
        }
        try {
            return Byte.parseByte((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    public short increaseByte(final String key, final byte increaseWith) {
        final byte newValue = (byte)(this.getByte(key) + increaseWith);
        this.set(key, newValue);
        return newValue;
    }
    
    public short increaseByte(final String key, final byte defaultValue, final byte increaseWith) {
        final byte newValue = (byte)(this.getByte(key, defaultValue) + increaseWith);
        this.set(key, newValue);
        return newValue;
    }
    
    public byte[] getByteArray(final String key, final String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Byte value required, but not specified");
        }
        if (val instanceof Number) {
            return new byte[] { ((Number)val).byteValue() };
        }
        int c = 0;
        final String[] vals = ((String)val).split(splitOn);
        final byte[] result = new byte[vals.length];
        for (final String v : vals) {
            try {
                result[c++] = Byte.parseByte(v);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
            }
        }
        return result;
    }
    
    public List<Byte> getByteList(final String key, final String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        final List<Byte> result = new ArrayList<Byte>();
        for (final Byte i : this.getByteArray(key, splitOn)) {
            result.add(i);
        }
        return result;
    }
    
    @Override
    public short getShort(final String key) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Short value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number)val).shortValue();
        }
        if (val instanceof String) {
            String stringVal = (String)val;
            if (stringVal.contains(".")) {
                stringVal = stringVal.substring(0, stringVal.indexOf("."));
            }
            if (Util.isInteger(stringVal)) {
                return Short.parseShort(stringVal);
            }
        }
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
    }
    
    @Override
    public short getShort(final String key, final short defaultValue) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number)val).shortValue();
        }
        try {
            return Short.parseShort((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    public short increaseShort(final String key, final short increaseWith) {
        final short newValue = (short)(this.getShort(key) + increaseWith);
        this.set(key, newValue);
        return newValue;
    }
    
    public short increaseShort(final String key, final short defaultValue, final short increaseWith) {
        final short newValue = (short)(this.getShort(key, defaultValue) + increaseWith);
        this.set(key, newValue);
        return newValue;
    }
    
    @Override
    public int getInt(final String key) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key));
        }
        return this.parseInt(val);
    }
    
    private int parseInt(final Object val) {
        if (val instanceof Number) {
            return ((Number)val).intValue();
        }
        if (val instanceof String) {
            String stringVal = (String)val;
            if (stringVal.contains(".")) {
                stringVal = stringVal.substring(0, stringVal.indexOf("."));
            }
            if (Util.isInteger(stringVal)) {
                return Integer.parseInt(stringVal);
            }
        }
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
    }
    
    @Override
    public int getInt(final String key, final int defaultValue) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        return this.parseInt(val);
    }
    
    public int increaseInt(final String key, final int increaseWith) {
        final int newValue = this.getInt(key) + increaseWith;
        this.set(key, newValue);
        return newValue;
    }
    
    public int increaseInt(final String key, final int defaultValue, final int increaseWith) {
        final int newValue = this.getInt(key, defaultValue) + increaseWith;
        this.set(key, newValue);
        return newValue;
    }
    
    public int[] getIntArray(final String key, final String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Integer value required, but not specified");
        }
        if (val instanceof Number) {
            return new int[] { ((Number)val).intValue() };
        }
        int c = 0;
        final String[] vals = ((String)val).split(splitOn);
        final int[] result = new int[vals.length];
        for (final String v : vals) {
            try {
                result[c++] = Integer.parseInt(v);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
            }
        }
        return result;
    }
    
    public List<Integer> getIntegerList(final String key, final String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        final List<Integer> result = new ArrayList<Integer>();
        for (final int i : this.getIntArray(key, splitOn)) {
            result.add(i);
        }
        return result;
    }
    
    @Override
    public long getLong(final String key) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Long value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number)val).longValue();
        }
        try {
            return Long.parseLong((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    @Override
    public long getLong(final String key, final long defaultValue) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number)val).longValue();
        }
        try {
            return Long.parseLong((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    public long increaseLong(final String key, final long increaseWith) {
        final long newValue = this.getLong(key) + increaseWith;
        this.set(key, newValue);
        return newValue;
    }
    
    public long increaseLong(final String key, final long defaultValue, final long increaseWith) {
        final long newValue = this.getLong(key, defaultValue) + increaseWith;
        this.set(key, newValue);
        return newValue;
    }
    
    @Override
    public float getFloat(final String key) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Float value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number)val).floatValue();
        }
        try {
            return Float.parseFloat((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    @Override
    public float getFloat(final String key, final float defaultValue) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number)val).floatValue();
        }
        try {
            return Float.parseFloat((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    public float increaseFloat(final String key, final float increaseWith) {
        final float newValue = this.getFloat(key) + increaseWith;
        this.set(key, newValue);
        return newValue;
    }
    
    public float increaseFloat(final String key, final float defaultValue, final float increaseWith) {
        final float newValue = this.getFloat(key, defaultValue) + increaseWith;
        this.set(key, newValue);
        return newValue;
    }
    
    @Override
    public double getDouble(final String key) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Double value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number)val).doubleValue();
        }
        try {
            return Double.parseDouble((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    @Override
    public double getDouble(final String key, final double defaultValue) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number)val).doubleValue();
        }
        try {
            return Double.parseDouble((String)val);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, val));
        }
    }
    
    public double increaseDouble(final String key, final double increaseWith) {
        final double newValue = this.getDouble(key) + increaseWith;
        this.set(key, newValue);
        return newValue;
    }
    
    public double increaseDouble(final String key, final double defaultValue, final double increaseWith) {
        final double newValue = this.getDouble(key, defaultValue) + increaseWith;
        this.set(key, newValue);
        return newValue;
    }
    
    @Override
    public String getString(final String key) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("String value required, but not specified");
        }
        return String.valueOf(val);
    }
    
    @Override
    public String getString(final String key, final String defaultValue) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        return String.valueOf(val);
    }
    
    @Override
    public Duration getDuration(final String key) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("String value required, but not specified");
        }
        return TimeUtil.parseDuration(String.valueOf(val));
    }
    
    @Override
    public Duration getDuration(final String key, final Duration defaultValue) {
        Objects.requireNonNull(key);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        return TimeUtil.parseDuration(String.valueOf(val));
    }
    
    @Override
    public <T extends Enum<T>> T getEnum(final String key, final Class<T> enumClass) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(enumClass);
        final Object val = this._set.get(key);
        if (val == null) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, enumClass.getName()));
        }
        if (enumClass.isInstance(val)) {
            return (T)val;
        }
        try {
            return Enum.valueOf(enumClass, String.valueOf(val));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, enumClass.getName(), val));
        }
    }
    
    @Override
    public <T extends Enum<T>> T getEnum(final String key, final Class<T> enumClass, final T defaultValue) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(enumClass);
        final Object val = this._set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (enumClass.isInstance(val)) {
            return (T)val;
        }
        try {
            return Enum.valueOf(enumClass, String.valueOf(val));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, enumClass.getName(), val));
        }
    }
    
    public final <A> A getObject(final String name, final Class<A> type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        final Object obj = this._set.get(name);
        if (obj == null || !type.isAssignableFrom(obj.getClass())) {
            return null;
        }
        return (A)obj;
    }
    
    public final <A> A getObject(final String name, final Class<A> type, final A defaultValue) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        final Object obj = this._set.get(name);
        if (obj == null || !type.isAssignableFrom(obj.getClass())) {
            return defaultValue;
        }
        return (A)obj;
    }
    
    public SkillHolder getSkillHolder(final String key) {
        Objects.requireNonNull(key);
        final Object obj = this._set.get(key);
        if (!(obj instanceof SkillHolder)) {
            return null;
        }
        return (SkillHolder)obj;
    }
    
    public Location getLocation(final String key) {
        Objects.requireNonNull(key);
        final Object obj = this._set.get(key);
        if (!(obj instanceof Location)) {
            return null;
        }
        return (Location)obj;
    }
    
    public List<MinionHolder> getMinionList(final String key) {
        Objects.requireNonNull(key);
        final Object obj = this._set.get(key);
        if (!(obj instanceof List)) {
            return Collections.emptyList();
        }
        return (List<MinionHolder>)obj;
    }
    
    public <T> List<T> getList(final String key, final Class<T> clazz) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(clazz);
        final Object obj = this._set.get(key);
        if (!(obj instanceof List)) {
            return null;
        }
        final List<Object> originalList = (List<Object>)obj;
        if (!originalList.isEmpty()) {
            final Stream<Object> stream = originalList.stream();
            Objects.requireNonNull(clazz);
            if (!stream.allMatch(clazz::isInstance)) {
                if (clazz.getSuperclass() == Enum.class) {
                    throw new IllegalAccessError("Please use getEnumList if you want to get list of Enums!");
                }
                final List<T> convertedList = this.convertList(originalList, clazz);
                if (convertedList == null) {
                    StatsSet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/reflect/Type;)Ljava/lang/String;, key, clazz.getSimpleName(), obj.getClass().getGenericInterfaces()[0]), (Throwable)new ClassNotFoundException());
                    return null;
                }
                this._set.put(key, convertedList);
                return convertedList;
            }
        }
        return (List<T>)obj;
    }
    
    public <T> List<T> getList(final String key, final Class<T> clazz, final List<T> defaultValue) {
        final List<T> list = this.getList(key, clazz);
        return (list == null) ? defaultValue : list;
    }
    
    public <T extends Enum<T>> EnumSet<T> getStringAsEnumSet(final String key, final Class<T> enumClass) {
        return this.getStringAsEnumSet(key, enumClass, " ");
    }
    
    public <T extends Enum<T>> EnumSet<T> getStringAsEnumSet(final String key, final Class<T> enumClass, final String delimiter) {
        final String values = this.getString(key, "");
        if (Util.isNotEmpty(values)) {
            try {
                return (EnumSet<T>)StreamUtil.collectToEnumSet((Class)enumClass, (Stream)Arrays.stream(values.split(delimiter)).map(e -> Enum.valueOf(enumClass, e)));
            }
            catch (Exception e2) {
                StatsSet.LOGGER.error(e2.getMessage(), (Throwable)e2);
            }
        }
        return EnumSet.noneOf(enumClass);
    }
    
    public <T extends Enum<T>> List<T> getEnumList(final String key, final Class<T> clazz) {
        final Object obj = this._set.get(key);
        if (!(obj instanceof List)) {
            return null;
        }
        final List<Object> originalList = (List<Object>)obj;
        if (!originalList.isEmpty() && obj.getClass().getGenericInterfaces()[0] != clazz && originalList.stream().allMatch(name -> GameUtils.isEnum(name.toString(), clazz))) {
            final Stream<Object> map = originalList.stream().map((Function<? super Object, ?>)Object::toString).map(name -> Enum.valueOf(clazz, name));
            Objects.requireNonNull(clazz);
            final List<T> convertedList = map.map((Function<? super Object, ?>)clazz::cast).collect((Collector<? super Object, ?, List<T>>)Collectors.toList());
            this._set.put(key, convertedList);
            return convertedList;
        }
        return (List<T>)obj;
    }
    
    private <T> List<T> convertList(final List<Object> originalList, final Class<T> clazz) {
        if (clazz == Integer.class) {
            if (originalList.stream().map((Function<? super Object, ?>)Object::toString).allMatch((Predicate<? super Object>)Util::isInteger)) {
                final Stream<Object> map = originalList.stream().map((Function<? super Object, ?>)Object::toString).map((Function<? super Object, ?>)Integer::valueOf);
                Objects.requireNonNull(clazz);
                return map.map((Function<? super Object, ?>)clazz::cast).collect((Collector<? super Object, ?, List<T>>)Collectors.toList());
            }
        }
        else if (clazz == Float.class) {
            if (originalList.stream().map((Function<? super Object, ?>)Object::toString).allMatch((Predicate<? super Object>)Util::isFloat)) {
                final Stream<Object> map2 = originalList.stream().map((Function<? super Object, ?>)Object::toString).map((Function<? super Object, ?>)Float::valueOf);
                Objects.requireNonNull(clazz);
                return map2.map((Function<? super Object, ?>)clazz::cast).collect((Collector<? super Object, ?, List<T>>)Collectors.toList());
            }
        }
        else if (clazz == Double.class && originalList.stream().map((Function<? super Object, ?>)Object::toString).allMatch((Predicate<? super Object>)Util::isFloat)) {
            final Stream<Object> map3 = originalList.stream().map((Function<? super Object, ?>)Object::toString).map((Function<? super Object, ?>)Double::valueOf);
            Objects.requireNonNull(clazz);
            return map3.map((Function<? super Object, ?>)clazz::cast).collect((Collector<? super Object, ?, List<T>>)Collectors.toList());
        }
        return null;
    }
    
    public <K, V> Map<K, V> getMap(final String key, final Class<K> keyClass, final Class<V> valueClass) {
        final Object obj = this._set.get(key);
        if (obj == null || !(obj instanceof Map)) {
            return null;
        }
        final Map<?, ?> originalList = (Map<?, ?>)obj;
        if (!originalList.isEmpty()) {
            final Stream<Object> stream = originalList.keySet().stream();
            Objects.requireNonNull(keyClass);
            if (stream.allMatch(keyClass::isInstance)) {
                final Stream<?> stream2 = originalList.values().stream();
                Objects.requireNonNull(valueClass);
                if (stream2.allMatch(valueClass::isInstance)) {
                    return (Map<K, V>)obj;
                }
            }
            StatsSet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/reflect/Type;)Ljava/lang/String;, key, keyClass.getSimpleName(), valueClass.getSimpleName(), obj.getClass().getGenericInterfaces()[0]), (Throwable)new ClassNotFoundException());
        }
        return (Map<K, V>)obj;
    }
    
    public StatsSet set(final String name, final Object value) {
        if (value == null) {
            return this;
        }
        this._set.put(name, value);
        return this;
    }
    
    public StatsSet set(final String key, final boolean value) {
        this._set.put(key, value);
        return this;
    }
    
    public StatsSet set(final String key, final byte value) {
        this._set.put(key, value);
        return this;
    }
    
    public StatsSet set(final String key, final short value) {
        this._set.put(key, value);
        return this;
    }
    
    public StatsSet set(final String key, final int value) {
        this._set.put(key, value);
        return this;
    }
    
    public StatsSet set(final String key, final long value) {
        this._set.put(key, value);
        return this;
    }
    
    public StatsSet set(final String key, final float value) {
        this._set.put(key, value);
        return this;
    }
    
    public StatsSet set(final String key, final double value) {
        this._set.put(key, value);
        return this;
    }
    
    public StatsSet set(final String key, final String value) {
        if (value == null) {
            return this;
        }
        this._set.put(key, value);
        return this;
    }
    
    public StatsSet set(final String key, final Enum<?> value) {
        if (value == null) {
            return this;
        }
        this._set.put(key, value);
        return this;
    }
    
    public void remove(final String key) {
        this._set.remove(key);
    }
    
    public boolean contains(final String name) {
        return this._set.containsKey(name);
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/util/Map;)Ljava/lang/String;, this._set);
    }
    
    static {
        EMPTY_STATSET = new StatsSet(Collections.emptyMap());
        LOGGER = LoggerFactory.getLogger((Class)StatsSet.class);
    }
}
