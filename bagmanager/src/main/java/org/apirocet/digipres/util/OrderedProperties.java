package org.apirocet.digipres.util;

import java.util.*;

public class OrderedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    private final HashSet<Object> keys = new LinkedHashSet<>();

    public Iterable<Object> orderedKeys() {
        return Collections.list(keys());
    }

    @Override
    public Enumeration<Object> keys() {
        return Collections.enumeration(keys);
    }

    @Override
    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }
}
