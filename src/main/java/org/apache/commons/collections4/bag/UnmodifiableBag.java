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
package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;

/**
 * Decorates another {@link Bag} to ensure it can't be altered.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * </p>
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException.
 * </p>
 *
 * @param <E> the type of elements in this bag
 * @since 3.0
 */
public final class UnmodifiableBag<E>
        extends AbstractBagDecorator<E> implements Unmodifiable {

    /** Serialization version */
    private static final long serialVersionUID = -1873799975157099624L;

    /**
     * Factory method to create an unmodifiable bag.
     * <p>
     * If the bag passed in is already unmodifiable, it is returned.
     *
     * @param <E> the type of the elements in the bag
     * @param bag  the bag to decorate, must not be null
     * @return an unmodifiable Bag
     * @throws NullPointerException if bag is null
     * @since 4.0
     */
    public static <E> Bag<E> unmodifiableBag(final Bag<? extends E> bag) {
        if (bag instanceof Unmodifiable) {
            @SuppressWarnings("unchecked") // safe to upcast
            final Bag<E> tmpBag = (Bag<E>) bag;
            return tmpBag;
        }
        return new UnmodifiableBag<>(bag);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param bag  the bag to decorate, must not be null
     * @throws NullPointerException if bag is null
     */
    @SuppressWarnings("unchecked") // safe to upcast
    private UnmodifiableBag(final Bag<? extends E> bag) {
        super((Bag<E>) bag);
    }

    @Override
    public boolean add(final E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(final E object, final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.<E>unmodifiableIterator(decorated().iterator());
    }

    /**
     * Deserializes the collection in using a custom routine.
     *
     * @param in  the input stream
     * @throws IOException if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     * @throws ClassCastException if deserialized object has wrong type
     */
    @SuppressWarnings("unchecked") // will throw CCE, see Javadoc
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setCollection((Collection<E>) in.readObject());
    }

    @Override
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object object, final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 4.4
     */
    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<E> uniqueSet() {
        final Set<E> set = decorated().uniqueSet();
        return UnmodifiableSet.<E>unmodifiableSet(set);
    }

    /**
     * Serializes this object to an ObjectOutputStream.
     *
     * @param out the target ObjectOutputStream.
     * @throws IOException thrown when an I/O errors occur writing to the target stream.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(decorated());
    }

}
