package com.uiname;
import com.pojos.User;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeAll;
public class UINameAPI {


    @BeforeAll
    public static void setup(){

        baseURI="https://cybertek-ui-names.herokuapp.com/api/";
    }

    //TEST CASES
    //1N.o pSaernadmasgtetsrtequest without providing any parameters
    //2. Verify status code 200, content type application/json; charset=utf-8
    //3. Verify that name, surname, gender, region fields have value


    @Test
    @DisplayName("Verifying of endpoint without parameter")
    public void noParamTest(){
        Response response = given().get();
                response. then().
                                contentType("application/json; charset=utf-8").
                          and().
                                statusCode(200).
                          and().
                                body("name",is(notNullValue())).
                                body("surname",is(notNullValue())).
                                body("gender", is(notNullValue())).
                                body("region",is(notNullValue()));
    }


    //Gender test
    //1. Create a request by providing query parameter: gender, male or female
    //2. Verify status code 200, content type application/json; charset=utf-8
    //3. Verify that value of gender field is same from step 1

    @Test
    @DisplayName("Verifying of gender")
    public void genderTest(){
        Response response = given().
                                    queryParam("gender","male").
                            when().
                                    get();
                    response.then().
                                    statusCode(200).
                             and().
                                    contentType("application/json; charset=utf-8").
                             and().
                                   body("gender",is(notNullValue()));
    }


    //2 params test
    //1. Create a request by providing query parameters: a valid region and gender
    // NOTE: Available region values are given in the documentation
    //2. Verify status code 200, content type application/json; charset=utf-8
    //3. Verify that value of gender field is same from step 1
    //4. Verify that value of region field is same from step 1

    @Test
    @DisplayName("Verifying of gender")
    public void twoParamsTest(){
        Response response = given().
                                    queryParam("gender","male").
                                    queryParam("region","Hungary").
                    when().
                            get();
                  response. then().
                                    statusCode(200).
                            and().
                                  contentType("application/json; charset=utf-8").
                            and().
                                  body("gender",is(notNullValue())).
                                  body("region",is(notNullValue()));

    }



    //Invalid gender test
    //1. Create a request by providing query parameter: invalid gender
    //2. Verify status code 400 and status line contains Bad Request
    //3. Verify that value of error field is Invalid gender

    @Test
    @DisplayName("Verifying of gender with invalid parameter")
    public void invalidGenderTest(){
        Response response = given().
                                   queryParam("gender","hello").
                            when().
                                   get();
                  response. then().
                                  statusCode(400).
                            and().
                                  statusLine(containsString("Bad Request")).
                            and().
                                  body("error",is("Invalid gender"));


    }

    //Invalid region test
    //1. Create a request by providing query parameter: invalid region
    //2. Verify status code 400 and status line contains Bad Request
    //3. Verify that value of error field is Region or language not found

    @Test
    @DisplayName("Verifying of region with invalid parameter")
    public void invalidRegionTest(){
        Response response = given().
                                    queryParam("region","hello").
                            when().
                                    get().prettyPeek();
                    response. then().
                                    statusCode(400).
                                    and().
                                    body("error",is("Region or language not found"));

        assertTrue(response.statusLine().contains("Bad Request"));

    }


    //Amount and regions test
    //1. Create request by providing query parameters: a valid region and amount (must be bigger than 1)
    //2. Verify status code 200, content type application/json; charset=utf-8
    //3. Verify that all objects have different name+surname combination

    @Test
    @DisplayName("Verifying of region with invalid parameter")
    public void amountAndRegionTest(){
        Response response = given().
                                   queryParam("region","United States").
                                   queryParam("amount",5).
                            when().
                                   get();

        List<User> usersList =  response.jsonPath().getList("",User.class);
        //System.out.println("usersList = " + usersList);
        //List<Map<String,String >> usersMap = response.jsonPath().getList("");
        //System.out.println("usersMap = " + usersMap);

        Set<String> uniqueNames = new LinkedHashSet<>();
        List<String> originalNames = new ArrayList<>();

        for (int i = 0; i < usersList.size() ; i++) {
            String fullName = usersList.get(i).getName() +" " + usersList.get(i).getSurname();
            uniqueNames.add(fullName);
            originalNames.add(fullName);
        }

                    response. then().
                                    statusCode(200).
                                    header("Content-Type","application/json; charset=utf-8").
                              and().
                                    body("size()",is(uniqueNames.size()));

                    assertTrue(uniqueNames.size()==originalNames.size());





    }




