package AuthAPI.AuthAPI.infra.segurity;



import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RegistroRateLimitService {
    private final Cache<String, Bucket> cacheRegistro = Caffeine.newBuilder()
            .maximumSize(5000)
            .expireAfterAccess(Duration.ofHours(1))
            .build();

    private void resetCache() {
        cacheRegistro.invalidateAll();
    }

    public void verificarLimiteDeRegistro(String ip){
        Bucket bucket = cacheRegistro.get(ip, k-> criarBaldeDeRegistro());
        if(!bucket.tryConsume(1)){
            throw new RegraDeNegocioException("Limite de criacao de contas excedido. Tente novamente");
        }
    }
    private Bucket criarBaldeDeRegistro() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(3)
                .refillGreedy(3, Duration.ofHours(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

}
