package com.drims.OAuth2;

import com.drims.OAuth2.json.serializable.models.AuthorizationQueryModel;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class RequestService {
	private final static Gson gson = new Gson();
	
	public void SaveRequest(AuthorizationQueryModel query,String reqId) {
		MongoClient mongoClient = new MongoClient();
		DB mongoDb = mongoClient.getDB("OAuth");
		DBCollection collection = mongoDb.getCollection("requests");
		BasicDBObject authorizationQueryObject =  BasicDBObject.parse((gson.toJson(query))); 
		BasicDBObject inserted = new BasicDBObject("request_id",reqId).append("query",authorizationQueryObject);
		collection.insert(inserted);
		return;
	}
	
	public AuthorizationQueryModel GetRequestByRequestId(String requestId) {
		BasicDBObject queryObject = new BasicDBObject("request_id",requestId);
		
		MongoClient mongoClient = new MongoClient();
		DB mongoDb = mongoClient.getDB("OAuth");
		DBCollection collection = mongoDb.getCollection("requests");
		DBCursor cursor = collection.find(queryObject);
		DBObject result = null;
		try {
		   while(cursor.hasNext()) {
		       result = cursor.next();
		   }
		} finally {
		   cursor.close();
		}
		if(result == null) return null;
		DBObject authorizationQueryObject =  (DBObject)result.get("query");
		return gson.fromJson(authorizationQueryObject.toString(), AuthorizationQueryModel.class);
	}
	
	public void DeleteRequestByRequestId(String requestId) {
		MongoClient mongoClient = new MongoClient();
		DB mongoDb = mongoClient.getDB("OAuth");
		DBCollection collection = mongoDb.getCollection("requests");
		collection.remove(new BasicDBObject("request_id",requestId));
	}
	
}
