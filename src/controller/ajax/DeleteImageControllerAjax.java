/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ajax;

import controller.AbstractController;
import entity.DAO.ImageEntity;
import utils.Constants;
import utils.http.JSONResponse;
import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;
import model.ModelImage;
import model.ModelLikeImage;
import org.json.JSONObject;

/**
 *
 * @author haipn
 */
public class DeleteImageControllerAjax extends AbstractController {

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        String data = "\"\"";
        try {
            int type = request.getParamInt("type");
            if (request.isLoggedIn()) {
                int id = request.getParamInt("id");
                ImageEntity entity = ModelImage.getInstance().getEntity(id);
                if (entity.userId.equals(request.account.email)) {
                    JSONObject jo = processDeleteImage(request, entity);
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

    private JSONObject processDeleteImage(RequestInfo request, ImageEntity entity) {
        JSONObject res = new JSONObject();
        try {
            ModelImage.getInstance().delete(entity);
            ModelLikeImage.getInstance().delete(entity);
            res.put("err", 0);
            res.put("id", entity.id);
            res.put("ratio", entity.ratio);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return res;
    }
}
