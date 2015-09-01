begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.oauth2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|oauth2
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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|ConnectionConfigurator
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
name|ReflectionUtils
import|;
end_import

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
name|HttpURLConnection
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|ACCESS_TOKEN_PROVIDER_KEY
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|Utils
operator|.
name|notNull
import|;
end_import

begin_comment
comment|/**  * Configure a connection to use OAuth2 authentication.  */
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
DECL|class|OAuth2ConnectionConfigurator
specifier|public
class|class
name|OAuth2ConnectionConfigurator
implements|implements
name|ConnectionConfigurator
block|{
DECL|field|HEADER
specifier|public
specifier|static
specifier|final
name|String
name|HEADER
init|=
literal|"Bearer "
decl_stmt|;
DECL|field|accessTokenProvider
specifier|private
specifier|final
name|AccessTokenProvider
name|accessTokenProvider
decl_stmt|;
DECL|field|sslConfigurator
specifier|private
name|ConnectionConfigurator
name|sslConfigurator
init|=
literal|null
decl_stmt|;
DECL|method|OAuth2ConnectionConfigurator (Configuration conf)
specifier|public
name|OAuth2ConnectionConfigurator
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|OAuth2ConnectionConfigurator (Configuration conf, ConnectionConfigurator sslConfigurator)
specifier|public
name|OAuth2ConnectionConfigurator
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ConnectionConfigurator
name|sslConfigurator
parameter_list|)
block|{
name|this
operator|.
name|sslConfigurator
operator|=
name|sslConfigurator
expr_stmt|;
name|notNull
argument_list|(
name|conf
argument_list|,
name|ACCESS_TOKEN_PROVIDER_KEY
argument_list|)
expr_stmt|;
name|Class
name|accessTokenProviderClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|ACCESS_TOKEN_PROVIDER_KEY
argument_list|,
name|ConfCredentialBasedAccessTokenProvider
operator|.
name|class
argument_list|,
name|AccessTokenProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|accessTokenProvider
operator|=
operator|(
name|AccessTokenProvider
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|accessTokenProviderClass
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|accessTokenProvider
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure (HttpURLConnection conn)
specifier|public
name|HttpURLConnection
name|configure
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sslConfigurator
operator|!=
literal|null
condition|)
block|{
name|sslConfigurator
operator|.
name|configure
argument_list|(
name|conn
argument_list|)
expr_stmt|;
block|}
name|String
name|accessToken
init|=
name|accessTokenProvider
operator|.
name|getAccessToken
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"AUTHORIZATION"
argument_list|,
name|HEADER
operator|+
name|accessToken
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
block|}
end_class

end_unit

