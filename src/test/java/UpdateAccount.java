import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UpdateAccount {
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
    public void GetUserPage() {

        Response responseGetUserPage = RestAssured
                .given()
                .contentType("text/html; charset=utf-8\n" + "\n")
                .cookies(cookies)
                .get("\n" + "https://academ-it.ru/mantisbt/account_page.php")
                .andReturn();

        System.out.println("\nResponse: ");
        System.out.println("\nStatusCode: " + responseGetUserPage.getStatusCode());
        responseGetUserPage.prettyPrint();


        Assertions.assertEquals(200, responseGetUserPage.getStatusCode());
        org.junit.jupiter.api.Assertions.assertTrue(responseGetUserPage
                .body().asString()
                .contains("\t<div class=\"widget-header widget-header-small\">\n" +
                        "\t\t<h4 class=\"widget-title lighter\">\n" +
                        "\t\t\t<i class=\"ace-icon fa fa-user\"></i>\n" +
                        "\t\t\tEdit Account\t\t</h4>\n" +
                        "\t</div>"));


    }
    @Test
    public void UpdateNameUser() throws InterruptedException {
        Response responseUpdateName = RestAssured
                .given()
                .contentType("text/html; charset=utf-8\n" +
                        "\n")
                .cookies(cookies)
                .body("password_current=&password=&password_confirm=&email=rov55an3014%40mail.ru&realname=new_real_name1")
                .post("https://academ-it.ru/mantisbt/account_update.php")
                .andReturn();

        System.out.println("\nResponse: ");
        System.out.println("\nStatusCode: " + responseUpdateName.getStatusCode());
        responseUpdateName.prettyPrint();
        Assertions.assertEquals(200, responseUpdateName.getStatusCode());
        org.junit.jupiter.api.Assertions.assertTrue(responseUpdateName
                .body().asString()
                .contains("Real name successfully updated"));

        Response responseCheckNewName = RestAssured
                .given()
                .contentType("text/html; charset=utf-8\n" + "\n")
                .cookies(cookies)
                .get("\n" + "https://academ-it.ru/mantisbt/account_page.php")
                .andReturn();

        System.out.println("\nResponse: ");
        System.out.println("\nStatusCode: " + responseCheckNewName.getStatusCode());
        responseCheckNewName.prettyPrint();

        Assertions.assertEquals(200, responseCheckNewName.getStatusCode());
        org.junit.jupiter.api.Assertions.assertTrue(responseCheckNewName
                .body().asString()
                .contains("<tr><td class=\"category\">Real Name</td><td><input class=\"input-sm\" id=\"realname\" type=\"text\" size=\"32\" maxlength=\"255\" name=\"realname\" value=\"new_real_name1\" /></td>\t\t\t</tr>"));



    }
}
