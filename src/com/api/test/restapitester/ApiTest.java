package com.api.test.restapitester;

import static io.restassured.RestAssured.get;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import io.restassured.http.ContentType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiTest {

    private String playbuzzItemsUrl = "http://rest-api-v2.playbuzz.com/v2/Items";
    private static String json_string = "";

    @Test
    public void a_001_makeSureSiteIsUp() {
	get(playbuzzItemsUrl)//
		.then()//
		.contentType(ContentType.JSON)//
		.and()//
		.assertThat()//
		.statusCode(200);
    }

    @Test
    public void a_002_getJsonResponse() {
	json_string = get(playbuzzItemsUrl)//
		.getBody()//
		.asString();
    }

    @Test
    public void a_010_verifyImageUrlContainsGuidAndStrings() {
	JSONArray items = getItemsObj();

	Pattern imagePattern = Pattern.compile("^" // start of string
		+ "(\\/" // first forward slash
		+ "\\/" // second forward slash
		+ "cdn" // cdn
		+ "\\." // dot
		+ "playbuzz" // playbuzz
		+ "\\." // dot
		+ "com" // com
		+ "\\/" // forward slash
		+ "cdn" // cdn
		+ "\\/" // forward slash
		+ ".*" // any characters (GUID)
		+ "\\/" // forward slash
		+ ".*" // any characters (STRING)
		+ "\\." // dot
		+ "jpg)" // jpeg
		+ "$"); // end of string

	// iterate over each item
	for (int i = 0; i < items.length(); i++) {
	    JSONObject item = items.getJSONObject(i);

	    // get keys from current item
	    Iterator<String> keys = item.keys();

	    // iterate over each key
	    while (keys.hasNext()) {
		String currentKey = (String) keys.next();

		// verify value pattern for key "imageXXX"
		if (currentKey.startsWith("image")) {

		    // get value for the key "imageXXX"
		    String imageUri = item.getString(currentKey);

		    Assert.assertTrue(imagePattern.matcher(imageUri).matches());
		}

	    }
	}
    }

    @Test
    public void b_001_verifyTagContainsNameAndWeightProperty() {
	JSONArray items = getItemsObj();

	// iterate over each item
	for (int i = 0; i < items.length(); i++) {

	    // get all tags from current item
	    JSONArray tags = items.getJSONObject(i).getJSONArray("tags");

	    // iterate over each tag
	    for (int j = 0; j < tags.length(); j++) {

		// get current tag
		JSONObject tag = (JSONObject) tags.get(j);
		// get keys from current tag
		Iterator<String> tagKeys = tag.keys();

		boolean hasNameKey = false;
		boolean hasWeightKey = false;

		// iterate over each key
		while (tagKeys.hasNext()) {
		    String currentKey = (String) tagKeys.next();

		    if (currentKey.equals("name")) {
			hasNameKey = true;
		    }

		    if (currentKey.equals("weight")) {
			hasWeightKey = true;
		    }
		}

		// verify if tag has "name" and "weight" keys
		if (!hasNameKey || !hasWeightKey) {

		    Assert.fail("Not found name-weight key pair in tag");
		}

	    }
	}
    }

    @Test
    public void c_001_verifyWeightsAreBetween0And1() {

	JSONArray items = getItemsObj();

	// iterate over each item
	for (int i = 0; i < items.length(); i++) {

	    // get all tags from current item
	    JSONArray tags = items.getJSONObject(i).getJSONArray("tags");

	    // iterate over each tag
	    for (int j = 0; j < tags.length(); j++) {
		// get current object from tag
		JSONObject tag = (JSONObject) tags.get(j);
		// get "weight" value
		String weightString = tag.getString("weight");
		// convert the value to Float
		Float weight = Float.parseFloat(weightString);

		// check if the value is in 0...1 range
		Assert.assertTrue(weight > 0.0);
		Assert.assertTrue(weight <= 1.0);
	    }
	}
    }

    @Test
    public void c_010_verifyIdsAreNotEmpty() {
	ReadContext ctx = JsonPath.parse(json_string);
	// get all ids
	List<String> ids = ctx.read("$..id");
	// verify the id is not empty string
	for (String id : ids) {
	    Assert.assertFalse(id.isEmpty());
	}
    }

    private JSONArray getItemsObj() {
	JSONArray items = new JSONObject(json_string)//
		.getJSONObject("payload")//
		.getJSONArray("items");
	return items;
    }
}
