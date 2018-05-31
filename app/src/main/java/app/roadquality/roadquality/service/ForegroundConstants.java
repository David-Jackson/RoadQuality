package app.roadquality.roadquality.service;


public class ForegroundConstants {

    public static final String STATUS_NAME = "ForegroundStatus";
    public static final int STATUS_ACTIVE = 753;
    public static final int STATUS_INACTIVE = 754;

    public interface ACTION {
        String MAIN_ACTION = "fyi.jackson.drew.foregroundservice.action.main";
        String INIT_ACTION = "fyi.jackson.drew.foregroundservice.action.init";
        String STARTFOREGROUND_ACTION = "fyi.jackson.drew.foregroundservice.action.startforeground";
        String STOPFOREGROUND_ACTION = "fyi.jackson.drew.foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
}
