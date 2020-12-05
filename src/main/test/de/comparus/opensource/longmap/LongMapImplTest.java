package de.comparus.opensource.longmap;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class LongMapImplTest {
    private static final String TEST_STRING_VALUE = "TEST_STRING_VALUE";
    private static final String TEST_APPLE = "TEST_APPLE";

    private static final List<String> fruitList =
            Arrays.asList("Apple", "Apricots", "Guava",
                    "Java-Plum", "Avocado", "Blackberries",
                    "Blueberries", "Breadfruit", "Clementine",
                    "Grapes", "Mango", "Peaches");

    private static final long TEN_MILLION = 10_000_000L;
    private static final long TEN_THOUSAND = 10_000L;

    LongMap<String> longMap;

    private static final Random random = new Random();

    @Before
    public void initMap() {
        longMap = new LongMapImpl<>();
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentExceptionWhenNonDefaultConstructorWithZeroValueInvoked(){
        LongMap<String> longMapWithNonDefaultConstructor = new LongMapImpl<>(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentExceptionWhenNonDefaultConstructorWithNegativeValueInvoked(){
        LongMap<String> longMapWithNonDefaultConstructor = new LongMapImpl<>(-100);
    }

    @Test
    public void putWithNotExistedEarlyKeyInMapValue() {
        assertNull(longMap.put(0L, TEST_STRING_VALUE));
        assertNull(longMap.put(1L, TEST_STRING_VALUE));
        assertNull(longMap.put(2L, TEST_STRING_VALUE));
        assertNull(longMap.put(3L, TEST_STRING_VALUE));

        assertEquals(TEST_STRING_VALUE, longMap.get(0L));
        assertEquals(TEST_STRING_VALUE, longMap.get(1L));
        assertEquals(TEST_STRING_VALUE, longMap.get(2L));
        assertEquals(TEST_STRING_VALUE, longMap.get(3L));

    }

    @Test
    public void putNullValueAndReplaceTest() {
        longMap.put(1L, null);
        assertNull(longMap.get(1L));

        assertNull(longMap.put(1L, TEST_STRING_VALUE));
        assertEquals(TEST_STRING_VALUE, longMap.get(1L));

        assertEquals(TEST_STRING_VALUE, longMap.put(1L, TEST_APPLE));
        assertEquals(TEST_APPLE, longMap.get(1L));

    }

    @Test
    public void putBigAmountValuesInMapTest() {
        populateMapWithTenMillionStrings();

        for (long i = 0; i < TEN_MILLION; i++) {
            assertEquals(getStringFromLong(i), longMap.get(i));
        }
    }

    @Test
    public void putBigAmountValuesWithReplaceByFruitsMapTest() {
        populateMapWithTenMillionStrings();

        for (long i = 1_000_000; i <= 1_000_700; i++) {
            assertEquals(getStringFromLong(i),
                    longMap.put(i, fruitList.get(random.nextInt(fruitList.size() - 1))));
        }

        for (long i = 0; i < TEN_MILLION; i++) {
            if (i >= 1_000_000 && i <= 1_000_700) {
                assertTrue(fruitList.contains(longMap.get(i)));
            } else {
                assertEquals(getStringFromLong(i), longMap.get(i));
            }
        }
    }

    @Test
    public void removeWithSizeMapTest() {
        longMap.put(1L, fruitList.get(1));
        longMap.put(2L, fruitList.get(2));
        longMap.put(3L, null);

        assertEquals(3, longMap.size());

        assertEquals(fruitList.get(1), longMap.remove(1L));
        assertEquals(fruitList.get(2), longMap.remove(2L));
        assertNull(longMap.remove(3L));

        assertEquals(0, longMap.size());
    }

    @Test
    public void isEmptyAfterInitializationMapTest() {
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void containsKeyAfterInitializationMapTest() {
        assertFalse(longMap.containsKey(random.nextLong()));
    }

    @Test
    public void isEmptyAfterRemoveValuesFromMapTest() {
        longMap.put(1L, TEST_STRING_VALUE);
        longMap.remove(1);

        assertTrue(longMap.isEmpty());
    }

    @Test
    public void isEmptyAfterRemoveFromMapBigAmountValuesTest() {
        populateMapWithTenMillionStrings();

        for (int i = 0; i < TEN_MILLION; i++) {
            assertEquals(getStringFromLong(i), longMap.remove(i));
        }

        assertEquals(0, longMap.size());
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void containsKeyThatAddedAndNotIfNotAddedInMapTest() {
        long key = 0;
        for (String fruit : fruitList) {
            longMap.put(key++, fruit);
        }

        for (int i = 0; i < fruitList.size(); i++) {
            assertTrue(longMap.containsKey(i));
        }

        for (int i = fruitList.size(); i < fruitList.size() + 100; i++) {
            assertFalse(longMap.containsKey(i));
        }
    }

    @Test
    public void inOneMapBucketFindingKeyTest() {
        longMap.put(15, fruitList.get(0));
        longMap.put(31, fruitList.get(1));
        longMap.put(63, fruitList.get(2));
        longMap.put(127, fruitList.get(3));
        longMap.put(255, fruitList.get(4));
        longMap.put(511, fruitList.get(5));
        longMap.put(1023, fruitList.get(6));

        assertTrue(longMap.containsKey(15));
        assertTrue(longMap.containsKey(31));
        assertTrue(longMap.containsKey(63));
        assertTrue(longMap.containsKey(127));
        assertTrue(longMap.containsKey(255));
        assertTrue(longMap.containsKey(511));
        assertTrue(longMap.containsKey(1023));
    }

    @Test
    public void keyWithNullValueExistsInMapTest() {
        longMap.put(1L, null);
        assertTrue(longMap.containsKey(1L));
    }

    @Test
    public void testBigAmountContainsKeyInMapTest() {
        populateMapWithTenMillionStrings();

        for (long i = 0; i < TEN_MILLION; i++) {
            assertTrue(longMap.containsKey(i));
        }
    }

    @Test
    public void inOneMapBucketFindingValueTest() {
        longMap.put(15, fruitList.get(0));
        longMap.put(31, fruitList.get(1));
        longMap.put(63, fruitList.get(2));
        longMap.put(127, fruitList.get(3));
        longMap.put(255, fruitList.get(4));
        longMap.put(511, fruitList.get(5));
        longMap.put(1023, fruitList.get(6));

        assertTrue(longMap.containsValue(fruitList.get(0)));
        assertTrue(longMap.containsValue(fruitList.get(1)));
        assertTrue(longMap.containsValue(fruitList.get(2)));
        assertTrue(longMap.containsValue(fruitList.get(3)));
        assertTrue(longMap.containsValue(fruitList.get(4)));
        assertTrue(longMap.containsValue(fruitList.get(5)));
        assertTrue(longMap.containsValue(fruitList.get(6)));
    }

    @Test
    public void containsValueInTenMillionsMapValuesTest() {
        populateMapWithTenMillionStrings();

        for (long i = 0; i < 10; i++) {
            int randomValue = random.nextInt((int) TEN_MILLION);
            String randomNumberInTenMillionsStr = Integer.toString(randomValue);
            assertTrue(longMap.containsValue(randomNumberInTenMillionsStr));
        }
    }

    @Test
    public void emptyArrayKeysIfEmptyMap() {
        long[] keys = longMap.keys();

        assertNotNull(keys);
        assertEquals(0, keys.length);
    }

    @Test
    public void keysAllAddedToMapExistTest() {
        populateMapWithTenThousandStrings();

        Set<Long> set = new HashSet<>();

        for (long key : longMap.keys()) {
            set.add(key);
        }

        for (long i = 1; i <= TEN_THOUSAND; i++) {
            assertTrue(set.contains(i));
        }
    }

    @Test
    public void keysArrayFromMapWithRemoveTest() {
        populateMapWithTenThousandStrings();

        long randLongValueInTenThousands = random.nextInt((int) TEN_THOUSAND);
        longMap.remove(randLongValueInTenThousands);

        Set<Long> keysFromMapSet = new HashSet<>();

        for (long key : longMap.keys()) {
            keysFromMapSet.add(key);
        }

        for (long i = 1; i <= TEN_THOUSAND; i++) {
            if (i == randLongValueInTenThousands) {
                assertFalse(keysFromMapSet.contains(randLongValueInTenThousands));
            } else
                assertTrue(keysFromMapSet.contains(i));
        }
    }

    @Test
    public void valuesNullIfEmptyMapTest() {
        String[] values = longMap.values();

        assertNull(values);
    }

    @Test
    public void arrayValuesExistFromMapTest() {
        populateMapWithTenThousandStrings();

        Set<String> set = new HashSet<>(Arrays.asList(longMap.values()));

        for (long i = 1; i <= TEN_THOUSAND; i++) {
            assertTrue(set.contains(getStringFromLong(i)));
        }
    }

    @Test
    public void arrayValuesExistWithRemoveFromMapTest() {
        populateMapWithTenThousandStrings();

        long randInTenThousandValue = random.nextInt((int) TEN_THOUSAND);
        longMap.remove(randInTenThousandValue);

        Set<String> set = new HashSet<>(Arrays.asList(longMap.values()));

        for (long i = 1; i <= TEN_THOUSAND; i++) {
            if (randInTenThousandValue == i) {
                assertFalse(set.contains(getStringFromLong(i)));
            } else
                assertTrue(set.contains(getStringFromLong(i)));
        }
    }

    @Test
    public void clearMapTest() {
        populateMapWithTenMillionStrings();

        longMap.clear();

        assertEquals(0, longMap.size());
        assertNull(longMap.values());
        assertEquals(0, longMap.keys().length);
    }

    private String getStringFromLong(long l) {
        return Long.toString(l);
    }

    private void populateMapWithTenMillionStrings() {
        for (long i = 0; i < TEN_MILLION; i++) {
            longMap.put(i, getStringFromLong(i));
        }
    }

    private void populateMapWithTenThousandStrings() {
        for (long i = 1; i <= TEN_THOUSAND; i++) {
            longMap.put(i, getStringFromLong(i));
        }
    }
}