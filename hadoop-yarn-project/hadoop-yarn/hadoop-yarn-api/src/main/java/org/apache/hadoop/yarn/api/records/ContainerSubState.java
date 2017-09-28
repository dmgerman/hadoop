begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Container Sub-State.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enum|ContainerSubState
specifier|public
enum|enum
name|ContainerSubState
block|{
comment|/*    * NEW, LOCALIZING, SCHEDULED,    * REINITIALIZING_AWAITING_KILL, RELAUNCHING,    */
DECL|enumConstant|SCHEDULED
name|SCHEDULED
block|,
comment|/*    * RUNNING, REINITIALIZING, PAUSING, KILLING    */
DECL|enumConstant|RUNNING
name|RUNNING
block|,
comment|/*    * PAUSED, RESUMING    */
DECL|enumConstant|PAUSED
name|PAUSED
block|,
comment|/*    * LOCALIZATION_FAILED, EXITED_WITH_SUCCESS,    * EXITED_WITH_FAILURE,    * CONTAINER_CLEANEDUP_AFTER_KILL,    * CONTAINER_RESOURCES_CLEANINGUP    */
DECL|enumConstant|COMPLETING
name|COMPLETING
block|,
comment|/*    * DONE    */
DECL|enumConstant|DONE
name|DONE
block|}
end_enum

end_unit

