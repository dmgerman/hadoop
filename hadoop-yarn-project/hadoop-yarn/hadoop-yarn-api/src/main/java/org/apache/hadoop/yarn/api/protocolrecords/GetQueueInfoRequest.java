begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
name|protocolrecords
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationClientProtocol
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
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>The request sent by clients to get<em>queue information</em>  * from the<code>ResourceManager</code>.</p>  *  * @see ApplicationClientProtocol#getQueueInfo(GetQueueInfoRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|GetQueueInfoRequest
specifier|public
specifier|abstract
class|class
name|GetQueueInfoRequest
block|{
annotation|@
name|Public
annotation|@
name|Stable
specifier|public
specifier|static
name|GetQueueInfoRequest
DECL|method|newInstance (String queueName, boolean includeApplications, boolean includeChildQueues, boolean recursive)
name|newInstance
parameter_list|(
name|String
name|queueName
parameter_list|,
name|boolean
name|includeApplications
parameter_list|,
name|boolean
name|includeChildQueues
parameter_list|,
name|boolean
name|recursive
parameter_list|)
block|{
name|GetQueueInfoRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetQueueInfoRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setQueueName
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
name|request
operator|.
name|setIncludeApplications
argument_list|(
name|includeApplications
argument_list|)
expr_stmt|;
name|request
operator|.
name|setIncludeChildQueues
argument_list|(
name|includeChildQueues
argument_list|)
expr_stmt|;
name|request
operator|.
name|setRecursive
argument_list|(
name|recursive
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Get the<em>queue name</em> for which to get queue information.    * @return<em>queue name</em> for which to get queue information    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueueName ()
specifier|public
specifier|abstract
name|String
name|getQueueName
parameter_list|()
function_decl|;
comment|/**    * Set the<em>queue name</em> for which to get queue information    * @param queueName<em>queue name</em> for which to get queue information    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setQueueName (String queueName)
specifier|public
specifier|abstract
name|void
name|setQueueName
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Is information about<em>active applications</em> required?    * @return<code>true</code> if applications' information is to be included,    *         else<code>false</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getIncludeApplications ()
specifier|public
specifier|abstract
name|boolean
name|getIncludeApplications
parameter_list|()
function_decl|;
comment|/**    * Should we get fetch information about<em>active applications</em>?    * @param includeApplications fetch information about<em>active     *                            applications</em>?    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setIncludeApplications (boolean includeApplications)
specifier|public
specifier|abstract
name|void
name|setIncludeApplications
parameter_list|(
name|boolean
name|includeApplications
parameter_list|)
function_decl|;
comment|/**    * Is information about<em>child queues</em> required?    * @return<code>true</code> if information about child queues is required,    *         else<code>false</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getIncludeChildQueues ()
specifier|public
specifier|abstract
name|boolean
name|getIncludeChildQueues
parameter_list|()
function_decl|;
comment|/**    * Should we fetch information about<em>child queues</em>?    * @param includeChildQueues fetch information about<em>child queues</em>?    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setIncludeChildQueues (boolean includeChildQueues)
specifier|public
specifier|abstract
name|void
name|setIncludeChildQueues
parameter_list|(
name|boolean
name|includeChildQueues
parameter_list|)
function_decl|;
comment|/**    * Is information on the entire<em>child queue hierarchy</em> required?    * @return<code>true</code> if information about entire hierarchy is     *         required,<code>false</code> otherwise    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getRecursive ()
specifier|public
specifier|abstract
name|boolean
name|getRecursive
parameter_list|()
function_decl|;
comment|/**    * Should we fetch information on the entire<em>child queue hierarchy</em>?    * @param recursive fetch information on the entire<em>child queue     *                  hierarchy</em>?    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setRecursive (boolean recursive)
specifier|public
specifier|abstract
name|void
name|setRecursive
parameter_list|(
name|boolean
name|recursive
parameter_list|)
function_decl|;
block|}
end_class

end_unit

