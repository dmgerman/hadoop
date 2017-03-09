begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
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
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
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
name|Closeable
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
comment|/**  * A Client for the storageContainer protocol.  */
end_comment

begin_interface
DECL|interface|XceiverClientSpi
specifier|public
interface|interface
name|XceiverClientSpi
extends|extends
name|Closeable
block|{
comment|/**    * Connects to the leader in the pipeline.    */
DECL|method|connect ()
name|void
name|connect
parameter_list|()
throws|throws
name|Exception
function_decl|;
annotation|@
name|Override
DECL|method|close ()
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Returns the pipeline of machines that host the container used by this    * client.    *    * @return pipeline of machines that host the container    */
DECL|method|getPipeline ()
name|Pipeline
name|getPipeline
parameter_list|()
function_decl|;
comment|/**    * Sends a given command to server and gets the reply back.    * @param request Request    * @return Response to the command    * @throws IOException    */
DECL|method|sendCommand ( ContainerCommandRequestProto request)
name|ContainerCommandResponseProto
name|sendCommand
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

