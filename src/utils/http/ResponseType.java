/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.http;

import utils.Constants;

/**
 *
 * @author haipn
 */
public enum ResponseType {
    JSON_TYPE("application/json;charset=UTF-8"),
    HTML_TYPE("text/html;charset=UTF-8"),
    CSV_TYPE("application/vnd.ms-excel;charset=UTF-8");
    private String _contentType;

    private ResponseType(String contentType) {
        this._contentType = contentType;
    }
    
    public String getContentType(){
        return _contentType;
    }
    
    public ResponseOutput createResponseOutput(){
        if (this == JSON_TYPE) {
            return new JSONResponse(Constants.ErrorCode.SUCCESS);
        } else {
            return new HtmlRespond();
        }
    }
}
