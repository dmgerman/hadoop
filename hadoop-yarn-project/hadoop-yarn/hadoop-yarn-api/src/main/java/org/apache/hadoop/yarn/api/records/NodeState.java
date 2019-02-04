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

begin_comment
comment|/**  *<p>State of a<code>Node</code>.</p>  */
end_comment

begin_enum
annotation|@
name|Public
annotation|@
name|Unstable
DECL|enum|NodeState
specifier|public
enum|enum
name|NodeState
block|{
comment|/** New node */
DECL|enumConstant|NEW
name|NEW
block|,
comment|/** Running node */
DECL|enumConstant|RUNNING
name|RUNNING
block|,
comment|/** Node is unhealthy */
DECL|enumConstant|UNHEALTHY
name|UNHEALTHY
block|,
comment|/** Node is out of service */
DECL|enumConstant|DECOMMISSIONED
name|DECOMMISSIONED
block|,
comment|/** Node has not sent a heartbeat for some configured time threshold*/
DECL|enumConstant|LOST
name|LOST
block|,
comment|/** Node has rebooted */
DECL|enumConstant|REBOOTED
name|REBOOTED
block|,
comment|/** Node decommission is in progress */
DECL|enumConstant|DECOMMISSIONING
name|DECOMMISSIONING
block|,
comment|/** Node has shutdown gracefully. */
DECL|enumConstant|SHUTDOWN
name|SHUTDOWN
block|;
DECL|method|isUnusable ()
specifier|public
name|boolean
name|isUnusable
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|UNHEALTHY
operator|||
name|this
operator|==
name|DECOMMISSIONED
operator|||
name|this
operator|==
name|LOST
operator|||
name|this
operator|==
name|SHUTDOWN
operator|)
return|;
block|}
DECL|method|isInactiveState ()
specifier|public
name|boolean
name|isInactiveState
parameter_list|()
block|{
return|return
name|this
operator|==
name|NodeState
operator|.
name|DECOMMISSIONED
operator|||
name|this
operator|==
name|NodeState
operator|.
name|LOST
operator|||
name|this
operator|==
name|NodeState
operator|.
name|REBOOTED
operator|||
name|this
operator|==
name|NodeState
operator|.
name|SHUTDOWN
return|;
block|}
DECL|method|isActiveState ()
specifier|public
name|boolean
name|isActiveState
parameter_list|()
block|{
return|return
name|this
operator|==
name|NodeState
operator|.
name|NEW
operator|||
name|this
operator|==
name|NodeState
operator|.
name|RUNNING
operator|||
name|this
operator|==
name|NodeState
operator|.
name|UNHEALTHY
operator|||
name|this
operator|==
name|NodeState
operator|.
name|DECOMMISSIONING
return|;
block|}
block|}
end_enum

end_unit

