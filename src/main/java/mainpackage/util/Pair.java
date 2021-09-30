package mainpackage.util;

import java.util.Objects;

public record Pair<A,B>(A one, B two) {
    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair<?, ?> pair) {
            return one.getClass().isArray() ? Objects.deepEquals(one, pair.one) : one.equals(pair.one)
                    && two.getClass().isArray() ? Objects.deepEquals(two, pair.two) : two.equals(pair.two);
        }
        return false;
    }
}
