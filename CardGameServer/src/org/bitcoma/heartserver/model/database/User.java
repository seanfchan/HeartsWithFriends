package org.bitcoma.heartserver.model.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import activejdbc.Model;

public class User extends Model {

    public static final int PENDING = 0;
    public static final int ACTIVE = 1;
    public static final int SUSPENDED = 2;

    public static final Collection<Long> BOT_IDS = new LinkedList<Long>();
    public static final User[] BOTS = { (User) new User().setInteger("id", 1).setString("user_name", "bot1"),
            (User) new User().setInteger("id", 2).setString("user_name", "bot2"),
            (User) new User().setInteger("id", 3).setString("user_name", "bot3"),
            (User) new User().setInteger("id", 4).setString("user_name", "bot4"),
            (User) new User().setInteger("id", 5).setString("user_name", "bot5"),
            (User) new User().setInteger("id", 6).setString("user_name", "bot6"),
            (User) new User().setInteger("id", 7).setString("user_name", "bot7"),
            (User) new User().setInteger("id", 8).setString("user_name", "bot8"),
            (User) new User().setInteger("id", 9).setString("user_name", "bot9"),
            (User) new User().setInteger("id", 10).setString("user_name", "bot10") };

    /**
     * Returns a random bot that is not already in the list of user ids.
     * 
     * @param userIds
     *            List of user ids that are already used.
     * @return A bot to place in this game.
     */
    public static User selectRandomBot(Collection<Long> userIds) {

        ArrayList<Long> botIdsCopy = new ArrayList<Long>(BOT_IDS);
        botIdsCopy.removeAll(userIds);

        // All bots have been chosen
        if (botIdsCopy.size() == 0)
            return null;

        while (true) {
            Random r = new Random();
            int choice = r.nextInt(botIdsCopy.size());

            int botChosen = (int) (long) botIdsCopy.get(choice);

            return BOTS[botChosen - 1];
        }
    }

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

        for (int i = 1; i <= BOTS.length; i++)
            BOT_IDS.add((long) i);
    }

}
