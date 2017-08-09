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
public class HtmlRespond extends ResponseOutput{

    public HtmlRespond() {
        super(ResponseType.HTML_TYPE);
    }
    
    @Override
    public void setRespond(String data) {
        super.setRespond(data);
    }
    
}
