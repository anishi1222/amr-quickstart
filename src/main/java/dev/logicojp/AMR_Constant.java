package dev.logicojp;

import com.azure.identity.AzureAuthorityHosts;

public class AMR_Constant {

    public enum MIType {
        USER_ASSINGED_MANAGED_IDENTITY,
        SYSTEM_ASSIGNED_MANAGED_IDENTITY
    }

    static final String SCOPE=System.getenv("MI_SCOPE");
    static final String HOST=System.getenv("REDIS_HOST");
    static final int PORT=Integer.parseInt(System.getenv("REDIS_PORT"));
    static final String UAMI_OBJECT_ID = System.getenv("UAMI_OBJECT_ID");
    static final String SAMI_OBJECT_ID = System.getenv("SAMI_OBJECT_ID");
    static final String UAMI_CLIENT_ID= System.getenv("UAMI_CLIENT_ID");
    static final String SAMI_CLIENT_ID= System.getenv("SAMI_CLIENT_ID");
    static final String AZURE_TENANT_ID=System.getenv("AZURE_TENANT_ID");
    static final String AUTHORITY_URL=String.format("%s%s/", AzureAuthorityHosts.AZURE_PUBLIC_CLOUD, AZURE_TENANT_ID);
}
