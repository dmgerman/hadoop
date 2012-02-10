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

begin_comment
comment|/**  *  Class to encapsulate Queue ACLs for a particular  *  user.  */
end_comment

begin_class
DECL|class|QueueAclsInfo
class|class
name|QueueAclsInfo
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|QueueAclsInfo
block|{
comment|/**    * Default constructor for QueueAclsInfo.    *     */
DECL|method|QueueAclsInfo ()
name|QueueAclsInfo
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Construct a new QueueAclsInfo object using the queue name and the    * queue operations array    *     * @param queueName Name of the job queue    * @param queue operations    *     */
DECL|method|QueueAclsInfo (String queueName, String[] operations)
name|QueueAclsInfo
parameter_list|(
name|String
name|queueName
parameter_list|,
name|String
index|[]
name|operations
parameter_list|)
block|{
name|super
argument_list|(
name|queueName
argument_list|,
name|operations
argument_list|)
expr_stmt|;
block|}
DECL|method|downgrade ( org.apache.hadoop.mapreduce.QueueAclsInfo acl)
specifier|public
specifier|static
name|QueueAclsInfo
name|downgrade
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|QueueAclsInfo
name|acl
parameter_list|)
block|{
return|return
operator|new
name|QueueAclsInfo
argument_list|(
name|acl
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|acl
operator|.
name|getOperations
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

