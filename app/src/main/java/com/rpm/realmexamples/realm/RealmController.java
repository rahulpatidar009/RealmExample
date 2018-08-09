package com.rpm.realmexamples.realm;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by RPM on 31/7/18.
 */

public class RealmController {
    private static final String TAG = RealmController.class.getSimpleName();
    private static RealmController instance;
    private static Context mContext;

    public static RealmController getInstance(Context context){
        mContext = context;
        if(instance == null){
            instance = new RealmController();
        }
        return instance;
    }

    public RealmConfiguration getRealmConfiguration(){
        Realm.init(mContext);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        return realmConfiguration;
//        return null;
    }

}
