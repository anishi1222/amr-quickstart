package dev.logicojp;
import redis.clients.authentication.core.TokenAuthConfig;
import redis.clients.authentication.entraid.EntraIDTokenAuthConfigBuilder;
import redis.clients.authentication.entraid.ManagedIdentityInfo;
import redis.clients.jedis.*;
import redis.clients.jedis.authentication.AuthXManager;

import java.util.Set;

public class JedisAppMI
{
    public void go(AMR_Constant.MIType type) {

        TokenAuthConfig authConfig;
        try(EntraIDTokenAuthConfigBuilder builder = EntraIDTokenAuthConfigBuilder.builder()) {
            switch (type) {
                case USER_ASSINGED_MANAGED_IDENTITY -> authConfig = builder
                        .userAssignedManagedIdentity(ManagedIdentityInfo.UserManagedIdentityType.OBJECT_ID, AMR_Constant.UAMI_OBJECT_ID)
                        .scopes(Set.of(AMR_Constant.SCOPE))
                        .authority(AMR_Constant.AUTHORITY_URL)
                        .clientId(AMR_Constant.UAMI_CLIENT_ID)
                        .build();
                case SYSTEM_ASSIGNED_MANAGED_IDENTITY -> authConfig = builder
                        .systemAssignedManagedIdentity()
                        .scopes(Set.of(AMR_Constant.SCOPE))
                        .authority(AMR_Constant.AUTHORITY_URL)
                        .clientId(AMR_Constant.SAMI_CLIENT_ID)
                        .build();
                default -> {
                    // This expression never runs...
                    return;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                // Include the `TokenAuthConfig` details.
                .authXManager(new AuthXManager(authConfig))
                .ssl(true)
                .build();

        UnifiedJedis jedis = new UnifiedJedis(
                new HostAndPort(AMR_Constant.HOST, AMR_Constant.PORT),
                config
        );

        System.out.println(jedis);
        System.out.println("Connected to redis as :" + config.getAuthXManager().get().getUser());
        System.out.println("Db size :" + jedis.dbSize());

        jedis.close();
    }
}
