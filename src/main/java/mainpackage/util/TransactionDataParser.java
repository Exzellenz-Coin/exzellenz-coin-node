package mainpackage.util;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class TransactionDataParser {
    public static List<Pair<PublicKey, byte[]>> parsePublicPairs(String data) {//TODO: translate received data in a transaction to this type
        List<Pair<PublicKey, byte[]>> temp = new ArrayList<>();
        var keypair = KeyHelper.generateKeyPair();
        temp.add(new Pair<>(keypair.getPublic(), new byte[] {24, 43, -43, 65, 89, 32, 113, 53, -44, 87, 12, 101, 82, 49, 31, -119, -76, 86, -117, 52, 77, 6, -80, 59, 33, 57, -2, -119, -14, -51, 127, 44, 16, 62, 48, -17, -73, -28, 3, -124, 22, -127, 4, 54, 54, -71, 56, -21, -117, 26, 27, -66, -19, 15, 13, -110, 2, 107, 107, 112, 109, 30, -90, 3})); //THIS IS A PLACEHOLDER
        return temp;
    }
}
