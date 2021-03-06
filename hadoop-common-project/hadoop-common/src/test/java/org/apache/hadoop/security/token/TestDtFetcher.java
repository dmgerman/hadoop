begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
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
name|io
operator|.
name|Text
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
name|Credentials
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
name|token
operator|.
name|Token
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
name|token
operator|.
name|DtFetcher
import|;
end_import

begin_class
DECL|class|TestDtFetcher
specifier|public
class|class
name|TestDtFetcher
implements|implements
name|DtFetcher
block|{
DECL|method|getServiceName ()
specifier|public
name|Text
name|getServiceName
parameter_list|()
block|{
return|return
name|TestDtUtilShell
operator|.
name|SERVICE_GET
return|;
block|}
DECL|method|isTokenRequired ()
specifier|public
name|boolean
name|isTokenRequired
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|addDelegationTokens (Configuration conf, Credentials creds, String renewer, String url)
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
name|addDelegationTokens
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Credentials
name|creds
parameter_list|,
name|String
name|renewer
parameter_list|,
name|String
name|url
parameter_list|)
throws|throws
name|Exception
block|{
name|creds
operator|.
name|addToken
argument_list|(
name|TestDtUtilShell
operator|.
name|MOCK_TOKEN
operator|.
name|getService
argument_list|()
argument_list|,
name|TestDtUtilShell
operator|.
name|MOCK_TOKEN
argument_list|)
expr_stmt|;
return|return
name|TestDtUtilShell
operator|.
name|MOCK_TOKEN
return|;
block|}
block|}
end_class

end_unit

