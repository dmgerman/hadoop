begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.constants
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|constants
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Responsible to keep all the Azure Blob File System configurations keys in Hadoop configuration file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ConfigurationKeys
specifier|public
specifier|final
class|class
name|ConfigurationKeys
block|{
DECL|field|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME
init|=
literal|"fs.azure.account.key."
decl_stmt|;
DECL|field|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME_REGX
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME_REGX
init|=
literal|"fs\\.azure\\.account\\.key\\.(.*)"
decl_stmt|;
DECL|field|FS_AZURE_SECURE_MODE
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_SECURE_MODE
init|=
literal|"fs.azure.secure.mode"
decl_stmt|;
comment|// Retry strategy defined by the user
DECL|field|AZURE_MIN_BACKOFF_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_MIN_BACKOFF_INTERVAL
init|=
literal|"fs.azure.io.retry.min.backoff.interval"
decl_stmt|;
DECL|field|AZURE_MAX_BACKOFF_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_MAX_BACKOFF_INTERVAL
init|=
literal|"fs.azure.io.retry.max.backoff.interval"
decl_stmt|;
DECL|field|AZURE_BACKOFF_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_BACKOFF_INTERVAL
init|=
literal|"fs.azure.io.retry.backoff.interval"
decl_stmt|;
DECL|field|AZURE_MAX_IO_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_MAX_IO_RETRIES
init|=
literal|"fs.azure.io.retry.max.retries"
decl_stmt|;
comment|// Read and write buffer sizes defined by the user
DECL|field|AZURE_WRITE_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_WRITE_BUFFER_SIZE
init|=
literal|"fs.azure.write.request.size"
decl_stmt|;
DECL|field|AZURE_READ_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_READ_BUFFER_SIZE
init|=
literal|"fs.azure.read.request.size"
decl_stmt|;
DECL|field|AZURE_BLOCK_SIZE_PROPERTY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_BLOCK_SIZE_PROPERTY_NAME
init|=
literal|"fs.azure.block.size"
decl_stmt|;
DECL|field|AZURE_BLOCK_LOCATION_HOST_PROPERTY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_BLOCK_LOCATION_HOST_PROPERTY_NAME
init|=
literal|"fs.azure.block.location.impersonatedhost"
decl_stmt|;
DECL|field|AZURE_CONCURRENT_CONNECTION_VALUE_OUT
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_CONCURRENT_CONNECTION_VALUE_OUT
init|=
literal|"fs.azure.concurrentRequestCount.out"
decl_stmt|;
DECL|field|AZURE_CONCURRENT_CONNECTION_VALUE_IN
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_CONCURRENT_CONNECTION_VALUE_IN
init|=
literal|"fs.azure.concurrentRequestCount.in"
decl_stmt|;
DECL|field|AZURE_TOLERATE_CONCURRENT_APPEND
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_TOLERATE_CONCURRENT_APPEND
init|=
literal|"fs.azure.io.read.tolerate.concurrent.append"
decl_stmt|;
DECL|field|AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
init|=
literal|"fs.azure.createRemoteFileSystemDuringInitialization"
decl_stmt|;
DECL|field|AZURE_SKIP_USER_GROUP_METADATA_DURING_INITIALIZATION
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_SKIP_USER_GROUP_METADATA_DURING_INITIALIZATION
init|=
literal|"fs.azure.skipUserGroupMetadataDuringInitialization"
decl_stmt|;
DECL|field|FS_AZURE_ENABLE_AUTOTHROTTLING
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ENABLE_AUTOTHROTTLING
init|=
literal|"fs.azure.enable.autothrottling"
decl_stmt|;
DECL|field|FS_AZURE_ATOMIC_RENAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ATOMIC_RENAME_KEY
init|=
literal|"fs.azure.atomic.rename.key"
decl_stmt|;
DECL|field|FS_AZURE_READ_AHEAD_QUEUE_DEPTH
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_READ_AHEAD_QUEUE_DEPTH
init|=
literal|"fs.azure.readaheadqueue.depth"
decl_stmt|;
DECL|field|FS_AZURE_ENABLE_FLUSH
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ENABLE_FLUSH
init|=
literal|"fs.azure.enable.flush"
decl_stmt|;
DECL|field|FS_AZURE_USER_AGENT_PREFIX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_USER_AGENT_PREFIX_KEY
init|=
literal|"fs.azure.user.agent.prefix"
decl_stmt|;
DECL|field|FS_AZURE_SSL_CHANNEL_MODE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_SSL_CHANNEL_MODE_KEY
init|=
literal|"fs.azure.ssl.channel.mode"
decl_stmt|;
DECL|field|AZURE_KEY_ACCOUNT_KEYPROVIDER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_KEY_ACCOUNT_KEYPROVIDER_PREFIX
init|=
literal|"fs.azure.account.keyprovider."
decl_stmt|;
DECL|field|AZURE_KEY_ACCOUNT_SHELLKEYPROVIDER_SCRIPT
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_KEY_ACCOUNT_SHELLKEYPROVIDER_SCRIPT
init|=
literal|"fs.azure.shellkeyprovider.script"
decl_stmt|;
comment|/** End point of ABFS account: {@value}. */
DECL|field|AZURE_ABFS_ENDPOINT
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_ABFS_ENDPOINT
init|=
literal|"fs.azure.abfs.endpoint"
decl_stmt|;
comment|/** Prefix for auth type properties: {@value}. */
DECL|field|FS_AZURE_ACCOUNT_AUTH_TYPE_PROPERTY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_AUTH_TYPE_PROPERTY_NAME
init|=
literal|"fs.azure.account.auth.type."
decl_stmt|;
comment|/** Prefix for oauth token provider type: {@value}. */
DECL|field|FS_AZURE_ACCOUNT_TOKEN_PROVIDER_TYPE_PROPERTY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_TOKEN_PROVIDER_TYPE_PROPERTY_NAME
init|=
literal|"fs.azure.account.oauth.provider.type."
decl_stmt|;
comment|/** Prefix for oauth AAD client id: {@value}. */
DECL|field|FS_AZURE_ACCOUNT_OAUTH_CLIENT_ID
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_OAUTH_CLIENT_ID
init|=
literal|"fs.azure.account.oauth2.client.id."
decl_stmt|;
comment|/** Prefix for oauth AAD client secret: {@value}. */
DECL|field|FS_AZURE_ACCOUNT_OAUTH_CLIENT_SECRET
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_OAUTH_CLIENT_SECRET
init|=
literal|"fs.azure.account.oauth2.client.secret."
decl_stmt|;
comment|/** Prefix for oauth AAD client endpoint: {@value}. */
DECL|field|FS_AZURE_ACCOUNT_OAUTH_CLIENT_ENDPOINT
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_OAUTH_CLIENT_ENDPOINT
init|=
literal|"fs.azure.account.oauth2.client.endpoint."
decl_stmt|;
comment|/** Prefix for oauth msi tenant id: {@value}. */
DECL|field|FS_AZURE_ACCOUNT_OAUTH_MSI_TENANT
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_OAUTH_MSI_TENANT
init|=
literal|"fs.azure.account.oauth2.msi.tenant."
decl_stmt|;
comment|/** Prefix for oauth user name: {@value}. */
DECL|field|FS_AZURE_ACCOUNT_OAUTH_USER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_OAUTH_USER_NAME
init|=
literal|"fs.azure.account.oauth2.user.name."
decl_stmt|;
comment|/** Prefix for oauth user password: {@value}. */
DECL|field|FS_AZURE_ACCOUNT_OAUTH_USER_PASSWORD
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_OAUTH_USER_PASSWORD
init|=
literal|"fs.azure.account.oauth2.user.password."
decl_stmt|;
comment|/** Prefix for oauth refresh token: {@value}. */
DECL|field|FS_AZURE_ACCOUNT_OAUTH_REFRESH_TOKEN
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_OAUTH_REFRESH_TOKEN
init|=
literal|"fs.azure.account.oauth2.refresh.token."
decl_stmt|;
DECL|field|FS_AZURE_ENABLE_DELEGATION_TOKEN
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ENABLE_DELEGATION_TOKEN
init|=
literal|"fs.azure.enable.delegation.token"
decl_stmt|;
DECL|field|FS_AZURE_DELEGATION_TOKEN_PROVIDER_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_DELEGATION_TOKEN_PROVIDER_TYPE
init|=
literal|"fs.azure.delegation.token.provider.type"
decl_stmt|;
DECL|method|ConfigurationKeys ()
specifier|private
name|ConfigurationKeys
parameter_list|()
block|{}
block|}
end_class

end_unit

