begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|FileSystemConfigurations
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|annotations
operator|.
name|ConfigurationValidationAnnotations
operator|.
name|IntegerConfigurationValidatorAnnotation
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|annotations
operator|.
name|ConfigurationValidationAnnotations
operator|.
name|LongConfigurationValidatorAnnotation
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|annotations
operator|.
name|ConfigurationValidationAnnotations
operator|.
name|StringConfigurationValidatorAnnotation
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|annotations
operator|.
name|ConfigurationValidationAnnotations
operator|.
name|Base64StringConfigurationValidatorAnnotation
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|annotations
operator|.
name|ConfigurationValidationAnnotations
operator|.
name|BooleanConfigurationValidatorAnnotation
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|AzureBlobFileSystemException
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|ConfigurationPropertyNotFoundException
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|InvalidConfigurationValueException
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|KeyProviderException
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
name|fs
operator|.
name|azurebfs
operator|.
name|diagnostics
operator|.
name|Base64StringConfigurationBasicValidator
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
name|fs
operator|.
name|azurebfs
operator|.
name|diagnostics
operator|.
name|BooleanConfigurationBasicValidator
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
name|fs
operator|.
name|azurebfs
operator|.
name|diagnostics
operator|.
name|IntegerConfigurationBasicValidator
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
name|fs
operator|.
name|azurebfs
operator|.
name|diagnostics
operator|.
name|LongConfigurationBasicValidator
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
name|fs
operator|.
name|azurebfs
operator|.
name|diagnostics
operator|.
name|StringConfigurationBasicValidator
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
name|fs
operator|.
name|azurebfs
operator|.
name|services
operator|.
name|KeyProvider
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
name|fs
operator|.
name|azurebfs
operator|.
name|services
operator|.
name|SimpleKeyProvider
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
name|fs
operator|.
name|azurebfs
operator|.
name|utils
operator|.
name|SSLSocketFactoryEx
import|;
end_import

begin_import
import|import static
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
operator|.
name|ConfigurationKeys
operator|.
name|FS_AZURE_SSL_CHANNEL_MODE_KEY
import|;
end_import

begin_import
import|import static
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
operator|.
name|FileSystemConfigurations
operator|.
name|DEFAULT_FS_AZURE_SSL_CHANNEL_MODE
import|;
end_import

