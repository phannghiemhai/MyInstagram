/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ajax;

import controller.AbstractController;
import java.util.Set;
import modal.ModalLikeImage;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Constants;
import utils.http.JSONResponse;
import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;

/**
 *
 * @author haipn
 */
public class LikeImageControllerAjax extends AbstractController {

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        String data = "\"\"";
        try {
            if (request.getRequest().getMethod().equals("POST") && request.isLoggedIn()) {
                int id = request.getParamInt("imgId");
                if (id > 0) {
                    JSONObject jo = processLikeImage(request, id);
                    data = jo.toString();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        ResponseOutput responseOutput = new JSONResponse(Constants.ErrorCode.SUCCESS);
        responseOutput.setRespond(data);
        return responseOutput;
    }

    private JSONObject processLikeImage(RequestInfo request, int imgId) {
        JSONObject res = new JSONObject();
        try {
            Set<String> sE = ModalLikeImage.getInstance().getEntities(imgId);
            int update = ModalLikeImage.getInstance()
                    .update(request.account.likedImgIds, sE, request.account.email, imgId);
            res.put("err", 1);
            res.put("data", update);
            res.put("totalLike", sE.size());
            return res;
        } catch (Exception ex) {
            System.out.println(ex);
            try {
                res.put("err", 1);
            } catch (JSONException ex1) {
            }
        }
        return res;
    }
}
