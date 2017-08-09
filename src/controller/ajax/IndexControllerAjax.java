/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ajax;

import controller.AbstractController;
import utils.Constants;
import utils.http.JSONResponse;
import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;

/**
 *
 * @author haipn
 */
public class IndexControllerAjax extends AbstractController {

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        String data = "\"\"";
        ResponseOutput responseOutput = new JSONResponse(Constants.ErrorCode.SUCCESS);
        responseOutput.setRespond(data);
        return responseOutput;
    }
}
