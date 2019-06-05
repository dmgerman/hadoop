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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|Charsets
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
name|TestConfigurationKeys
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
name|utils
operator|.
name|Base64
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
name|DEFAULT_READ_BUFFER_SIZE
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
name|DEFAULT_WRITE_BUFFER_SIZE
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
name|DEFAULT_MAX_RETRY_ATTEMPTS
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
name|DEFAULT_BACKOFF_INTERVAL
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
name|DEFAULT_MAX_BACKOFF_INTERVAL
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
name|DEFAULT_MIN_BACKOFF_INTERVAL
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
name|MAX_AZURE_BLOCK_SIZE
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
name|AZURE_BLOCK_LOCATION_HOST_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotEquals
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
name|utils
operator|.
name|SSLSocketFactoryEx
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test ConfigurationServiceFieldsValidation.  */
end_comment

begin_class
DECL|class|TestAbfsConfigurationFieldsValidation
specifier|public
class|class
name|TestAbfsConfigurationFieldsValidation
block|{
DECL|field|abfsConfiguration
specifier|private
name|AbfsConfiguration
name|abfsConfiguration
decl_stmt|;
DECL|field|INT_KEY
specifier|private
specifier|static
specifier|final
name|String
name|INT_KEY
init|=
literal|"intKey"
decl_stmt|;
DECL|field|LONG_KEY
specifier|private
specifier|static
specifier|final
name|String
name|LONG_KEY
init|=
literal|"longKey"
decl_stmt|;
DECL|field|STRING_KEY
specifier|private
specifier|static
specifier|final
name|String
name|STRING_KEY
init|=
literal|"stringKey"
decl_stmt|;
DECL|field|BASE64_KEY
specifier|private
specifier|static
specifier|final
name|String
name|BASE64_KEY
init|=
literal|"base64Key"
decl_stmt|;
DECL|field|BOOLEAN_KEY
specifier|private
specifier|static
specifier|final
name|String
name|BOOLEAN_KEY
init|=
literal|"booleanKey"
decl_stmt|;
DECL|field|DEFAULT_INT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_INT
init|=
literal|4194304
decl_stmt|;
DECL|field|DEFAULT_LONG
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_LONG
init|=
literal|4194304
decl_stmt|;
DECL|field|TEST_INT
specifier|private
specifier|static
specifier|final
name|int
name|TEST_INT
init|=
literal|1234565
decl_stmt|;
DECL|field|TEST_LONG
specifier|private
specifier|static
specifier|final
name|int
name|TEST_LONG
init|=
literal|4194304
decl_stmt|;
DECL|field|accountName
specifier|private
specifier|final
name|String
name|accountName
decl_stmt|;
DECL|field|encodedString
specifier|private
specifier|final
name|String
name|encodedString
decl_stmt|;
DECL|field|encodedAccountKey
specifier|private
specifier|final
name|String
name|encodedAccountKey
decl_stmt|;
annotation|@
name|IntegerConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|INT_KEY
argument_list|,
name|MinValue
operator|=
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|MaxValue
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|DefaultValue
operator|=
name|DEFAULT_INT
argument_list|)
DECL|field|intField
specifier|private
name|int
name|intField
decl_stmt|;
annotation|@
name|LongConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|LONG_KEY
argument_list|,
name|MinValue
operator|=
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|MaxValue
operator|=
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|DefaultValue
operator|=
name|DEFAULT_LONG
argument_list|)
DECL|field|longField
specifier|private
name|int
name|longField
decl_stmt|;
annotation|@
name|StringConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|STRING_KEY
argument_list|,
name|DefaultValue
operator|=
literal|"default"
argument_list|)
DECL|field|stringField
specifier|private
name|String
name|stringField
decl_stmt|;
annotation|@
name|Base64StringConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|BASE64_KEY
argument_list|,
name|DefaultValue
operator|=
literal|"base64"
argument_list|)
DECL|field|base64Field
specifier|private
name|String
name|base64Field
decl_stmt|;
annotation|@
name|BooleanConfigurationValidatorAnnotation
argument_list|(
name|ConfigurationKey
operator|=
name|BOOLEAN_KEY
argument_list|,
name|DefaultValue
operator|=
literal|false
argument_list|)
DECL|field|boolField
specifier|private
name|boolean
name|boolField
decl_stmt|;
DECL|method|TestAbfsConfigurationFieldsValidation ()
specifier|public
name|TestAbfsConfigurationFieldsValidation
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|accountName
operator|=
literal|"testaccount1.blob.core.windows.net"
expr_stmt|;
name|this
operator|.
name|encodedString
operator|=
name|Base64
operator|.
name|encode
argument_list|(
literal|"base64Value"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|encodedAccountKey
operator|=
name|Base64
operator|.
name|encode
argument_list|(
literal|"someAccountKey"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|addResource
argument_list|(
name|TestConfigurationKeys
operator|.
name|TEST_CONFIGURATION_FILE_NAME
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|INT_KEY
argument_list|,
literal|"1234565"
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|LONG_KEY
argument_list|,
literal|"4194304"
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|STRING_KEY
argument_list|,
literal|"stringValue"
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|BASE64_KEY
argument_list|,
name|encodedString
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|BOOLEAN_KEY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME
operator|+
literal|"."
operator|+
name|accountName
argument_list|,
name|this
operator|.
name|encodedAccountKey
argument_list|)
expr_stmt|;
name|abfsConfiguration
operator|=
operator|new
name|AbfsConfiguration
argument_list|(
name|configuration
argument_list|,
name|accountName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateFunctionsInConfigServiceImpl ()
specifier|public
name|void
name|testValidateFunctionsInConfigServiceImpl
parameter_list|()
throws|throws
name|Exception
block|{
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
name|assertEquals
argument_list|(
name|TEST_INT
argument_list|,
name|abfsConfiguration
operator|.
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
name|assertEquals
argument_list|(
name|DEFAULT_LONG
argument_list|,
name|abfsConfiguration
operator|.
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
name|assertEquals
argument_list|(
literal|"stringValue"
argument_list|,
name|abfsConfiguration
operator|.
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
name|assertEquals
argument_list|(
name|this
operator|.
name|encodedString
argument_list|,
name|abfsConfiguration
operator|.
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
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|abfsConfiguration
operator|.
name|validateBoolean
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testConfigServiceImplAnnotatedFieldsInitialized ()
specifier|public
name|void
name|testConfigServiceImplAnnotatedFieldsInitialized
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test that all the ConfigurationServiceImpl annotated fields have been initialized in the constructor
name|assertEquals
argument_list|(
name|DEFAULT_WRITE_BUFFER_SIZE
argument_list|,
name|abfsConfiguration
operator|.
name|getWriteBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_READ_BUFFER_SIZE
argument_list|,
name|abfsConfiguration
operator|.
name|getReadBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_MIN_BACKOFF_INTERVAL
argument_list|,
name|abfsConfiguration
operator|.
name|getMinBackoffIntervalMilliseconds
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_MAX_BACKOFF_INTERVAL
argument_list|,
name|abfsConfiguration
operator|.
name|getMaxBackoffIntervalMilliseconds
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_BACKOFF_INTERVAL
argument_list|,
name|abfsConfiguration
operator|.
name|getBackoffIntervalMilliseconds
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_MAX_RETRY_ATTEMPTS
argument_list|,
name|abfsConfiguration
operator|.
name|getMaxIoRetries
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX_AZURE_BLOCK_SIZE
argument_list|,
name|abfsConfiguration
operator|.
name|getAzureBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AZURE_BLOCK_LOCATION_HOST_DEFAULT
argument_list|,
name|abfsConfiguration
operator|.
name|getAzureBlockLocationHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetAccountKey ()
specifier|public
name|void
name|testGetAccountKey
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|accountKey
init|=
name|abfsConfiguration
operator|.
name|getStorageAccountKey
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|encodedAccountKey
argument_list|,
name|accountKey
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConfigurationPropertyNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testGetAccountKeyWithNonExistingAccountName ()
specifier|public
name|void
name|testGetAccountKeyWithNonExistingAccountName
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|addResource
argument_list|(
name|TestConfigurationKeys
operator|.
name|TEST_CONFIGURATION_FILE_NAME
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|unset
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME
argument_list|)
expr_stmt|;
name|AbfsConfiguration
name|abfsConfig
init|=
operator|new
name|AbfsConfiguration
argument_list|(
name|configuration
argument_list|,
literal|"bogusAccountName"
argument_list|)
decl_stmt|;
name|abfsConfig
operator|.
name|getStorageAccountKey
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSSLSocketFactoryConfiguration ()
specifier|public
name|void
name|testSSLSocketFactoryConfiguration
parameter_list|()
throws|throws
name|InvalidConfigurationValueException
throws|,
name|IllegalAccessException
throws|,
name|IOException
block|{
name|assertEquals
argument_list|(
name|SSLSocketFactoryEx
operator|.
name|SSLChannelMode
operator|.
name|Default
argument_list|,
name|abfsConfiguration
operator|.
name|getPreferredSSLFactoryOption
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|SSLSocketFactoryEx
operator|.
name|SSLChannelMode
operator|.
name|Default_JSSE
argument_list|,
name|abfsConfiguration
operator|.
name|getPreferredSSLFactoryOption
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|SSLSocketFactoryEx
operator|.
name|SSLChannelMode
operator|.
name|OpenSSL
argument_list|,
name|abfsConfiguration
operator|.
name|getPreferredSSLFactoryOption
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setEnum
argument_list|(
name|FS_AZURE_SSL_CHANNEL_MODE_KEY
argument_list|,
name|SSLSocketFactoryEx
operator|.
name|SSLChannelMode
operator|.
name|Default_JSSE
argument_list|)
expr_stmt|;
name|AbfsConfiguration
name|localAbfsConfiguration
init|=
operator|new
name|AbfsConfiguration
argument_list|(
name|configuration
argument_list|,
name|accountName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SSLSocketFactoryEx
operator|.
name|SSLChannelMode
operator|.
name|Default_JSSE
argument_list|,
name|localAbfsConfiguration
operator|.
name|getPreferredSSLFactoryOption
argument_list|()
argument_list|)
expr_stmt|;
name|configuration
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|configuration
operator|.
name|setEnum
argument_list|(
name|FS_AZURE_SSL_CHANNEL_MODE_KEY
argument_list|,
name|SSLSocketFactoryEx
operator|.
name|SSLChannelMode
operator|.
name|OpenSSL
argument_list|)
expr_stmt|;
name|localAbfsConfiguration
operator|=
operator|new
name|AbfsConfiguration
argument_list|(
name|configuration
argument_list|,
name|accountName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SSLSocketFactoryEx
operator|.
name|SSLChannelMode
operator|.
name|OpenSSL
argument_list|,
name|localAbfsConfiguration
operator|.
name|getPreferredSSLFactoryOption
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

