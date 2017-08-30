package com.drims.OAuth2;

import com.drims.OAuth2.json.serializable.models.AccessTokenContainerModel;
import com.drims.OAuth2.json.serializable.models.AuthorizationQueryModel;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class AccessTokenContainerService {
	private final static Gson gson = new Gson();
	private final static MongoClient mongoClient = new MongoClient();
	
	public void Save(AccessTokenContainerModel accessTokenContainer) {
		DB mongoDb = mongoClient.getDB("OAuth");
		DBCollection collection = mongoDb.getCollection("access_token_containers");
		
		DBObject inserted = BasicDBObject.parse((gson.toJson(accessTokenContainer)));
		collection.insert(inserted);
		return;
	}
	
	public AuthorizationQueryModel GetRequestByAccessToken(String access_token) {
		BasicDBObject queryObject = new BasicDBObject("access_token",access_token);
		
		DB mongoDb = mongoClient.getDB("OAuth");
		DBCollection collection = mongoDb.getCollection("access_token_containers");
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
		AuthorizationQueryModel authorizationQueryModel = gson.fromJson(result.toString(), AuthorizationQueryModel.class);
		return authorizationQueryModel;
	}
	
	public void DeleteRequestByAccessToken(String access_token) {
		
		DB mongoDb = mongoClient.getDB("OAuth");
		DBCollection collection = mongoDb.getCollection("access_token_containers");
		collection.remove(new BasicDBObject("access_token",access_token));
	}
}
