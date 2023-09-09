import com.sun.source.tree.AssertTree;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;



public class ViewAllBugsTests {

    private String PHPSESSID;
    private String MANTIS_secure_session;
    private String MANTIS_STRING_COOKIE;
    private Map<String, String> cookies = new HashMap<>();

    @BeforeEach
    public void getCookies() {
        Response responseLogin = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .body("return=%2Fmantisbt%2Fview_all_bug_page.php&username=admin&password=admin20&secure_session=on")
                .when()
                .post("https://academ-it.ru/mantisbt/login.php")
                .andReturn();

        PHPSESSID = responseLogin.cookie("PHPSESSID");
        System.out.println("PHPSESSID = " + PHPSESSID);

        MANTIS_secure_session = responseLogin.cookie("MANTIS_secure_session");
        System.out.println("MANTIS_secure_session = " + MANTIS_secure_session);

        MANTIS_STRING_COOKIE = responseLogin.cookie("MANTIS_STRING_COOKIE");
        System.out.println("MANTIS_STRING_COOKIE = " + MANTIS_STRING_COOKIE);

        cookies.put("PHPSESSID", PHPSESSID);
        cookies.put("MANTIS_secure_session", MANTIS_secure_session);
        cookies.put("MANTIS_STRING_COOKIE", MANTIS_STRING_COOKIE);
    }

    @Test
    public void getViewAllBugsTest() {
        Response response = RestAssured
                .given()
                .cookies(cookies)
                .get("https://academ-it.ru/mantisbt/view_all_bug_page.php")
                .andReturn();

        System.out.println("\nResponse:");
        response.prettyPrint();

        Assertions.assertEquals(200, response.statusCode(), "Response status code is not as expected");
        Assertions.assertTrue(response.body().asString().contains("Viewing Issues"));

    }

    @Test
    public void UpdateBugStatusTest() {
        String bugId = "0031306";
        Response responseUpdateBug = RestAssured
                .given()
                .contentType("text/html; charset=utf-8\n" +
                        "\n")
                .cookies(cookies)
                .body("bug_id = " + bugId + "&last_updated=1694092219&category_id=1&view_state=10&handler_id=0&priority=30&severity=50&reproducibility=70&status=20&resolution=10&platform=&os=&os_build=&summary=Status&description=Status&steps_to_reproduce=&additional_information=&bugnote_text=")
                .post("\n" +
                        "https://academ-it.ru/mantisbt/bug_update.php")
                .andReturn();

        System.out.println("\nResponse: ");
        responseUpdateBug.prettyPrint();
        Assertions.assertEquals(200, responseUpdateBug.getStatusCode(), "Response status code is not expected");

        Response responseViewBug = RestAssured
                .given()
                .cookies(cookies)
                .get("https://academ-it.ru/mantisbt/view.php?id=" + bugId)
                .andReturn();

        System.out.println("\nResponse: ");
        responseUpdateBug.prettyPrint();
        Assertions.assertEquals(200, responseViewBug.getStatusCode(), "Response status code is not expected");
        org.junit.jupiter.api.Assertions.assertTrue(responseViewBug
                .body().asString()
                .contains("bug-status"));


    }



}
