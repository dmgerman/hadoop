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
name|ApplicationMasterProtocol
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
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
name|exceptions
operator|.
name|ApplicationAttemptNotFoundException
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
name|exceptions
operator|.
name|ApplicationMasterNotRegisteredException
import|;
end_import

begin_comment
comment|/**  * Command sent by the Resource Manager to the Application Master in the   * AllocateResponse   * @see AllocateResponse  */
end_comment

begin_enum
annotation|@
name|Public
annotation|@
name|Unstable
DECL|enum|AMCommand
specifier|public
enum|enum
name|AMCommand
block|{
comment|/**    * @deprecated Sent by Resource Manager when it is out of sync with the AM and    *             wants the AM get back in sync.    *     *             Note: Instead of sending this command,    *             {@link ApplicationMasterNotRegisteredException} will be thrown    *             when ApplicationMaster is out of sync with ResourceManager and    *             ApplicationMaster is expected to re-register with RM by calling    *             {@link ApplicationMasterProtocol#registerApplicationMaster(RegisterApplicationMasterRequest)}    */
DECL|enumConstant|AM_RESYNC
name|AM_RESYNC
block|,
comment|/**    * @deprecated Sent by Resource Manager when it wants the AM to shutdown.    *             Note: This command was earlier sent by ResourceManager to    *             instruct AM to shutdown if RM had restarted. Now    *             {@link ApplicationAttemptNotFoundException} will be thrown in case    *             that RM has restarted and AM is supposed to handle this    *             exception by shutting down itself.    */
DECL|enumConstant|AM_SHUTDOWN
name|AM_SHUTDOWN
block|}
end_enum

end_unit

