package pl.tomkal.ngworks.dao.impl;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

import pl.tomkal.ngworks.dao.FileStorageDao;

@Service
public class FileStorageDaoImpl implements FileStorageDao {
	
	@Autowired
	private GridFsTemplate gridFsTemplate;
	
	@Override
	public String store(InputStream inputStream, String fileName,
			String contentType, DBObject metaData) {
		return this.gridFsTemplate.store(inputStream, fileName,contentType, metaData).getId().toString();
	}

	@Override
	public GridFSDBFile retrive(String fileName) {
		
		return gridFsTemplate.findOne(new Query(Criteria.where("filename").is(fileName)));
	}

	@Override
	public GridFSDBFile getById(String id) {
		return this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
	}

	@Override
	public GridFSDBFile getByFilename(String filename) {
		return gridFsTemplate.findOne(new Query(Criteria.where("filename").is(filename)));
	}

	@Override
	public List<GridFSDBFile> findAll() {
		return gridFsTemplate.find(null);
	}

	public GridFsTemplate getGridFsTemplate() {
		return gridFsTemplate;
	}

	public void setGridFsTemplate(GridFsTemplate gridFsTemplate) {
		this.gridFsTemplate = gridFsTemplate;
	}
	
	
}
