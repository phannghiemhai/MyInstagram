/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpservice;

import auth.Auth;
import controller.AbstractController;
import controller.IndexController;
import controller.LogInController;
import controller.LogOutController;
import controller.Page404Controller;
import controller.ajax.AddImageControllerAjax;
import controller.ajax.DeleteImageControllerAjax;
import controller.ajax.LikeImageControllerAjax;
import controller.ajax.LoadDataControllerAjax;
import utils.Constants;
import utils.http.ResponseOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import utils.Constants.AccessOption;
import utils.http.JSONResponse;
import utils.http.RequestInfo;
import utils.http.ResponseType;
import java.util.Map;

/**
 *
 * @author haipn
 */
public class MainHandler extends AbstractHandler {

    private List<ControllerHolder> _controllersHolders;

    public MainHandler() {
        _controllersHolders = new ArrayList<>();
        //controller
        addMapping(new IndexController(), AccessOption.INDEX, false);
        addMapping(new Page404Controller(), AccessOption.PAGE404, false);
        addMapping(new LogInController(), AccessOption.LOGIN, false);
        addMapping(new LogOutController(), AccessOption.LOGOUT, false);

        //ajax
        addMapping(new AddImageControllerAjax(), AccessOption.AJAX_ADD_IMG, false);
        addMapping(new LikeImageControllerAjax(), AccessOption.AJAX_LIKE_IMG, false);
        addMapping(new LoadDataControllerAjax(), AccessOption.AJAX_LOAD_DATA, false);
        addMapping(new DeleteImageControllerAjax(), AccessOption.AJAX_DELETE_IMG, false);
    }

    private boolean filter(String target, Request baseReq, RequestInfo request) {
        AccessOption targetOption = AccessOption.findByMainTarget(target);
        try {
            if (target.equals("/admin/log-in")) {
                Map<String, String[]> params = request.getRequest().getParameterMap();
                String url = "/log-in?";
                for (String key : params.keySet()) {
                    String[] vals = params.get(key);
                    for (String val : vals) {
                        url += key + "=" + val + "&";
                    }
                }
                request.sendRedirect(url);
                return false;
            }
            if (!Auth.getInstance().validUserWithPassPort(request)) {
                if (!targetOption.isPublic) {
                    if (targetOption.responseType == ResponseType.JSON_TYPE) {
                        JSONResponse ro = new JSONResponse(Constants.ErrorCode.FAIL_AUTHEN);
                        request.handleResponseOutput(ro);
                    } else {
                        request.sendRedirect(AccessOption.LOGIN.mainTarget);
                    }
                    return false;
                }
            } else {
                if (targetOption == AccessOption.LOGIN) {
                    request.sendRedirect(AccessOption.INDEX.mainTarget);
                    return false;
                }
            }

            if (target.isEmpty() || target.equals("/")) {
                try {
                    request.sendRedirect(AccessOption.INDEX.mainTarget);
                    return false;
                } catch (IOException ex) {
                    Logger.getLogger(MainHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return true;
        } catch (Exception ex) {

        }
        return false;
    }

    @Override
    public void handle(String target, Request baseReq,
            HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        RequestInfo request = new RequestInfo(req, resp);
        if (filter(target, baseReq, request)) {
            String pathInfo = req.getPathInfo();
            if (pathInfo.endsWith("/")) {
                pathInfo = pathInfo.substring(0, pathInfo.length() - 1); // Remove slash letter
            }
            for (ControllerHolder holder : _controllersHolders) {
                if (holder.canHandleRequest(pathInfo)) {
                    holder.doHandle(request);
                    baseReq.setHandled(true); // Tell jetty current request is served success
                    break;
                }
            }

            // No mapping found
            if (!baseReq.isHandled()) {
//                resp.getWriter().write("{\"status:\"404, \"msg\":\"Page not found: " + target + "\"}");
                resp.sendRedirect(AccessOption.PAGE404.getRedirectLink(req.toString()));
                resp.setStatus(HttpStatus.NOT_FOUND_404);
            }
        }
    }

    private void addMapping(AbstractController controller, AccessOption accessOption, boolean handleAllSubPath) {
        ControllerHolder holder = new ControllerHolder(controller, accessOption, handleAllSubPath);
        _controllersHolders.add(holder);

        Collections.sort(_controllersHolders, new Comparator<ControllerHolder>() {
            @Override
            public int compare(ControllerHolder o1, ControllerHolder o2) {
                if (o1._accessOption.mainTarget.length() > o2._accessOption.mainTarget.length()) {
                    return -1;
                } else if (o1._accessOption.mainTarget.length() < o2._accessOption.mainTarget.length()) {
                    return 1;
                }

                return 0;
            }
        });
    }

    private static class ControllerHolder {

        private final String _controllerName;
        private final AccessOption _accessOption;
        private final AbstractController _controller;
        private final boolean _handleAllSubPath;

        public ControllerHolder(AbstractController controller, AccessOption accessOption,
                boolean handleAllSubPath) {
            this._accessOption = accessOption;
            this._controller = controller;
            this._handleAllSubPath = handleAllSubPath;
            this._controllerName = _controller.getClass().getSimpleName();
        }

        public String getHandlerName() {
            return _controllerName;
        }

        boolean canHandleRequest(String targetPathInfo) {
            if (_handleAllSubPath) {
                return targetPathInfo.startsWith(_accessOption.mainTarget);
            } else {
                return _accessOption.hasAccess(targetPathInfo);
            }
        }

        private void doHandle(RequestInfo request) {
            try {
                ResponseOutput result = _controller.doHandle(request, _accessOption.responseType);
                request.handleResponseOutput(result);
            } catch (Exception ex) {
                System.err.println(ex);
            } finally {

            }
        }

        @Override
        public String toString() {
            return String.format("%s : %s", _controller.toString(), _accessOption.mainTarget);
        }

    }

}
