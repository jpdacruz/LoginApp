package com.jpdacruz.login;

import com.google.firebase.auth.FirebaseAuth;

public class Presenter implements InterfaceGeneral.Presenter {

    private InterfaceGeneral.View view;
    private InterfaceGeneral.Model model;

    public Presenter(InterfaceGeneral.View view) {
        this.view = view;
        model = new Model(this);
    }

    @Override
    public void firebaseGetInstance(FirebaseAuth mFirebaseAuth) {

        model.firebaseGetInstance(mFirebaseAuth);
    }

    @Override
    public void firebaseSetInstance(FirebaseAuth mFirebaseAuth) {

    }
}
