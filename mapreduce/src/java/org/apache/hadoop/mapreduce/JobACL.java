begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|*
import|;
end_import

begin_comment
comment|/**  * Job related ACLs  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|JobACL
specifier|public
enum|enum
name|JobACL
block|{
comment|/**    * ACL for 'viewing' job. Dictates who can 'view' some or all of the job    * related details.    */
DECL|enumConstant|VIEW_JOB
name|VIEW_JOB
parameter_list|(
name|MRJobConfig
operator|.
name|JOB_ACL_VIEW_JOB
parameter_list|)
operator|,
comment|/**    * ACL for 'modifying' job. Dictates who can 'modify' the job for e.g., by    * killing the job, killing/failing a task of the job or setting priority of    * the job.    */
DECL|enumConstant|MODIFY_JOB
constructor|MODIFY_JOB(MRJobConfig.JOB_ACL_MODIFY_JOB
block|)
enum|;
end_enum

begin_decl_stmt
DECL|field|aclName
name|String
name|aclName
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|JobACL (String name)
name|JobACL
argument_list|(
name|String
name|name
argument_list|)
block|{
name|this
operator|.
name|aclName
operator|=
name|name
block|;   }
comment|/**    * Get the name of the ACL. Here it is same as the name of the configuration    * property for specifying the ACL for the job.    *     * @return aclName    */
DECL|method|getAclName ()
specifier|public
name|String
name|getAclName
argument_list|()
block|{
return|return
name|aclName
return|;
block|}
end_expr_stmt

unit|}
end_unit

