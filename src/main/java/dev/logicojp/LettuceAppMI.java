package dev.logicojp;

import io.lettuce.authx.TokenBasedRedisCredentialsProvider;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import redis.clients.authentication.entraid.EntraIDTokenAuthConfigBuilder;
import redis.clients.authentication.entraid.ManagedIdentityInfo;

import java.util.Set;

public class LettuceAppMI
{
    public void go(AMR_Constant.MIType type) {
        TokenBasedRedisCredentialsProvider credentialManager;
        try(EntraIDTokenAuthConfigBuilder builder = EntraIDTokenAuthConfigBuilder.builder()) {
            switch (type) {
                case SYSTEM_ASSIGNED_MANAGED_IDENTITY -> builder.clientId(AMR_Constant.SAMI_CLIENT_ID)
                   .systemAssignedManagedIdentity()
                   .scopes(Set.of(AMR_Constant.SCOPE))
                   .authority(AMR_Constant.AUTHORITY_URL);
                case USER_ASSINGED_MANAGED_IDENTITY -> builder.clientId(AMR_Constant.UAMI_CLIENT_ID)
                        .userAssignedManagedIdentity(ManagedIdentityInfo.UserManagedIdentityType.OBJECT_ID, AMR_Constant.UAMI_OBJECT_ID)
                        .scopes(Set.of(AMR_Constant.SCOPE))
                        .authority(AMR_Constant.AUTHORITY_URL);
                default -> {
                    // This expression never runs.
                    return;
                }
            }
            credentialManager = TokenBasedRedisCredentialsProvider.create(builder.build());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        ClientOptions clientOptions=ClientOptions.builder()
                .reauthenticateBehavior(ClientOptions.ReauthenticateBehavior.ON_NEW_CREDENTIALS)
                .build();
        System.out.println(clientOptions);

        RedisURI redisURI = RedisURI.builder()
                .withHost(AMR_Constant.HOST)
                .withPort(AMR_Constant.PORT)
                .withAuthentication(credentialManager)
                .withSsl(true)
                .build();

        System.out.println(redisURI);
        RedisClient redisClient = RedisClient.create(redisURI);
        System.out.println(redisClient);
        redisClient.setOptions(clientOptions);
        try {
            StatefulRedisConnection<String, String> userConnection = redisClient.connect(StringCodec.UTF8);
            System.out.println("Connected to redis as :" + userConnection.sync().aclWhoami());
            System.out.println("Db size :" + userConnection.sync().dbsize());
        }
        finally {
            redisClient.shutdown();
        }
    }
}
