begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.replication
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|replication
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CompletableFuture
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
import|;
end_import

begin_comment
comment|/**  * Service to download container data from other datanodes.  *<p>  * The implementation of this interface should copy the raw container data in  * compressed form to working directory.  *<p>  * A smart implementation would use multiple sources to do parallel download.  */
end_comment

begin_interface
DECL|interface|ContainerDownloader
specifier|public
interface|interface
name|ContainerDownloader
extends|extends
name|Closeable
block|{
DECL|method|getContainerDataFromReplicas (long containerId, List<DatanodeDetails> sources)
name|CompletableFuture
argument_list|<
name|Path
argument_list|>
name|getContainerDataFromReplicas
parameter_list|(
name|long
name|containerId
parameter_list|,
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|sources
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

