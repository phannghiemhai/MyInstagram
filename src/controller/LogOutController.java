/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import auth.Auth;
import utils.Constants.AccessOption;
import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;

/**
 *
 * @author haipn
 */
public class LogOutController extends AbstractController {

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        ResponseOutput response = responseType.createResponseOutput();
        String dataS = process(request);
        response.setRespond(dataS);
        return response;
    }

    private String process(RequestInfo requestInfo) {
        try {
//            Auth.getInstance().removeSessionWithPassPort(requestInfo.getRequest(), requestInfo.getResponse());
            Auth.getInstance().removeSessionWithPassPort(requestInfo.getRequest(), requestInfo.getResponse());
            return requestInfo.getRedirectCode(AccessOption.LOGIN.mainTarget);
        } catch (Exception ex) {
        }
        return "";
    }
}
