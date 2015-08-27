begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|api
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
name|records
operator|.
name|ContainerId
import|;
end_import

begin_comment
comment|/**  * Context class for {@link ContainerLogAggregationPolicy}.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ContainerLogContext
specifier|public
class|class
name|ContainerLogContext
block|{
DECL|field|containerId
specifier|private
specifier|final
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|containerType
specifier|private
specifier|final
name|ContainerType
name|containerType
decl_stmt|;
DECL|field|exitCode
specifier|private
name|int
name|exitCode
decl_stmt|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|ContainerLogContext (ContainerId containerId, ContainerType containerType, int exitCode)
specifier|public
name|ContainerLogContext
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|ContainerType
name|containerType
parameter_list|,
name|int
name|exitCode
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|containerType
operator|=
name|containerType
expr_stmt|;
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
block|}
comment|/**    * Get {@link ContainerId} of the container.    *    * @return the container ID    */
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
comment|/**    * Get {@link ContainerType} the type of the container.    *    * @return the type of the container    */
DECL|method|getContainerType ()
specifier|public
name|ContainerType
name|getContainerType
parameter_list|()
block|{
return|return
name|containerType
return|;
block|}
comment|/**    * Get the exit code of the container.    *    * @return the exit code    */
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|exitCode
return|;
block|}
block|}
end_class

end_unit

