package chace.smsdialupbackend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chace Zwagerman 11/23/2021.
 */

public class AuthUser {

    public static List<AuthUser> userList = new ArrayList<AuthUser>();

    private String mobileNumber;

    private long timeLeft;

    public static AuthUser getAuthUser(String mobileNumber) {
        for (AuthUser user : userList) {
            if (user.mobileNumber == mobileNumber)
                return user;
        }
        return null;
    }

    public boolean isAnAuthUser(String mobileNumber) {
        for (AuthUser user : userList) {
            if ((user.mobileNumber == mobileNumber) && timeLeft != 0) return true;
        }
        return false;
    }

}
