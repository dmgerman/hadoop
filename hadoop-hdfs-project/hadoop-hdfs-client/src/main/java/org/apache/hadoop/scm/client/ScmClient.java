begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|client
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * The interface to call into underlying container layer.  *  * Written as interface to allow easy testing: implement a mock container layer  * for standalone testing of CBlock API without actually calling into remote  * containers. Actual container layer can simply re-implement this.  *  * NOTE this is temporarily needed class. When SCM containers are full-fledged,  * this interface will likely be removed.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|ScmClient
specifier|public
interface|interface
name|ScmClient
block|{
comment|/**    * Creates a Container on SCM and returns the pipeline.    * @param containerId - String container ID    * @return Pipeline    * @throws IOException    */
DECL|method|createContainer (String containerId)
name|Pipeline
name|createContainer
parameter_list|(
name|String
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets a container by Name -- Throws if the container does not exist.    * @param containerId - String Container ID    * @return Pipeline    * @throws IOException    */
DECL|method|getContainer (String containerId)
name|Pipeline
name|getContainer
parameter_list|(
name|String
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delets an existing container.    * @param pipeline - Pipeline that represents the container.    * @throws IOException    */
DECL|method|deleteContainer (Pipeline pipeline)
name|void
name|deleteContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the container size -- Computed by SCM from Container Reports.    * @param pipeline - Pipeline    * @return number of bytes used by this container.    * @throws IOException    */
DECL|method|getContainerSize (Pipeline pipeline)
name|long
name|getContainerSize
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

