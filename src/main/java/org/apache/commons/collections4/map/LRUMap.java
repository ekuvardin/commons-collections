/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.collections4.BoundedMap;

/**
 * A {@code Map} implementation with a fixed maximum size which removes
 * the least recently used entry if an entry is added when full.
 * <p>
 * The least recently used algorithm works on the get and put operations only.
 * Iteration of any kind, including setting the value by iteration, does not
 * change the order. Queries such as containsKey and containsValue or access
 * via views also do not change the order.
 * </p>
 * <p>
 * A somewhat subtle ramification of the least recently used
 * algorithm is that calls to {@link #get(Object)} stand a very good chance
 * of modifying the map's iteration order and thus invalidating any
 * iterators currently in use.  It is therefore suggested that iterations
 * over an {@link LRUMap} instance access entry values only through a
 * {@link org.apache.commons.collections4.MapIterator MapIterator} or {@link #entrySet()} iterator.
 * </p>
 * <p>
 * The map implements {@code OrderedMap} and entries may be queried using
 * the bidirectional {@code OrderedMapIterator}. The order returned is
 * least recently used to most recently used. Iterators from map views can
 * also be cast to {@code OrderedIterator} if required.
 * </p>
 * <p>
 * All the available iterators can be reset back to the start by casting to
 * {@code ResettableIterator} and calling {@code reset()}.
 * </p>
 * <p>
 * <strong>Note that LRUMap is not synchronized and is not thread-safe.</strong>
 * If you wish to use this map from multiple threads concurrently, you must use
 * appropriate synchronization. The simplest approach is to wrap this map
 * using {@link java.util.Collections#synchronizedMap(Map)}. This class may throw
 * {@code NullPointerException}'s when accessed by concurrent threads.
 * </p>
 *
 * @param <K> the type of the keys in this map
 * @param <V> the type of the values in this map
 * @since 3.0 (previously in main package v1.0)
 */
