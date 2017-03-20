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
DECL|method|Constants ()
specifier|private
name|Constants
parameter_list|()
block|{   }
comment|/**    * Configuration parameter name expected in the Configuration    * object to provide the url of the remote service {@value}    */
DECL|field|KEY_CRED_SERVICE_URL
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CRED_SERVICE_URL
init|=
literal|"fs.azure.cred.service.url"
decl_stmt|;
comment|/**    * Default port of the remote service used as delegation token manager and Azure storage SAS key generator.    */
DECL|field|DEFAULT_CRED_SERVICE_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CRED_SERVICE_PORT
init|=
literal|50911
decl_stmt|;
comment|/**    * Default remote delegation token manager endpoint.    */
DECL|field|DEFAULT_DELEGATION_TOKEN_MANAGER_ENDPOINT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DELEGATION_TOKEN_MANAGER_ENDPOINT
init|=
literal|"/tokenmanager/v1"
decl_stmt|;
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
block|}
end_class

end_unit

