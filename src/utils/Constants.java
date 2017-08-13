/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import utils.http.ResponseType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haipn
 */
public class Constants {

    public static class TimeFormat {

        public static String DATETIME_UI_FORMAT = "HH:mm yyyy-MM-dd";
    }

    public static class PageMsg {

        public static String page404 = "Hi, You are reaching the Beyond, there's nothing there. <b> Click Me</b> to comback!";
        public static String index = "Hi, I'm <b>Nghiêm Hải Frank</b>, and this's my Website.";
    }

    public static class Gallery {

        public static final int nCols = 4;
        public static final int itemPerPage = 4;
    }

    public static class CmdQueue {

        public static String name = "MyInstagram";
    }

    public enum AccessOption {
        INDEX(1, "/index", true, ResponseType.HTML_TYPE, null),
        PAGE404(2, "/page404", true, ResponseType.HTML_TYPE, null),
        LOGIN(3, "/log-in", true, ResponseType.HTML_TYPE, null),
        LOGOUT(4, "/log-out", false, ResponseType.HTML_TYPE, null),
        AJAX_ADD_IMG(5, "/ajax/add-image", false, ResponseType.JSON_TYPE, null),
        AJAX_LIKE_IMG(6, "/ajax/like-image", false, ResponseType.JSON_TYPE, null),
        AJAX_LOAD_DATA(7, "/ajax/load-data", true, ResponseType.JSON_TYPE, null),
        AJAX_DELETE_IMG(8, "/ajax/delete-image", false, ResponseType.JSON_TYPE, null);

        public int id;
        public String mainTarget;
        public boolean isPublic;
        public ResponseType responseType;
        private Set<String> s_target;

        private AccessOption(int id, String mainTarget, boolean isPublic, ResponseType responseType, String accessStr) {
            this.id = id;
            this.mainTarget = mainTarget;
            this.isPublic = isPublic;
            this.responseType = responseType;
            s_target = new HashSet<>();
            if (accessStr == null) {
                return;
            }
            String[] accesses = accessStr.split(";");
            for (String access : accesses) {
                if (!s_target.contains(access)) {
                    s_target.add(access);
                }
            }
        }

        public boolean hasAccess(String target) {
            if (target.equals(this.mainTarget)) {
                return true;
            }
            return s_target.contains(target);
        }

        public static AccessOption findByMainTarget(String target) {
            for (AccessOption option : values()) {
                if (option.mainTarget.equals(target)) {
                    return option;
                }
            }
            return PAGE404;
        }

        public String getRedirectLink(String url) {
            try {
                return mainTarget + "?url=" + URLEncoder.encode(url, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Constants.class.getName()).log(Level.SEVERE, null, ex);
            }
            return mainTarget;
        }
    }

    public enum ErrorCode {
        SUCCESS(0, true, "Success"),
        FAIL(-1, false, "Fail"),
        FAIL_AUTHEN(-1, false, "Fail Authen");

        public int val;
        public boolean flag; //success or fail
        public String des;

        private ErrorCode(int val, boolean flag, String des) {
            this.val = val;
            this.flag = flag;
            this.des = des;
        }

        public ErrorCode findByValue(int val) {
            for (ErrorCode item : values()) {
                if (item.val == val) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum Platform {
        WINDOWS("windows", false),
        MAC("mac", false),
        UNIX("x11", false),
        ANDROID("android", true),
        IPHONE("iphone", true),
        UNKNOWN("unknown", false);
        ;
        private String des;
        private boolean isPhone;

        private Platform(String des, boolean isPhone) {
            this.des = des;
            this.isPhone = isPhone;
        }

        public String getDes() {
            return des;
        }

        public boolean isPhone() {
            return isPhone;
        }

        @Override
        public String toString() {
            return des;
        }
    }

    public enum Browser {

        ;
        private String des;

        private Browser(String des) {
            this.des = des;
        }
    }
}
