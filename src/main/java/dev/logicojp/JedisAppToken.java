package dev.logicojp;
import com.azure.identity.AzureAuthorityHosts;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.core.credential.TokenRequestContext;

import java.util.Objects;

public class JedisAppToken
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

        Jedis jedis;
        switch (type) {
            case SYSTEM_ASSIGNED_MANAGED_IDENTITY -> jedis =
                    new Jedis(
                            AMR_Constant.HOST,
                            AMR_Constant.PORT,
                            DefaultJedisClientConfig.builder().ssl(true)
                                .user(AMR_Constant.SAMI_OBJECT_ID)
                                .password(token)
                                .build());
            case USER_ASSINGED_MANAGED_IDENTITY -> jedis =
                    new Jedis(
                            AMR_Constant.HOST,
                            AMR_Constant.PORT,
                            DefaultJedisClientConfig.builder()
                                .ssl(true)
                                .user(AMR_Constant.UAMI_OBJECT_ID)
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
