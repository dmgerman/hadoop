begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|mapred
operator|.
name|AuditLogger
operator|.
name|Constants
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
name|mapreduce
operator|.
name|MRConfig
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

begin_comment
comment|/**  * Manages MapReduce cluster administrators and access checks for  * job level operations and queue level operations.  * Uses JobACLsManager for access checks of job level operations and  * QueueManager for queue operations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ACLsManager
specifier|public
class|class
name|ACLsManager
block|{
DECL|field|LOG
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ACLsManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// MROwner(user who started this mapreduce cluster)'s ugi
DECL|field|mrOwner
specifier|private
specifier|final
name|UserGroupInformation
name|mrOwner
decl_stmt|;
comment|// mapreduce cluster administrators
DECL|field|adminAcl
specifier|private
specifier|final
name|AccessControlList
name|adminAcl
decl_stmt|;
DECL|field|jobACLsManager
specifier|private
specifier|final
name|JobACLsManager
name|jobACLsManager
decl_stmt|;
DECL|field|queueManager
specifier|private
specifier|final
name|QueueManager
name|queueManager
decl_stmt|;
DECL|field|aclsEnabled
specifier|private
specifier|final
name|boolean
name|aclsEnabled
decl_stmt|;
DECL|method|ACLsManager (Configuration conf, JobACLsManager jobACLsManager, QueueManager queueManager)
specifier|public
name|ACLsManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|JobACLsManager
name|jobACLsManager
parameter_list|,
name|QueueManager
name|queueManager
parameter_list|)
throws|throws
name|IOException
block|{
name|mrOwner
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|adminAcl
operator|=
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|MR_ADMINS
argument_list|,
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
name|adminAcl
operator|.
name|addUser
argument_list|(
name|mrOwner
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|deprecatedSuperGroup
init|=
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|MR_SUPERGROUP
argument_list|)
decl_stmt|;
if|if
condition|(
name|deprecatedSuperGroup
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|MRConfig
operator|.
name|MR_SUPERGROUP
operator|+
literal|" is deprecated. Use "
operator|+
name|MRConfig
operator|.
name|MR_ADMINS
operator|+
literal|" instead"
argument_list|)
expr_stmt|;
name|adminAcl
operator|.
name|addGroup
argument_list|(
name|deprecatedSuperGroup
argument_list|)
expr_stmt|;
block|}
name|aclsEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|jobACLsManager
operator|=
name|jobACLsManager
expr_stmt|;
name|this
operator|.
name|queueManager
operator|=
name|queueManager
expr_stmt|;
block|}
DECL|method|getMROwner ()
specifier|public
name|UserGroupInformation
name|getMROwner
parameter_list|()
block|{
return|return
name|mrOwner
return|;
block|}
DECL|method|getAdminsAcl ()
name|AccessControlList
name|getAdminsAcl
parameter_list|()
block|{
return|return
name|adminAcl
return|;
block|}
DECL|method|getJobACLsManager ()
specifier|public
name|JobACLsManager
name|getJobACLsManager
parameter_list|()
block|{
return|return
name|jobACLsManager
return|;
block|}
comment|/**    * Is the calling user an admin for the mapreduce cluster ?    * i.e. either cluster owner or cluster administrator    * @return true, if user is an admin    */
DECL|method|isMRAdmin (UserGroupInformation callerUGI)
specifier|public
name|boolean
name|isMRAdmin
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|)
block|{
if|if
condition|(
name|adminAcl
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
comment|/**    * Check the ACLs for a user doing the passed operation.    *<ul>    *<li>If ACLs are disabled, allow all users.</li>    *<li>Otherwise, if the operation is not a job operation(for eg.    *  submit-job-to-queue), then allow only (a) clusterOwner(who started the    *  cluster), (b) cluster administrators and (c) members of    *  queue-submit-job-acl for the queue.</li>    *<li>If the operation is a job operation, then allow only (a) jobOwner,    * (b) clusterOwner(who started the cluster), (c) cluster administrators,    * (d) members of queue admins acl for the queue and (e) members of job    * acl for the job operation</li>    *</ul>    *     * @param job   the job on which operation is requested    * @param callerUGI  the user who is requesting the operation    * @param operation  the operation for which authorization is needed    * @throws AccessControlException    */
DECL|method|checkAccess (JobInProgress job, UserGroupInformation callerUGI, Operation operation)
specifier|public
name|void
name|checkAccess
parameter_list|(
name|JobInProgress
name|job
parameter_list|,
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|Operation
name|operation
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|String
name|queue
init|=
name|job
operator|.
name|getProfile
argument_list|()
operator|.
name|getQueueName
argument_list|()
decl_stmt|;
name|String
name|jobId
init|=
name|job
operator|.
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|JobStatus
name|jobStatus
init|=
name|job
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|String
name|jobOwner
init|=
name|jobStatus
operator|.
name|getUsername
argument_list|()
decl_stmt|;
name|AccessControlList
name|jobAcl
init|=
name|jobStatus
operator|.
name|getJobACLs
argument_list|()
operator|.
name|get
argument_list|(
name|operation
operator|.
name|jobACLNeeded
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|jobId
argument_list|,
name|callerUGI
argument_list|,
name|queue
argument_list|,
name|operation
argument_list|,
name|jobOwner
argument_list|,
name|jobAcl
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check the ACLs for a user doing the passed job operation.    *<ul>    *<li>If ACLs are disabled, allow all users.</li>    *<li>Otherwise, allow only (a) jobOwner,    * (b) clusterOwner(who started the cluster), (c) cluster administrators,    * (d) members of job acl for the jobOperation</li>    *</ul>    *     * @param jobStatus  the status of the job    * @param callerUGI  the user who is trying to perform the operation    * @param queue      the job queue name    * @param operation  the operation for which authorization is needed    */
DECL|method|checkAccess (JobStatus jobStatus, UserGroupInformation callerUGI, String queue, Operation operation)
name|void
name|checkAccess
parameter_list|(
name|JobStatus
name|jobStatus
parameter_list|,
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|String
name|queue
parameter_list|,
name|Operation
name|operation
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|String
name|jobId
init|=
name|jobStatus
operator|.
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|jobOwner
init|=
name|jobStatus
operator|.
name|getUsername
argument_list|()
decl_stmt|;
name|AccessControlList
name|jobAcl
init|=
name|jobStatus
operator|.
name|getJobACLs
argument_list|()
operator|.
name|get
argument_list|(
name|operation
operator|.
name|jobACLNeeded
argument_list|)
decl_stmt|;
comment|// If acls are enabled, check if callerUGI is jobOwner, queue admin,
comment|// cluster admin or part of job ACL
name|checkAccess
argument_list|(
name|jobId
argument_list|,
name|callerUGI
argument_list|,
name|queue
argument_list|,
name|operation
argument_list|,
name|jobOwner
argument_list|,
name|jobAcl
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check the ACLs for a user doing the passed operation.    *<ul>    *<li>If ACLs are disabled, allow all users.</li>    *<li>Otherwise, if the operation is not a job operation(for eg.    *  submit-job-to-queue), then allow only (a) clusterOwner(who started the    *  cluster), (b) cluster administrators and (c) members of    *  queue-submit-job-acl for the queue.</li>    *<li>If the operation is a job operation, then allow only (a) jobOwner,    * (b) clusterOwner(who started the cluster), (c) cluster administrators,    * (d) members of queue admins acl for the queue and (e) members of job    * acl for the job operation</li>    *</ul>    *     * @param jobId      the job id    * @param callerUGI  the user who is trying to perform the operation    * @param queue      the job queue name    * @param operation  the operation for which authorization is needed    * @param jobOwner   the user who submitted(or is submitting) this job    * @param jobAcl     could be job-view-acl or job-modify-acl depending on the    *                   job operation.    */
DECL|method|checkAccess (String jobId, UserGroupInformation callerUGI, String queue, Operation operation, String jobOwner, AccessControlList jobAcl)
name|void
name|checkAccess
parameter_list|(
name|String
name|jobId
parameter_list|,
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|String
name|queue
parameter_list|,
name|Operation
name|operation
parameter_list|,
name|String
name|jobOwner
parameter_list|,
name|AccessControlList
name|jobAcl
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|String
name|user
init|=
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|String
name|targetResource
init|=
name|jobId
operator|+
literal|" in queue "
operator|+
name|queue
decl_stmt|;
if|if
condition|(
operator|!
name|aclsEnabled
condition|)
block|{
name|AuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
argument_list|,
name|operation
operator|.
name|name
argument_list|()
argument_list|,
name|targetResource
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Allow mapreduce cluster admins to do any queue operation and
comment|// any job operation
if|if
condition|(
name|isMRAdmin
argument_list|(
name|callerUGI
argument_list|)
condition|)
block|{
name|AuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
argument_list|,
name|operation
operator|.
name|name
argument_list|()
argument_list|,
name|targetResource
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|SUBMIT_JOB
condition|)
block|{
comment|// This is strictly queue operation(not a job operation)
if|if
condition|(
operator|!
name|queueManager
operator|.
name|hasAccess
argument_list|(
name|queue
argument_list|,
name|operation
operator|.
name|qACLNeeded
argument_list|,
name|callerUGI
argument_list|)
condition|)
block|{
name|AuditLogger
operator|.
name|logFailure
argument_list|(
name|user
argument_list|,
name|operation
operator|.
name|name
argument_list|()
argument_list|,
name|queueManager
operator|.
name|getQueueACL
argument_list|(
name|queue
argument_list|,
name|operation
operator|.
name|qACLNeeded
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|targetResource
argument_list|,
name|Constants
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
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|" cannot perform "
operator|+
literal|"operation "
operator|+
name|operation
operator|.
name|name
argument_list|()
operator|+
literal|" on queue "
operator|+
name|queue
operator|+
literal|".\n Please run \"hadoop queue -showacls\" "
operator|+
literal|"command to find the queues you have access to ."
argument_list|)
throw|;
block|}
else|else
block|{
name|AuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
argument_list|,
name|operation
operator|.
name|name
argument_list|()
argument_list|,
name|targetResource
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// Check if callerUGI is queueAdmin(in some cases only), jobOwner or
comment|// part of job-acl.
comment|// queueManager and queue are null only when called from
comment|// TaskTracker(i.e. from TaskLogServlet) for the operation VIEW_TASK_LOGS.
comment|// Caller of this method takes care of checking if callerUGI is a
comment|// queue administrator for that operation.
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|VIEW_TASK_LOGS
condition|)
block|{
if|if
condition|(
name|jobACLsManager
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|operation
operator|.
name|jobACLNeeded
argument_list|,
name|jobOwner
argument_list|,
name|jobAcl
argument_list|)
condition|)
block|{
name|AuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
argument_list|,
name|operation
operator|.
name|name
argument_list|()
argument_list|,
name|targetResource
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|queueManager
operator|.
name|hasAccess
argument_list|(
name|queue
argument_list|,
name|operation
operator|.
name|qACLNeeded
argument_list|,
name|callerUGI
argument_list|)
operator|||
name|jobACLsManager
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|operation
operator|.
name|jobACLNeeded
argument_list|,
name|jobOwner
argument_list|,
name|jobAcl
argument_list|)
condition|)
block|{
name|AuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
argument_list|,
name|operation
operator|.
name|name
argument_list|()
argument_list|,
name|targetResource
argument_list|)
expr_stmt|;
return|return;
block|}
name|AuditLogger
operator|.
name|logFailure
argument_list|(
name|user
argument_list|,
name|operation
operator|.
name|name
argument_list|()
argument_list|,
name|jobAcl
operator|.
name|toString
argument_list|()
argument_list|,
name|targetResource
argument_list|,
name|Constants
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
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|" cannot perform operation "
operator|+
name|operation
operator|.
name|name
argument_list|()
operator|+
literal|" on "
operator|+
name|jobId
operator|+
literal|" that is in the queue "
operator|+
name|queue
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

