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
name|Map
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
name|records
operator|.
name|ApplicationTimeoutType
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
comment|/**  *<p>  * The response sent by the<code>ResourceManager</code> to the client on update  * application timeout.  *</p>  *<p>  * A response without exception means that the update has completed  * successfully.  *</p>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|UpdateApplicationTimeoutsResponse
specifier|public
specifier|abstract
class|class
name|UpdateApplicationTimeoutsResponse
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|UpdateApplicationTimeoutsResponse
name|newInstance
parameter_list|()
block|{
name|UpdateApplicationTimeoutsResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|UpdateApplicationTimeoutsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Get<code>ApplicationTimeouts</code> of the application. Timeout value is    * in ISO8601 standard with format<b>yyyy-MM-dd'T'HH:mm:ss.SSSZ</b>.    * @return all<code>ApplicationTimeouts</code> of the application.    */
DECL|method|getApplicationTimeouts ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|String
argument_list|>
name|getApplicationTimeouts
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationTimeouts</code> for the application. Timeout value    * is absolute. Timeout value should meet ISO8601 format. Support ISO8601    * format is<b>yyyy-MM-dd'T'HH:mm:ss.SSSZ</b>. All pre-existing Map entries    * are cleared before adding the new Map.    * @param applicationTimeouts<code>ApplicationTimeouts</code>s for the    *          application    */
DECL|method|setApplicationTimeouts ( Map<ApplicationTimeoutType, String> applicationTimeouts)
specifier|public
specifier|abstract
name|void
name|setApplicationTimeouts
parameter_list|(
name|Map
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|String
argument_list|>
name|applicationTimeouts
parameter_list|)
function_decl|;
block|}
end_class

end_unit

