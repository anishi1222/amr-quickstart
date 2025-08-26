# AMR Quickstart

A quickstart project demonstrating how to connect to Azure Managed Redis (not Azure Cache for Redis) using Azure Managed Identity authentication.

## Overview

This project implements Azure Managed Identity authentication with the following Redis client libraries:
- Jedis
- Lettuce

## Prerequisites

- Java 8 or higher
- Maven 3.6 or higher
- Azure subscription
- Azure Managed Redis instance
- Azure Managed Identity configuration

## Environment Variables Setup

Set the following environment variables are needed.

### Bash/Linux Environment
```bash
export REDIS_HOST="your-redis-host.redis.azure.net"
export REDIS_PORT=10000
export UAMI_OBJECT_ID="your-user-assigned-managed-identity-object-id"
export UAMI_CLIENT_ID="your-user-assigned-managed-identity-client-id"
export SAMI_OBJECT_ID="your-system-assigned-managed-identity-object-id"
export SAMI_CLIENT_ID="your-system-assigned-managed-identity-client-id"
export MI_SCOPE="https://redis.azure.com"
export AZURE_TENANT_ID="your-azure-tenant-id"
```

### Windows Environment
```powershell
$env:REDIS_HOST="your-redis-host.redis.azure.net"
$env:REDIS_PORT=10000
$env:UAMI_OBJECT_ID="your-user-assigned-managed-identity-object-id"
$env:UAMI_CLIENT_ID="your-user-assigned-managed-identity-client-id"
$env:SAMI_OBJECT_ID="your-system-assigned-managed-identity-object-id"
$env:SAMI_CLIENT_ID="your-system-assigned-managed-identity-client-id"
$env:MI_SCOPE="https://redis.azure.com"
$env:AZURE_TENANT_ID="your-azure-tenant-id"
```

## Build and Run

### Build this project and package into a jar file
```bash
mvn clean package
```

### Run the application
```bash
java -cp target/amr-quickstart-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Project structure
```aiignore
src/main/java/dev/logicojp/
├── App.java              # Main application
├── JedisAppMI.java       # Jedis + Managed Identity
├── JedisAppToken.java    # Jedis + Token authentication
├── LettuceAppMI.java     # Lettuce + Managed Identity
├── LettuceAppToken.java  # Lettuce + Token authentication
└── MIType.java           # Managed Identity type definitions
```

