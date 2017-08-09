/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import hapax.TemplateException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;
/**
 *
 * @author hoavtn
 */
public class UploadFTPController extends AbstractController {

    protected ResponseOutput handleRequest(RequestInfo request) throws ServletException, IOException, JSONException, TemplateException {
        ResponseOutput output = new ResponseOutput(ResponseType.HTML_TYPE);

        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request.getRequest());
            // process only if its multipart content
            if (isMultipart) {
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                String pathFile = "";
                String itemName = "";
                String typeImage = "";
                File fi = null;
                try {
                    List<FileItem> multiparts = upload.parseRequest(request.getRequest());

                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            itemName = item.getName();
                            pathFile = "./static/upload" + File.separator + itemName;
                            item.write(new File(pathFile));
                        }

                        if (item.getFieldName().equals("typeImage")) {
                            typeImage = item.getString();
                        }
                    }

                    if (StringUtils.isNotBlank(pathFile)) {
                        fi = new File(pathFile);
                        if (fi.exists()) {
                            output.setRespond("<script>top.uploadFTP.setLink('./upload/" + fi.getName() + "');top.uploadFTP.setErrorMsg('Delay 5 minus to upload Success');</script>");
                        } else {
                            output.setRespond("<script>top.uploadFTP.setErrorMsg('Fail')</script>");
                        }
                    } else {
                    }
                } catch (Exception e) {
                    System.err.println(e);
                } finally {
                    if (fi != null && fi.exists()) {
                        fi.delete();
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

        return output;
    }

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
