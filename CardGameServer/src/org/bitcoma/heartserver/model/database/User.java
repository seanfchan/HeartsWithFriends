package org.bitcoma.heartserver.model.database;

import activejdbc.Model;

public class User extends Model {

    public static final int PENDING = 0;
    public static final int ACTIVE = 1;
    public static final int SUSPENDED = 2;

    // Add validations here using activejdbc
    // See documentation about this here:
    // http://code.google.com/p/activejdbc/wiki/Validations
    static {
        validatePresenceOf("email", "user_name", "password");

        // Checks against regular expression. Doesn't check if really exists.
        validateEmailOf("email");

        // Make sure password is at least 6 characters long (no-whitespace)
        validateRegexpOf("user_name", "[^\\s]{6,}");
        validateRegexpOf("password", "[^\\s]{6,}");
    }

}
