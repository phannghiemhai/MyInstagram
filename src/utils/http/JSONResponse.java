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
public class JSONResponse extends ResponseOutput {

    private int _err;
    private String _msg;

    public JSONResponse(int err, String msg) {
        super(ResponseType.JSON_TYPE);
        this._err = err;
        this._msg = msg;
        setRespond(null);
    }
    
    public JSONResponse(Constants.ErrorCode e) {
        super(ResponseType.JSON_TYPE);
        this._err = e.val;
        this._msg = e.des;
        setRespond(null);
    }

    @Override
    public void setRespond(String data) {
        if (data == null || data.isEmpty()){
            data = "{}";
        }
        super.setRespond(String.format("{\"err\":%s,\"msg\":\"%s\",\"data\":%s}",
                _err, _msg, data));
    }

}
