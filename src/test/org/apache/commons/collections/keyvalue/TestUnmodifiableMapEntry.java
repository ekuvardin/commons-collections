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
package org.apache.commons.collections.keyvalue;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.Unmodifiable;

/**
 * Test the UnmodifiableMapEntry class.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Neil O'Toole
 */
public class TestUnmodifiableMapEntry<K, V> extends AbstractTestMapEntry<K, V> {

    public TestUnmodifiableMapEntry(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestUnmodifiableMapEntry.class);
    }

    public static Test suite() {
        return new TestSuite(TestUnmodifiableMapEntry.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Make an instance of Map.Entry with the default (null) key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    @Override
    public Map.Entry<K, V> makeMapEntry() {
        return new UnmodifiableMapEntry<K, V>(null, null);
    }

    /**
     * Make an instance of Map.Entry with the specified key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    @Override
    public Map.Entry<K, V> makeMapEntry(K key, V value) {
        return new UnmodifiableMapEntry<K, V>(key, value);
    }

    //-----------------------------------------------------------------------
    /**
     * Subclasses should override this method.
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public void testConstructors() {
        // 1. test key-value constructor
        Map.Entry<K, V> entry = new UnmodifiableMapEntry<K, V>((K) key, (V) value);
        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // 2. test pair constructor
        KeyValue<K, V> pair = new DefaultKeyValue<K, V>((K) key, (V) value);
        entry = new UnmodifiableMapEntry<K, V>(pair);
        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // 3. test copy constructor
        Map.Entry<K, V> entry2 = new UnmodifiableMapEntry<K, V>(entry);
        assertSame(key, entry2.getKey());
        assertSame(value, entry2.getValue());

        assertTrue(entry instanceof Unmodifiable);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void testAccessorsAndMutators() {
        Map.Entry<K, V> entry = makeMapEntry((K) key, (V) value);

        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // check that null doesn't do anything funny
        entry = makeMapEntry(null, null);
        assertSame(null, entry.getKey());
        assertSame(null, entry.getValue());
    }

    @Override
    public void testSelfReferenceHandling() {
        // block
    }

    public void testUnmodifiable() {
        Map.Entry<K, V> entry = makeMapEntry();
        try {
            entry.setValue(null);
            fail();
        } catch (UnsupportedOperationException ex) {}
    }

}
