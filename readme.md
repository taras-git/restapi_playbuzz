Backend API testing


The project is done using Java with RestAssured and JsonPath libraries.
To run the test open the project in Eclipse and right-click on src/com/api/test/restapitester/ApiTest.java file,
choose "Run as" -> "JUnit test".

After test passed verify there are no failed tests.


Tests are run in alphabetic order.


Tests list:

- a_001_makeSureSiteIsUp: verify the API link is correct (response 200 is received),
and the response format is JSON.

- a_002_getJsonResponse: not a test in strict meaning, populates "json_string" for next tests.

- a_010_verifyImageUrlContainsGuidAndStrings: verifies that the property, which starts with
"Image" (i.e. "ImageLarge") are in from //cdn.playbuzz.com/cdn/ GUID / STRING .jpg.

- b_001_verifyTagContainsNameAndWeightProperty: verifies that all the objects in the
"tags" property, should have "name" and "weight" properties.

- c_001_verifyWeightsAreBetween0And1: verifies that value of "weight" properties are
in range 0...1 (0.0 < weight <= 1.0).

- c_010_verifyIdsAreNotEmpty: verifies that there are no empty "id" properties.