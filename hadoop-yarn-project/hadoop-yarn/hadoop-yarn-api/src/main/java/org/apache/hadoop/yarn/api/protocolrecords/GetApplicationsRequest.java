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
name|Set
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
name|collections
operator|.
name|buffer
operator|.
name|UnboundedFifoBuffer
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
name|lang
operator|.
name|math
operator|.
name|LongRange
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>The request from clients to get a report of Applications  * in the cluster from the<code>ResourceManager</code>.</p>  *  *  * @see ApplicationClientProtocol#getApplications(GetApplicationsRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|GetApplicationsRequest
specifier|public
specifier|abstract
class|class
name|GetApplicationsRequest
block|{
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance ()
specifier|public
specifier|static
name|GetApplicationsRequest
name|newInstance
parameter_list|()
block|{
name|GetApplicationsRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetApplicationsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|request
return|;
block|}
comment|/**    *<p>    * The request from clients to get a report of Applications matching the    * giving application types in the cluster from the    *<code>ResourceManager</code>.    *</p>    *    *    * @see ApplicationClientProtocol#getApplications(GetApplicationsRequest)    */
annotation|@
name|Public
annotation|@
name|Stable
specifier|public
specifier|static
name|GetApplicationsRequest
DECL|method|newInstance (Set<String> applicationTypes)
name|newInstance
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
parameter_list|)
block|{
name|GetApplicationsRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetApplicationsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationTypes
argument_list|(
name|applicationTypes
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    *<p>    * The request from clients to get a report of Applications matching the    * giving application states in the cluster from the    *<code>ResourceManager</code>.    *</p>    *    *    * @see ApplicationClientProtocol#getApplications(GetApplicationsRequest)    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance ( EnumSet<YarnApplicationState> applicationStates)
specifier|public
specifier|static
name|GetApplicationsRequest
name|newInstance
parameter_list|(
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|applicationStates
parameter_list|)
block|{
name|GetApplicationsRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetApplicationsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationStates
argument_list|(
name|applicationStates
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    *<p>    * The request from clients to get a report of Applications matching the    * giving and application types and application types in the cluster from the    *<code>ResourceManager</code>.    *</p>    *    *    * @see ApplicationClientProtocol#getApplications(GetApplicationsRequest)    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance ( Set<String> applicationTypes, EnumSet<YarnApplicationState> applicationStates)
specifier|public
specifier|static
name|GetApplicationsRequest
name|newInstance
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
parameter_list|,
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|applicationStates
parameter_list|)
block|{
name|GetApplicationsRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetApplicationsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationTypes
argument_list|(
name|applicationTypes
argument_list|)
expr_stmt|;
name|request
operator|.
name|setApplicationStates
argument_list|(
name|applicationStates
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Get the application types to filter applications on    *    * @return Set of Application Types to filter on    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationTypes ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getApplicationTypes
parameter_list|()
function_decl|;
comment|/**    * Set the application types to filter applications on    *    * @param applicationTypes    * A Set of Application Types to filter on.    * If not defined, match all applications    */
annotation|@
name|Private
annotation|@
name|Unstable
specifier|public
specifier|abstract
name|void
DECL|method|setApplicationTypes (Set<String> applicationTypes)
name|setApplicationTypes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
parameter_list|)
function_decl|;
comment|/**    * Get the application states to filter applications on    *    * @return Set of Application states to filter on    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationStates ()
specifier|public
specifier|abstract
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|getApplicationStates
parameter_list|()
function_decl|;
comment|/**    * Set the application states to filter applications on    *    * @param applicationStates    * A Set of Application states to filter on.    * If not defined, match all running applications    */
annotation|@
name|Private
annotation|@
name|Unstable
specifier|public
specifier|abstract
name|void
DECL|method|setApplicationStates (EnumSet<YarnApplicationState> applicationStates)
name|setApplicationStates
parameter_list|(
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|applicationStates
parameter_list|)
function_decl|;
comment|/**    * Set the application states to filter applications on    *    * @param applicationStates all lower-case string representation of the    *                          application states to filter on    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationStates (Set<String> applicationStates)
specifier|public
specifier|abstract
name|void
name|setApplicationStates
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|applicationStates
parameter_list|)
function_decl|;
comment|/**    * Get the users to filter applications on    *    * @return set of users to filter applications on    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getUsers ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getUsers
parameter_list|()
function_decl|;
comment|/**    * Set the users to filter applications on    *    * @param users set of users to filter applications on    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setUsers (Set<String> users)
specifier|public
specifier|abstract
name|void
name|setUsers
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|users
parameter_list|)
function_decl|;
comment|/**    * Get the queues to filter applications on    *    * @return set of queues to filter applications on    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getQueues ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getQueues
parameter_list|()
function_decl|;
comment|/**    * Set the queue to filter applications on    *    * @param queue user to filter applications on    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setQueues (Set<String> queue)
specifier|public
specifier|abstract
name|void
name|setQueues
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|queue
parameter_list|)
function_decl|;
comment|/**    * Get the limit on the number applications to return    *    * @return number of applications to limit to    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getLimit ()
specifier|public
specifier|abstract
name|long
name|getLimit
parameter_list|()
function_decl|;
comment|/**    * Limit the number applications to return    *    * @param limit number of applications to limit to    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setLimit (long limit)
specifier|public
specifier|abstract
name|void
name|setLimit
parameter_list|(
name|long
name|limit
parameter_list|)
function_decl|;
comment|/**    * Get the range of start times to filter applications on    *    * @return {@link LongRange} of start times to filter applications on    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getStartRange ()
specifier|public
specifier|abstract
name|LongRange
name|getStartRange
parameter_list|()
function_decl|;
comment|/**    * Set the range of start times to filter applications on    *    * @param begin beginning of the range    * @param end end of the range    * @throws IllegalArgumentException    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setStartRange (long begin, long end)
specifier|public
specifier|abstract
name|void
name|setStartRange
parameter_list|(
name|long
name|begin
parameter_list|,
name|long
name|end
parameter_list|)
throws|throws
name|IllegalArgumentException
function_decl|;
comment|/**    * Get the range of finish times to filter applications on    *    * @return {@link LongRange} of finish times to filter applications on    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getFinishRange ()
specifier|public
specifier|abstract
name|LongRange
name|getFinishRange
parameter_list|()
function_decl|;
comment|/**    * Set the range of finish times to filter applications on    *    * @param begin beginning of the range    * @param end end of the range    * @throws IllegalArgumentException    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setFinishRange (long begin, long end)
specifier|public
specifier|abstract
name|void
name|setFinishRange
parameter_list|(
name|long
name|begin
parameter_list|,
name|long
name|end
parameter_list|)
function_decl|;
block|}
end_class

end_unit

