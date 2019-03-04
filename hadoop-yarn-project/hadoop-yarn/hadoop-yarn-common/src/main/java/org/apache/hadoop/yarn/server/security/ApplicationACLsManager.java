begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|security
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|AccessControlException
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAccessType
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|security
operator|.
name|AdminACLsManager
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ApplicationACLsManager
specifier|public
class|class
name|ApplicationACLsManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ApplicationACLsManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_YARN_APP_ACL
specifier|private
specifier|static
name|AccessControlList
name|DEFAULT_YARN_APP_ACL
init|=
operator|new
name|AccessControlList
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_YARN_APP_ACL
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|adminAclsManager
specifier|private
specifier|final
name|AdminACLsManager
name|adminAclsManager
decl_stmt|;
DECL|field|applicationACLS
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|>
name|applicationACLS
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationId
argument_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|ApplicationACLsManager ()
specifier|public
name|ApplicationACLsManager
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ApplicationACLsManager (Configuration conf)
specifier|public
name|ApplicationACLsManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|adminAclsManager
operator|=
operator|new
name|AdminACLsManager
argument_list|(
name|this
operator|.
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|areACLsEnabled ()
specifier|public
name|boolean
name|areACLsEnabled
parameter_list|()
block|{
return|return
name|adminAclsManager
operator|.
name|areACLsEnabled
argument_list|()
return|;
block|}
DECL|method|addApplication (ApplicationId appId, Map<ApplicationAccessType, String> acls)
specifier|public
name|void
name|addApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|)
block|{
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|AccessControlList
argument_list|>
name|finalMap
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationAccessType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|(
name|acls
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acl
range|:
name|acls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|finalMap
operator|.
name|put
argument_list|(
name|acl
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|acl
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|applicationACLS
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|finalMap
argument_list|)
expr_stmt|;
block|}
DECL|method|removeApplication (ApplicationId appId)
specifier|public
name|void
name|removeApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|this
operator|.
name|applicationACLS
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
expr_stmt|;
block|}
comment|/**    * If authorization is enabled, checks whether the user (in the callerUGI) is    * authorized to perform the access specified by 'applicationAccessType' on    * the application by checking if the user is applicationOwner or part of    * application ACL for the specific access-type.    *<ul>    *<li>The owner of the application can have all access-types on the    * application</li>    *<li>For all other users/groups application-acls are checked</li>    *</ul>    *     * @param callerUGI    * @param applicationAccessType    * @param applicationOwner    * @param applicationId    */
DECL|method|checkAccess (UserGroupInformation callerUGI, ApplicationAccessType applicationAccessType, String applicationOwner, ApplicationId applicationId)
specifier|public
name|boolean
name|checkAccess
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|ApplicationAccessType
name|applicationAccessType
parameter_list|,
name|String
name|applicationOwner
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Verifying access-type "
operator|+
name|applicationAccessType
operator|+
literal|" for "
operator|+
name|callerUGI
operator|+
literal|" on application "
operator|+
name|applicationId
operator|+
literal|" owned by "
operator|+
name|applicationOwner
argument_list|)
expr_stmt|;
block|}
name|String
name|user
init|=
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|areACLsEnabled
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|AccessControlList
name|applicationACL
init|=
name|DEFAULT_YARN_APP_ACL
decl_stmt|;
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
name|this
operator|.
name|applicationACLS
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|acls
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"ACL not found for application "
operator|+
name|applicationId
operator|+
literal|" owned by "
operator|+
name|applicationOwner
operator|+
literal|". Using default ["
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_YARN_APP_ACL
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|AccessControlList
name|applicationACLInMap
init|=
name|acls
operator|.
name|get
argument_list|(
name|applicationAccessType
argument_list|)
decl_stmt|;
if|if
condition|(
name|applicationACLInMap
operator|!=
literal|null
condition|)
block|{
name|applicationACL
operator|=
name|applicationACLInMap
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"ACL not found for access-type "
operator|+
name|applicationAccessType
operator|+
literal|" for application "
operator|+
name|applicationId
operator|+
literal|" owned by "
operator|+
name|applicationOwner
operator|+
literal|". Using default ["
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_YARN_APP_ACL
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Allow application-owner for any type of access on the application
if|if
condition|(
name|this
operator|.
name|adminAclsManager
operator|.
name|isAdmin
argument_list|(
name|callerUGI
argument_list|)
operator|||
name|user
operator|.
name|equals
argument_list|(
name|applicationOwner
argument_list|)
operator|||
name|applicationACL
operator|.
name|isUserAllowed
argument_list|(
name|callerUGI
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Check if the given user in an admin.    *    * @param calledUGI    *          UserGroupInformation for the user    * @return true if the user is an admin, false otherwise    */
DECL|method|isAdmin (final UserGroupInformation calledUGI)
specifier|public
specifier|final
name|boolean
name|isAdmin
parameter_list|(
specifier|final
name|UserGroupInformation
name|calledUGI
parameter_list|)
block|{
return|return
name|this
operator|.
name|adminAclsManager
operator|.
name|isAdmin
argument_list|(
name|calledUGI
argument_list|)
return|;
block|}
block|}
end_class

end_unit

