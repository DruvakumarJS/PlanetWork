package com.netiapps.planetwork.utils;

import com.netiapps.planetwork.BuildConfig;

public class Constants {

    public static final String BASE_URL = "http://app-staging.planetwork.in/";  // staging

   //public static final String BASE_URL = "http://app.planetwork.in/";  //Live

    public static final String API = "api/";

    public static final String LOG = "PlanetWork";

    public static final String LOGIN_API_URL = BASE_URL + API + "get-otp";

    public static final String VERIFY_OTP = BASE_URL + API + "verify-otp";

    public static final String LEAVECOUNT = BASE_URL + API + "leaves";

    public static final String APPLY_LEAVE =  BASE_URL + API + "apply-leave";

    public static final String LEAVE_HISTORY =  BASE_URL + API + "leave-history";

    public static final String JOBS  =  BASE_URL + API + "jobs";

    public static final String MYJOBS = "jobs";

    public static final String UPDATE_JOBS = BASE_URL + API + "job-update";

    public static final String TASK = BASE_URL +API + "tasks";

    public static final String CUSTOMERS = BASE_URL + API + "customers";

    public static final String OVERTIME = BASE_URL + API + "add-over-time";

    public static final String LOCATIONINSERT = BASE_URL + API + "track-location";

    public static final String LOGINDETAILSINSERT = BASE_URL + API + "attendance";

    public static final String GETCUSTOMER_MAP = BASE_URL + API + "get-user-path";

    public static final String GETTRIP_DETAILS = BASE_URL + API + "work-report";

    public static  final String WORK_DETAILS = BASE_URL + API +  "work-report-details";

    public static final String sharedPreferencesKey = "SharedPreferences_Key";

    public static final String REVERSE_FIRST_TIME = "ReverseFirstTime";


    public static String loginStatusKey = "loginStatusKey";

    public static String ACESS_TOKEN = "AccessToken";

    public static String TOKEN_TYPE = "TokenType";

    public static String logOutPauseKey = "logoutpause";

    public static String  loginplayKey = "LoginplayKey";

    public  static  String USERID="user_id";

    public  static  String JOBASSIGNID="job_assign_id";

    public  static  String JOBID="job_id";

    public  static  String SR_NUMBER="sr_no";

    public static String userIdKey = "userIdkey";

    public static String userNameKey = "userNameKey";

    public static String email = "Email";

    public static String mobileNumber = "MobileNumber";

    public static String photo = "ProfilePhoto";

    public static String latitude = "Getlat";

    public static String Longitutude = "GetLong";

    public  static  String TASK_PENDING= "Pending";

    public  static  String TASK_IN_PROGRESS= "In progress";

    public  static  String TASK_COMPLETED= "Completed";

    public  static  String TASK_IN_HOLD= "On Hold";

    public static final String OPERATIONA_MODE_SELECT_BEFORE_KEY = "OperationalModeSelected";

    public static boolean isDebug = true;

    public static String LOCATION_SERVICE_SAVING_TO_SERVER_STARTED = "LocatonServiceToServer";

}
