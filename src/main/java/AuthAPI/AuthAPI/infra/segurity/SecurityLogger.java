package AuthAPI.AuthAPI.infra.segurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
public class SecurityLogger {

    private static final Logger logger = LoggerFactory.getLogger("SECURITY_AUDIT");

    public void logLoginFalho(String email, String ip){
        logger.warn("[SECURITY_ALERT] [FAILED_LOGIN] Tentativa de login incorreta. Email: {} | IP: {}", email, ip);
    }
    public void logTentativaBloqueadaPorRateLimit(String ip, String endpoint){
        logger.error("[SECURITY_ALERT] [RATE_LIMIT_BLOCKED] IP bloqueado por excesso de requisicoes. IP: {} | Endpoint: {}", ip, endpoint);
    }
    public void logSolicitacaoRecuperacaoSenha(String email, String ip){
        logger.info("[SECURITY_AUDIT] [PASSWORD_RESET_REQUEST] Pedido de redefinicao de senha. Email: {} | IP: {}", email, ip);

    }
    public void logSenhaAlteradaComSucesso(String email, String ip){
        logger.info("[SECURITY_AUDIT] [PASSWORD_RESET_SUCCESS] Senha alterada com sucesso via token. Email: {} | IP: {}", email, ip);
    }
}
