/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.http;

import entity.AccountEntity;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author haipn
 */
public class RequestInfo {

    public AccountEntity account;
    private final HttpServletRequest _request;
    private final HttpServletResponse _response;

    public RequestInfo(HttpServletRequest req, HttpServletResponse resp) {
        _request = req;
        _response = resp;
        account = null;
    }

    public HttpServletRequest getRequest() {
        return _request;
    }

    public HttpServletResponse getResponse() {
        return _response;
    }

    public void handleResponseOutput(ResponseOutput ro) {
        try {
            //set common headers
            _response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            _response.setHeader("Pragma", "no-cache");
            _response.setHeader("Expires", "0");
            _response.setCharacterEncoding("UTF-8");
            
            _response.setContentType(ro.getType().getContentType());
            _response.getWriter().write(ro.getRespondData());
            _response.getWriter().close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public boolean isLoggedIn() {
        return account != null;
    }

    public void sendRedirect(String path) throws IOException {
        _response.sendRedirect(path);
    }

    public String getRedirectCode(String path) {
        return String.format("<script>window.location.href=''</script>", path);
    }

    public String getParamStr(String key) {
        String value = _request.getParameter(key);
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return value;
    }

    public int getParamInt(String key) {
        String value = _request.getParameter(key);
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return NumberUtils.toInt(value);
    }

    public long getParamLong(String key) {
        String value = _request.getParameter(key);
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return Long.parseLong(value);
    }

    public byte getParamByte(String key) {
        String value = _request.getParameter(key);
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return Byte.parseByte(value);
    }

    public boolean getParamBolean(String key) {
        String value = _request.getParameter(key);
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    public String[] getParameterValues(String key) {
        return _request.getParameterValues(key);
    }
}
