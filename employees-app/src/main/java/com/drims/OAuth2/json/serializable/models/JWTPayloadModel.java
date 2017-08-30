package com.drims.OAuth2.json.serializable.models;

public class JWTPayloadModel {
	public String iss; //Issuer of the token
	public String sub; //Subject identifier
	public String aud; //audience identifier -> typically the client id
	public String iat; //Issuing time of the jwt
	public String exp; //Expirationtime of the jwt
	public String nonce; // OPTIONAL String value used to associate a Client session with an ID Token, and to mitigate replay attacks
}
