package dev.logicojp;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.core.credential.TokenRequestContext;

import java.util.Objects;

public class JedisAppToken
{
    final String UAMI_OBJECT_ID = System.getenv("UAMI_OBJECT_ID");
    final String SAMI_OBJECT_ID = System.getenv("SAMI_OBJECT_ID");
    final String UAMI_CLIENT_ID= System.getenv("UAMI_CLIENT_ID");
    final String SAMI_CLIENT_ID= System.getenv("SAMI_CLIENT_ID");
    final String SCOPE=System.getenv("MI_SCOPE");
    final String HOST=System.getenv("REDIS_HOST");
    final int PORT=Integer.parseInt(System.getenv("REDIS_PORT"));

    public void go(MIType type) {

        DefaultAzureCredential credential;
        switch (type) {
            case SYSTEM_ASSIGNED_MANAGED_IDENTITY -> credential = new DefaultAzureCredentialBuilder().managedIdentityClientId(SAMI_CLIENT_ID).build();
            case USER_ASSINGED_MANAGED_IDENTITY ->  credential = new DefaultAzureCredentialBuilder().managedIdentityClientId(UAMI_CLIENT_ID).build();
            default -> {
                // This expression never runs...
                return;
            }
        }
        String token = Objects.requireNonNull(credential.getToken(new TokenRequestContext().addScopes(SCOPE))
                        .block())
                .getToken();

        Jedis jedis;
        switch (type) {
            case SYSTEM_ASSIGNED_MANAGED_IDENTITY -> jedis = new Jedis(HOST, PORT, DefaultJedisClientConfig.builder()
                    .ssl(true)
                    .user(SAMI_OBJECT_ID)
                    .password(token)
                    .build());
            case USER_ASSINGED_MANAGED_IDENTITY -> jedis = new Jedis(HOST, PORT, DefaultJedisClientConfig.builder()
                    .ssl(true)
                    .user(UAMI_OBJECT_ID)
                    .password(token)
                    .build());
            default -> {
                // This expression never runs...
                return;
            }
        }

        System.out.println(jedis);

        System.out.println("Connected to redis as :" + jedis.aclWhoAmI());
        System.out.println("Db size :" + jedis.dbSize());

        jedis.close();
    }
}
