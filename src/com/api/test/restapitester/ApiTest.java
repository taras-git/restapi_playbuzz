package com.api.test.restapitester;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import io.restassured.http.ContentType;

public class ApiTest {

	String purgomalumServiceUrl = "http://www.purgomalum.com/service";
	String jsonRequestUrl = purgomalumServiceUrl + "/json?text=";
	String profanityRequestUrl = purgomalumServiceUrl + "/containsprofanity?text=";

	String textInput = "example of text input";
	String textRequest = jsonRequestUrl + textInput;
	String textResponse = textInput;
	String asterixFilledRequest = "&add=input&fill_char=*";
	String asterixFilledResponse = textInput.replace("input", "*****");

	String filteredWords = "this,is,example,some,new,filtered,word,text";

	@Test
	public void isSiteUp() {
		get(textRequest)//
				.then()//
				.contentType(ContentType.JSON)//
				.and()//
				.assertThat()//
				.statusCode(200);
	}

	@Test
	public void jsonTextRequest() {
		get(textRequest)//
				.then()//
				.body("result", equalTo(textResponse));
	}

	@Test
	public void textResponseWithAsterixFilteredWords() {
		get(textRequest + asterixFilledRequest)//
				.then()//
				.body("result", equalTo(asterixFilledResponse));
	}

	@Test
	public void textResponseWithMinusFilteredWords() {
		String response = replaceWordsWithMinus();

		get(textRequest + "&add=" + filteredWords + "&fill_char=-")//
				.then()//
				.body("result", equalTo(response));
	}

	@Test
	public void containsProfanityWord() {
		get(profanityRequestUrl + "shit")//
				.then()//
				.contentType(ContentType.TEXT)//
				.and()//
				.assertThat()//
				.body(equalTo("true"));
	}

	@Test
	public void notContainsProfanityWord() {
		get(profanityRequestUrl + "shot")//
				.then()//
				.contentType(ContentType.TEXT)//
				.and()//
				.assertThat()//
				.body(equalTo("false"));
	}

	@Test
	public void containsObscureProfanityWord() {
		get(profanityRequestUrl + "b@st@rd")//
				.then()//
				.contentType(ContentType.TEXT)//
				.and()//
				.assertThat()//
				.body(equalTo("true"));
	}

	private String replaceWordsWithMinus() {
		String response = textResponse;
		for (String word : filteredWords.split(",")) {
			String filteredWord = "";
			for (int i = 0; i < word.length(); i++) {
				filteredWord = filteredWord + "-";
			}
			response = response.replace(word, filteredWord);
		}
		return response;
	}

}
