package AuthAPI.AuthAPI.infra.segurity;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

import java.time.Duration;


@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(Duration.ofMillis(10))
            .build();
    private Bucket criarNovoBalde(){
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)throws ServletException, IOException {
        String ip = HttpUtils.getClientIp(request);
        Bucket bucket = cache.get(ip, k-> criarNovoBalde());
        if (bucket.tryConsume(1)){
            filterChain.doFilter(request,response);
        }else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Muitas requisicoes. Tente novamente em 1 minuto");
        }
    }

}
