package com.drims.OAuth2;

import com.drims.OAuth2.json.serializable.models.CodeModel;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class CodeService {
	private final static Gson gson = new Gson();
	private final static MongoClient mongoClient = new MongoClient();
	
	public void SaveCode(CodeModel codeModel) {
		DB mongoDb = mongoClient.getDB("OAuth");
		DBCollection collection = mongoDb.getCollection("codes");
		
		DBObject inserted = BasicDBObject.parse((gson.toJson(codeModel)));
		collection.insert(inserted);
		return;
	}
	
	public CodeModel GetRequestByCode(String code) {
		BasicDBObject queryObject = new BasicDBObject("code",code);
		
		DB mongoDb = mongoClient.getDB("OAuth");
		DBCollection collection = mongoDb.getCollection("codes");
		DBCursor cursor = collection.find(queryObject);
		DBObject result = null;
		try {
		   while(cursor.hasNext()) {
		       result =  cursor.next();
		   }
		} finally {
		   cursor.close();
		}
		if(result == null) return null;
		CodeModel authorizationQueryModel = gson.fromJson(result.toString(), CodeModel.class);
		return authorizationQueryModel;
	}
	
	public void DeleteRequestByRequestId(String code) {
		
		DB mongoDb = mongoClient.getDB("OAuth");
		DBCollection collection = mongoDb.getCollection("codes");
		collection.remove(new BasicDBObject("code",code));
	}
}
