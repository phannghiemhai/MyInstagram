/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.DAO.ImageEntity;
import utils.Configuration;
import utils.RenderUtils;
import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;
import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import java.util.Collection;
import java.util.Set;
import modal.ModalImage;
import modal.ModalLikeImage;
import org.apache.commons.lang.StringUtils;
import utils.Constants;
import utils.Utils;

/**
 *
 * @author haipn
 */
public class IndexController extends AbstractController {

    private static final String[] clockIds = {"mainClock"};

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        ResponseOutput responseOutput = responseType.createResponseOutput();
        String data = "";
        String type = request.getParamStr("type");
        try {
            TemplateDataDictionary dict = TemplateDictionary.create();
            dict.setVariable("STATIC_URL", Configuration.URL.STATIC_URL);
            renderPeopleGallery(request, dict);
            renderHomeGallery(request, dict);
            RenderUtils.renderNavigationBar(request, dict);
            RenderUtils.renderFooter(request, dict, clockIds);
            RenderUtils.renderModalAddImageByLink(request, dict);
            RenderUtils.renderModalAddImageByUpload(request, dict);
            Template template = RenderUtils.getTemplate("index", "content");
            data = RenderUtils.renderMainPageDetail(request, "", template.renderToString(dict));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        responseOutput.setRespond(data);
        return responseOutput;
    }

    private void renderPeopleGallery(RequestInfo request, TemplateDataDictionary dict) {
        try {
            String key = request.getParamStr("search");
            Collection<ImageEntity> entities = null;
            if (StringUtils.isBlank(key)) {
                entities = ModalImage.getInstance().getEntities(1, Constants.Gallery.itemPerPage);
            } else {
                entities = ModalImage.getInstance().getEntities(key, 1, Constants.Gallery.itemPerPage);
                dict.setVariable("searchBar", key);
            }
            float[] colHeights = new float[Constants.Gallery.nCols];
            TemplateDataDictionary[] colDicts = new TemplateDataDictionary[Constants.Gallery.nCols];
            for (int i = 0; i < Constants.Gallery.nCols; i++) {
                colHeights[i] = 0;
                colDicts[i] = dict.addSection("GALLERY_COL");
                colDicts[i].setVariable("gallery_col_num", String.valueOf(i));
            }
            if (request.isLoggedIn()) {
                colHeights[0] = 1;
                colDicts[0].showSection("BTN_ADD_GALLERY_IMG");
            }
            for (ImageEntity entity : entities) {
                int idx = getMinColHeightIdx(colHeights);
                TemplateDataDictionary imgDict = colDicts[idx].addSection("COL_IMG");
                imgDict.setVariable("id", String.valueOf(entity.id));
                imgDict.setVariable("src", entity.src);
                imgDict.setVariable("ratio", String.valueOf(entity.ratio * 100 + "%"));
                imgDict.setVariable("username", entity.userId);
                Set<String> sE = ModalLikeImage.getInstance().getEntities(entity.id);
                if (sE.size() >= 100) {
                    imgDict.showSection("ICON_NONE_DISPLAY");
                }
                if (sE.isEmpty()) {
                    imgDict.showSection("TOTAL_NONE_DISPLAY");
                }
                imgDict.setVariable("totalLikeImage", String.valueOf(sE.size()));

                renderTimestamp(imgDict, entity.timestamp);
                if (request.isLoggedIn() && request.account.likedImgIds.contains(entity.id)) {
                    imgDict.showSection("LIKE_ACTIVE");
                } else {
                    imgDict.showSection("LIKE");
                }
                colHeights[idx] += entity.ratio;
                TemplateDataDictionary initMap = dict.addSection("INIT_MAP");
                initMap.setVariable("key", String.valueOf(entity.id));
                initMap.setVariable("value", String.valueOf(idx));
                initMap.setVariable("idx", String.valueOf(0));
            }
            dict.addSection("FIRST_COL_HEIGHT").setVariable("value", String.valueOf(colHeights[0]));
            for (int i = 1; i < Constants.Gallery.nCols; i++) {
                dict.addSection("COL_HEIGHT").setVariable("value", String.valueOf(colHeights[i]));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void renderHomeGallery(RequestInfo request, TemplateDataDictionary dict) {
        try {
            if (!request.isLoggedIn()) {
                return;
            }
            Collection<ImageEntity> entities = ModalImage.getInstance().getEntities(request.account.email, 1, Constants.Gallery.itemPerPage);

            float[] colHeights = new float[Constants.Gallery.nCols];
            TemplateDataDictionary[] colDicts = new TemplateDataDictionary[Constants.Gallery.nCols];
            for (int i = 0; i < Constants.Gallery.nCols; i++) {
                colHeights[i] = 0;
                colDicts[i] = dict.addSection("HOME_GALLERY_COL");
                colDicts[i].setVariable("gallery_col_num", String.valueOf(i));
            }
            if (request.isLoggedIn()) {
                colHeights[0] = 1;
                colDicts[0].showSection("BTN_ADD_GALLERY_IMG");
            }
            for (ImageEntity entity : entities) {
                int idx = getMinColHeightIdx(colHeights);
                TemplateDataDictionary imgDict = colDicts[idx].addSection("COL_IMG");
                imgDict.setVariable("id", String.valueOf(entity.id));
                imgDict.setVariable("src", entity.src);
                imgDict.setVariable("ratio", String.valueOf(entity.ratio * 100 + "%"));
                imgDict.setVariable("username", entity.userId);
                Set<String> sE = ModalLikeImage.getInstance().getEntities(entity.id);
                if (sE.size() >= 100) {
                    imgDict.showSection("ICON_NONE_DISPLAY");
                }
                if (sE.isEmpty()) {
                    imgDict.showSection("TOTAL_NONE_DISPLAY");
                }
                imgDict.setVariable("totalLikeImage", String.valueOf(sE.size()));
                renderTimestamp(imgDict, entity.timestamp);
                if (request.isLoggedIn() && request.account.likedImgIds.contains(entity.id)) {
                    imgDict.showSection("LIKE_ACTIVE");
                } else {
                    imgDict.showSection("LIKE");
                }
                colHeights[idx] += entity.ratio + 0.3;
                TemplateDataDictionary initMap = dict.addSection("INIT_MAP");
                initMap.setVariable("key", String.valueOf(entity.id));
                initMap.setVariable("value", String.valueOf(idx));
                initMap.setVariable("idx", String.valueOf(1));
            }
            dict.addSection("FIRST_HOME_COL_HEIGHT").setVariable("value", String.valueOf(colHeights[0]));
            for (int i = 1; i < Constants.Gallery.nCols; i++) {
                dict.addSection("COL_HOME_HEIGHT").setVariable("value", String.valueOf(colHeights[i]));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private int getMinColHeightIdx(float[] colHeights) {
        int idx = 0;
        for (int i = 1; i < colHeights.length; i++) {
            if (colHeights[idx] > colHeights[i]) {
                idx = i;
            }
        }
        return idx;
    }

    private void renderTimestamp(TemplateDataDictionary dict, long timestamp) {
        String timestampStr = Utils.getTimestampStr(timestamp, Constants.TimeFormat.DATETIME_UI_FORMAT);
        dict.setVariable("timestamp", timestampStr);
    }

}
