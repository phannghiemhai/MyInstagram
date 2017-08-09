/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import auth.Auth;
import utils.Configuration;
import utils.Constants.AccessOption;
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
public class LogInController extends AbstractController {

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        ResponseOutput responseOutput = responseType.createResponseOutput();
        String data = "";
        try {
            if (Auth.getInstance().setSessionWithPassPort(request)) {
                String url = request.getParamStr("url");
                if (url == null || url.isEmpty() || url.equals(AccessOption.LOGIN.mainTarget)) {
                    url = AccessOption.INDEX.mainTarget;
                }
                request.sendRedirect(url);
                return responseOutput;
            }
                        
            TemplateDataDictionary dict = TemplateDictionary.create();
            dict.setVariable("STATIC_URL", Configuration.URL.STATIC_URL);
            RenderUtils.renderNavigationBar(request, dict);
            RenderUtils.renderFooter(request, dict, null);
            Template template = RenderUtils.getTemplate("page_log_in", "content");
            dict.setVariable("clientId", Configuration.Auth.ClientId);
            dict.setVariable("apiId", Configuration.Auth.ApiKey);
            data = RenderUtils.renderMainPageDetail(request, "", template.renderToString(dict));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        responseOutput.setRespond(data);
        return responseOutput;
    }
}
