package com.example.blitzbuy.config;

import com.example.blitzbuy.pojo.User;

/**
 * UserContext: store user in thread local
 * @author Heather
 * @version 1.0
 */

public class UserContext {
    // every thread has its own ThreadLocal
    // store user in thread local,so that each thread can get its own user
    // to keep thread safe
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user){
        userThreadLocal.set(user);
    }
    public static User getUser(){
        return userThreadLocal.get();
    }
    public static void removeUser(){
        userThreadLocal.remove();
    }

}
