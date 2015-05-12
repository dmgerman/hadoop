begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|resourcemanager
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|List
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
name|security
operator|.
name|authorize
operator|.
name|ProxyUsers
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
name|ApplicationAttemptId
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
name|ApplicationResourceUsageReport
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
name|ContainerId
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
name|NodeState
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
name|QueueInfo
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
name|Resource
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
name|ResourceBlacklistRequest
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
name|ResourceRequest
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
name|YarnApplicationAttemptState
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
name|YarnApplicationState
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
name|exceptions
operator|.
name|InvalidContainerReleaseException
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
name|exceptions
operator|.
name|InvalidResourceBlacklistRequestException
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
name|exceptions
operator|.
name|InvalidResourceRequestException
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|YarnAuthorizationProvider
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppState
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptState
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|SchedulerUtils
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|YarnScheduler
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
name|server
operator|.
name|utils
operator|.
name|BuilderUtils
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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_comment
comment|/**  * Utility methods to aid serving RM data through the REST and RPC APIs  */
end_comment

begin_class
DECL|class|RMServerUtils
specifier|public
class|class
name|RMServerUtils
block|{
DECL|method|queryRMNodes (RMContext context, EnumSet<NodeState> acceptedStates)
specifier|public
specifier|static
name|List
argument_list|<
name|RMNode
argument_list|>
name|queryRMNodes
parameter_list|(
name|RMContext
name|context
parameter_list|,
name|EnumSet
argument_list|<
name|NodeState
argument_list|>
name|acceptedStates
parameter_list|)
block|{
comment|// nodes contains nodes that are NEW, RUNNING OR UNHEALTHY
name|ArrayList
argument_list|<
name|RMNode
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|RMNode
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|acceptedStates
operator|.
name|contains
argument_list|(
name|NodeState
operator|.
name|NEW
argument_list|)
operator|||
name|acceptedStates
operator|.
name|contains
argument_list|(
name|NodeState
operator|.
name|RUNNING
argument_list|)
operator|||
name|acceptedStates
operator|.
name|contains
argument_list|(
name|NodeState
operator|.
name|UNHEALTHY
argument_list|)
condition|)
block|{
for|for
control|(
name|RMNode
name|rmNode
range|:
name|context
operator|.
name|getRMNodes
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|acceptedStates
operator|.
name|contains
argument_list|(
name|rmNode
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|rmNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// inactiveNodes contains nodes that are DECOMMISSIONED, LOST, OR REBOOTED
if|if
condition|(
name|acceptedStates
operator|.
name|contains
argument_list|(
name|NodeState
operator|.
name|DECOMMISSIONED
argument_list|)
operator|||
name|acceptedStates
operator|.
name|contains
argument_list|(
name|NodeState
operator|.
name|LOST
argument_list|)
operator|||
name|acceptedStates
operator|.
name|contains
argument_list|(
name|NodeState
operator|.
name|REBOOTED
argument_list|)
condition|)
block|{
for|for
control|(
name|RMNode
name|rmNode
range|:
name|context
operator|.
name|getInactiveRMNodes
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|acceptedStates
operator|.
name|contains
argument_list|(
name|rmNode
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|rmNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|results
return|;
block|}
comment|/**    * Utility method to validate a list resource requests, by insuring that the    * requested memory/vcore is non-negative and not greater than max    */
DECL|method|normalizeAndValidateRequests (List<ResourceRequest> ask, Resource maximumResource, String queueName, YarnScheduler scheduler, RMContext rmContext)
specifier|public
specifier|static
name|void
name|normalizeAndValidateRequests
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|String
name|queueName
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|InvalidResourceRequestException
block|{
comment|// Get queue from scheduler
name|QueueInfo
name|queueInfo
init|=
literal|null
decl_stmt|;
try|try
block|{
name|queueInfo
operator|=
name|scheduler
operator|.
name|getQueueInfo
argument_list|(
name|queueName
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
for|for
control|(
name|ResourceRequest
name|resReq
range|:
name|ask
control|)
block|{
name|SchedulerUtils
operator|.
name|normalizeAndvalidateRequest
argument_list|(
name|resReq
argument_list|,
name|maximumResource
argument_list|,
name|queueName
argument_list|,
name|scheduler
argument_list|,
name|rmContext
argument_list|,
name|queueInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * @throw<code>InvalidResourceBlacklistRequestException</code> if the    * resource is not able to be added to the blacklist.    */
DECL|method|validateBlacklistRequest ( ResourceBlacklistRequest blacklistRequest)
specifier|public
specifier|static
name|void
name|validateBlacklistRequest
parameter_list|(
name|ResourceBlacklistRequest
name|blacklistRequest
parameter_list|)
throws|throws
name|InvalidResourceBlacklistRequestException
block|{
if|if
condition|(
name|blacklistRequest
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|plus
init|=
name|blacklistRequest
operator|.
name|getBlacklistAdditions
argument_list|()
decl_stmt|;
if|if
condition|(
name|plus
operator|!=
literal|null
operator|&&
name|plus
operator|.
name|contains
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidResourceBlacklistRequestException
argument_list|(
literal|"Cannot add "
operator|+
name|ResourceRequest
operator|.
name|ANY
operator|+
literal|" to the blacklist!"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * It will validate to make sure all the containers belong to correct    * application attempt id. If not then it will throw    * {@link InvalidContainerReleaseException}    *     * @param containerReleaseList    *          containers to be released as requested by application master.    * @param appAttemptId    *          Application attempt Id    * @throws InvalidContainerReleaseException    */
specifier|public
specifier|static
name|void
DECL|method|validateContainerReleaseRequest (List<ContainerId> containerReleaseList, ApplicationAttemptId appAttemptId)
name|validateContainerReleaseRequest
parameter_list|(
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerReleaseList
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|InvalidContainerReleaseException
block|{
for|for
control|(
name|ContainerId
name|cId
range|:
name|containerReleaseList
control|)
block|{
if|if
condition|(
operator|!
name|appAttemptId
operator|.
name|equals
argument_list|(
name|cId
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidContainerReleaseException
argument_list|(
literal|"Cannot release container : "
operator|+
name|cId
operator|.
name|toString
argument_list|()
operator|+
literal|" not belonging to this application attempt : "
operator|+
name|appAttemptId
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|verifyAdminAccess ( YarnAuthorizationProvider authorizer, String method, final Log LOG)
specifier|public
specifier|static
name|UserGroupInformation
name|verifyAdminAccess
parameter_list|(
name|YarnAuthorizationProvider
name|authorizer
parameter_list|,
name|String
name|method
parameter_list|,
specifier|final
name|Log
name|LOG
parameter_list|)
throws|throws
name|IOException
block|{
comment|// by default, this method will use AdminService as module name
return|return
name|verifyAdminAccess
argument_list|(
name|authorizer
argument_list|,
name|method
argument_list|,
literal|"AdminService"
argument_list|,
name|LOG
argument_list|)
return|;
block|}
comment|/**    * Utility method to verify if the current user has access based on the    * passed {@link AccessControlList}    * @param authorizer the {@link AccessControlList} to check against    * @param method the method name to be logged    * @param module like AdminService or NodeLabelManager    * @param LOG the logger to use    * @return {@link UserGroupInformation} of the current user    * @throws IOException    */
DECL|method|verifyAdminAccess ( YarnAuthorizationProvider authorizer, String method, String module, final Log LOG)
specifier|public
specifier|static
name|UserGroupInformation
name|verifyAdminAccess
parameter_list|(
name|YarnAuthorizationProvider
name|authorizer
parameter_list|,
name|String
name|method
parameter_list|,
name|String
name|module
parameter_list|,
specifier|final
name|Log
name|LOG
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|user
decl_stmt|;
try|try
block|{
name|user
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't get current user"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
literal|"UNKNOWN"
argument_list|,
name|method
argument_list|,
literal|""
argument_list|,
literal|"AdminService"
argument_list|,
literal|"Couldn't get current user"
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
if|if
condition|(
operator|!
name|authorizer
operator|.
name|isAdmin
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"User "
operator|+
name|user
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|" doesn't have permission"
operator|+
literal|" to call '"
operator|+
name|method
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|method
argument_list|,
literal|""
argument_list|,
name|module
argument_list|,
name|RMAuditLogger
operator|.
name|AuditConstants
operator|.
name|UNAUTHORIZED_USER
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"User "
operator|+
name|user
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|" doesn't have permission"
operator|+
literal|" to call '"
operator|+
name|method
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|method
operator|+
literal|" invoked by user "
operator|+
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|user
return|;
block|}
DECL|method|createApplicationState ( RMAppState rmAppState)
specifier|public
specifier|static
name|YarnApplicationState
name|createApplicationState
parameter_list|(
name|RMAppState
name|rmAppState
parameter_list|)
block|{
switch|switch
condition|(
name|rmAppState
condition|)
block|{
case|case
name|NEW
case|:
return|return
name|YarnApplicationState
operator|.
name|NEW
return|;
case|case
name|NEW_SAVING
case|:
return|return
name|YarnApplicationState
operator|.
name|NEW_SAVING
return|;
case|case
name|SUBMITTED
case|:
return|return
name|YarnApplicationState
operator|.
name|SUBMITTED
return|;
case|case
name|ACCEPTED
case|:
return|return
name|YarnApplicationState
operator|.
name|ACCEPTED
return|;
case|case
name|RUNNING
case|:
return|return
name|YarnApplicationState
operator|.
name|RUNNING
return|;
case|case
name|FINISHING
case|:
case|case
name|FINISHED
case|:
return|return
name|YarnApplicationState
operator|.
name|FINISHED
return|;
case|case
name|KILLED
case|:
return|return
name|YarnApplicationState
operator|.
name|KILLED
return|;
case|case
name|FAILED
case|:
return|return
name|YarnApplicationState
operator|.
name|FAILED
return|;
default|default:
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Unknown state passed!"
argument_list|)
throw|;
block|}
block|}
DECL|method|createApplicationAttemptState ( RMAppAttemptState rmAppAttemptState)
specifier|public
specifier|static
name|YarnApplicationAttemptState
name|createApplicationAttemptState
parameter_list|(
name|RMAppAttemptState
name|rmAppAttemptState
parameter_list|)
block|{
switch|switch
condition|(
name|rmAppAttemptState
condition|)
block|{
case|case
name|NEW
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|NEW
return|;
case|case
name|SUBMITTED
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|SUBMITTED
return|;
case|case
name|SCHEDULED
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|SCHEDULED
return|;
case|case
name|ALLOCATED
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|ALLOCATED
return|;
case|case
name|LAUNCHED
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|LAUNCHED
return|;
case|case
name|ALLOCATED_SAVING
case|:
case|case
name|LAUNCHED_UNMANAGED_SAVING
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|ALLOCATED_SAVING
return|;
case|case
name|RUNNING
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|RUNNING
return|;
case|case
name|FINISHING
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|FINISHING
return|;
case|case
name|FINISHED
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|FINISHED
return|;
case|case
name|KILLED
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|KILLED
return|;
case|case
name|FAILED
case|:
return|return
name|YarnApplicationAttemptState
operator|.
name|FAILED
return|;
default|default:
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Unknown state passed!"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Statically defined dummy ApplicationResourceUsageREport.  Used as    * a return value when a valid report cannot be found.    */
specifier|public
specifier|static
specifier|final
name|ApplicationResourceUsageReport
DECL|field|DUMMY_APPLICATION_RESOURCE_USAGE_REPORT
name|DUMMY_APPLICATION_RESOURCE_USAGE_REPORT
init|=
name|BuilderUtils
operator|.
name|newApplicationResourceUsageReport
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Find all configs whose name starts with    * YarnConfiguration.RM_PROXY_USER_PREFIX, and add a record for each one by    * replacing the prefix with ProxyUsers.CONF_HADOOP_PROXYUSER    */
DECL|method|processRMProxyUsersConf (Configuration conf)
specifier|public
specifier|static
name|void
name|processRMProxyUsersConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|rmProxyUsers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|conf
control|)
block|{
name|String
name|propName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|propName
operator|.
name|startsWith
argument_list|(
name|YarnConfiguration
operator|.
name|RM_PROXY_USER_PREFIX
argument_list|)
condition|)
block|{
name|rmProxyUsers
operator|.
name|put
argument_list|(
name|ProxyUsers
operator|.
name|CONF_HADOOP_PROXYUSER
operator|+
literal|"."
operator|+
name|propName
operator|.
name|substring
argument_list|(
name|YarnConfiguration
operator|.
name|RM_PROXY_USER_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|rmProxyUsers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

