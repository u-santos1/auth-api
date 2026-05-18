package AuthAPI.AuthAPI.infra.segurity;

import jakarta.servlet.http.HttpServletRequest;

public class HttpUtils {

    public static String getClientIp(HttpServletRequest request){
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor == null || xForwardedFor.isEmpty()){
            return request.getRemoteAddr();
        }
        return xForwardedFor.split(",")[0].trim();
    }
}
