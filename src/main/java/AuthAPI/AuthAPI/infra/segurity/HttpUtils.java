package AuthAPI.AuthAPI.infra.segurity;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public class HttpUtils {
    public static String getClientIp(HttpServletRequest request){
       return request.getRemoteAddr();
       }
}
