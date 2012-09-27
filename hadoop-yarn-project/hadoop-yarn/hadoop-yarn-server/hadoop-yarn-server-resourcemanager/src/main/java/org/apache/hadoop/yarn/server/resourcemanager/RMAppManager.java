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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|DataInputByteBuffer
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
name|util
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
name|ApplicationSubmissionContext
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
name|event
operator|.
name|EventHandler
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
name|ipc
operator|.
name|RPCUtil
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
name|client
operator|.
name|ClientToAMSecretManager
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
name|client
operator|.
name|ClientTokenIdentifier
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
name|RMAuditLogger
operator|.
name|AuditConstants
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
name|recovery
operator|.
name|ApplicationsStore
operator|.
name|ApplicationStore
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
name|RMApp
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
name|RMAppEvent
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
name|RMAppEventType
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
name|RMAppImpl
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
name|RMAppRejectedEvent
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
name|RMAppAttempt
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
name|security
operator|.
name|ApplicationACLsManager
import|;
end_import

begin_comment
comment|/**  * This class manages the list of applications for the resource manager.   */
end_comment

begin_class
DECL|class|RMAppManager
specifier|public
class|class
name|RMAppManager
implements|implements
name|EventHandler
argument_list|<
name|RMAppManagerEvent
argument_list|>
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
name|RMAppManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|completedAppsMax
specifier|private
name|int
name|completedAppsMax
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_RM_MAX_COMPLETED_APPLICATIONS
decl_stmt|;
DECL|field|completedApps
specifier|private
name|LinkedList
argument_list|<
name|ApplicationId
argument_list|>
name|completedApps
init|=
operator|new
name|LinkedList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|clientToAMSecretManager
specifier|private
specifier|final
name|ClientToAMSecretManager
name|clientToAMSecretManager
decl_stmt|;
DECL|field|masterService
specifier|private
specifier|final
name|ApplicationMasterService
name|masterService
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|YarnScheduler
name|scheduler
decl_stmt|;
DECL|field|applicationACLsManager
specifier|private
specifier|final
name|ApplicationACLsManager
name|applicationACLsManager
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|RMAppManager (RMContext context, ClientToAMSecretManager clientToAMSecretManager, YarnScheduler scheduler, ApplicationMasterService masterService, ApplicationACLsManager applicationACLsManager, Configuration conf)
specifier|public
name|RMAppManager
parameter_list|(
name|RMContext
name|context
parameter_list|,
name|ClientToAMSecretManager
name|clientToAMSecretManager
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|,
name|ApplicationMasterService
name|masterService
parameter_list|,
name|ApplicationACLsManager
name|applicationACLsManager
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|clientToAMSecretManager
operator|=
name|clientToAMSecretManager
expr_stmt|;
name|this
operator|.
name|masterService
operator|=
name|masterService
expr_stmt|;
name|this
operator|.
name|applicationACLsManager
operator|=
name|applicationACLsManager
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|setCompletedAppsMax
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_MAX_COMPLETED_APPLICATIONS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_MAX_COMPLETED_APPLICATIONS
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *  This class is for logging the application summary.    */
DECL|class|ApplicationSummary
specifier|static
class|class
name|ApplicationSummary
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ApplicationSummary
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Escape sequences
DECL|field|EQUALS
specifier|static
specifier|final
name|char
name|EQUALS
init|=
literal|'='
decl_stmt|;
DECL|field|charsToEscape
specifier|static
specifier|final
name|char
index|[]
name|charsToEscape
init|=
block|{
name|StringUtils
operator|.
name|COMMA
block|,
name|EQUALS
block|,
name|StringUtils
operator|.
name|ESCAPE_CHAR
block|}
decl_stmt|;
DECL|class|SummaryBuilder
specifier|static
class|class
name|SummaryBuilder
block|{
DECL|field|buffer
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// A little optimization for a very common case
DECL|method|add (String key, long value)
name|SummaryBuilder
name|add
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
name|_add
argument_list|(
name|key
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
DECL|method|add (String key, T value)
parameter_list|<
name|T
parameter_list|>
name|SummaryBuilder
name|add
parameter_list|(
name|String
name|key
parameter_list|,
name|T
name|value
parameter_list|)
block|{
return|return
name|_add
argument_list|(
name|key
argument_list|,
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|ESCAPE_CHAR
argument_list|,
name|charsToEscape
argument_list|)
argument_list|)
return|;
block|}
DECL|method|add (SummaryBuilder summary)
name|SummaryBuilder
name|add
parameter_list|(
name|SummaryBuilder
name|summary
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|buffer
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|COMMA
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|summary
operator|.
name|buffer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|_add (String key, String value)
name|SummaryBuilder
name|_add
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|buffer
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|COMMA
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
name|EQUALS
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|toString ()
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * create a summary of the application's runtime.      *       * @param app {@link RMApp} whose summary is to be created, cannot      *            be<code>null</code>.      */
DECL|method|createAppSummary (RMApp app)
specifier|public
specifier|static
name|SummaryBuilder
name|createAppSummary
parameter_list|(
name|RMApp
name|app
parameter_list|)
block|{
name|String
name|trackingUrl
init|=
literal|"N/A"
decl_stmt|;
name|String
name|host
init|=
literal|"N/A"
decl_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
if|if
condition|(
name|attempt
operator|!=
literal|null
condition|)
block|{
name|trackingUrl
operator|=
name|attempt
operator|.
name|getTrackingUrl
argument_list|()
expr_stmt|;
name|host
operator|=
name|attempt
operator|.
name|getHost
argument_list|()
expr_stmt|;
block|}
name|SummaryBuilder
name|summary
init|=
operator|new
name|SummaryBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"appId"
argument_list|,
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|app
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"user"
argument_list|,
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"queue"
argument_list|,
name|app
operator|.
name|getQueue
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"state"
argument_list|,
name|app
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"trackingUrl"
argument_list|,
name|trackingUrl
argument_list|)
operator|.
name|add
argument_list|(
literal|"appMasterHost"
argument_list|,
name|host
argument_list|)
operator|.
name|add
argument_list|(
literal|"startTime"
argument_list|,
name|app
operator|.
name|getStartTime
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"finishTime"
argument_list|,
name|app
operator|.
name|getFinishTime
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|summary
return|;
block|}
comment|/**      * Log a summary of the application's runtime.      *       * @param app {@link RMApp} whose summary is to be logged      */
DECL|method|logAppSummary (RMApp app)
specifier|public
specifier|static
name|void
name|logAppSummary
parameter_list|(
name|RMApp
name|app
parameter_list|)
block|{
if|if
condition|(
name|app
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|createAppSummary
argument_list|(
name|app
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setCompletedAppsMax (int max)
specifier|protected
specifier|synchronized
name|void
name|setCompletedAppsMax
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|this
operator|.
name|completedAppsMax
operator|=
name|max
expr_stmt|;
block|}
DECL|method|getCompletedAppsListSize ()
specifier|protected
specifier|synchronized
name|int
name|getCompletedAppsListSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|completedApps
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|finishApplication (ApplicationId applicationId)
specifier|protected
specifier|synchronized
name|void
name|finishApplication
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
if|if
condition|(
name|applicationId
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"RMAppManager received completed appId of null, skipping"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Inform the DelegationTokenRenewer
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|rmContext
operator|.
name|getDelegationTokenRenewer
argument_list|()
operator|.
name|applicationFinished
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
name|completedApps
operator|.
name|add
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|writeAuditLog
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeAuditLog (ApplicationId appId)
specifier|protected
name|void
name|writeAuditLog
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|RMApp
name|app
init|=
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|String
name|operation
init|=
literal|"UNKONWN"
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
switch|switch
condition|(
name|app
operator|.
name|getState
argument_list|()
condition|)
block|{
case|case
name|FAILED
case|:
name|operation
operator|=
name|AuditConstants
operator|.
name|FINISH_FAILED_APP
expr_stmt|;
break|break;
case|case
name|FINISHED
case|:
name|operation
operator|=
name|AuditConstants
operator|.
name|FINISH_SUCCESS_APP
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|KILLED
case|:
name|operation
operator|=
name|AuditConstants
operator|.
name|FINISH_KILLED_APP
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
break|break;
default|default:
block|}
if|if
condition|(
name|success
condition|)
block|{
name|RMAuditLogger
operator|.
name|logSuccess
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|,
name|operation
argument_list|,
literal|"RMAppManager"
argument_list|,
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|StringBuilder
name|diag
init|=
name|app
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
name|String
name|msg
init|=
name|diag
operator|==
literal|null
condition|?
literal|null
else|:
name|diag
operator|.
name|toString
argument_list|()
decl_stmt|;
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|,
name|operation
argument_list|,
name|msg
argument_list|,
literal|"RMAppManager"
argument_list|,
literal|"App failed with state: "
operator|+
name|app
operator|.
name|getState
argument_list|()
argument_list|,
name|appId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * check to see if hit the limit for max # completed apps kept    */
DECL|method|checkAppNumCompletedLimit ()
specifier|protected
specifier|synchronized
name|void
name|checkAppNumCompletedLimit
parameter_list|()
block|{
while|while
condition|(
name|completedApps
operator|.
name|size
argument_list|()
operator|>
name|this
operator|.
name|completedAppsMax
condition|)
block|{
name|ApplicationId
name|removeId
init|=
name|completedApps
operator|.
name|remove
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Application should be expired, max # apps"
operator|+
literal|" met. Removing app: "
operator|+
name|removeId
argument_list|)
expr_stmt|;
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|remove
argument_list|(
name|removeId
argument_list|)
expr_stmt|;
name|this
operator|.
name|applicationACLsManager
operator|.
name|removeApplication
argument_list|(
name|removeId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|submitApplication ( ApplicationSubmissionContext submissionContext, long submitTime)
specifier|protected
specifier|synchronized
name|void
name|submitApplication
parameter_list|(
name|ApplicationSubmissionContext
name|submissionContext
parameter_list|,
name|long
name|submitTime
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|submissionContext
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|RMApp
name|application
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|clientTokenStr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|Token
argument_list|<
name|ClientTokenIdentifier
argument_list|>
name|clientToken
init|=
operator|new
name|Token
argument_list|<
name|ClientTokenIdentifier
argument_list|>
argument_list|(
operator|new
name|ClientTokenIdentifier
argument_list|(
name|applicationId
argument_list|)
argument_list|,
name|this
operator|.
name|clientToAMSecretManager
argument_list|)
decl_stmt|;
name|clientTokenStr
operator|=
name|clientToken
operator|.
name|encodeToUrlString
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending client token as "
operator|+
name|clientTokenStr
argument_list|)
expr_stmt|;
block|}
comment|// Sanity checks
if|if
condition|(
name|submissionContext
operator|.
name|getQueue
argument_list|()
operator|==
literal|null
condition|)
block|{
name|submissionContext
operator|.
name|setQueue
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_QUEUE_NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|submissionContext
operator|.
name|getApplicationName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|submissionContext
operator|.
name|setApplicationName
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_APPLICATION_NAME
argument_list|)
expr_stmt|;
block|}
comment|// Store application for recovery
name|ApplicationStore
name|appStore
init|=
name|rmContext
operator|.
name|getApplicationsStore
argument_list|()
operator|.
name|createApplicationStore
argument_list|(
name|submissionContext
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|submissionContext
argument_list|)
decl_stmt|;
comment|// Create RMApp
name|application
operator|=
operator|new
name|RMAppImpl
argument_list|(
name|applicationId
argument_list|,
name|rmContext
argument_list|,
name|this
operator|.
name|conf
argument_list|,
name|submissionContext
operator|.
name|getApplicationName
argument_list|()
argument_list|,
name|submissionContext
operator|.
name|getUser
argument_list|()
argument_list|,
name|submissionContext
operator|.
name|getQueue
argument_list|()
argument_list|,
name|submissionContext
argument_list|,
name|clientTokenStr
argument_list|,
name|appStore
argument_list|,
name|this
operator|.
name|scheduler
argument_list|,
name|this
operator|.
name|masterService
argument_list|,
name|submitTime
argument_list|)
expr_stmt|;
comment|// Sanity check - duplicate?
if|if
condition|(
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|putIfAbsent
argument_list|(
name|applicationId
argument_list|,
name|application
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Application with id "
operator|+
name|applicationId
operator|+
literal|" is already present! Cannot add a duplicate!"
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// Inform the ACLs Manager
name|this
operator|.
name|applicationACLsManager
operator|.
name|addApplication
argument_list|(
name|applicationId
argument_list|,
name|submissionContext
operator|.
name|getAMContainerSpec
argument_list|()
operator|.
name|getApplicationACLs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Setup tokens for renewal
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|rmContext
operator|.
name|getDelegationTokenRenewer
argument_list|()
operator|.
name|addApplication
argument_list|(
name|applicationId
argument_list|,
name|parseCredentials
argument_list|(
name|submissionContext
argument_list|)
argument_list|,
name|submissionContext
operator|.
name|getCancelTokensWhenComplete
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// All done, start the RMApp
name|this
operator|.
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppEvent
argument_list|(
name|applicationId
argument_list|,
name|RMAppEventType
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"RMAppManager submit application exception"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
if|if
condition|(
name|application
operator|!=
literal|null
condition|)
block|{
comment|// Sending APP_REJECTED is fine, since we assume that the
comment|// RMApp is in NEW state and thus we havne't yet informed the
comment|// Scheduler about the existence of the application
name|this
operator|.
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppRejectedEvent
argument_list|(
name|applicationId
argument_list|,
name|ie
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseCredentials (ApplicationSubmissionContext application)
specifier|private
name|Credentials
name|parseCredentials
parameter_list|(
name|ApplicationSubmissionContext
name|application
parameter_list|)
throws|throws
name|IOException
block|{
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|DataInputByteBuffer
name|dibb
init|=
operator|new
name|DataInputByteBuffer
argument_list|()
decl_stmt|;
name|ByteBuffer
name|tokens
init|=
name|application
operator|.
name|getAMContainerSpec
argument_list|()
operator|.
name|getContainerTokens
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokens
operator|!=
literal|null
condition|)
block|{
name|dibb
operator|.
name|reset
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|readTokenStorageStream
argument_list|(
name|dibb
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|rewind
argument_list|()
expr_stmt|;
block|}
return|return
name|credentials
return|;
block|}
annotation|@
name|Override
DECL|method|handle (RMAppManagerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|RMAppManagerEvent
name|event
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|event
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"RMAppManager processing event for "
operator|+
name|applicationId
operator|+
literal|" of type "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|APP_COMPLETED
case|:
block|{
name|finishApplication
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|ApplicationSummary
operator|.
name|logAppSummary
argument_list|(
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
argument_list|)
expr_stmt|;
name|checkAppNumCompletedLimit
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|APP_SUBMIT
case|:
block|{
name|ApplicationSubmissionContext
name|submissionContext
init|=
operator|(
operator|(
name|RMAppManagerSubmitEvent
operator|)
name|event
operator|)
operator|.
name|getSubmissionContext
argument_list|()
decl_stmt|;
name|long
name|submitTime
init|=
operator|(
operator|(
name|RMAppManagerSubmitEvent
operator|)
name|event
operator|)
operator|.
name|getSubmitTime
argument_list|()
decl_stmt|;
name|submitApplication
argument_list|(
name|submissionContext
argument_list|,
name|submitTime
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid eventtype "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|". Ignoring!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

