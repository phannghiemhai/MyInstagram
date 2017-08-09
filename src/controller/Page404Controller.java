/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import utils.Configuration;
import utils.Constants;
import utils.RenderUtils;
import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;
import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;

/**
 *
 * @author haipn
 */
public class Page404Controller extends AbstractController {

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        ResponseOutput responseOutput = responseType.createResponseOutput();
        String data = "";
        try {
            TemplateDataDictionary dict = TemplateDictionary.create();
            dict.setVariable("STATIC_URL", Configuration.URL.STATIC_URL);
            RenderUtils.renderNavigationBar(request, dict);
            RenderUtils.renderFooter(request, dict, null);
            dict.setVariable("msg", Constants.PageMsg.page404);
            Template template = RenderUtils.getTemplate("page404", "content");
            data = RenderUtils.renderMainPageDetail(request, "", template.renderToString(dict));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        responseOutput.setRespond(data);
        return responseOutput;
    }

}
