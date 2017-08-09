/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ajax;

import controller.AbstractController;
import entity.DAO.ImageEntity;
import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Set;
import modal.ModalImage;
import modal.ModalLikeImage;
import org.apache.commons.lang.StringUtils;
import utils.Constants;
import utils.RenderUtils;
import utils.Utils;
import utils.http.JSONResponse;
import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;

/**
 *
 * @author haipn
 */
public class LoadDataControllerAjax extends AbstractController {

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        String data = "\"\"";
        try {
            int page = request.getParamInt("page");
            data = getData(request, page);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        JSONResponse responseOutput = new JSONResponse(Constants.ErrorCode.SUCCESS);
        responseOutput.setRespond(data);
        return responseOutput;
    }

    private String getData(RequestInfo request, int page) {
        String key = request.getParamStr("search");
        int type = request.getParamInt("type");
//        System.out.println(type);
        Collection<ImageEntity> entities = null;
        switch (type) {
            case 0:
                if (StringUtils.isBlank(key)) {
                    entities = ModalImage.getInstance().getEntities(page, Constants.Gallery.itemPerPage);
                } else {
                    entities = ModalImage.getInstance().getEntities(key, page, Constants.Gallery.itemPerPage);
                }
                break;
            case 1:
                if (request.isLoggedIn()) {
                    entities = ModalImage.getInstance().getEntities(request.account.email, page, Constants.Gallery.itemPerPage);
                } else {
                    try {
                        request.sendRedirect("log-in");
                    } catch (IOException ex) {
                    }
                    return "";
                }
                break;
            default:
                break;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int idx = 0;
        try {
            Template template = RenderUtils.getTemplate("col_image", "content");
            for (ImageEntity entity : entities) {
                TemplateDataDictionary dict = TemplateDictionary.create();
                if (type == 0) {
                    dict.showSection("PEOPLE_IMAGE");
                } else if (type == 1) {
                    dict.showSection("HOME_IMAGE");
                }
                dict.setVariable("id", String.valueOf(entity.id));
                dict.setVariable("src", entity.src);
                dict.setVariable("ratio", String.valueOf(entity.ratio * 100 + "%"));
                dict.setVariable("username", entity.userId);
                Set<String> sE = ModalLikeImage.getInstance().getEntities(entity.id);
                if (sE.size() >= 100) {
                    dict.showSection("ICON_NONE_DISPLAY");
                }
                if (sE.isEmpty()) {
                    dict.showSection("TOTAL_NONE_DISPLAY");
                }
                dict.setVariable("totalLikeImage", String.valueOf(sE.size()));

                renderTimestamp(dict, entity.timestamp);
                if (request.isLoggedIn() && request.account.likedImgIds.contains(entity.id)) {
                    dict.showSection("LIKE_ACTIVE");
                } else {
                    dict.showSection("LIKE");
                }
                String data = URLEncoder.encode(template.renderToString(dict), "UTF-8");
                if (idx != 0) {
                    builder = builder.append(",");
                }
                builder = builder.append("{\"div\":\"").append(data).append("\", \"ratio\":").append(entity.ratio + 0.3).append(", \"type\":").append(type).append(", \"id\":").append(entity.id).append("}");
                idx++;
            }
            return builder.append("]").toString();
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return "\"\"";
    }

    private void renderTimestamp(TemplateDataDictionary dict, long timestamp) {
        String timestampStr = Utils.getTimestampStr(timestamp, Constants.TimeFormat.DATETIME_UI_FORMAT);
        dict.setVariable("timestamp", timestampStr);
    }
}
