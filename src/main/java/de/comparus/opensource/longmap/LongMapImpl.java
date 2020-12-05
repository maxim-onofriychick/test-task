package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V> {

    private static final float DEFAULT_LOAD_FACTOR = 0.75F;

    private static final int DEFAULT_CAPACITY = 16;

    private int capacity;

    private Node<V>[] table;

    private int size = 0;

    static class Node<V> {
        long key;
        V value;
        Node<V> next;

        Node(long key, V value, Node<V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return key == node.key &&
                    Objects.equals(value, node.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }

    static int convertCapacityToPowOfTwo(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : n + 1;
    }

    public LongMapImpl(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("unacceptable initial capacity");

        this.capacity = convertCapacityToPowOfTwo(capacity);
    }

    public LongMapImpl() {
        this.capacity = DEFAULT_CAPACITY;
    }


    public V put(long key, V value) {
        Node<V> newNode = new Node<>(key, value, null);
        if (table != null) {
            int indexNum = getIndex(key, table.length);
            Node<V> oldNode = table[indexNum];

            if (oldNode != null) {
                if (oldNode.key == newNode.key) {
                    V oldValue = oldNode.value;
                    oldNode.value = newNode.value;
                    return oldValue;
                } else
                    return checkLinkedListAndReplaceOrAdd(newNode, oldNode);
            } else {
                table[indexNum] = newNode;
                size++;
                table = resize();
                return null;
            }
        } else {
            table = resize();
            int indexNum = getIndex(key, table.length);
            table[indexNum] = newNode;
            size++;
            table = resize();
            return null;
        }
    }

    private int getIndex(long key, int length) {
        return (length - 1) & Long.hashCode(key);
    }


    private V checkLinkedListAndReplaceOrAdd(Node<V> newNode, Node<V> oldNodeHead) {
        Node<V> next = oldNodeHead;
        Node<V> prev = oldNodeHead;

        while (next != null) {
            if (next.key == newNode.key) {

                if (next.equals(newNode))
                    return next.value;

                prev.next = newNode;
                newNode.next = next.next;

                return next.value;
            } else {
                prev = next;
                next = next.next;
                if (next == null) {
                    prev.next = newNode;
                    size++;
                    table = resize();
                    return null;
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    private Node<V>[] resize() {
        Node<V>[] oldTable = table;
        if (table == null || size == 0) {
            return (Node<V>[]) new Node[capacity];
        } else if ((float) (size / oldTable.length) >= DEFAULT_LOAD_FACTOR) {

            int oldCapacity = oldTable.length;
            int newCapacity;

            if (oldCapacity >= Integer.MAX_VALUE)
                return oldTable;

            if (willAdditionOverflow(oldCapacity, oldCapacity)) {
                newCapacity = Integer.MAX_VALUE;
            } else {
                newCapacity = oldCapacity << 1;
            }
            capacity = newCapacity;
            return restructure(oldTable, newCapacity);

        } else {
            return oldTable;
        }
    }

    private boolean willAdditionOverflow(int left, int right) {
        try {
            Math.addExact(left, right);
            return false;
        } catch (ArithmeticException e) {
            return true;
        }
    }

    @SuppressWarnings({"unchecked"})
    private Node<V>[] restructure(Node<V>[] old, int newCapacity) {
        Node<V>[] newTable = (Node<V>[]) new Node[newCapacity];

        for (int i = 0; i < old.length; i++) {
            Node<V> elem = old[i];
            if (elem != null) {
                old[i] = null;
                putElemInNewTable(newTable, elem);
                Node<V> next = elem.next;
                while (next != null) {
                    putElemInNewTable(newTable, next);
                    next = next.next;
                }
            }
        }
        return newTable;
    }

    private void putElemInNewTable(Node<V>[] newTable, Node<V> next) {
        if (newTable[getIndex(next.key, newTable.length)] != null) {
            Node<V> existedNode = newTable[getIndex(next.key, newTable.length)];
            newTable[getIndex(next.key, newTable.length)] = appendNodeToList(existedNode, next);
        } else {
            newTable[getIndex(next.key, newTable.length)] = next;
        }
    }

    private Node<V> appendNodeToList(Node<V> existed, Node<V> node) {
        Node<V> next = existed;
        while (next != null) {

            if (next.next == null) {
                next.next = node;
                break;
            }

            next = next.next;
        }
        return existed;
    }


    public V get(long key) {
        if (table == null || size == 0)
            return null;

        int indexNum = getIndex(key, table.length);
        Node<V> node = table[indexNum];

        if (node == null)
            return null;

        if (node.next == null) {
            if (Objects.equals(node.key, key))
                return node.value;
            else
                return null;
        }

        return findValueInNodeList(key, node);
    }

    private V findValueInNodeList(long key, Node<V> node) {
        Node<V> next = node;
        while (next != null) {

            if (Objects.equals(key, next.key))
                return next.value;

            next = next.next;
        }
        return null;
    }

    public V remove(long key) {
        Node<V> eHead = table[getIndex(key, table.length)];
        if (eHead != null) {
            if (Objects.equals(eHead.key, key)) {
                table[getIndex(key, table.length)] = eHead.next;
                size--;
                return eHead.value;
            } else {
                Node<V> next = eHead.next;
                Node<V> prev = eHead.next;
                while (next != null) {
                    if (Objects.equals(next.key, key)) {
                        prev.next = next.next;
                        size--;
                        return next.value;
                    }
                    prev = next;
                    next = next.next;
                }
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return table == null || size() == 0;
    }

    public boolean containsKey(long key) {
        Node<V>[] table = this.table;
        Node<V> head;

        if (table == null || size == 0)
            return false;

        head = table[getIndex(key, table.length)];

        if (head != null) {

            if (Objects.equals(head.key, key))
                return true;

            Node<V> next = head.next;

            while (next != null) {

                if (Objects.equals(next.key, key))
                    return true;

                next = next.next;
            }
        }


        return false;
    }

    public boolean containsValue(V value) {
        Node<V>[] table = this.table;
        Node<V> vHead;
        if (table == null || size == 0)
            return false;

        for (Node<V> vNode : table) {
            vHead = vNode;
            if (vHead != null) {
                if (Objects.equals(value, vHead.value)) {
                    return true;
                } else {
                    Node<V> next = vHead.next;
                    while (next != null) {
                        if (Objects.equals(value, next.value))
                            return true;

                        next = next.next;
                    }
                }
            }
        }

        return false;
    }

    public long[] keys() {
        if (table == null || size == 0)
            return new long[0];

        long[] keys = new long[size];
        int i = 0;

        Node<V> headV;

        for (Node<V> vNode : table) {
            headV = vNode;

            if (headV != null) {
                keys[i] = headV.key;
                i++;
                Node<V> next = headV.next;

                while (next != null) {
                    keys[i] = next.key;
                    i++;
                    next = next.next;
                }
            }
        }

        return keys;
    }

    @SuppressWarnings("unchecked")
    public V[] values() {
        if (table == null || size == 0)
            return null;

        V[] values = null;

        int i = 0;
        Node<V> headV;

        for (Node<V> node : table) {
            if (node != null && node.value != null) {

                Class<?> aClass = node.value.getClass();
                values = (V[]) Array.newInstance(aClass, size);
                break;
            }
        }

        for (Node<V> vNode : table) {
            headV = vNode;

            if (headV != null && values != null) {
                values[i] = headV.value;
                i++;

                Node<V> next = headV.next;

                while (next != null) {
                    values[i] = next.value;
                    i++;

                    next = next.next;
                }
            }
        }

        return values;
    }

    public long size() {
        return size;
    }

    public void clear() {
        if (table == null || size == 0)
            return;

        Arrays.fill(table, null);
        size = 0;
    }
}
