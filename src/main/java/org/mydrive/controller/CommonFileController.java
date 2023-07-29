package org.mydrive.controller;

import org.mydrive.entity.config.AppConfig;
import org.mydrive.entity.constants.Constants;
import org.mydrive.utils.StringTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

public class CommonFileController extends ABaseController {
    @Resource
    private AppConfig appConfig;

    public void getImage(HttpServletResponse response, String imageFolder, String imageName) {
        if (StringTools.isEmpty(imageFolder) || StringTools.isEmpty(imageName) || !StringTools.pathIsOk(imageFolder) || !StringTools.pathIsOk(imageName)) {
            return;
        }
        String imageSuffix = StringTools.getFileNameSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
        imageSuffix = imageSuffix.replace(".", "");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, filePath);

    }
}
