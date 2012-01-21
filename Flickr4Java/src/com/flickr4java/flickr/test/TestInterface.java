/*
 * Copyright (c) 2005 Aetrion LLC.
 */
package com.flickr4java.flickr.test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.Response;
import com.flickr4java.flickr.Transport;
import com.flickr4java.flickr.people.User;

/**
 * Interface for testing Flickr connectivity.
 *
 * @author Matt Ray
 */
public class TestInterface {

    public static final String METHOD_ECHO = "flickr.test.echo";
    public static final String METHOD_LOGIN = "flickr.test.login";
    public static final String METHOD_NULL = "flickr.test.null";

    private String apiKey;
    private String sharedSecret;
    private Transport transport;

    public TestInterface(
        String apiKey,
        String sharedSecret,
        Transport transportAPI
    ) {
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        this.transport = transportAPI;
    }

    /**
     * A testing method which echo's all paramaters back in the response.
     *
     * @param params The parameters
     * @return The Collection of echoed elements
     * @throws IOException
     * @throws SAXException
     * @throws FlickrException
     */
    public Collection echo(Map params) throws IOException, SAXException, FlickrException {
    	Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("method", METHOD_ECHO);
        parameters.put("api_key", apiKey);
        parameters.putAll(params);

        Response response = transport.post(transport.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }
        return response.getPayloadCollection();
    }

    /**
     * A testing method which checks if the caller is logged in then returns a User object.
     *
     * @return The User object
     * @throws IOException
     * @throws SAXException
     * @throws FlickrException
     */
    public User login() throws IOException, SAXException, FlickrException {
    	Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("method", METHOD_LOGIN);
        parameters.put("api_key", apiKey);


        Response response = transport.post(transport.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }
        Element userElement = response.getPayload();
        User user = new User();
        user.setId(userElement.getAttribute("id"));

        Element usernameElement = (Element) userElement.getElementsByTagName("username").item(0);
        user.setUsername(((Text) usernameElement.getFirstChild()).getData());

        return user;
    }
    
    /**
     * Null test.
     * This method requires authentication with 'read' permission.
     * @throws SAXException 
     * @throws IOException 
     * @throws FlickrException 
     */
    public void null_() throws IOException, SAXException, FlickrException {
    	Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("method", METHOD_NULL);
        parameters.put("api_key", apiKey);


        Response response = transport.get(transport.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }
    }

}
