package org.mydrive.controller;

import org.mydrive.entity.config.AppConfig;
import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.enums.FileCategoryEnum;
import org.mydrive.entity.po.FileInfo;
import org.mydrive.service.FileInfoService;
import org.mydrive.utils.StringTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class CommonFileController extends ABaseController {
    @Resource
    private AppConfig appConfig;

    @Resource
    private FileInfoService fileInfoService;

    /**
     * 获取图片预览
     *
     * @param response
     * @param imageFolder
     * @param imageName
     */
    protected void getImage(HttpServletResponse response, String imageFolder, String imageName) {
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

    /**
     * 获取视频/文档预览
     *
     * @param response
     * @param fileId
     * @param userId
     */
    protected void getFile(HttpServletResponse response, String fileId, String userId) {
        String filePath = null;

        if (fileId.endsWith(".ts")) {
            String[] tsArray = fileId.split("_");
            String realFileId = tsArray[0];
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            String fileName = fileInfo.getFilePath();
            fileName = StringTools.getFileNameNoSuffix(fileName) + "/" + fileId;
            filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileName;

        } else {
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (null == fileInfo) {
                return;
            }
            if (FileCategoryEnum.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
                // 视频文件重新设置路径
                String fileNameNoSuffix = StringTools.getFileNameNoSuffix(fileInfo.getFilePath());
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileNameNoSuffix + "/" + Constants.M3U8_NAME;
            } else {
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
            }


            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
        }

        readFile(response, filePath);
    }

}
