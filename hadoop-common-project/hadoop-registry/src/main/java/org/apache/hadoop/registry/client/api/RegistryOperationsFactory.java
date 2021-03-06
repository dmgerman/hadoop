begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|api
package|;
end_package

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
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|service
operator|.
name|ServiceStateException
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|RegistryOperationsClient
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryConstants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A factory for registry operation service instances.  *<p>  *<i>Each created instance will be returned initialized.</i>  *<p>  * That is, the service will have had<code>Service.init(conf)</code> applied  * to it âpossibly after the configuration has been modified to  * support the specific binding/security mechanism used  */
end_comment

begin_class
DECL|class|RegistryOperationsFactory
specifier|public
specifier|final
class|class
name|RegistryOperationsFactory
block|{
DECL|method|RegistryOperationsFactory ()
specifier|private
name|RegistryOperationsFactory
parameter_list|()
block|{   }
comment|/**    * Create and initialize a registry operations instance.    * Access writes will be determined from the configuration    * @param conf configuration    * @return a registry operations instance    * @throws ServiceStateException on any failure to initialize    */
DECL|method|createInstance (Configuration conf)
specifier|public
specifier|static
name|RegistryOperations
name|createInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|createInstance
argument_list|(
literal|"RegistryOperations"
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Create and initialize a registry operations instance.    * Access rights will be determined from the configuration    * @param name name of the instance    * @param conf configuration    * @return a registry operations instance    * @throws ServiceStateException on any failure to initialize    */
DECL|method|createInstance (String name, Configuration conf)
specifier|public
specifier|static
name|RegistryOperations
name|createInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|!=
literal|null
argument_list|,
literal|"Null configuration"
argument_list|)
expr_stmt|;
name|RegistryOperationsClient
name|operations
init|=
operator|new
name|RegistryOperationsClient
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|operations
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|operations
return|;
block|}
DECL|method|createClient (String name, Configuration conf)
specifier|public
specifier|static
name|RegistryOperationsClient
name|createClient
parameter_list|(
name|String
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|!=
literal|null
argument_list|,
literal|"Null configuration"
argument_list|)
expr_stmt|;
name|RegistryOperationsClient
name|operations
init|=
operator|new
name|RegistryOperationsClient
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|operations
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|operations
return|;
block|}
comment|/**    * Create and initialize an anonymous read/write registry operations instance.    * In a secure cluster, this instance will only have read access to the    * registry.    * @param conf configuration    * @return an anonymous registry operations instance    *    * @throws ServiceStateException on any failure to initialize    */
DECL|method|createAnonymousInstance (Configuration conf)
specifier|public
specifier|static
name|RegistryOperations
name|createAnonymousInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|!=
literal|null
argument_list|,
literal|"Null configuration"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_CLIENT_AUTH
argument_list|,
name|REGISTRY_CLIENT_AUTH_ANONYMOUS
argument_list|)
expr_stmt|;
return|return
name|createInstance
argument_list|(
literal|"AnonymousRegistryOperations"
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Create and initialize an secure, Kerberos-authenticated instance.    *    * The user identity will be inferred from the current user    *    * The authentication of this instance will expire when any kerberos    * tokens needed to authenticate with the registry infrastructure expire.    * @param conf configuration    * @param jaasContext the JAAS context of the account.    * @return a registry operations instance    * @throws ServiceStateException on any failure to initialize    */
DECL|method|createKerberosInstance (Configuration conf, String jaasContext)
specifier|public
specifier|static
name|RegistryOperations
name|createKerberosInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|jaasContext
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|!=
literal|null
argument_list|,
literal|"Null configuration"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_CLIENT_AUTH
argument_list|,
name|REGISTRY_CLIENT_AUTH_KERBEROS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_CLIENT_JAAS_CONTEXT
argument_list|,
name|jaasContext
argument_list|)
expr_stmt|;
return|return
name|createInstance
argument_list|(
literal|"KerberosRegistryOperations"
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Create a kerberos registry service client    * @param conf configuration    * @param jaasClientEntry the name of the login config entry    * @param principal principal of the client.    * @param keytab location to the keytab file    * @return a registry service client instance    */
DECL|method|createKerberosInstance (Configuration conf, String jaasClientEntry, String principal, String keytab)
specifier|public
specifier|static
name|RegistryOperations
name|createKerberosInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|jaasClientEntry
parameter_list|,
name|String
name|principal
parameter_list|,
name|String
name|keytab
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|!=
literal|null
argument_list|,
literal|"Null configuration"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_CLIENT_AUTH
argument_list|,
name|REGISTRY_CLIENT_AUTH_KERBEROS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_CLIENT_JAAS_CONTEXT
argument_list|,
name|jaasClientEntry
argument_list|)
expr_stmt|;
name|RegistryOperationsClient
name|operations
init|=
operator|new
name|RegistryOperationsClient
argument_list|(
literal|"KerberosRegistryOperations"
argument_list|)
decl_stmt|;
name|operations
operator|.
name|setKerberosPrincipalAndKeytab
argument_list|(
name|principal
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|operations
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|operations
return|;
block|}
comment|/**    * Create and initialize an operations instance authenticated with write    * access via an<code>id:password</code> pair.    *    * The instance will have the read access    * across the registry, but write access only to that part of the registry    * to which it has been give the relevant permissions.    * @param conf configuration    * @param id user ID    * @param password password    * @return a registry operations instance    * @throws ServiceStateException on any failure to initialize    * @throws IllegalArgumentException if an argument is invalid    */
DECL|method|createAuthenticatedInstance (Configuration conf, String id, String password)
specifier|public
specifier|static
name|RegistryOperations
name|createAuthenticatedInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|id
argument_list|)
argument_list|,
literal|"empty Id"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|password
argument_list|)
argument_list|,
literal|"empty Password"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|!=
literal|null
argument_list|,
literal|"Null configuration"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_CLIENT_AUTH
argument_list|,
name|REGISTRY_CLIENT_AUTH_DIGEST
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_CLIENT_AUTHENTICATION_ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_CLIENT_AUTHENTICATION_PASSWORD
argument_list|,
name|password
argument_list|)
expr_stmt|;
return|return
name|createInstance
argument_list|(
literal|"DigestRegistryOperations"
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

