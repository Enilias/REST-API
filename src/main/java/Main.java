import java.net.HttpCookie;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<HttpCookie> cookies = HttpRreceivingAndSending.httpGetCookie("http://94.198.50.185:7081/api/users/");
        User user = new User(3L, "James", "Brown", (byte) 23);
        System.out.println(HttpRreceivingAndSending.httpPostPutObject("http://94.198.50.185:7081/api/users/", "POST", cookies, user));
        User user1 = new User(3L, "Thomas", "Shelby", (byte) 23);
        System.out.println(HttpRreceivingAndSending.httpPostPutObject("http://94.198.50.185:7081/api/users/", "PUT", cookies, user1));
        System.out.println(HttpRreceivingAndSending.httpPostPutObject("http://94.198.50.185:7081/api/users/", "DELETE", cookies, user1));

    }
}
