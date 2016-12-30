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

begin_comment
comment|/**  *<p>State of a<code>Container</code>.</p>  */
end_comment

begin_enum
annotation|@
name|Public
annotation|@
name|Stable
DECL|enum|ContainerState
specifier|public
enum|enum
name|ContainerState
block|{
comment|/** New container */
DECL|enumConstant|NEW
name|NEW
block|,
comment|/** Running container */
DECL|enumConstant|RUNNING
name|RUNNING
block|,
comment|/** Completed container */
DECL|enumConstant|COMPLETE
name|COMPLETE
block|,
comment|/** Scheduled (awaiting resources) at the NM. */
DECL|enumConstant|InterfaceStability.Unstable
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enumConstant|SCHEDULED
name|SCHEDULED
block|}
end_enum

end_unit

