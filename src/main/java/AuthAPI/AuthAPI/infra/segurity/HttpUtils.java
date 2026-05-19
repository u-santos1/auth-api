package AuthAPI.AuthAPI.infra.segurity;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public class HttpUtils {

    private static final List<String> TRUSTED_PROXIES = List.of("127.0.0.1", "0:0:0:0:0:0:0:1", "10.0.0.1");

    public static String getClientIp(HttpServletRequest request){
       String remoteAddr = request.getRemoteAddr();

       if(TRUSTED_PROXIES.contains(remoteAddr)){
           String xForwardedFor = request.getHeader("X-Forwarded-For");
           if (xForwardedFor != null && !xForwardedFor.isBlank()){
               return xForwardedFor.split(",")[0].trim();
           }
       }
        return remoteAddr;
    }
}
