package com.drims.OAuth2.json.serializable.models;

public class UserModel {
	public UserModel() {}
	public UserModel(String user_key,
			String sub, 
			String prefered_user_name, 
			String name,
			String email,
			boolean email_verified) {
		
		this.user_key = user_key;
		this.sub = sub; 
		this.prefered_user_name = prefered_user_name;
		this.name = name;
		this.email = email;
		this.email_verified = email_verified;
		
	}
	
	public String user_key;
	public String sub;
	public String prefered_user_name;
	public String name;
	public String email;
	public boolean email_verified;
}
