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

begin_comment
comment|/**  *  DtFetcher is an interface which permits the abstraction and separation of  *  delegation token fetch implementaions across different packages and  *  compilation units.  Resolution of fetcher impl will be done at runtime.  */
end_comment

begin_interface
DECL|interface|DtFetcher
specifier|public
interface|interface
name|DtFetcher
block|{
comment|/** Return a key used to identify the object/service implementation. */
DECL|method|getServiceName ()
name|Text
name|getServiceName
parameter_list|()
function_decl|;
comment|/** Used to allow the service API to indicate whether a token is required. */
DECL|method|isTokenRequired ()
name|boolean
name|isTokenRequired
parameter_list|()
function_decl|;
comment|/** Add any number of delegation tokens to Credentials object and return    *  a token instance that is appropriate for aliasing, or null if none. */
DECL|method|addDelegationTokens (Configuration conf, Credentials creds, String renewer, String url)
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
function_decl|;
block|}
end_interface

end_unit

