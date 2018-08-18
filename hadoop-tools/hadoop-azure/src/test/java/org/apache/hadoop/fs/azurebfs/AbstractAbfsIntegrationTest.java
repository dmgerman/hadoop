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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|CommonConfigurationKeysPublic
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
name|FileSystem
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
name|Path
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
name|AuthType
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
name|azure
operator|.
name|AbstractWasbTestWithTimeout
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
name|azure
operator|.
name|AzureNativeFileSystemStore
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
name|azure
operator|.
name|NativeAzureFileSystem
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
name|azure
operator|.
name|metrics
operator|.
name|AzureFileSystemInstrumentation
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
name|FileSystemUriSchemes
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
name|AbfsRestOperationException
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
name|UriUtils
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
name|contract
operator|.
name|ContractTestUtils
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
name|io
operator|.
name|IOUtils
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
name|*
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
name|contracts
operator|.
name|services
operator|.
name|AzureServiceErrorCode
operator|.
name|FILE_SYSTEM_NOT_FOUND
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
name|TestConfigurationKeys
operator|.
name|*
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Base for AzureBlobFileSystem Integration tests.  *  *<I>Important: This is for integration tests only.</I>  */
end_comment

begin_class
DECL|class|AbstractAbfsIntegrationTest
specifier|public
specifier|abstract
class|class
name|AbstractAbfsIntegrationTest
extends|extends
name|AbstractWasbTestWithTimeout
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractAbfsIntegrationTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|isEmulator
specifier|private
name|boolean
name|isEmulator
decl_stmt|;
DECL|field|wasb
specifier|private
name|NativeAzureFileSystem
name|wasb
decl_stmt|;
DECL|field|abfs
specifier|private
name|AzureBlobFileSystem
name|abfs
decl_stmt|;
DECL|field|abfsScheme
specifier|private
name|String
name|abfsScheme
decl_stmt|;
DECL|field|configuration
specifier|private
name|Configuration
name|configuration
decl_stmt|;
DECL|field|fileSystemName
specifier|private
name|String
name|fileSystemName
decl_stmt|;
DECL|field|accountName
specifier|private
name|String
name|accountName
decl_stmt|;
DECL|field|testUrl
specifier|private
name|String
name|testUrl
decl_stmt|;
DECL|field|authType
specifier|private
name|AuthType
name|authType
decl_stmt|;
DECL|method|AbstractAbfsIntegrationTest ()
specifier|protected
name|AbstractAbfsIntegrationTest
parameter_list|()
block|{
name|fileSystemName
operator|=
name|ABFS_TEST_CONTAINER_PREFIX
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|configuration
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|configuration
operator|.
name|addResource
argument_list|(
name|ABFS_TEST_RESOURCE_XML
argument_list|)
expr_stmt|;
name|this
operator|.
name|accountName
operator|=
name|this
operator|.
name|configuration
operator|.
name|get
argument_list|(
name|FS_AZURE_TEST_ACCOUNT_NAME
argument_list|)
expr_stmt|;
name|authType
operator|=
name|configuration
operator|.
name|getEnum
argument_list|(
name|FS_AZURE_ACCOUNT_AUTH_TYPE_PROPERTY_NAME
operator|+
name|accountName
argument_list|,
name|AuthType
operator|.
name|SharedKey
argument_list|)
expr_stmt|;
name|abfsScheme
operator|=
name|authType
operator|==
name|AuthType
operator|.
name|SharedKey
condition|?
name|FileSystemUriSchemes
operator|.
name|ABFS_SCHEME
else|:
name|FileSystemUriSchemes
operator|.
name|ABFS_SECURE_SCHEME
expr_stmt|;
name|String
name|accountName
init|=
name|configuration
operator|.
name|get
argument_list|(
name|FS_AZURE_TEST_ACCOUNT_NAME
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assumeTrue
argument_list|(
literal|"Not set: "
operator|+
name|FS_AZURE_TEST_ACCOUNT_NAME
argument_list|,
operator|!
name|accountName
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"The key in "
operator|+
name|FS_AZURE_TEST_ACCOUNT_KEY_PREFIX
operator|+
literal|" is not bound to an ABFS account"
argument_list|,
name|accountName
argument_list|,
name|containsString
argument_list|(
literal|"dfs.core.windows.net"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|fullKey
init|=
name|FS_AZURE_TEST_ACCOUNT_KEY_PREFIX
operator|+
name|accountName
decl_stmt|;
if|if
condition|(
name|authType
operator|==
name|AuthType
operator|.
name|SharedKey
condition|)
block|{
name|assumeTrue
argument_list|(
literal|"Not set: "
operator|+
name|fullKey
argument_list|,
name|configuration
operator|.
name|get
argument_list|(
name|fullKey
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|accessTokenProviderKey
init|=
name|FS_AZURE_ACCOUNT_TOKEN_PROVIDER_TYPE_PROPERTY_NAME
operator|+
name|accountName
decl_stmt|;
name|assumeTrue
argument_list|(
literal|"Not set: "
operator|+
name|accessTokenProviderKey
argument_list|,
name|configuration
operator|.
name|get
argument_list|(
name|accessTokenProviderKey
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|abfsUrl
init|=
name|this
operator|.
name|getFileSystemName
argument_list|()
operator|+
literal|"@"
operator|+
name|this
operator|.
name|getAccountName
argument_list|()
decl_stmt|;
name|URI
name|defaultUri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|defaultUri
operator|=
operator|new
name|URI
argument_list|(
name|abfsScheme
argument_list|,
name|abfsUrl
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|this
operator|.
name|testUrl
operator|=
name|defaultUri
operator|.
name|toString
argument_list|()
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|defaultUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|setBoolean
argument_list|(
name|AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|isEmulator
operator|=
name|this
operator|.
name|configuration
operator|.
name|getBoolean
argument_list|(
name|FS_AZURE_EMULATOR_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Create filesystem first to make sure getWasbFileSystem() can return an existing filesystem.
name|createFileSystem
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isEmulator
operator|&&
name|authType
operator|==
name|AuthType
operator|.
name|SharedKey
condition|)
block|{
specifier|final
name|URI
name|wasbUri
init|=
operator|new
name|URI
argument_list|(
name|abfsUrlToWasbUrl
argument_list|(
name|getTestUrl
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AzureNativeFileSystemStore
name|azureNativeFileSystemStore
init|=
operator|new
name|AzureNativeFileSystemStore
argument_list|()
decl_stmt|;
name|azureNativeFileSystemStore
operator|.
name|initialize
argument_list|(
name|wasbUri
argument_list|,
name|getConfiguration
argument_list|()
argument_list|,
operator|new
name|AzureFileSystemInstrumentation
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|wasb
operator|=
operator|new
name|NativeAzureFileSystem
argument_list|(
name|azureNativeFileSystemStore
argument_list|)
expr_stmt|;
name|wasb
operator|.
name|initialize
argument_list|(
name|wasbUri
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|wasb
argument_list|)
expr_stmt|;
name|wasb
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|abfs
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|AzureBlobFileSystemStore
name|abfsStore
init|=
name|abfs
operator|.
name|getAbfsStore
argument_list|()
decl_stmt|;
name|abfsStore
operator|.
name|deleteFilesystem
argument_list|()
expr_stmt|;
name|AbfsRestOperationException
name|ex
init|=
name|intercept
argument_list|(
name|AbfsRestOperationException
operator|.
name|class
argument_list|,
operator|new
name|Callable
argument_list|<
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|abfsStore
operator|.
name|getFilesystemProperties
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|FILE_SYSTEM_NOT_FOUND
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|ex
operator|.
name|getStatusCode
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Deleted test filesystem may still exist: {}"
argument_list|,
name|abfs
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"During cleanup: {}"
argument_list|,
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|abfs
argument_list|)
expr_stmt|;
name|abfs
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getFileSystem ()
specifier|public
name|AzureBlobFileSystem
name|getFileSystem
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|abfs
return|;
block|}
DECL|method|getFileSystem (Configuration configuration)
specifier|public
name|AzureBlobFileSystem
name|getFileSystem
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
operator|(
name|AzureBlobFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
return|return
name|fs
return|;
block|}
DECL|method|getFileSystem (String abfsUri)
specifier|public
name|AzureBlobFileSystem
name|getFileSystem
parameter_list|(
name|String
name|abfsUri
parameter_list|)
throws|throws
name|Exception
block|{
name|configuration
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|abfsUri
argument_list|)
expr_stmt|;
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
operator|(
name|AzureBlobFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
return|return
name|fs
return|;
block|}
comment|/**    * Creates the filesystem; updates the {@link #abfs} field.    * @return the created filesystem.    * @throws IOException failure during create/init.    */
DECL|method|createFileSystem ()
specifier|public
name|AzureBlobFileSystem
name|createFileSystem
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|abfs
operator|==
literal|null
argument_list|,
literal|"existing ABFS instance exists: %s"
argument_list|,
name|abfs
argument_list|)
expr_stmt|;
name|abfs
operator|=
operator|(
name|AzureBlobFileSystem
operator|)
name|FileSystem
operator|.
name|newInstance
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
return|return
name|abfs
return|;
block|}
DECL|method|getWasbFileSystem ()
specifier|protected
name|NativeAzureFileSystem
name|getWasbFileSystem
parameter_list|()
block|{
return|return
name|wasb
return|;
block|}
DECL|method|getHostName ()
specifier|protected
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|configuration
operator|.
name|get
argument_list|(
name|FS_AZURE_TEST_HOST_NAME
argument_list|)
return|;
block|}
DECL|method|setTestUrl (String testUrl)
specifier|protected
name|void
name|setTestUrl
parameter_list|(
name|String
name|testUrl
parameter_list|)
block|{
name|this
operator|.
name|testUrl
operator|=
name|testUrl
expr_stmt|;
block|}
DECL|method|getTestUrl ()
specifier|protected
name|String
name|getTestUrl
parameter_list|()
block|{
return|return
name|testUrl
return|;
block|}
DECL|method|setFileSystemName (String fileSystemName)
specifier|protected
name|void
name|setFileSystemName
parameter_list|(
name|String
name|fileSystemName
parameter_list|)
block|{
name|this
operator|.
name|fileSystemName
operator|=
name|fileSystemName
expr_stmt|;
block|}
DECL|method|getFileSystemName ()
specifier|protected
name|String
name|getFileSystemName
parameter_list|()
block|{
return|return
name|fileSystemName
return|;
block|}
DECL|method|getAccountName ()
specifier|protected
name|String
name|getAccountName
parameter_list|()
block|{
return|return
name|configuration
operator|.
name|get
argument_list|(
name|FS_AZURE_TEST_ACCOUNT_NAME
argument_list|)
return|;
block|}
DECL|method|getAccountKey ()
specifier|protected
name|String
name|getAccountKey
parameter_list|()
block|{
return|return
name|configuration
operator|.
name|get
argument_list|(
name|FS_AZURE_TEST_ACCOUNT_KEY_PREFIX
operator|+
name|getAccountName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getConfiguration ()
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
DECL|method|isEmulator ()
specifier|protected
name|boolean
name|isEmulator
parameter_list|()
block|{
return|return
name|isEmulator
return|;
block|}
DECL|method|getAuthType ()
specifier|protected
name|AuthType
name|getAuthType
parameter_list|()
block|{
return|return
name|this
operator|.
name|authType
return|;
block|}
comment|/**    * Write a buffer to a file.    * @param path path    * @param buffer buffer    * @throws IOException failure    */
DECL|method|write (Path path, byte[] buffer)
specifier|protected
name|void
name|write
parameter_list|(
name|Path
name|path
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|ContractTestUtils
operator|.
name|writeDataset
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|,
name|buffer
argument_list|,
name|buffer
operator|.
name|length
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Touch a file in the test store. Will overwrite any existing file.    * @param path path    * @throws IOException failure.    */
DECL|method|touch (Path path)
specifier|protected
name|void
name|touch
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|ContractTestUtils
operator|.
name|touch
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|wasbUrlToAbfsUrl (final String wasbUrl)
specifier|protected
specifier|static
name|String
name|wasbUrlToAbfsUrl
parameter_list|(
specifier|final
name|String
name|wasbUrl
parameter_list|)
block|{
return|return
name|convertTestUrls
argument_list|(
name|wasbUrl
argument_list|,
name|FileSystemUriSchemes
operator|.
name|WASB_SCHEME
argument_list|,
name|FileSystemUriSchemes
operator|.
name|WASB_SECURE_SCHEME
argument_list|,
name|FileSystemUriSchemes
operator|.
name|WASB_DNS_PREFIX
argument_list|,
name|FileSystemUriSchemes
operator|.
name|ABFS_SCHEME
argument_list|,
name|FileSystemUriSchemes
operator|.
name|ABFS_SECURE_SCHEME
argument_list|,
name|FileSystemUriSchemes
operator|.
name|ABFS_DNS_PREFIX
argument_list|)
return|;
block|}
DECL|method|abfsUrlToWasbUrl (final String abfsUrl)
specifier|protected
specifier|static
name|String
name|abfsUrlToWasbUrl
parameter_list|(
specifier|final
name|String
name|abfsUrl
parameter_list|)
block|{
return|return
name|convertTestUrls
argument_list|(
name|abfsUrl
argument_list|,
name|FileSystemUriSchemes
operator|.
name|ABFS_SCHEME
argument_list|,
name|FileSystemUriSchemes
operator|.
name|ABFS_SECURE_SCHEME
argument_list|,
name|FileSystemUriSchemes
operator|.
name|ABFS_DNS_PREFIX
argument_list|,
name|FileSystemUriSchemes
operator|.
name|WASB_SCHEME
argument_list|,
name|FileSystemUriSchemes
operator|.
name|WASB_SECURE_SCHEME
argument_list|,
name|FileSystemUriSchemes
operator|.
name|WASB_DNS_PREFIX
argument_list|)
return|;
block|}
DECL|method|convertTestUrls ( final String url, final String fromNonSecureScheme, final String fromSecureScheme, final String fromDnsPrefix, final String toNonSecureScheme, final String toSecureScheme, final String toDnsPrefix)
specifier|private
specifier|static
name|String
name|convertTestUrls
parameter_list|(
specifier|final
name|String
name|url
parameter_list|,
specifier|final
name|String
name|fromNonSecureScheme
parameter_list|,
specifier|final
name|String
name|fromSecureScheme
parameter_list|,
specifier|final
name|String
name|fromDnsPrefix
parameter_list|,
specifier|final
name|String
name|toNonSecureScheme
parameter_list|,
specifier|final
name|String
name|toSecureScheme
parameter_list|,
specifier|final
name|String
name|toDnsPrefix
parameter_list|)
block|{
name|String
name|data
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
name|fromNonSecureScheme
operator|+
literal|"://"
argument_list|)
condition|)
block|{
name|data
operator|=
name|url
operator|.
name|replace
argument_list|(
name|fromNonSecureScheme
operator|+
literal|"://"
argument_list|,
name|toNonSecureScheme
operator|+
literal|"://"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
name|fromSecureScheme
operator|+
literal|"://"
argument_list|)
condition|)
block|{
name|data
operator|=
name|url
operator|.
name|replace
argument_list|(
name|fromSecureScheme
operator|+
literal|"://"
argument_list|,
name|toSecureScheme
operator|+
literal|"://"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|data
operator|=
name|data
operator|.
name|replace
argument_list|(
literal|"."
operator|+
name|fromDnsPrefix
operator|+
literal|"."
argument_list|,
literal|"."
operator|+
name|toDnsPrefix
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
DECL|method|getTestPath ()
specifier|public
name|Path
name|getTestPath
parameter_list|()
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|UriUtils
operator|.
name|generateUniqueTestPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|path
return|;
block|}
comment|/**    * Create a path under the test path provided by    * {@link #getTestPath()}.    * @param filepath path string in    * @return a path qualified by the test filesystem    * @throws IOException IO problems    */
DECL|method|path (String filepath)
specifier|protected
name|Path
name|path
parameter_list|(
name|String
name|filepath
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|filepath
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

