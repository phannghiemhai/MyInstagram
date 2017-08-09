/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.http;

/**
 *
 * @author haipn
 */
public class ResponseOutput {
    private final ResponseType _type;
    private String _data;

    public ResponseOutput(ResponseType type) {
        this._type = type;
        this._data = "[Empty]";
    }
    
    public ResponseType getType(){
        return _type;
    }
    
    public void setRespond(String data) {
        this._data = data;
    }
    
    public String getRespondData(){
        return _data;
    }
}
