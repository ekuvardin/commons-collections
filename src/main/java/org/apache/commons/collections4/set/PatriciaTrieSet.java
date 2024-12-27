package org.apache.commons.collections4.set;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.io.IOException;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.io.Serializable;

/**
 * Add  {@code Set} to work with PatriciaTree with unique key ignore corresponding values
 * <p>
 * This set exists to provide interface to work with Set + PatriciaTree
 * </p>
 * <p>
 * One usage would be to ensure that no null entries are added to the set.
 * </p>
 * <pre>PatriciaTrieSet = new PatriciaTrieSet();</pre>
 * <p>
 * This class is Serializable and Cloneable.
 * </p>
 *
 * @since 5.0
 */
public class PatriciaTrieSet extends AbstractSet<String> implements Set<String>, Serializable {

    // Stub for all values in PatriciaTrie
    static final Object PRESENT = new Object();
    transient PatriciaTrie<Object> trieSet;

    public PatriciaTrieSet() {
        trieSet = new PatriciaTrie<>();
    }

    @Override
    public Iterator<String> iterator() {
        return trieSet.keySet().iterator();
    }

    @Override
    public int size() {
        return trieSet.size();
    }

    @Override
    public boolean isEmpty() {
        return trieSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return trieSet.containsKey(o);
    }

    @Override
    public boolean add(String e) {
        return trieSet.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return trieSet.remove(o) == PRESENT;
    }

    @Override
    public void clear() {
        trieSet.clear();
    }

    /**
     * Serializes this object to an ObjectOutputStream.
     *
     * @param out the target ObjectOutputStream.
     * @throws java.io.IOException thrown when an I/O errors occur writing to the target stream.
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws java.io.IOException {
        out.defaultWriteObject();
        out.writeInt(trieSet.size());

        for (String entry : this.trieSet.keySet()) {
            out.writeObject(entry);
        }
    }

    /**
     * Deserializes the set in using a custom routine.
     *
     * @param s the input stream
     * @throws IOException            if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        s.readFields();
        int size = s.readInt();
        this.trieSet = new PatriciaTrie<>();

        for (int i = 0; i < size; i++) {
            this.add((String) s.readObject());
        }
    }
}
