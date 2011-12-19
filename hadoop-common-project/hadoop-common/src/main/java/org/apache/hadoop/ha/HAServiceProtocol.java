begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
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
name|ipc
operator|.
name|VersionedProtocol
import|;
end_import

begin_comment
comment|/**  * Protocol interface that provides High Availability related primitives to  * monitor and fail-over the service.  *   * This interface could be used by HA frameworks to manage the service.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|HAServiceProtocol
specifier|public
interface|interface
name|HAServiceProtocol
extends|extends
name|VersionedProtocol
block|{
comment|/**    * Initial version of the protocol    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * An HA service may be in active or standby state.    */
DECL|enum|HAServiceState
specifier|public
enum|enum
name|HAServiceState
block|{
DECL|enumConstant|ACTIVE
name|ACTIVE
argument_list|(
literal|"active"
argument_list|)
block|,
DECL|enumConstant|STANDBY
name|STANDBY
argument_list|(
literal|"standby"
argument_list|)
block|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|HAServiceState (String name)
name|HAServiceState
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
comment|/**    * Monitor the health of service. This periodically called by the HA    * frameworks to monitor the health of the service.    *     * Service is expected to perform checks to ensure it is functional.    * If the service is not healthy due to failure or partial failure,    * it is expected to throw {@link HealthCheckFailedException}.    * The definition of service not healthy is left to the service.    *     * Note that when health check of an Active service fails,    * failover to standby may be done.    *     * @throws HealthCheckFailedException    *           if the health check of a service fails.    */
DECL|method|monitorHealth ()
specifier|public
name|void
name|monitorHealth
parameter_list|()
throws|throws
name|HealthCheckFailedException
function_decl|;
comment|/**    * Request service to transition to active state. No operation, if the    * service is already in active state.    *     * @throws ServiceFailedException    *           if transition from standby to active fails.    */
DECL|method|transitionToActive ()
specifier|public
name|void
name|transitionToActive
parameter_list|()
throws|throws
name|ServiceFailedException
function_decl|;
comment|/**    * Request service to transition to standby state. No operation, if the    * service is already in standby state.    *     * @throws ServiceFailedException    *           if transition from active to standby fails.    */
DECL|method|transitionToStandby ()
specifier|public
name|void
name|transitionToStandby
parameter_list|()
throws|throws
name|ServiceFailedException
function_decl|;
comment|/**    * Return the current state of the service.    */
DECL|method|getServiceState ()
specifier|public
name|HAServiceState
name|getServiceState
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

