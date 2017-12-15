package fyi.jackson.drew.roadquality.service;


public class ForegroundConstants {


    public interface ACTION {
        public static String MAIN_ACTION = "fyi.jackson.drew.foregroundservice.action.main";
        public static String INIT_ACTION = "fyi.jackson.drew.foregroundservice.action.init";
        public static String STARTFOREGROUND_ACTION = "fyi.jackson.drew.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "fyi.jackson.drew.foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