begin_comment
comment|/**  * Configuration for Azure Blob FileSystem.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AbfsConfiguration
specifier|public
class|class
name|AbfsConfiguration
block|{
DECL|field|configuration
specifier|private
specifier|final
name|Configuration
name|configuration
decl_stmt|;
DECL|field|isSecure
specifier|private
specifier|final
name|boolean
name|isSecure
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_WRITE_BUFFER_SIZE
argument_list|,
name|MinValue
operator|=
name|FileSystemConfigurations
operator|.
name|MIN_BUFFER_SIZE
argument_list|,
name|MaxValue
operator|=
name|FileSystemConfigurations
operator|.
name|MAX_BUFFER_SIZE
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_WRITE_BUFFER_SIZE
argument_list|)
DECL|field|writeBufferSize
specifier|private
name|int
name|writeBufferSize
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_READ_BUFFER_SIZE
argument_list|,
name|MinValue
operator|=
name|FileSystemConfigurations
operator|.
name|MIN_BUFFER_SIZE
argument_list|,
name|MaxValue
operator|=
name|FileSystemConfigurations
operator|.
name|MAX_BUFFER_SIZE
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_READ_BUFFER_SIZE
argument_list|)
DECL|field|readBufferSize
specifier|private
name|int
name|readBufferSize
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_MIN_BACKOFF_INTERVAL
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_MIN_BACKOFF_INTERVAL
argument_list|)
DECL|field|minBackoffInterval
specifier|private
name|int
name|minBackoffInterval
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_MAX_BACKOFF_INTERVAL
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_MAX_BACKOFF_INTERVAL
argument_list|)
DECL|field|maxBackoffInterval
specifier|private
name|int
name|maxBackoffInterval
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_BACKOFF_INTERVAL
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_BACKOFF_INTERVAL
argument_list|)
DECL|field|backoffInterval
specifier|private
name|int
name|backoffInterval
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_MAX_IO_RETRIES
argument_list|,
name|MinValue
operator|=
literal|0
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_MAX_RETRY_ATTEMPTS
argument_list|)
DECL|field|maxIoRetries
specifier|private
name|int
name|maxIoRetries
decl_stmt|;
annotation|@
name|LongConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_BLOCK_SIZE_PROPERTY_NAME
argument_list|,
name|MinValue
operator|=
literal|0
argument_list|,
name|MaxValue
operator|=
name|FileSystemConfigurations
operator|.
name|MAX_AZURE_BLOCK_SIZE
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|MAX_AZURE_BLOCK_SIZE
argument_list|)
DECL|field|azureBlockSize
specifier|private
name|long
name|azureBlockSize
decl_stmt|;
annotation|@
name|StringConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_BLOCK_LOCATION_HOST_PROPERTY_NAME
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|AZURE_BLOCK_LOCATION_HOST_DEFAULT
argument_list|)
DECL|field|azureBlockLocationHost
specifier|private
name|String
name|azureBlockLocationHost
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_CONCURRENT_CONNECTION_VALUE_OUT
argument_list|,
name|MinValue
operator|=
literal|1
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|MAX_CONCURRENT_WRITE_THREADS
argument_list|)
DECL|field|maxConcurrentWriteThreads
specifier|private
name|int
name|maxConcurrentWriteThreads
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_CONCURRENT_CONNECTION_VALUE_IN
argument_list|,
name|MinValue
operator|=
literal|1
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|MAX_CONCURRENT_READ_THREADS
argument_list|)
DECL|field|maxConcurrentReadThreads
specifier|private
name|int
name|maxConcurrentReadThreads
decl_stmt|;
annotation|@
name|BooleanConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_TOLERATE_CONCURRENT_APPEND
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_READ_TOLERATE_CONCURRENT_APPEND
argument_list|)
DECL|field|tolerateOobAppends
specifier|private
name|boolean
name|tolerateOobAppends
decl_stmt|;
annotation|@
name|StringConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|FS_AZURE_ATOMIC_RENAME_KEY
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_FS_AZURE_ATOMIC_RENAME_DIRECTORIES
argument_list|)
DECL|field|azureAtomicDirs
specifier|private
name|String
name|azureAtomicDirs
decl_stmt|;
annotation|@
name|BooleanConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
argument_list|)
DECL|field|createRemoteFileSystemDuringInitialization
specifier|private
name|boolean
name|createRemoteFileSystemDuringInitialization
decl_stmt|;
annotation|@
name|BooleanConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|AZURE_SKIP_USER_GROUP_METADATA_DURING_INITIALIZATION
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_AZURE_SKIP_USER_GROUP_METADATA_DURING_INITIALIZATION
argument_list|)
DECL|field|skipUserGroupMetadataDuringInitialization
specifier|private
name|boolean
name|skipUserGroupMetadataDuringInitialization
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|FS_AZURE_READ_AHEAD_QUEUE_DEPTH
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_READ_AHEAD_QUEUE_DEPTH
argument_list|)
DECL|field|readAheadQueueDepth
specifier|private
name|int
name|readAheadQueueDepth
decl_stmt|;
annotation|@
name|BooleanConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|FS_AZURE_ENABLE_FLUSH
argument_list|,
name|DefaultValue
operator|=
name|FileSystemConfigurations
operator|.
name|DEFAULT_ENABLE_FLUSH
argument_list|)
DECL|field|enableFlush
specifier|private
name|boolean
name|enableFlush
decl_stmt|;
annotation|@
name|StringConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|ConfigurationKeys
operator|.
name|FS_AZURE_USER_AGENT_PREFIX_KEY
argument_list|,
name|DefaultValue
operator|=
literal|""
argument_list|)
DECL|field|userAgentId
specifier|private
name|String
name|userAgentId
decl_stmt|;
DECL|field|storageAccountKeys
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|storageAccountKeys
decl_stmt|;
DECL|method|AbfsConfiguration (final Configuration configuration)
specifier|public
name|AbfsConfiguration
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InvalidConfigurationValueException
block|{
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
name|this
operator|.
name|isSecure
operator|=
name|this
operator|.
name|configuration
operator|.
name|getBoolean
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_SECURE_MODE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|validateStorageAccountKeys
argument_list|()
expr_stmt|;
name|Field
index|[]
name|fields
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|isAnnotationPresent
argument_list|(
name|IntegerConfigurationValidatorAnnotation
operator|.
name|class
argument_list|)
condition|)
block|{
name|field
operator|.
name|set
argument_list|(
name|this
argument_list|,
name|validateInt
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|isAnnotationPresent
argument_list|(
name|LongConfigurationValidatorAnnotation
operator|.
name|class
argument_list|)
condition|)
block|{
name|field
operator|.
name|set
argument_list|(
name|this
argument_list|,
name|validateLong
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|isAnnotationPresent
argument_list|(
name|StringConfigurationValidatorAnnotation
operator|.
name|class
argument_list|)
condition|)
block|{
name|field
operator|.
name|set
argument_list|(
name|this
argument_list|,
name|validateString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|isAnnotationPresent
argument_list|(
name|Base64StringConfigurationValidatorAnnotation
operator|.
name|class
argument_list|)
condition|)
block|{
name|field
operator|.
name|set
argument_list|(
name|this
argument_list|,
name|validateBase64String
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|isAnnotationPresent
argument_list|(
name|BooleanConfigurationValidatorAnnotation
operator|.
name|class
argument_list|)
condition|)
block|{
name|field
operator|.
name|set
argument_list|(
name|this
argument_list|,
name|validateBoolean
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isEmulator ()
specifier|public
name|boolean
name|isEmulator
parameter_list|()
block|{
return|return
name|this
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_EMULATOR_ENABLED
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|isSecureMode ()
specifier|public
name|boolean
name|isSecureMode
parameter_list|()
block|{
return|return
name|this
operator|.
name|isSecure
return|;
block|}
DECL|method|getStorageAccountKey (final String accountName)
specifier|public
name|String
name|getStorageAccountKey
parameter_list|(
specifier|final
name|String
name|accountName
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
block|{
name|String
name|key
decl_stmt|;
name|String
name|keyProviderClass
init|=
name|configuration
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|AZURE_KEY_ACCOUNT_KEYPROVIDER_PREFIX
operator|+
name|accountName
argument_list|)
decl_stmt|;
name|KeyProvider
name|keyProvider
decl_stmt|;
if|if
condition|(
name|keyProviderClass
operator|==
literal|null
condition|)
block|{
comment|// No key provider was provided so use the provided key as is.
name|keyProvider
operator|=
operator|new
name|SimpleKeyProvider
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// create an instance of the key provider class and verify it
comment|// implements KeyProvider
name|Object
name|keyProviderObject
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|configuration
operator|.
name|getClassByName
argument_list|(
name|keyProviderClass
argument_list|)
decl_stmt|;
name|keyProviderObject
operator|=
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|KeyProviderException
argument_list|(
literal|"Unable to load key provider class."
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|keyProviderObject
operator|instanceof
name|KeyProvider
operator|)
condition|)
block|{
throw|throw
operator|new
name|KeyProviderException
argument_list|(
name|keyProviderClass
operator|+
literal|" specified in config is not a valid KeyProvider class."
argument_list|)
throw|;
block|}
name|keyProvider
operator|=
operator|(
name|KeyProvider
operator|)
name|keyProviderObject
expr_stmt|;
block|}
name|key
operator|=
name|keyProvider
operator|.
name|getStorageAccountKey
argument_list|(
name|accountName
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConfigurationPropertyNotFoundException
argument_list|(
name|accountName
argument_list|)
throw|;
block|}
return|return
name|key
return|;
block|}
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|this
operator|.
name|configuration
return|;
block|}
DECL|method|getWriteBufferSize ()
specifier|public
name|int
name|getWriteBufferSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|writeBufferSize
return|;
block|}
DECL|method|getReadBufferSize ()
specifier|public
name|int
name|getReadBufferSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|readBufferSize
return|;
block|}
DECL|method|getMinBackoffIntervalMilliseconds ()
specifier|public
name|int
name|getMinBackoffIntervalMilliseconds
parameter_list|()
block|{
return|return
name|this
operator|.
name|minBackoffInterval
return|;
block|}
DECL|method|getMaxBackoffIntervalMilliseconds ()
specifier|public
name|int
name|getMaxBackoffIntervalMilliseconds
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxBackoffInterval
return|;
block|}
DECL|method|getBackoffIntervalMilliseconds ()
specifier|public
name|int
name|getBackoffIntervalMilliseconds
parameter_list|()
block|{
return|return
name|this
operator|.
name|backoffInterval
return|;
block|}
DECL|method|getMaxIoRetries ()
specifier|public
name|int
name|getMaxIoRetries
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxIoRetries
return|;
block|}
DECL|method|getAzureBlockSize ()
specifier|public
name|long
name|getAzureBlockSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|azureBlockSize
return|;
block|}
DECL|method|getAzureBlockLocationHost ()
specifier|public
name|String
name|getAzureBlockLocationHost
parameter_list|()
block|{
return|return
name|this
operator|.
name|azureBlockLocationHost
return|;
block|}
DECL|method|getMaxConcurrentWriteThreads ()
specifier|public
name|int
name|getMaxConcurrentWriteThreads
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxConcurrentWriteThreads
return|;
block|}
DECL|method|getMaxConcurrentReadThreads ()
specifier|public
name|int
name|getMaxConcurrentReadThreads
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxConcurrentReadThreads
return|;
block|}
DECL|method|getTolerateOobAppends ()
specifier|public
name|boolean
name|getTolerateOobAppends
parameter_list|()
block|{
return|return
name|this
operator|.
name|tolerateOobAppends
return|;
block|}
DECL|method|getAzureAtomicRenameDirs ()
specifier|public
name|String
name|getAzureAtomicRenameDirs
parameter_list|()
block|{
return|return
name|this
operator|.
name|azureAtomicDirs
return|;
block|}
DECL|method|getCreateRemoteFileSystemDuringInitialization ()
specifier|public
name|boolean
name|getCreateRemoteFileSystemDuringInitialization
parameter_list|()
block|{
return|return
name|this
operator|.
name|createRemoteFileSystemDuringInitialization
return|;
block|}
DECL|method|getSkipUserGroupMetadataDuringInitialization ()
specifier|public
name|boolean
name|getSkipUserGroupMetadataDuringInitialization
parameter_list|()
block|{
return|return
name|this
operator|.
name|skipUserGroupMetadataDuringInitialization
return|;
block|}
DECL|method|getReadAheadQueueDepth ()
specifier|public
name|int
name|getReadAheadQueueDepth
parameter_list|()
block|{
return|return
name|this
operator|.
name|readAheadQueueDepth
return|;
block|}
DECL|method|isFlushEnabled ()
specifier|public
name|boolean
name|isFlushEnabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|enableFlush
return|;
block|}
DECL|method|getCustomUserAgentPrefix ()
specifier|public
name|String
name|getCustomUserAgentPrefix
parameter_list|()
block|{
return|return
name|this
operator|.
name|userAgentId
return|;
block|}
DECL|method|getPreferredSSLFactoryOption ()
specifier|public
name|SSLSocketFactoryEx
operator|.
name|SSLChannelMode
name|getPreferredSSLFactoryOption
parameter_list|()
block|{
return|return
name|configuration
operator|.
name|getEnum
argument_list|(
name|FS_AZURE_SSL_CHANNEL_MODE_KEY
argument_list|,
name|DEFAULT_FS_AZURE_SSL_CHANNEL_MODE
argument_list|)
return|;
block|}
DECL|method|validateStorageAccountKeys ()
name|void
name|validateStorageAccountKeys
parameter_list|()
throws|throws
name|InvalidConfigurationValueException
block|{
name|Base64StringConfigurationBasicValidator
name|validator
init|=
operator|new
name|Base64StringConfigurationBasicValidator
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME
argument_list|,
literal|""
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|this
operator|.
name|storageAccountKeys
operator|=
name|this
operator|.
name|configuration
operator|.
name|getValByRegex
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME_REGX
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|account
range|:
name|this
operator|.
name|storageAccountKeys
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|validator
operator|.
name|validate
argument_list|(
name|account
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validateInt (Field field)
name|int
name|validateInt
parameter_list|(
name|Field
name|field
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InvalidConfigurationValueException
block|{
name|IntegerConfigurationValidatorAnnotation
name|validator
init|=
name|field
operator|.
name|getAnnotation
argument_list|(
name|IntegerConfigurationValidatorAnnotation
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|)
decl_stmt|;
comment|// validate
return|return
operator|new
name|IntegerConfigurationBasicValidator
argument_list|(
name|validator
operator|.
name|MinValue
argument_list|()
argument_list|,
name|validator
operator|.
name|MaxValue
argument_list|()
argument_list|,
name|validator
operator|.
name|DefaultValue
argument_list|()
argument_list|,
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|,
name|validator
operator|.
name|ThrowIfInvalid
argument_list|()
argument_list|)
operator|.
name|validate
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|validateLong (Field field)
name|long
name|validateLong
parameter_list|(
name|Field
name|field
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InvalidConfigurationValueException
block|{
name|LongConfigurationValidatorAnnotation
name|validator
init|=
name|field
operator|.
name|getAnnotation
argument_list|(
name|LongConfigurationValidatorAnnotation
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|)
decl_stmt|;
comment|// validate
return|return
operator|new
name|LongConfigurationBasicValidator
argument_list|(
name|validator
operator|.
name|MinValue
argument_list|()
argument_list|,
name|validator
operator|.
name|MaxValue
argument_list|()
argument_list|,
name|validator
operator|.
name|DefaultValue
argument_list|()
argument_list|,
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|,
name|validator
operator|.
name|ThrowIfInvalid
argument_list|()
argument_list|)
operator|.
name|validate
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|validateString (Field field)
name|String
name|validateString
parameter_list|(
name|Field
name|field
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InvalidConfigurationValueException
block|{
name|StringConfigurationValidatorAnnotation
name|validator
init|=
name|field
operator|.
name|getAnnotation
argument_list|(
name|StringConfigurationValidatorAnnotation
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|)
decl_stmt|;
comment|// validate
return|return
operator|new
name|StringConfigurationBasicValidator
argument_list|(
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|,
name|validator
operator|.
name|DefaultValue
argument_list|()
argument_list|,
name|validator
operator|.
name|ThrowIfInvalid
argument_list|()
argument_list|)
operator|.
name|validate
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|validateBase64String (Field field)
name|String
name|validateBase64String
parameter_list|(
name|Field
name|field
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InvalidConfigurationValueException
block|{
name|Base64StringConfigurationValidatorAnnotation
name|validator
init|=
name|field
operator|.
name|getAnnotation
argument_list|(
operator|(
name|Base64StringConfigurationValidatorAnnotation
operator|.
name|class
operator|)
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|)
decl_stmt|;
comment|// validate
return|return
operator|new
name|Base64StringConfigurationBasicValidator
argument_list|(
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|,
name|validator
operator|.
name|DefaultValue
argument_list|()
argument_list|,
name|validator
operator|.
name|ThrowIfInvalid
argument_list|()
argument_list|)
operator|.
name|validate
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|validateBoolean (Field field)
name|boolean
name|validateBoolean
parameter_list|(
name|Field
name|field
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InvalidConfigurationValueException
block|{
name|BooleanConfigurationValidatorAnnotation
name|validator
init|=
name|field
operator|.
name|getAnnotation
argument_list|(
name|BooleanConfigurationValidatorAnnotation
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|)
decl_stmt|;
comment|// validate
return|return
operator|new
name|BooleanConfigurationBasicValidator
argument_list|(
name|validator
operator|.
name|ConfigurationKey
argument_list|()
argument_list|,
name|validator
operator|.
name|DefaultValue
argument_list|()
argument_list|,
name|validator
operator|.
name|ThrowIfInvalid
argument_list|()
argument_list|)
operator|.
name|validate
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setReadBufferSize (int bufferSize)
name|void
name|setReadBufferSize
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
name|this
operator|.
name|readBufferSize
operator|=
name|bufferSize
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setWriteBufferSize (int bufferSize)
name|void
name|setWriteBufferSize
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
name|this
operator|.
name|writeBufferSize
operator|=
name|bufferSize
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setEnableFlush (boolean enableFlush)
name|void
name|setEnableFlush
parameter_list|(
name|boolean
name|enableFlush
parameter_list|)
block|{
name|this
operator|.
name|enableFlush
operator|=
name|enableFlush
expr_stmt|;
block|}
block|}
end_class

end_unit

