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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.ResettableIterator;

/**
 * Adapts a Map entrySet to the MapIterator interface.
 *
 * @param <K> the type of the keys in the map
 * @param <V> the type of the values in the map
 * @since 4.0
 */
public class EntrySetToMapIteratorAdapter<K, V> implements MapIterator<K, V>, ResettableIterator<K> {

    /** The adapted Map entry Set. */
    final Set<Map.Entry<K, V>> entrySet;

    /** The resettable iterator in use. */
    transient Iterator<Map.Entry<K, V>> iterator;

    /** The currently positioned Map entry. */
    transient Map.Entry<K, V> entry;

    /**
     * Create a new EntrySetToMapIteratorAdapter.
     * @param entrySet  the entrySet to adapt
     */
    public EntrySetToMapIteratorAdapter(final Set<Map.Entry<K, V>> entrySet) {
        this.entrySet = entrySet;
        reset();
    }

    /**
     * Gets the currently active entry.
     * @return Map.Entry&lt;K, V&gt;
     */
    protected synchronized Map.Entry<K, V> current() {
        if (entry == null) {
            throw new IllegalStateException();
        }
        return entry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K getKey() {
        return current().getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getValue() {
        return current().getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K next() {
        entry = iterator.next();
        return getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        iterator.remove();
        entry = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void reset() {
        iterator = entrySet.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V setValue(final V value) {
        return current().setValue(value);
    }
}
