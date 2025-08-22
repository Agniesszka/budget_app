package budget_app.util;

import org.apache.commons.codec.digest.DigestUtils;

public class HashUtil {
    public static String hashPassword(String password) {
        return DigestUtils.md5Hex(password);
    }

}
