package com.drims.OAuth2.json.serializable.models;

import java.util.Arrays;
import java.util.List;

public class Client {
	private String _clientId;
	private String _clientSecret;
	private List<String> _redirectUris;
	private String _scope;
	
	public Client() {}
	
	public Client(String clientId,String clientSecret, String[] redirectUris,String scope) {
		_clientId = clientId;
		_clientSecret = clientSecret;
		_redirectUris = Arrays.asList(redirectUris);
		_scope = scope;
	}
	
	public String get_clientId() {
		return _clientId;
	}
	public void set_clientId(String _clientId) {
		this._clientId = _clientId;
	}
	public String get_clientSecret() {
		return _clientSecret;
	}
	public void set_clientSecret(String _clientSecret) {
		this._clientSecret = _clientSecret;
	}
	public List<String> get_redirectUris() {
		return _redirectUris;
	}
	public void set_redirectUris(List<String> _redirectUris) {
		this._redirectUris = _redirectUris;
	}
	public String get_scope() {
		return _scope;
	}
	public void set_scope(String _scope) {
		this._scope = _scope;
	}
}
