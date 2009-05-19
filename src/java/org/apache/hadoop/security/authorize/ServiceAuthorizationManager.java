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
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Permission
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedActionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**  * An authorization manager which handles service-level authorization  * for incoming service requests.  */
end_comment

begin_class
DECL|class|ServiceAuthorizationManager
specifier|public
class|class
name|ServiceAuthorizationManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ServiceAuthorizationManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Configuration key for controlling service-level authorization for Hadoop.    */
DECL|field|SERVICE_AUTHORIZATION_CONFIG
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_AUTHORIZATION_CONFIG
init|=
literal|"hadoop.security.authorization"
decl_stmt|;
DECL|field|protocolToPermissionMap
specifier|private
specifier|static
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Permission
argument_list|>
name|protocolToPermissionMap
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Permission
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Authorize the user to access the protocol being used.    *     * @param user user accessing the service     * @param protocol service being accessed    * @throws AuthorizationException on authorization failure    */
DECL|method|authorize (Subject user, Class<?> protocol)
specifier|public
specifier|static
name|void
name|authorize
parameter_list|(
name|Subject
name|user
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|)
throws|throws
name|AuthorizationException
block|{
name|Permission
name|permission
init|=
name|protocolToPermissionMap
operator|.
name|get
argument_list|(
name|protocol
argument_list|)
decl_stmt|;
if|if
condition|(
name|permission
operator|==
literal|null
condition|)
block|{
name|permission
operator|=
operator|new
name|ConnectionPermission
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
name|protocolToPermissionMap
operator|.
name|put
argument_list|(
name|protocol
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
name|checkPermission
argument_list|(
name|user
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check if the given {@link Subject} has all of necessary {@link Permission}     * set.    *     * @param user<code>Subject</code> to be authorized    * @param permissions<code>Permission</code> set    * @throws AuthorizationException if the authorization failed    */
DECL|method|checkPermission (final Subject user, final Permission... permissions)
specifier|private
specifier|static
name|void
name|checkPermission
parameter_list|(
specifier|final
name|Subject
name|user
parameter_list|,
specifier|final
name|Permission
modifier|...
name|permissions
parameter_list|)
throws|throws
name|AuthorizationException
block|{
try|try
block|{
name|Subject
operator|.
name|doAs
argument_list|(
name|user
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
for|for
control|(
name|Permission
name|permission
range|:
name|permissions
control|)
block|{
name|AccessController
operator|.
name|checkPermission
argument_list|(
name|permission
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ace
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Authorization failed for "
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUGI
argument_list|()
argument_list|,
name|ace
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AuthorizationException
argument_list|(
name|ace
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PrivilegedActionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|(
name|e
operator|.
name|getException
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

