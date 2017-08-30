package com.drims.OAuth2;

import com.drims.OAuth2.json.serializable.models.UserModel;

public class UserService {
	private UserModel[] userArray = new UserModel[] { 
			new UserModel("alice","9XE3-JI34-00132A","alice","Alice","alice.wonderland@example.com",true),
			new UserModel("bob","1ZT5-OE63-57383B","bob","Bob","bob.loblob@example.net",false),
		};
	
	public UserModel getUser(String user_key)
	{
		for(UserModel currentUser : userArray)
		{
			if(currentUser.user_key.equals(user_key)) {
				return currentUser;
			}
		}
		return null;
	}
}
