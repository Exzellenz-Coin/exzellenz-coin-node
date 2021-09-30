package mainpackage.util;

import java.util.Arrays;

public record Pair<A,B>(A one, B two) {
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Pair<?, ?> pair) {
            if (two instanceof byte[]) { //special case for signatures
                return one.equals(pair.one) && Arrays.equals((byte[])two, (byte[])pair.two);
            }
            return one.equals(pair.one) && two.equals(pair.two);
        }
        return false;
    }
}
