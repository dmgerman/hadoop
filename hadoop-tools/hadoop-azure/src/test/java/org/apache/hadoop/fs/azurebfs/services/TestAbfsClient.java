begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
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
name|services
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|AbfsConfiguration
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
name|security
operator|.
name|ssl
operator|.
name|DelegatingSSLSocketFactory
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
name|util
operator|.
name|VersionInfo
import|;
end_import

begin_comment
comment|/**  * Test useragent of abfs client.  *  */
end_comment

begin_class
DECL|class|TestAbfsClient
specifier|public
specifier|final
class|class
name|TestAbfsClient
block|{
DECL|field|accountName
specifier|private
specifier|final
name|String
name|accountName
init|=
literal|"bogusAccountName"
decl_stmt|;
DECL|method|validateUserAgent (String expectedPattern, URL baseUrl, AbfsConfiguration config, boolean includeSSLProvider)
specifier|private
name|void
name|validateUserAgent
parameter_list|(
name|String
name|expectedPattern
parameter_list|,
name|URL
name|baseUrl
parameter_list|,
name|AbfsConfiguration
name|config
parameter_list|,
name|boolean
name|includeSSLProvider
parameter_list|)
block|{
name|AbfsClient
name|client
init|=
operator|new
name|AbfsClient
argument_list|(
name|baseUrl
argument_list|,
literal|null
argument_list|,
name|config
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|sslProviderName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|includeSSLProvider
condition|)
block|{
name|sslProviderName
operator|=
name|DelegatingSSLSocketFactory
operator|.
name|getDefaultFactory
argument_list|()
operator|.
name|getProviderName
argument_list|()
expr_stmt|;
block|}
name|String
name|userAgent
init|=
name|client
operator|.
name|initializeUserAgent
argument_list|(
name|config
argument_list|,
name|sslProviderName
argument_list|)
decl_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|expectedPattern
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Incorrect User Agent String"
argument_list|,
name|pattern
operator|.
name|matcher
argument_list|(
name|userAgent
argument_list|)
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|verifyUnknownUserAgent ()
specifier|public
name|void
name|verifyUnknownUserAgent
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clientVersion
init|=
literal|"Azure Blob FS/"
operator|+
name|VersionInfo
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|String
name|expectedUserAgentPattern
init|=
name|String
operator|.
name|format
argument_list|(
name|clientVersion
operator|+
literal|" %s"
argument_list|,
literal|"\\(JavaJRE ([^\\)]+)\\)"
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|unset
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_USER_AGENT_PREFIX_KEY
argument_list|)
expr_stmt|;
name|AbfsConfiguration
name|abfsConfiguration
init|=
operator|new
name|AbfsConfiguration
argument_list|(
name|configuration
argument_list|,
name|accountName
argument_list|)
decl_stmt|;
name|validateUserAgent
argument_list|(
name|expectedUserAgentPattern
argument_list|,
operator|new
name|URL
argument_list|(
literal|"http://azure.com"
argument_list|)
argument_list|,
name|abfsConfiguration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|verifyUserAgent ()
specifier|public
name|void
name|verifyUserAgent
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clientVersion
init|=
literal|"Azure Blob FS/"
operator|+
name|VersionInfo
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|String
name|expectedUserAgentPattern
init|=
name|String
operator|.
name|format
argument_list|(
name|clientVersion
operator|+
literal|" %s"
argument_list|,
literal|"\\(JavaJRE ([^\\)]+)\\) Partner Service"
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_USER_AGENT_PREFIX_KEY
argument_list|,
literal|"Partner Service"
argument_list|)
expr_stmt|;
name|AbfsConfiguration
name|abfsConfiguration
init|=
operator|new
name|AbfsConfiguration
argument_list|(
name|configuration
argument_list|,
name|accountName
argument_list|)
decl_stmt|;
name|validateUserAgent
argument_list|(
name|expectedUserAgentPattern
argument_list|,
operator|new
name|URL
argument_list|(
literal|"http://azure.com"
argument_list|)
argument_list|,
name|abfsConfiguration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|verifyUserAgentWithSSLProvider ()
specifier|public
name|void
name|verifyUserAgentWithSSLProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clientVersion
init|=
literal|"Azure Blob FS/"
operator|+
name|VersionInfo
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|String
name|expectedUserAgentPattern
init|=
name|String
operator|.
name|format
argument_list|(
name|clientVersion
operator|+
literal|" %s"
argument_list|,
literal|"\\(JavaJRE ([^\\)]+)\\) Partner Service"
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_USER_AGENT_PREFIX_KEY
argument_list|,
literal|"Partner Service"
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_SSL_CHANNEL_MODE_KEY
argument_list|,
name|DelegatingSSLSocketFactory
operator|.
name|SSLChannelMode
operator|.
name|Default_JSSE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|AbfsConfiguration
name|abfsConfiguration
init|=
operator|new
name|AbfsConfiguration
argument_list|(
name|configuration
argument_list|,
name|accountName
argument_list|)
decl_stmt|;
name|validateUserAgent
argument_list|(
name|expectedUserAgentPattern
argument_list|,
operator|new
name|URL
argument_list|(
literal|"https://azure.com"
argument_list|)
argument_list|,
name|abfsConfiguration
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

