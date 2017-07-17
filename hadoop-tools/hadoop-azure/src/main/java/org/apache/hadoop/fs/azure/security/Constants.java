begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure.security
package|package
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
name|security
package|;
end_package

begin_comment
comment|/**  * Constants for used with WASB security implementation.  */
end_comment

begin_class
DECL|class|Constants
specifier|public
specifier|final
class|class
name|Constants
block|{
comment|/**    * The configuration property to enable Kerberos support.    */
DECL|field|AZURE_KERBEROS_SUPPORT_PROPERTY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_KERBEROS_SUPPORT_PROPERTY_NAME
init|=
literal|"fs.azure.enable.kerberos.support"
decl_stmt|;
comment|/**    * The configuration property to enable SPNEGO token cache.    */
DECL|field|AZURE_ENABLE_SPNEGO_TOKEN_CACHE
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_ENABLE_SPNEGO_TOKEN_CACHE
init|=
literal|"fs.azure.enable.spnego.token.cache"
decl_stmt|;
comment|/**    * Parameter to be used for impersonation.    */
DECL|field|DOAS_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|DOAS_PARAM
init|=
literal|"doas"
decl_stmt|;
comment|/**    * Error message for Authentication failures.    */
DECL|field|AUTHENTICATION_FAILED_ERROR_MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|AUTHENTICATION_FAILED_ERROR_MESSAGE
init|=
literal|"Authentication Failed "
decl_stmt|;
DECL|method|Constants ()
specifier|private
name|Constants
parameter_list|()
block|{   }
block|}
end_class

end_unit

