package pl.tomkal.ngworks.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import pl.tomkal.ngworks.dao.FileStorageDao;

@RestController
@RequestMapping(name="/resource")
public class FileUploadController {
	
	private static Logger LOG = LoggerFactory.getLogger(FileUploadController.class);
	
	@Autowired
	private FileStorageDao fileStorage;
	
	@RequestMapping("resource/home")
	public Map<String,Object> home() {
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("id", UUID.randomUUID().toString());
		model.put("content", "Hello World");
		return model;
	}
	@RequestMapping(value="resource/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }
	
	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleMongoStoreException(IOException e) {
		LOG.error("MongoFS store exception with message: " + e.getMessage());
		return e.getMessage();
	} 
	
	@RequestMapping(value="resource/upload", method=RequestMethod.POST)
	public @ResponseBody String uploadFile(@RequestParam(value="file", required=true) MultipartFile file ) throws IOException{
		LOG.info("About to upload a file with name: " +  file.getOriginalFilename());
		
		DBObject metaData = new BasicDBObject();
		metaData.put("fileName", file.getOriginalFilename());
		metaData.put("uploadDate", getCurrentDate());
		String id = fileStorage.store(file.getInputStream(), file.getOriginalFilename(),"image/jpeg", metaData);
		LOG.info("Uploaded file with name: " +  file.getOriginalFilename());
		return id;
	}
	public FileStorageDao getFileStorage() {
		return fileStorage;
	}
	public void setFileStorage(FileStorageDao fileStorage) {
		this.fileStorage = fileStorage;
	}
	
	private static String getCurrentDate() {
		DateFormat df = new SimpleDateFormat("dd-mm-yy HH:mm:ss");
		Date today = Calendar.getInstance().getTime();
		return df.format(today);
		
	}
	
	

}
