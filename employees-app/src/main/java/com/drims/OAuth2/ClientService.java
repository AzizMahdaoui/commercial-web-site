package com.drims.OAuth2;

import com.drims.OAuth2.json.serializable.models.Client;

public class ClientService {
	private Client[] clientArray = new Client[] { 
			new Client(
					"fft_shopping",
					"o-auth-client-secret", 
					new String[] {"http:localhost:8080/callback"},
					"openid profile email phone address")
		};
	
	
	public Client getClient(String clientId)
	{
		for(Client currentClient : clientArray)
		{
			if(currentClient.get_clientId().equals(clientId)) {
				return currentClient;
			}
		}
		return null;
	}
}
