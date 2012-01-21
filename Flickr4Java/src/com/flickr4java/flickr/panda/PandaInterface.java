package com.flickr4java.flickr.panda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.Response;
import com.flickr4java.flickr.Transport;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotoUtils;
import com.flickr4java.flickr.util.StringUtilities;
import com.flickr4java.flickr.util.XMLUtilities;

/**
 * Flickr Panda.
 *
 * @author mago
 * @version $Id: PandaInterface.java,v 1.3 2009/07/11 20:30:27 x-mago Exp $
 * @see <a href="http://www.flickr.com/explore/panda">Flickr Panda</a>
 */
public class PandaInterface {
    private static final String METHOD_GET_PHOTOS = "flickr.panda.getPhotos";
    private static final String METHOD_GET_LIST = "flickr.panda.getList";

    private String apiKey;
    private String sharedSecret;
    private Transport transportAPI;

    public PandaInterface(
        String apiKey,
        String sharedSecret,
        Transport transportAPI
    ) {
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        this.transportAPI = transportAPI;
    }

    /**
     * Return a list of Flickr pandas, from whom you can request photos using
     * the {@link com.flickr4java.flickr.test.panda.PandaInterface#getPhotos(Panda, Set, int, int)}
     * API method.
     *
     * This method does not require authentication.
     *
     * @return A list of pandas
     * @throws FlickrException
     * @throws IOException
     * @throws SAXException
     */
    public ArrayList getList() throws FlickrException, IOException, SAXException {
        ArrayList pandas = new ArrayList();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("method", METHOD_GET_LIST);
        parameters.put("api_key", apiKey);

        Response response = transportAPI.get(transportAPI.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }
        Element pandaElement = response.getPayload();
        NodeList pandaNodes = pandaElement.getElementsByTagName("panda");
        for (int i = 0; i < pandaNodes.getLength(); i++) {
            pandaElement = (Element) pandaNodes.item(i);
            Panda panda = new Panda();
            panda.setName(XMLUtilities.getValue(pandaElement));
            pandas.add(panda);
        }
        return pandas;
    }

    /**
     * Ask the Flickr Pandas for a list of recent public (and "safe") photos.
     *
     * This method does not require authentication.
     *
     * @param panda The panda to ask for photos from.
     * @param extras A set of Strings controlling the extra information to fetch for each returned record. {@link com.flickr4java.flickr.test.photos.Extras#ALL_EXTRAS}
     * @param perPage The number of photos to show per page
     * @param page The page offset
     * @return A PhotoList
     * @throws FlickrException
     * @throws IOException
     * @throws SAXException
     * @see com.flickr4java.flickr.test.photos.Extras
     */
    public PhotoList getPhotos(Panda panda, Set extras, int perPage, int page) throws FlickrException, IOException, SAXException {
        ArrayList pandas = new ArrayList();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("method", METHOD_GET_PHOTOS);
        parameters.put("api_key", apiKey);

        parameters.put("panda_name", panda.getName());

        if (extras != null && !extras.isEmpty()) {
            parameters.put("extras", StringUtilities.join(extras, ","));
        }
        if (perPage > 0) {
            parameters.put("per_page", Integer.toString(perPage));
        }
        if (page > 0) {
            parameters.put("page", Integer.toString(page));
        }

        Response response = transportAPI.get(transportAPI.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }
        Element photosElement = response.getPayload();
        PhotoList photos = PhotoUtils.createPhotoList(photosElement);
        return photos;
    }
}
