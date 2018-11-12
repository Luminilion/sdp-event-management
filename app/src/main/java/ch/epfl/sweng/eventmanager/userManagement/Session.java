package ch.epfl.sweng.eventmanager.userManagement;

import android.app.Activity;

import com.google.android.gms.tasks.OnCompleteListener;

import ch.epfl.sweng.eventmanager.repository.data.User;

public final class Session {
    private static InMemorySession session = new InMemoryFirebaseSession();

    /**
     * Used in tests to bypass Firebase Auth which is broken in our CI.
     */
    public static void enforceDummySessions() {
        session = new DummyInMemorySession();
    }

    public static User getCurrentUser() {
       return session.getCurrentUser();
    }

    public static boolean hasPermission(User.Permission permission) {
        return isLoggedIn() && getCurrentUser().hasPermission(permission);
    }

    public static boolean isLoggedIn() {
       return session.isLoggedIn();
    }

    public static void login(String email, String password, Activity context, OnCompleteListener callback) {
        session.login(email, password, context, callback);
    }

    public static void logout() {
        session.logout();
    }
}