package mainpackage.util;

import java.util.Arrays;
import java.util.Objects;

public record Pair<A,B>(A one, B two) {
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Pair<?, ?> pair) {
            if (one.getClass().isArray() || two.getClass().isArray()) { //array equals
                return one.equals(pair.one) && Objects.deepEquals(two, pair.two);
            }
            return one.equals(pair.one) && two.equals(pair.two);
        }
        return false;
    }
}
