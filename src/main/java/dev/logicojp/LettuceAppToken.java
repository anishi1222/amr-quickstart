package dev.logicojp;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.ClientOptions;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.core.credential.TokenRequestContext;

import java.util.Objects;

public class LettuceAppToken
{
    final String UAMI_OBJECT_ID = System.getenv("UAMI_OBJECT_ID");
    final String SAMI_OBJECT_ID = System.getenv("SAMI_OBJECT_ID");
    final String UAMI_CLIENT_ID= System.getenv("UAMI_CLIENT_ID");
    final String SCOPE=System.getenv("MI_SCOPE");
    final String HOST=System.getenv("REDIS_HOST");
    final int PORT=Integer.parseInt(System.getenv("REDIS_PORT"));

    public void go(MIType type) {

        DefaultAzureCredential credential;
        switch (type) {
            case SYSTEM_ASSIGNED_MANAGED_IDENTITY -> credential = new DefaultAzureCredentialBuilder().build();
            case USER_ASSINGED_MANAGED_IDENTITY ->  credential = new DefaultAzureCredentialBuilder().managedIdentityClientId(UAMI_CLIENT_ID).build();
            default -> {
                // This expression never runs...
                return;
            }
        }
        String token = Objects.requireNonNull(credential.getToken(new TokenRequestContext().addScopes(SCOPE))
                        .block()).getToken();

        RedisURI redisURI;
        switch (type) {
            case SYSTEM_ASSIGNED_MANAGED_IDENTITY -> {
                redisURI = RedisURI.builder()
                        .withHost(HOST).withPort(PORT)
                        .withAuthentication(SAMI_OBJECT_ID, token.toCharArray())
                        .withSsl(true)
                        .build();
            }
            case USER_ASSINGED_MANAGED_IDENTITY -> {
                redisURI = RedisURI.builder()
                        .withHost(HOST).withPort(PORT)
                        .withAuthentication(UAMI_OBJECT_ID, token.toCharArray())
                        .withSsl(true)
                        .build();
            }
            default -> {
                return;
            }
        }

        System.out.println(redisURI);
        ClientOptions clientOptions = ClientOptions.builder()
                .reauthenticateBehavior(ClientOptions.ReauthenticateBehavior.ON_NEW_CREDENTIALS)
                .build();
        System.out.println(clientOptions);
        RedisClient redisClient = RedisClient.create(redisURI);
        System.out.println(redisClient);
        redisClient.setOptions(clientOptions);

        StatefulRedisConnection<String, String> userConnection = redisClient.connect(StringCodec.UTF8);
        System.out.println("Connected to redis as :" + userConnection.sync().aclWhoami());
        System.out.println("Db size :" + userConnection.sync().dbsize());

        redisClient.shutdown();
    }
}
