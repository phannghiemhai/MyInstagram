/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;

/**
 *
 * @author haipn
 */
public abstract class AbstractController {

    public ResponseOutput doHandle(RequestInfo requestInfo, ResponseType responseType) {
        return handleRequest(requestInfo, responseType);
    }
    
    abstract protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType);
    
}
