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

begin_comment
comment|/**  * Enum representing an AccessControlList that drives set of operations that  * can be performed on a queue.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|QueueACL
specifier|public
enum|enum
name|QueueACL
block|{
DECL|enumConstant|SUBMIT_JOB
name|SUBMIT_JOB
argument_list|(
literal|"acl-submit-job"
argument_list|)
block|,
DECL|enumConstant|ADMINISTER_JOBS
name|ADMINISTER_JOBS
argument_list|(
literal|"acl-administer-jobs"
argument_list|)
block|;
comment|// Currently this ACL acl-administer-jobs is checked for the operations
comment|// FAIL_TASK, KILL_TASK, KILL_JOB, SET_JOB_PRIORITY and VIEW_JOB.
comment|// TODO: Add ACL for LIST_JOBS when we have ability to authenticate
comment|//       users in UI
comment|// TODO: Add ACL for CHANGE_ACL when we have an admin tool for
comment|//       configuring queues.
DECL|field|aclName
specifier|private
specifier|final
name|String
name|aclName
decl_stmt|;
DECL|method|QueueACL (String aclName)
name|QueueACL
parameter_list|(
name|String
name|aclName
parameter_list|)
block|{
name|this
operator|.
name|aclName
operator|=
name|aclName
expr_stmt|;
block|}
DECL|method|getAclName ()
specifier|public
specifier|final
name|String
name|getAclName
parameter_list|()
block|{
return|return
name|aclName
return|;
block|}
block|}
end_enum

end_unit

