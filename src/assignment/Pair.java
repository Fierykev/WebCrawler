package assignment;

import java.io.Serializable;

/**
 * Pair two objects together under a single object.
 * 
 * @author Kevin
 *
 * @param <K>
 *            First object.
 * @param <V>
 *            Second object
 */

public class Pair<K, V> implements Comparable, Serializable {
    private static final long serialVersionUID = 1L;

    private K a0;
    private V a1;

    public Pair(K a0, V a1) {
        this.a0 = a0;
        this.a1 = a1;
    }

    public void setFirstElement(K a0) {
        this.a0 = a0;
    }

    public void setSecondElement(V a1) {
        this.a1 = a1;
    }

    public K getFirstElement() {
        return a0;
    }

    public V getSecondElement() {
        return a1;
    }

    public String toString() {
        return "<" + a0.toString() + ", " + a1.toString() + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair<?, ?>))
            return false;

        return a0.equals(((Pair) obj).a0) && a1.equals(((Pair) obj).a1);
    }

    /**
     * Create a hash code from the objects in the class.
     */
    public int hashCode() {
        return (a0 != null ? a0.hashCode() : 0)
                ^ (a1 != null ? a1.hashCode() : 0);
    }

    @Override
    public int compareTo(Object obj) {
        // not an instance of the object
        if (!(obj instanceof Pair<?, ?>))
            return -1;

        // same pointer
        if (this == obj)
            return 0;

        // check if comparable
        Pair p = (Pair) obj;

        if (a0 instanceof Comparable) {
            final int compare = ((Comparable) a0).compareTo(p.a0);

            // check if the first element is the same
            if (compare == 0 && a1 instanceof Comparable)
                return ((Comparable) a1).compareTo(p.a1);

            return compare;
        }

        // not comparable so can't do anything
        return -1;
    }
}