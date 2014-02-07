begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authorize
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|CommonConfigurationKeys
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
name|KerberosInfo
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
name|SecurityUtil
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
name|UserGroupInformation
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

begin_comment
comment|/**  * An authorization manager which handles service-level authorization  * for incoming service requests.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ServiceAuthorizationManager
specifier|public
class|class
name|ServiceAuthorizationManager
block|{
DECL|field|HADOOP_POLICY_FILE
specifier|private
specifier|static
specifier|final
name|String
name|HADOOP_POLICY_FILE
init|=
literal|"hadoop-policy.xml"
decl_stmt|;
DECL|field|protocolToAcl
specifier|private
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|AccessControlList
argument_list|>
name|protocolToAcl
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Configuration key for controlling service-level authorization for Hadoop.    *     * @deprecated Use    *             {@link CommonConfigurationKeys#HADOOP_SECURITY_AUTHORIZATION}    *             instead.    */
annotation|@
name|Deprecated
DECL|field|SERVICE_AUTHORIZATION_CONFIG
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_AUTHORIZATION_CONFIG
init|=
literal|"hadoop.security.authorization"
decl_stmt|;
DECL|field|AUDITLOG
specifier|public
specifier|static
specifier|final
name|Log
name|AUDITLOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"SecurityLogger."
operator|+
name|ServiceAuthorizationManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|AUTHZ_SUCCESSFUL_FOR
specifier|private
specifier|static
specifier|final
name|String
name|AUTHZ_SUCCESSFUL_FOR
init|=
literal|"Authorization successful for "
decl_stmt|;
DECL|field|AUTHZ_FAILED_FOR
specifier|private
specifier|static
specifier|final
name|String
name|AUTHZ_FAILED_FOR
init|=
literal|"Authorization failed for "
decl_stmt|;
comment|/**    * Authorize the user to access the protocol being used.    *     * @param user user accessing the service     * @param protocol service being accessed    * @param conf configuration to use    * @param addr InetAddress of the client    * @throws AuthorizationException on authorization failure    */
DECL|method|authorize (UserGroupInformation user, Class<?> protocol, Configuration conf, InetAddress addr )
specifier|public
name|void
name|authorize
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|InetAddress
name|addr
parameter_list|)
throws|throws
name|AuthorizationException
block|{
name|AccessControlList
name|acl
init|=
name|protocolToAcl
operator|.
name|get
argument_list|(
name|protocol
argument_list|)
decl_stmt|;
if|if
condition|(
name|acl
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|(
literal|"Protocol "
operator|+
name|protocol
operator|+
literal|" is not known."
argument_list|)
throw|;
block|}
comment|// get client principal key to verify (if available)
name|KerberosInfo
name|krbInfo
init|=
name|SecurityUtil
operator|.
name|getKerberosInfo
argument_list|(
name|protocol
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|String
name|clientPrincipal
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|krbInfo
operator|!=
literal|null
condition|)
block|{
name|String
name|clientKey
init|=
name|krbInfo
operator|.
name|clientPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientKey
operator|!=
literal|null
operator|&&
operator|!
name|clientKey
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|clientPrincipal
operator|=
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|clientKey
argument_list|)
argument_list|,
name|addr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|AuthorizationException
operator|)
operator|new
name|AuthorizationException
argument_list|(
literal|"Can't figure out Kerberos principal name for connection from "
operator|+
name|addr
operator|+
literal|" for user="
operator|+
name|user
operator|+
literal|" protocol="
operator|+
name|protocol
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
operator|(
name|clientPrincipal
operator|!=
literal|null
operator|&&
operator|!
name|clientPrincipal
operator|.
name|equals
argument_list|(
name|user
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|)
operator|||
operator|!
name|acl
operator|.
name|isUserAllowed
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|AUDITLOG
operator|.
name|warn
argument_list|(
name|AUTHZ_FAILED_FOR
operator|+
name|user
operator|+
literal|" for protocol="
operator|+
name|protocol
operator|+
literal|", expected client Kerberos principal is "
operator|+
name|clientPrincipal
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AuthorizationException
argument_list|(
literal|"User "
operator|+
name|user
operator|+
literal|" is not authorized for protocol "
operator|+
name|protocol
operator|+
literal|", expected client Kerberos principal is "
operator|+
name|clientPrincipal
argument_list|)
throw|;
block|}
name|AUDITLOG
operator|.
name|info
argument_list|(
name|AUTHZ_SUCCESSFUL_FOR
operator|+
name|user
operator|+
literal|" for protocol="
operator|+
name|protocol
argument_list|)
expr_stmt|;
block|}
DECL|method|refresh (Configuration conf, PolicyProvider provider)
specifier|public
specifier|synchronized
name|void
name|refresh
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|PolicyProvider
name|provider
parameter_list|)
block|{
comment|// Get the system property 'hadoop.policy.file'
name|String
name|policyFile
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"hadoop.policy.file"
argument_list|,
name|HADOOP_POLICY_FILE
argument_list|)
decl_stmt|;
comment|// Make a copy of the original config, and load the policy file
name|Configuration
name|policyConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|policyConf
operator|.
name|addResource
argument_list|(
name|policyFile
argument_list|)
expr_stmt|;
name|refreshWithConfiguration
argument_list|(
name|policyConf
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
DECL|method|refreshWithConfiguration (Configuration conf, PolicyProvider provider)
specifier|public
specifier|synchronized
name|void
name|refreshWithConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|PolicyProvider
name|provider
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|AccessControlList
argument_list|>
name|newAcls
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
comment|// Parse the config file
name|Service
index|[]
name|services
init|=
name|provider
operator|.
name|getServices
argument_list|()
decl_stmt|;
if|if
condition|(
name|services
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Service
name|service
range|:
name|services
control|)
block|{
name|AccessControlList
name|acl
init|=
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|service
operator|.
name|getServiceKey
argument_list|()
argument_list|,
name|AccessControlList
operator|.
name|WILDCARD_ACL_VALUE
argument_list|)
argument_list|)
decl_stmt|;
name|newAcls
operator|.
name|put
argument_list|(
name|service
operator|.
name|getProtocol
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Flip to the newly parsed permissions
name|protocolToAcl
operator|=
name|newAcls
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getProtocolsWithAcls ()
specifier|public
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getProtocolsWithAcls
parameter_list|()
block|{
return|return
name|protocolToAcl
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getProtocolsAcls (Class<?> className)
specifier|public
name|AccessControlList
name|getProtocolsAcls
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|className
parameter_list|)
block|{
return|return
name|protocolToAcl
operator|.
name|get
argument_list|(
name|className
argument_list|)
return|;
block|}
block|}
end_class

end_unit

