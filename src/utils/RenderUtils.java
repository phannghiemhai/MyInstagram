/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entity.DAO.ImageEntity;
import utils.http.RequestInfo;
import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;
import hapax.TemplateResourceLoader;
import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author haipn
 */
public class RenderUtils {

    public static String renderMainPageDetail(RequestInfo request, String controller, String innerContent) {
        String content = "";
        try {
            TemplateDictionary dict = TemplateDictionary.create();
            dict.setVariable(controller, "active");
            dict.setVariable("STATIC_URL", Configuration.URL.STATIC_URL);
            dict.setVariable("contents", innerContent);
            dict.setVariable("time_current", System.currentTimeMillis() + "");
            Template template = getTemplate("homepage", "template");
            content = template.renderToString(dict);
        } catch (Exception ex) {
        }
        return content;
    }

    public static Template getTemplate(String templateName, String folder) throws TemplateException {
        String apppath = System.getProperty("apppath");
        if (!apppath.endsWith("/") && !apppath.endsWith("\\")) {
            apppath += File.separator;
        }
        if (!folder.startsWith("views")) {
            folder = "views/" + folder;
        }
        if (!folder.endsWith("/") && !folder.endsWith("\\")) {
            folder += "/";
        }
        TemplateLoader templateLoader = TemplateResourceLoader.create(apppath + folder, true);
        Template template = templateLoader.getTemplate(templateName);
        return template;
    }

    public static void renderGalleryByCols(RequestInfo requestInfo,
            List<TemplateDataDictionary> colDicts,
            Collection<ImageEntity> entities) {

        if (entities == null || entities.isEmpty() || colDicts == null || colDicts.isEmpty()) {
            return;
        }

        float[] colHeights = new float[colDicts.size()];
        for (int i = 0; i < colHeights.length; i++) {
            colHeights[i] = 0;
        }

        if (requestInfo.isLoggedIn()) {
            colHeights[0] = 1;
        }

        for (ImageEntity entity : entities) {
            int idx = getMinIdx(colHeights);
            TemplateDataDictionary item = colDicts.get(idx).addSection("COL_IMG");
            item.setVariable("src", entity.src);
            item.setVariable("width", String.valueOf(entity.width));
            item.setVariable("height", String.valueOf(entity.height));
            colHeights[idx] += 1.0 / entity.ratio;
        }
    }

    private static int getMinIdx(float[] l) {
        int idx = 0;
        for (int i = 1; i < l.length; i++) {
            if (l[i] < l[idx]) {
                idx = i;
            }
        }
        return idx;
    }

    public static void renderNavigationBar(RequestInfo request, TemplateDataDictionary dict) {
        dict.showSection("navigation_bar");
        if (request.isLoggedIn()) {
            dict.setVariable("email", request.account.email.replace("@", "\n@"));
            dict.showSection("LOG_OUT");
        } else {
            dict.setVariable("email", "GUEST");
            dict.showSection("LOG_IN");
        }
    }

    public static void renderFooter(RequestInfo request, TemplateDataDictionary dict, String[] clockIds) {
        dict.showSection("footer");
        if (clockIds != null) {
            for (String clockId : clockIds) {
                TemplateDataDictionary itemSection = dict.addSection("CLOCK");
                itemSection.setVariable("name", clockId);
            }
        }
    }

    public static void renderModalAddImageByLink(RequestInfo request, TemplateDataDictionary dict) {
        dict.showSection("modal_add_image_by_link");
    }

    public static void renderModalAddImageByUpload(RequestInfo request, TemplateDataDictionary dict) {
        dict.showSection("modal_add_image_by_upload");
    }
}