public class LRUMap<K, V>
        extends AbstractLinkedMap<K, V> implements BoundedMap<K, V>, Serializable, Cloneable {

    /** Serialization version */
    private static final long serialVersionUID = -612114643488955218L;
    /** Default maximum size */
    protected static final int DEFAULT_MAX_SIZE = 100;

    /** Maximum size */
    private transient int maxSize;
    /** Scan behavior */
    private final boolean scanUntilRemovable;

    /**
     * Constructs a new empty map with a maximum size of 100.
     */
    public LRUMap() {
        this(DEFAULT_MAX_SIZE, DEFAULT_LOAD_FACTOR, false);
    }

    /**
     * Constructs a new, empty map with the specified maximum size.
     *
     * @param maxSize  the maximum size of the map
     * @throws IllegalArgumentException if the maximum size is less than one
     */
    public LRUMap(final int maxSize) {
        this(maxSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new, empty map with the specified maximum size.
     *
     * @param maxSize  the maximum size of the map
     * @param scanUntilRemovable  scan until a removable entry is found, default false
     * @throws IllegalArgumentException if the maximum size is less than one
     * @since 3.1
     */
    public LRUMap(final int maxSize, final boolean scanUntilRemovable) {
        this(maxSize, DEFAULT_LOAD_FACTOR, scanUntilRemovable);
    }

    /**
     * Constructs a new, empty map with the specified max capacity and
     * load factor.
     *
     * @param maxSize  the maximum size of the map
     * @param loadFactor  the load factor
     * @throws IllegalArgumentException if the maximum size is less than one
     * @throws IllegalArgumentException if the load factor is less than zero
     */
    public LRUMap(final int maxSize, final float loadFactor) {
        this(maxSize, loadFactor, false);
    }

    /**
     * Constructs a new, empty map with the specified max capacity and load factor.
     *
     * @param maxSize  the maximum size of the map
     * @param loadFactor  the load factor
     * @param scanUntilRemovable  scan until a removable entry is found, default false
     * @throws IllegalArgumentException if the maximum size is less than one
     * @throws IllegalArgumentException if the load factor is less than zero
     * @since 3.1
     */
    public LRUMap(final int maxSize, final float loadFactor, final boolean scanUntilRemovable) {
        this(maxSize, maxSize, loadFactor, scanUntilRemovable);
    }

    /**
     * Constructs a new, empty map with the specified maximum size.
     *
     * @param maxSize  the maximum size of the map
     * @param initialSize  the initial size of the map
     * @throws IllegalArgumentException if the maximum size is less than one
     * @throws IllegalArgumentException if the initial size is negative or larger than the maximum size
     * @since 4.1
     */
    public LRUMap(final int maxSize, final int initialSize) {
        this(maxSize, initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new, empty map with the specified max / initial capacity and
     * load factor.
     *
     * @param maxSize  the maximum size of the map
     * @param initialSize  the initial size of the map
     * @param loadFactor  the load factor
     * @throws IllegalArgumentException if the maximum size is less than one
     * @throws IllegalArgumentException if the initial size is negative or larger than the maximum size
     * @throws IllegalArgumentException if the load factor is less than zero
     * @since 4.1
     */
    public LRUMap(final int maxSize, final int initialSize, final float loadFactor) {
        this(maxSize, initialSize, loadFactor, false);
    }

    /**
     * Constructs a new, empty map with the specified max / initial capacity and load factor.
     *
     * @param maxSize  the maximum size of the map
     * @param initialSize  the initial size of the map
     * @param loadFactor  the load factor
     * @param scanUntilRemovable  scan until a removable entry is found, default false
     * @throws IllegalArgumentException if the maximum size is less than one
     * @throws IllegalArgumentException if the initial size is negative or larger than the maximum size
     * @throws IllegalArgumentException if the load factor is less than zero
     * @since 4.1
     */
    public LRUMap(final int maxSize,
                  final int initialSize,
                  final float loadFactor,
                  final boolean scanUntilRemovable) {

        super(initialSize, loadFactor);
        if (maxSize < 1) {
            throw new IllegalArgumentException("LRUMap max size must be greater than 0");
        }
        if (initialSize > maxSize) {
            throw new IllegalArgumentException("LRUMap initial size must not be greater than max size");
        }
        this.maxSize = maxSize;
        this.scanUntilRemovable = scanUntilRemovable;
    }

    /**
     * Constructor copying elements from another map.
     * <p>
     * The maximum size is set from the map's size.
     * </p>
     *
     * @param map  the map to copy
     * @throws NullPointerException if the map is null
     * @throws IllegalArgumentException if the map is empty
     */
    public LRUMap(final Map<? extends K, ? extends V> map) {
        this(map, false);
    }

    /**
     * Constructor copying elements from another map.
     *
     * <p>The maximum size is set from the map's size.</p>
     *
     * @param map  the map to copy
     * @param scanUntilRemovable  scan until a removable entry is found, default false
     * @throws NullPointerException if the map is null
     * @throws IllegalArgumentException if the map is empty
     * @since 3.1
     */
    public LRUMap(final Map<? extends K, ? extends V> map, final boolean scanUntilRemovable) {
        this(map.size(), DEFAULT_LOAD_FACTOR, scanUntilRemovable);
        putAll(map);
    }

    /**
     * Adds a new key-value mapping into this map.
     * <p>
     * This implementation checks the LRU size and determines whether to
     * discard an entry or not using {@link #removeLRU(AbstractLinkedMap.LinkEntry)}.
     * </p>
     * <p>
     * From Commons Collections 3.1 this method uses {@link #isFull()} rather
     * than accessing {@code size} and {@code maxSize} directly.
     * It also handles the scanUntilRemovable functionality.
     * </p>
     *
     * @param hashIndex  the index into the data array to store at
     * @param hashCode  the hash code of the key to add
     * @param key  the key to add
     * @param value  the value to add
     */
    @Override
    protected void addMapping(final int hashIndex, final int hashCode, final K key, final V value) {
        if (isFull()) {
            LinkEntry<K, V> reuse = header.after;
            boolean removeLRUEntry = false;
            if (scanUntilRemovable) {
                while (reuse != header && reuse != null) {
                    if (removeLRU(reuse)) {
                        removeLRUEntry = true;
                        break;
                    }
                    reuse = reuse.after;
                }
                if (reuse == null) {
                    throw new IllegalStateException(
                        "Entry.after=null, header.after=" + header.after + " header.before=" + header.before +
                        " key=" + key + " value=" + value + " size=" + size + " maxSize=" + maxSize +
                        " This should not occur if your keys are immutable and you used synchronization properly.");
                }
            } else {
                removeLRUEntry = removeLRU(reuse);
            }

            if (removeLRUEntry) {
                if (reuse == null) {
                    throw new IllegalStateException(
                        "reuse=null, header.after=" + header.after + " header.before=" + header.before +
                        " key=" + key + " value=" + value + " size=" + size + " maxSize=" + maxSize +
                        " This should not occur if your keys are immutable and you used synchronization properly.");
                }
                reuseMapping(reuse, hashIndex, hashCode, key, value);
            } else {
                super.addMapping(hashIndex, hashCode, key, value);
            }
        } else {
            super.addMapping(hashIndex, hashCode, key, value);
        }
    }

    /**
     * Clones the map without cloning the keys or values.
     *
     * @return a shallow clone
     */
    @Override
    public LRUMap<K, V> clone() {
        return (LRUMap<K, V>) super.clone();
    }

    /**
     * Reads the data necessary for {@code put()} to work in the superclass.
     *
     * @param in  the input stream
     * @throws IOException if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     */
    @Override
    protected void doReadObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        maxSize = in.readInt();
        super.doReadObject(in);
    }

    /**
     * Writes the data necessary for {@code put()} to work in deserialization.
     *
     * @param out  the output stream
     * @throws IOException if an error occurs while writing to the stream
     */
    @Override
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(maxSize);
        super.doWriteObject(out);
    }

    /**
     * Gets the value mapped to the key specified.
     * <p>
     * This operation changes the position of the key in the map to the
     * most recently used position (last).
     *
     * @param key  the key
     * @return the mapped value, null if no match
     */
    @Override
    public V get(final Object key) {
        return get(key, true);
    }

    /**
     * Gets the value mapped to the key specified.
     * <p>
     * If {@code updateToMRU} is {@code true}, the position of the key in the map
     * is changed to the most recently used position (last), otherwise the iteration
     * order is not changed by this operation.
     * </p>
     *
     * @param key  the key
     * @param updateToMRU  whether the key shall be updated to the
     *   most recently used position
     * @return the mapped value, null if no match
     * @since 4.1
     */
    public V get(final Object key, final boolean updateToMRU) {
        final LinkEntry<K, V> entry = getEntry(key);
        if (entry == null) {
            return null;
        }
        if (updateToMRU) {
            moveToMRU(entry);
        }
        return entry.getValue();
    }

    /**
     * Returns true if this map is full and no new mappings can be added.
     *
     * @return {@code true} if the map is full
     */
    @Override
    public boolean isFull() {
        return size >= maxSize;
    }

    /**
     * Tests whether this LRUMap will scan until a removable entry is found when the
     * map is full.
     *
     * @return true if this map scans
     * @since 3.1
     */
    public boolean isScanUntilRemovable() {
        return scanUntilRemovable;
    }

    /**
     * Gets the maximum size of the map (the bound).
     *
     * @return the maximum number of elements the map can hold
     */
    @Override
    public int maxSize() {
        return maxSize;
    }

    /**
     * Moves an entry to the MRU position at the end of the list.
     * <p>
     * This implementation moves the updated entry to the end of the list.
     * </p>
     *
     * @param entry  the entry to update
     */
    protected void moveToMRU(final LinkEntry<K, V> entry) {
        if (entry.after != header) {
            modCount++;
            // remove
            if (entry.before == null) {
                throw new IllegalStateException("Entry.before is null." +
                    " This should not occur if your keys are immutable, and you have used synchronization properly.");
            }
            entry.before.after = entry.after;
            entry.after.before = entry.before;
            // add first
            entry.after = header;
            entry.before = header.before;
            header.before.after = entry;
            header.before = entry;
        } else if (entry == header) {
            throw new IllegalStateException("Can't move header to MRU" +
                    " This should not occur if your keys are immutable, and you have used synchronization properly.");
        }
    }

    /**
     * Deserializes the map in using a custom routine.
     *
     * @param in the input stream
     * @throws IOException if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        doReadObject(in);
    }

    /**
     * Subclass method to control removal of the least recently used entry from the map.
     * <p>
     * This method exists for subclasses to override. A subclass may wish to
     * provide cleanup of resources when an entry is removed. For example:
     * </p>
     * <pre>
     * protected boolean removeLRU(LinkEntry entry) {
     *   releaseResources(entry.getValue());  // release resources held by entry
     *   return true;  // actually delete entry
     * }
     * </pre>
     * <p>
     * Alternatively, a subclass may choose to not remove the entry or selectively
     * keep certain LRU entries. For example:
     * </p>
     * <pre>
     * protected boolean removeLRU(LinkEntry entry) {
     *   if (entry.getKey().toString().startsWith("System.")) {
     *     return false;  // entry not removed from LRUMap
     *   } else {
     *     return true;  // actually delete entry
     *   }
     * }
     * </pre>
     * <p>
     * The effect of returning false is dependent on the scanUntilRemovable flag.
     * If the flag is true, the next LRU entry will be passed to this method and so on
     * until one returns false and is removed, or every entry in the map has been passed.
     * If the scanUntilRemovable flag is false, the map will exceed the maximum size.
     * </p>
     * <p>
     * Note: Commons Collections 3.0 passed the wrong entry to this method.
     * This is fixed in version 3.1 onwards.
     * </p>
     *
     * @param entry  the entry to be removed
     * @return {@code true}
     */
    protected boolean removeLRU(final LinkEntry<K, V> entry) {
        return true;
    }

    /**
     * Reuses an entry by removing it and moving it to a new place in the map.
     * <p>
     * This method uses {@link #removeEntry}, {@link #reuseEntry} and {@link #addEntry}.
     *
     * @param entry  the entry to reuse
     * @param hashIndex  the index into the data array to store at
     * @param hashCode  the hash code of the key to add
     * @param key  the key to add
     * @param value  the value to add
     */
    protected void reuseMapping(final LinkEntry<K, V> entry, final int hashIndex, final int hashCode,
                                final K key, final V value) {
        // find the entry before the entry specified in the hash table
        // remember that the parameters (except the first) refer to the new entry,
        // not the old one
        try {
            final int removeIndex = hashIndex(entry.hashCode, data.length);
            final HashEntry<K, V>[] tmp = data;  // may protect against some sync issues
            HashEntry<K, V> loop = tmp[removeIndex];
            HashEntry<K, V> previous = null;
            while (loop != entry && loop != null) {
                previous = loop;
                loop = loop.next;
            }
            if (loop == null) {
                throw new IllegalStateException(
                    "Entry.next=null, data[removeIndex]=" + data[removeIndex] + " previous=" + previous +
                    " key=" + key + " value=" + value + " size=" + size + " maxSize=" + maxSize +
                    " This should not occur if your keys are immutable, and you have used synchronization properly.");
            }

            // reuse the entry
            modCount++;
            removeEntry(entry, removeIndex, previous);
            reuseEntry(entry, hashIndex, hashCode, key, value);
            addEntry(entry, hashIndex);
        } catch (final NullPointerException ex) {
            throw new IllegalStateException("NPE, entry=" + entry + " entryIsHeader=" + (entry == header) + " key=" + key + " value=" + value + " size=" + size
                    + " maxSize=" + maxSize + " This should not occur if your keys are immutable, and you have used synchronization properly.");
        }
    }

    /**
     * Updates an existing key-value mapping.
     * <p>
     * This implementation moves the updated entry to the end of the list
     * using {@link #moveToMRU(AbstractLinkedMap.LinkEntry)}.
     * </p>
     *
     * @param entry  the entry to update
     * @param newValue  the new value to store
     */
    @Override
    protected void updateEntry(final HashEntry<K, V> entry, final V newValue) {
        moveToMRU((LinkEntry<K, V>) entry);  // handles modCount
        entry.setValue(newValue);
    }

    /**
     * Serializes this object to an ObjectOutputStream.
     *
     * @param out the target ObjectOutputStream.
     * @throws IOException thrown when an I/O errors occur writing to the target stream.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

}
