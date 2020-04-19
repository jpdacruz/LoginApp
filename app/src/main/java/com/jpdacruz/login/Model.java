package com.jpdacruz.login;

import com.google.firebase.auth.FirebaseAuth;

public class Model implements InterfaceGeneral.Model {

    private InterfaceGeneral.Presenter presenter;
    private FirebaseAuth mFirebaseAuth;

    public Model(InterfaceGeneral.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void firebaseGetInstance(FirebaseAuth mFirebaseAuth) {

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

}
