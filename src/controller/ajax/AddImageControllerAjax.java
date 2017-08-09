/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ajax;

import controller.AbstractController;
import entity.DAO.ImageEntity;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import utils.Constants;
import utils.http.JSONResponse;
import utils.http.RequestInfo;
import utils.http.ResponseOutput;
import utils.http.ResponseType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.imageio.ImageIO;
import modal.ModalImage;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author haipn
 */
public class AddImageControllerAjax extends AbstractController {

    @Override
    protected ResponseOutput handleRequest(RequestInfo request, ResponseType responseType) {
        String data = "\"\"";
        try {
            int type = request.getParamInt("type");
            if (request.isLoggedIn()) {
                switch (type) {
                    case 0:
                        String imageLink = request.getParamStr("imageLink");
                        if (imageLink != null) {
                            ImageEntity entity = new ImageEntity(0, request.account.email,
                                    imageLink, type, "", "", System.currentTimeMillis(), 0, 0, 0);
                            data = processAddImage(request, entity);
                        }
                        break;
                    case 1:
                        data = processUploadImage(request);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        ResponseOutput responseOutput = new JSONResponse(Constants.ErrorCode.SUCCESS);
        responseOutput.setRespond(data);
        return responseOutput;
    }

    private String processAddImage(RequestInfo request, ImageEntity entity) throws UnsupportedEncodingException {
        entity.updateSize();
        int id = ModalImage.getInstance().insert(entity);
        if (id > 0) {
            return "\"" + URLEncoder.encode(entity.src, "UTF-8") + "\"";
        }
        return "\"\"";
    }

    private String processUploadImage(RequestInfo request) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request.getRequest());
        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                List<FileItem> multiparts = upload.parseRequest(request.getRequest());

                for (FileItem item : multiparts) {
                    if (!item.isFormField()) {
//                        return processParseFile(item.getString(), info, awards);
                        BufferedImage image = ImageIO.read(item.getInputStream());
                        if (image != null && image.getHeight() >= 0 && image.getWidth() >= 0) {
                            String filename = getFilename(request, item.getContentType());
                            try (OutputStream os = new FileOutputStream(filename)) {
                                os.write(item.get());
                                os.close();
                                ImageEntity entity = new ImageEntity(0, request.account.email,
                                        filename, 1, "", "", System.currentTimeMillis(),
                                        image.getWidth(), image.getHeight(),
                                        1f * image.getHeight() / image.getWidth());
                                ModalImage.getInstance().insert(entity);
                                request.sendRedirect(Constants.AccessOption.INDEX.mainTarget);
                                return "{\"err\" : 0, \"msg\" : \"Success\"}";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return "{\"err\" : 1, \"msg\" : \"File is not a picture\"}";
    }

    private String getFilename(RequestInfo request, String type) {
        return String.format("./static/upload/%s_%s.%s", request.account.email,
                System.currentTimeMillis(), type.replace("image/", ""));
    }
}