    //3 params test
    //1. Create a request by providing query parameters: a valid region, gender and amount (must be bigger than 1)
    //2. Verify status code 200, content type application/json; charset=utf-8
    //3. Verify that all objects the response have the same region and gender passed in step 1

    @Test
    @DisplayName("Verifying of region with invalid parameter")
    public void threeParamsTest() {
        Response response = given().
                                    queryParam("region", "United States").
                                    queryParam("gender","male").
                                    queryParam("amount", 5).
                            when().
                                    get();
                    response.then().
                                    statusCode(200).
                                    contentType("application/json; charset=utf-8").
                              and().
                                    body("size()",is(5)).
                                    body("gender",everyItem(is("male"))).
                                    body("region",everyItem(is("United States")));
        // 2. way
        List<String> usersRegion = response.jsonPath().getList("region");
        //System.out.println("usersRegions = " + usersRegion);
        List<String> usersGender = response.jsonPath().getList("gender");
        //System.out.println("usersGender = " + usersGender);

        Set<String> usersRegionSet = new HashSet<>();
        usersRegionSet.addAll(usersRegion);
        //System.out.println("usersRegionSet = " + usersRegionSet);
        Set<String> usersGenderSet = new HashSet<>();
        usersGenderSet.addAll(usersGender);
        //System.out.println("usersGenderSet = " + usersGenderSet);

            assertTrue(usersRegionSet.size()==1);
            assertTrue(usersGenderSet.size()==1);


    }

    //      3 params test
//1. Create a request by providing query parameters:
// a valid region, gender and amount (must be bigger than 1)
//2. Verify status code 200, content type application/json; charset=utf-8
//3. Verify that all objects the response have the same region and gender passed in step 1

    //nextInt () method from Random class will create numbers between 0-499
    //That's why we add + 1
    //in this case my random number will be between 1-500
    int randomAmount = new Random().nextInt(500) + 1;
    List<String> genders = Arrays.asList("male","female");//We will pick random gender for each execution
    public String generateRandomGender() {
        Collections.shuffle(genders);
        return genders.get(0);
    }
    //getProperty("user.dir") will provide project path: C:\Users\1\Desktop\bugbusters\APIHomeworks
    File namesJson = new File(System.getProperty("user.dir") + File.separator + "names.json");
    JsonPath jsonPath = new JsonPath(namesJson);
    List<String>regions = jsonPath.getList("region");
    public String generateRandomRegion() {
        Collections.shuffle(regions);
        return regions.get(0);
    }
    @Test
    @DisplayName("3 params test")
    public void threeParamsTest2() {
        String randomGender = generateRandomGender();
        String randomRegion = generateRandomRegion();
        System.out.println("randomRegion = " + randomRegion);
        System.out.println("randomAmount = " + randomAmount);
        System.out.println("randomGender = " + randomGender);
        Response response =
                given().
                        queryParams("region",randomRegion).
                        queryParams("gender",randomGender).
                        queryParams("amount",randomAmount).
                        when().
                        get().prettyPeek();
        response.then().
                assertThat().
                statusCode(200).
                and().
                contentType("application/json; charset=utf-8").
                and().
                body("size()",is(randomAmount)).
                body("gender",everyItem(is(randomGender))).
                body("region",everyItem(is(randomRegion)));
    }





    //Amount count test
    //1. Create a request by providing query parameter: amount (must be bigger than 1)
    //2. Verify status code 200, content type application/json; charset=utf-8
    //3. Verify that number of objects returned in the response is same as the amount passed in step 1

    @Test
    @DisplayName("Verifying of region with invalid parameter")
    public void amountCountTest() {

        given().
                queryParam("amount", randomAmount).
        when().
                get().
        then().
                statusCode(200).
                contentType("application/json; charset=utf-8").
        and().
                body("size()",is(randomAmount));




    }



}
