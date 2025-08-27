package dev.logicojp;

import com.azure.identity.AzureAuthorityHosts;
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
    public void go(AMR_Constant.MIType type) {

        DefaultAzureCredential credential;
        switch (type) {
            case SYSTEM_ASSIGNED_MANAGED_IDENTITY -> credential = new DefaultAzureCredentialBuilder()
                    .managedIdentityClientId(AMR_Constant.SAMI_CLIENT_ID)
                    .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                    .tenantId(AMR_Constant.AZURE_TENANT_ID)
                    .build();
            case USER_ASSINGED_MANAGED_IDENTITY ->  credential = new DefaultAzureCredentialBuilder()
                    .managedIdentityClientId(AMR_Constant.UAMI_CLIENT_ID)
                    .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                    .tenantId(AMR_Constant.AZURE_TENANT_ID)
                    .build();
            default -> {
                // This expression never runs...
                return;
            }
        }
        String token = Objects.requireNonNull(credential.getToken(new TokenRequestContext().addScopes(AMR_Constant.SCOPE))
                        .block()).getToken();

        RedisURI redisURI;
        switch (type) {
            case SYSTEM_ASSIGNED_MANAGED_IDENTITY -> redisURI = RedisURI.builder()
                    .withHost(AMR_Constant.HOST).withPort(AMR_Constant.PORT)
                    .withAuthentication(AMR_Constant.SAMI_OBJECT_ID, token.toCharArray())
                    .withSsl(true)
                    .build();
            case USER_ASSINGED_MANAGED_IDENTITY -> redisURI = RedisURI.builder()
                    .withHost(AMR_Constant.HOST).withPort(AMR_Constant.PORT)
                    .withAuthentication(AMR_Constant.UAMI_OBJECT_ID, token.toCharArray())
                    .withSsl(true)
                    .build();
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
