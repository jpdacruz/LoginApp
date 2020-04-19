package com.jpdacruz.login;

import com.google.firebase.auth.FirebaseAuth;

public interface InterfaceGeneral {


    interface View{



    }

    interface Presenter{

        void firebaseGetInstance(FirebaseAuth mFirebaseAuth);


    }

    interface Model{

        void firebaseGetInstance(FirebaseAuth mFirebaseAuth);
    }
}
