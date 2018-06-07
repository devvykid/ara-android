package com.xfl.kakaotalkbot;

/**
 * Created by XFL on 2/19/2018.
 */

public class MainScreen /*extends Fragment implements View.OnClickListener*/ {/*
    static Switch onOffSwitch;
    private static String PREFS_KEY = "bot";
    private static String ON_KEY = "on";
    ProgressBar progressBar;
    private boolean compiling = false;
    private TextView permissionCheck;


    static boolean getOn(Context ctx) {

        return ctx.getSharedPreferences(PREFS_KEY, 0).getBoolean(ON_KEY, false);

    }



    @Override
    public void onResume() {
        super.onResume();
        if (permissionCheck == null) return;
        ContentResolver contentResolver = getContext().getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getContext().getPackageName();
        boolean granted = false;
// check to see if the enabledNotificationListeners String contains our package name
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            // in this situation we know that the user has not granted the app the Notification access permission
            granted = false;
        } else {
            granted = true;
        }
        permissionCheck.setText(granted ? "" : getResources().getString(R.string.no_noti_read_permission));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_mainscreen, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        if (!compiling) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        Button setAccessBtn = view.findViewById(R.id.btn_AccessSet);
        setAccessBtn.setOnClickListener(this);
        Button compileBtn = view.findViewById(R.id.btn_compile);
        compileBtn.setOnClickListener(this);
        permissionCheck = view.findViewById(R.id.permissionCheck);
        onOffSwitch = (Switch) view.findViewById(R.id.switch1);
        putOn(getContext(), getOn(getActivity().getApplicationContext()));

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(CompoundButton v, boolean b) {

                putOn(MainApplication.getContext(), b);

            }

        });


        return view;

    }

    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_AccessSet:
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
                break;
            case R.id.btn_compile:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationListener.UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                compiling = true;
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });

                        NotificationListener.initializeScript();
                        NotificationListener.UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                compiling = false;
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }
                }).start();

                break;

        }
    }*/

}
