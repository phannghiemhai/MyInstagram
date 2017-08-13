/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auth;

import entity.AccountEntity;
import utils.Utils;
import utils.http.HttpClientUtils;
import utils.http.RequestInfo;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ModelLikeImage;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import utils.Config;

/**
 *
 * @author haipn
 */
public class Auth {

    private static Auth _instance = null;

    private Auth() {

    }

    public static Auth getInstance() {
        if (_instance == null) {
            _instance = new Auth();
        }

        return _instance;
    }

    public boolean validUserWithPassPort(RequestInfo request) {
        HttpServletRequest req = request.getRequest();
        HttpServletResponse resp = request.getResponse();
        Cookie[] _listCookie = req.getCookies();
        String sessionId = "";
        String userId = "";
        String email = "";
        if (_listCookie != null && _listCookie.length > 0) {
            for (Cookie c : _listCookie) {
                if (c.getName().equalsIgnoreCase("session_id")) {
                    sessionId = c.getValue();
                }
                if (c.getName().equalsIgnoreCase("user_id")) {
                    userId = c.getValue();
                }
                if (c.getName().equalsIgnoreCase("email")) {
                    email = c.getValue();
                }
            }
            if (!sessionId.isEmpty()) {
                //define email here
                if (isValidUser(sessionId, userId)) {
//                    req.setAttribute("isLogged", true);
//                    req.setAttribute("userId", userId);
                    request.account = new AccountEntity(0, userId, email, "", 
                            AccountEntity.AccountType.GUEST, null);
                    request.account.likedImgIds = ModelLikeImage.getInstance().getEntities(email);
                    return true;
                }
            }
        }
        request.account = null;
        return false;
    }

    private boolean isValidUser(String sessionId, String userId) {
        if (sessionId.equals(Utils.MD5(Config.getParam("auth", "private_key") + userId))) {
            return true;
        }
        return false;
    }

    private JSONObject authWithGoogle(String accessToken) {
        JSONObject rs = new JSONObject();
        String pathLink = "https://www.googleapis.com/oauth2/v1/tokeninfo";
        try {
            rs.put("returnCode", false);
            long time = System.currentTimeMillis();
            URIBuilder url = HttpClientUtils.createRequest(pathLink);
            List<NameValuePair> entity = new ArrayList<>();
            entity.add(new BasicNameValuePair("access_token", accessToken));
            String response = HttpClientUtils.executePost(url, entity, "application/x-www-form-urlencoded");
            rs = new JSONObject(response);
//            String email = rs.getString("email");
            String userId = rs.getString("user_id");
            if (userId != null && !userId.isEmpty()) {
                rs.put("returnCode", true);
            } else {
                rs.put("returnCode", false);
            }
        } catch (Exception ex) {
        }
        return rs;
    }

    public boolean setSessionWithPassPort(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String accessToken = req.getParameter("accessToken");
            JSONObject res = authWithGoogle(accessToken);
            if (res.getBoolean("returnCode")) {
                String userId = res.getString("user_id");
                String email = res.getString("email");
                String sessionId = Utils.MD5(Config.getParam("auth", "private_key") + userId);
                addCookie(resp, "user_id", userId, 60 * 60 * 24 * 30);
                addCookie(resp, "session_id", sessionId, 60 * 60 * 24 * 30);
                addCookie(resp, "email", email, 60 * 60 * 24 * 30);
                return true;
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

    public boolean setSessionWithPassPort(RequestInfo requestInfo) {
        return setSessionWithPassPort(requestInfo.getRequest(), requestInfo.getResponse());
    }

    private void addCookie(HttpServletResponse resp, String key, String value, int expireTime) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expireTime);
        resp.addCookie(cookie);
    }

    public boolean removeSessionWithPassPort(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] _listCookie = req.getCookies();
        String SessionId = "";
        if (_listCookie != null && _listCookie.length > 0) {
            for (Cookie c : _listCookie) {
                if (c.getName().equalsIgnoreCase("session_id")) {
                    SessionId = c.getValue();
                }
            }
            if (!SessionId.isEmpty()) {
                for (Cookie c : _listCookie) {
                    if (c.getName().equals("session_id")
                            || c.getName().equals("user_id")
                            || c.getName().equals("email")) {
                        c.setMaxAge(0);
                        resp.addCookie(c);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean removeSessionWithPassPort(RequestInfo request) {
        return removeSessionWithPassPort(request.getRequest(), request.getResponse());
    }

}
