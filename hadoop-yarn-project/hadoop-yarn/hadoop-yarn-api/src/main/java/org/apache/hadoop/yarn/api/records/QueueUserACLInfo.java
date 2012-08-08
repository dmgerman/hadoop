begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Public
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
operator|.
name|Stable
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
operator|.
name|Unstable
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
name|ClientRMProtocol
import|;
end_import

begin_comment
comment|/**  *<p><code>QueueUserACLInfo</code> provides information {@link QueueACL} for  * the given user.</p>  *   * @see QueueACL  * @see ClientRMProtocol#getQueueUserAcls(org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|QueueUserACLInfo
specifier|public
interface|interface
name|QueueUserACLInfo
block|{
comment|/**    * Get the<em>queue name</em> of the queue.    * @return<em>queue name</em> of the queue    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueueName ()
name|String
name|getQueueName
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setQueueName (String queueName)
name|void
name|setQueueName
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Get the list of<code>QueueACL</code> for the given user.    * @return list of<code>QueueACL</code> for the given user    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUserAcls ()
name|List
argument_list|<
name|QueueACL
argument_list|>
name|getUserAcls
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setUserAcls (List<QueueACL> acls)
name|void
name|setUserAcls
parameter_list|(
name|List
argument_list|<
name|QueueACL
argument_list|>
name|acls
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

