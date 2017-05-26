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
name|UnsupportedEncodingException
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
name|ipc
operator|.
name|CallerContext
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
name|ipc
operator|.
name|Server
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
name|Resource
import|;
end_import

begin_comment
comment|/**   * Manages ResourceManager audit logs.   *  * Audit log format is written as key=value pairs. Tab separated.  */
end_comment

begin_class
DECL|class|RMAuditLogger
specifier|public
class|class
name|RMAuditLogger
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
name|RMAuditLogger
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|enum|Keys
DECL|enumConstant|USER
DECL|enumConstant|OPERATION
DECL|enumConstant|TARGET
DECL|enumConstant|RESULT
DECL|enumConstant|IP
DECL|enumConstant|PERMISSIONS
enum|enum
name|Keys
block|{
name|USER
block|,
name|OPERATION
block|,
name|TARGET
block|,
name|RESULT
block|,
name|IP
block|,
name|PERMISSIONS
block|,
DECL|enumConstant|DESCRIPTION
DECL|enumConstant|APPID
DECL|enumConstant|APPATTEMPTID
DECL|enumConstant|CONTAINERID
name|DESCRIPTION
block|,
name|APPID
block|,
name|APPATTEMPTID
block|,
name|CONTAINERID
block|,
DECL|enumConstant|CALLERCONTEXT
DECL|enumConstant|CALLERSIGNATURE
DECL|enumConstant|RESOURCE
name|CALLERCONTEXT
block|,
name|CALLERSIGNATURE
block|,
name|RESOURCE
block|}
DECL|class|AuditConstants
specifier|public
specifier|static
class|class
name|AuditConstants
block|{
DECL|field|SUCCESS
specifier|static
specifier|final
name|String
name|SUCCESS
init|=
literal|"SUCCESS"
decl_stmt|;
DECL|field|FAILURE
specifier|static
specifier|final
name|String
name|FAILURE
init|=
literal|"FAILURE"
decl_stmt|;
DECL|field|KEY_VAL_SEPARATOR
specifier|static
specifier|final
name|String
name|KEY_VAL_SEPARATOR
init|=
literal|"="
decl_stmt|;
DECL|field|PAIR_SEPARATOR
specifier|static
specifier|final
name|char
name|PAIR_SEPARATOR
init|=
literal|'\t'
decl_stmt|;
DECL|field|FAIL_ATTEMPT_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|FAIL_ATTEMPT_REQUEST
init|=
literal|"Fail Attempt Request"
decl_stmt|;
DECL|field|KILL_APP_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|KILL_APP_REQUEST
init|=
literal|"Kill Application Request"
decl_stmt|;
DECL|field|SUBMIT_APP_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|SUBMIT_APP_REQUEST
init|=
literal|"Submit Application Request"
decl_stmt|;
DECL|field|MOVE_APP_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|MOVE_APP_REQUEST
init|=
literal|"Move Application Request"
decl_stmt|;
DECL|field|GET_APP_STATE
specifier|public
specifier|static
specifier|final
name|String
name|GET_APP_STATE
init|=
literal|"Get Application State"
decl_stmt|;
DECL|field|GET_APP_PRIORITY
specifier|public
specifier|static
specifier|final
name|String
name|GET_APP_PRIORITY
init|=
literal|"Get Application Priority"
decl_stmt|;
DECL|field|GET_APP_QUEUE
specifier|public
specifier|static
specifier|final
name|String
name|GET_APP_QUEUE
init|=
literal|"Get Application Queue"
decl_stmt|;
DECL|field|GET_APP_ATTEMPTS
specifier|public
specifier|static
specifier|final
name|String
name|GET_APP_ATTEMPTS
init|=
literal|"Get Application Attempts"
decl_stmt|;
DECL|field|GET_APP_ATTEMPT_REPORT
specifier|public
specifier|static
specifier|final
name|String
name|GET_APP_ATTEMPT_REPORT
init|=
literal|"Get Application Attempt Report"
decl_stmt|;
DECL|field|GET_CONTAINERS
specifier|public
specifier|static
specifier|final
name|String
name|GET_CONTAINERS
init|=
literal|"Get Containers"
decl_stmt|;
DECL|field|GET_CONTAINER_REPORT
specifier|public
specifier|static
specifier|final
name|String
name|GET_CONTAINER_REPORT
init|=
literal|"Get Container Report"
decl_stmt|;
DECL|field|FINISH_SUCCESS_APP
specifier|public
specifier|static
specifier|final
name|String
name|FINISH_SUCCESS_APP
init|=
literal|"Application Finished - Succeeded"
decl_stmt|;
DECL|field|FINISH_FAILED_APP
specifier|public
specifier|static
specifier|final
name|String
name|FINISH_FAILED_APP
init|=
literal|"Application Finished - Failed"
decl_stmt|;
DECL|field|FINISH_KILLED_APP
specifier|public
specifier|static
specifier|final
name|String
name|FINISH_KILLED_APP
init|=
literal|"Application Finished - Killed"
decl_stmt|;
DECL|field|REGISTER_AM
specifier|public
specifier|static
specifier|final
name|String
name|REGISTER_AM
init|=
literal|"Register App Master"
decl_stmt|;
DECL|field|AM_ALLOCATE
specifier|public
specifier|static
specifier|final
name|String
name|AM_ALLOCATE
init|=
literal|"App Master Heartbeats"
decl_stmt|;
DECL|field|UNREGISTER_AM
specifier|public
specifier|static
specifier|final
name|String
name|UNREGISTER_AM
init|=
literal|"Unregister App Master"
decl_stmt|;
DECL|field|ALLOC_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|ALLOC_CONTAINER
init|=
literal|"AM Allocated Container"
decl_stmt|;
DECL|field|RELEASE_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|RELEASE_CONTAINER
init|=
literal|"AM Released Container"
decl_stmt|;
DECL|field|UPDATE_APP_PRIORITY
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_APP_PRIORITY
init|=
literal|"Update Application Priority"
decl_stmt|;
DECL|field|UPDATE_APP_TIMEOUTS
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_APP_TIMEOUTS
init|=
literal|"Update Application Timeouts"
decl_stmt|;
DECL|field|GET_APP_TIMEOUTS
specifier|public
specifier|static
specifier|final
name|String
name|GET_APP_TIMEOUTS
init|=
literal|"Get Application Timeouts"
decl_stmt|;
DECL|field|CHANGE_CONTAINER_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|CHANGE_CONTAINER_RESOURCE
init|=
literal|"AM Changed Container Resource"
decl_stmt|;
DECL|field|SIGNAL_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|SIGNAL_CONTAINER
init|=
literal|"Signal Container Request"
decl_stmt|;
comment|// Some commonly used descriptions
DECL|field|UNAUTHORIZED_USER
specifier|public
specifier|static
specifier|final
name|String
name|UNAUTHORIZED_USER
init|=
literal|"Unauthorized user"
decl_stmt|;
comment|// For Reservation system
DECL|field|CREATE_NEW_RESERVATION_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_NEW_RESERVATION_REQUEST
init|=
literal|"Create "
operator|+
literal|"Reservation Request"
decl_stmt|;
DECL|field|SUBMIT_RESERVATION_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|SUBMIT_RESERVATION_REQUEST
init|=
literal|"Submit Reservation Request"
decl_stmt|;
DECL|field|UPDATE_RESERVATION_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_RESERVATION_REQUEST
init|=
literal|"Update Reservation Request"
decl_stmt|;
DECL|field|DELETE_RESERVATION_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|DELETE_RESERVATION_REQUEST
init|=
literal|"Delete Reservation Request"
decl_stmt|;
DECL|field|LIST_RESERVATION_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|LIST_RESERVATION_REQUEST
init|=
literal|"List "
operator|+
literal|"Reservation Request"
decl_stmt|;
block|}
DECL|method|createSuccessLog (String user, String operation, String target, ApplicationId appId, ApplicationAttemptId attemptId, ContainerId containerId, Resource resource)
specifier|static
name|String
name|createSuccessLog
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|target
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
return|return
name|createSuccessLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|target
argument_list|,
name|appId
argument_list|,
name|attemptId
argument_list|,
name|containerId
argument_list|,
name|resource
argument_list|,
literal|null
argument_list|,
name|Server
operator|.
name|getRemoteIp
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * A helper api for creating an audit log for a successful event.    */
DECL|method|createSuccessLog (String user, String operation, String target, ApplicationId appId, ApplicationAttemptId attemptId, ContainerId containerId, Resource resource, CallerContext callerContext, InetAddress ip)
specifier|static
name|String
name|createSuccessLog
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|target
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|resource
parameter_list|,
name|CallerContext
name|callerContext
parameter_list|,
name|InetAddress
name|ip
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|start
argument_list|(
name|Keys
operator|.
name|USER
argument_list|,
name|user
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|ip
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|IP
argument_list|,
name|ip
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|add
argument_list|(
name|Keys
operator|.
name|OPERATION
argument_list|,
name|operation
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Keys
operator|.
name|TARGET
argument_list|,
name|target
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Keys
operator|.
name|RESULT
argument_list|,
name|AuditConstants
operator|.
name|SUCCESS
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|appId
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|APPID
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attemptId
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|APPATTEMPTID
argument_list|,
name|attemptId
operator|.
name|toString
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|containerId
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|CONTAINERID
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|RESOURCE
argument_list|,
name|resource
operator|.
name|toString
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|appendCallerContext
argument_list|(
name|b
argument_list|,
name|callerContext
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|appendCallerContext (StringBuilder sb, CallerContext callerContext)
specifier|private
specifier|static
name|void
name|appendCallerContext
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|CallerContext
name|callerContext
parameter_list|)
block|{
name|String
name|context
init|=
literal|null
decl_stmt|;
name|byte
index|[]
name|signature
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|callerContext
operator|!=
literal|null
condition|)
block|{
name|context
operator|=
name|callerContext
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|signature
operator|=
name|callerContext
operator|.
name|getSignature
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|CALLERCONTEXT
argument_list|,
name|context
argument_list|,
name|sb
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|signature
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|String
name|sigStr
init|=
operator|new
name|String
argument_list|(
name|signature
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|Keys
operator|.
name|CALLERSIGNATURE
argument_list|,
name|sigStr
argument_list|,
name|sb
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|// ignore this signature
block|}
block|}
block|}
comment|/**    * Create a readable and parseable audit log string for a successful event.    *    * @param user User who made the service request to the ResourceManager    * @param operation Operation requested by the user.    * @param target The target on which the operation is being performed.     * @param appId Application Id in which operation was performed.    * @param containerId Container Id in which operation was performed.    * @param resource Resource associated with container.    *    *<br><br>    * Note that the {@link RMAuditLogger} uses tabs ('\t') as a key-val delimiter    * and hence the value fields should not contains tabs ('\t').    */
DECL|method|logSuccess (String user, String operation, String target, ApplicationId appId, ContainerId containerId, Resource resource)
specifier|public
specifier|static
name|void
name|logSuccess
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|target
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|createSuccessLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|target
argument_list|,
name|appId
argument_list|,
literal|null
argument_list|,
name|containerId
argument_list|,
name|resource
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a readable and parseable audit log string for a successful event.    *    * @param user User who made the service request to the ResourceManager.    * @param operation Operation requested by the user.    * @param target The target on which the operation is being performed.     * @param appId Application Id in which operation was performed.    * @param attemptId Application Attempt Id in which operation was performed.    *    *<br><br>    * Note that the {@link RMAuditLogger} uses tabs ('\t') as a key-val delimiter    * and hence the value fields should not contains tabs ('\t').    */
DECL|method|logSuccess (String user, String operation, String target, ApplicationId appId, ApplicationAttemptId attemptId)
specifier|public
specifier|static
name|void
name|logSuccess
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|target
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|createSuccessLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|target
argument_list|,
name|appId
argument_list|,
name|attemptId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logSuccess (String user, String operation, String target, ApplicationId appId, CallerContext callerContext)
specifier|public
specifier|static
name|void
name|logSuccess
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|target
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|CallerContext
name|callerContext
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|createSuccessLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|target
argument_list|,
name|appId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|callerContext
argument_list|,
name|Server
operator|.
name|getRemoteIp
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a readable and parseable audit log string for a successful event.    *    * @param user    *          User who made the service request to the ResourceManager.    * @param operation    *          Operation requested by the user.    * @param target    *          The target on which the operation is being performed.    * @param appId    *          Application Id in which operation was performed.    * @param ip    *          The ip address of the caller.    *    *<br>    *<br>    *          Note that the {@link RMAuditLogger} uses tabs ('\t') as a key-val    *          delimiter and hence the value fields should not contains tabs    *          ('\t').    */
DECL|method|logSuccess (String user, String operation, String target, ApplicationId appId, InetAddress ip)
specifier|public
specifier|static
name|void
name|logSuccess
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|target
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|InetAddress
name|ip
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|createSuccessLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|target
argument_list|,
name|appId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|ip
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a readable and parseable audit log string for a successful event.    *    * @param user User who made the service request to the ResourceManager.    * @param operation Operation requested by the user.    * @param target The target on which the operation is being performed.     * @param appId Application Id in which operation was performed.    *    *<br><br>    * Note that the {@link RMAuditLogger} uses tabs ('\t') as a key-val delimiter    * and hence the value fields should not contains tabs ('\t').    */
DECL|method|logSuccess (String user, String operation, String target, ApplicationId appId)
specifier|public
specifier|static
name|void
name|logSuccess
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|target
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|createSuccessLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|target
argument_list|,
name|appId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a readable and parseable audit log string for a successful event.    *    * @param user User who made the service request.     * @param operation Operation requested by the user.    * @param target The target on which the operation is being performed.     *    *<br><br>    * Note that the {@link RMAuditLogger} uses tabs ('\t') as a key-val delimiter    * and hence the value fields should not contains tabs ('\t').    */
DECL|method|logSuccess (String user, String operation, String target)
specifier|public
specifier|static
name|void
name|logSuccess
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|target
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|createSuccessLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|target
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createFailureLog (String user, String operation, String perm, String target, String description, ApplicationId appId, ApplicationAttemptId attemptId, ContainerId containerId, Resource resource, CallerContext callerContext)
specifier|static
name|String
name|createFailureLog
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|perm
parameter_list|,
name|String
name|target
parameter_list|,
name|String
name|description
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|resource
parameter_list|,
name|CallerContext
name|callerContext
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|start
argument_list|(
name|Keys
operator|.
name|USER
argument_list|,
name|user
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|addRemoteIP
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Keys
operator|.
name|OPERATION
argument_list|,
name|operation
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Keys
operator|.
name|TARGET
argument_list|,
name|target
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Keys
operator|.
name|RESULT
argument_list|,
name|AuditConstants
operator|.
name|FAILURE
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Keys
operator|.
name|DESCRIPTION
argument_list|,
name|description
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Keys
operator|.
name|PERMISSIONS
argument_list|,
name|perm
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|appId
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|APPID
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attemptId
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|APPATTEMPTID
argument_list|,
name|attemptId
operator|.
name|toString
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|containerId
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|CONTAINERID
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|RESOURCE
argument_list|,
name|resource
operator|.
name|toString
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|appendCallerContext
argument_list|(
name|b
argument_list|,
name|callerContext
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * A helper api for creating an audit log for a failure event.    */
DECL|method|createFailureLog (String user, String operation, String perm, String target, String description, ApplicationId appId, ApplicationAttemptId attemptId, ContainerId containerId, Resource resource)
specifier|static
name|String
name|createFailureLog
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|perm
parameter_list|,
name|String
name|target
parameter_list|,
name|String
name|description
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
return|return
name|createFailureLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|perm
argument_list|,
name|target
argument_list|,
name|description
argument_list|,
name|appId
argument_list|,
name|attemptId
argument_list|,
name|containerId
argument_list|,
name|resource
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Create a readable and parseable audit log string for a failed event.    *    * @param user User who made the service request.     * @param operation Operation requested by the user.    * @param perm Target permissions.     * @param target The target on which the operation is being performed.     * @param description Some additional information as to why the operation    *                    failed.    * @param appId Application Id in which operation was performed.    * @param containerId Container Id in which operation was performed.    * @param resource Resources associated with container.    *    *<br><br>    * Note that the {@link RMAuditLogger} uses tabs ('\t') as a key-val delimiter    * and hence the value fields should not contains tabs ('\t').    */
DECL|method|logFailure (String user, String operation, String perm, String target, String description, ApplicationId appId, ContainerId containerId, Resource resource)
specifier|public
specifier|static
name|void
name|logFailure
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|perm
parameter_list|,
name|String
name|target
parameter_list|,
name|String
name|description
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|createFailureLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|perm
argument_list|,
name|target
argument_list|,
name|description
argument_list|,
name|appId
argument_list|,
literal|null
argument_list|,
name|containerId
argument_list|,
name|resource
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a readable and parseable audit log string for a failed event.    *    * @param user User who made the service request.     * @param operation Operation requested by the user.    * @param perm Target permissions.    * @param target The target on which the operation is being performed.     * @param description Some additional information as to why the operation    *                    failed.    * @param appId ApplicationId in which operation was performed.    *    *<br><br>    * Note that the {@link RMAuditLogger} uses tabs ('\t') as a key-val delimiter    * and hence the value fields should not contains tabs ('\t').    */
DECL|method|logFailure (String user, String operation, String perm, String target, String description, ApplicationId appId, ApplicationAttemptId attemptId)
specifier|public
specifier|static
name|void
name|logFailure
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|perm
parameter_list|,
name|String
name|target
parameter_list|,
name|String
name|description
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|createFailureLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|perm
argument_list|,
name|target
argument_list|,
name|description
argument_list|,
name|appId
argument_list|,
name|attemptId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logFailure (String user, String operation, String perm, String target, String description, ApplicationId appId, CallerContext callerContext)
specifier|public
specifier|static
name|void
name|logFailure
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|perm
parameter_list|,
name|String
name|target
parameter_list|,
name|String
name|description
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|CallerContext
name|callerContext
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|createFailureLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|perm
argument_list|,
name|target
argument_list|,
name|description
argument_list|,
name|appId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|callerContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a readable and parseable audit log string for a failed event.    *    * @param user User who made the service request.     * @param operation Operation requested by the user.    * @param perm Target permissions.    * @param target The target on which the operation is being performed.     * @param description Some additional information as to why the operation    *                    failed.    * @param appId ApplicationId in which operation was performed.    *    *<br><br>    * Note that the {@link RMAuditLogger} uses tabs ('\t') as a key-val delimiter    * and hence the value fields should not contains tabs ('\t').    */
DECL|method|logFailure (String user, String operation, String perm, String target, String description, ApplicationId appId)
specifier|public
specifier|static
name|void
name|logFailure
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|perm
parameter_list|,
name|String
name|target
parameter_list|,
name|String
name|description
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|createFailureLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|perm
argument_list|,
name|target
argument_list|,
name|description
argument_list|,
name|appId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a readable and parseable audit log string for a failed event.    *    * @param user User who made the service request.    * @param operation Operation requested by the user.    * @param perm Target permissions.     * @param target The target on which the operation is being performed.     * @param description Some additional information as to why the operation    *                    failed.    *    *<br><br>    * Note that the {@link RMAuditLogger} uses tabs ('\t') as a key-val delimiter    * and hence the value fields should not contains tabs ('\t').    */
DECL|method|logFailure (String user, String operation, String perm, String target, String description)
specifier|public
specifier|static
name|void
name|logFailure
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|perm
parameter_list|,
name|String
name|target
parameter_list|,
name|String
name|description
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|createFailureLog
argument_list|(
name|user
argument_list|,
name|operation
argument_list|,
name|perm
argument_list|,
name|target
argument_list|,
name|description
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * A helper api to add remote IP address    */
DECL|method|addRemoteIP (StringBuilder b)
specifier|static
name|void
name|addRemoteIP
parameter_list|(
name|StringBuilder
name|b
parameter_list|)
block|{
name|InetAddress
name|ip
init|=
name|Server
operator|.
name|getRemoteIp
argument_list|()
decl_stmt|;
comment|// ip address can be null for testcases
if|if
condition|(
name|ip
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|Keys
operator|.
name|IP
argument_list|,
name|ip
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Adds the first key-val pair to the passed builder in the following format    * key=value    */
DECL|method|start (Keys key, String value, StringBuilder b)
specifier|static
name|void
name|start
parameter_list|(
name|Keys
name|key
parameter_list|,
name|String
name|value
parameter_list|,
name|StringBuilder
name|b
parameter_list|)
block|{
name|b
operator|.
name|append
argument_list|(
name|key
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|AuditConstants
operator|.
name|KEY_VAL_SEPARATOR
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Appends the key-val pair to the passed builder in the following format    *<pair-delim>key=value    */
DECL|method|add (Keys key, String value, StringBuilder b)
specifier|static
name|void
name|add
parameter_list|(
name|Keys
name|key
parameter_list|,
name|String
name|value
parameter_list|,
name|StringBuilder
name|b
parameter_list|)
block|{
name|b
operator|.
name|append
argument_list|(
name|AuditConstants
operator|.
name|PAIR_SEPARATOR
argument_list|)
operator|.
name|append
argument_list|(
name|key
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|AuditConstants
operator|.
name|KEY_VAL_SEPARATOR
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

