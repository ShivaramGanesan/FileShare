package com.fileshare.floppy.controller;

import com.fileshare.floppy.Constants.AppConstants;
import com.fileshare.floppy.util.FileShareUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class AppController {

    @Value("${file.path.upload}")
    private String uploadFilePath;

    Logger LOGGER = Logger.getLogger(AppController.class.getName());

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file){
        try{
            if(file.getSize() > AppConstants.MAX_UPLOAD_SIZE){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size too large");
            }
            //upload to AWS S3
            String fileName = FileShareUtil.getRandomAlphaNumericString(AppConstants.FILENAME_LENGTH);
            String pathName = uploadFilePath + fileName;
            File uFile = new java.io.File(pathName);
            file.transferTo(uFile.toPath());
            return ResponseEntity.status(HttpStatus.OK).body(new JSONObject().put("message", "File Uploaded successfully").put("file_name", fileName).toString());

        }
        catch(Exception e){
            LOGGER.log(Level.SEVERE, "Error occured while uploading file :: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to upload file : " + e.getMessage());
        }
    }
}
