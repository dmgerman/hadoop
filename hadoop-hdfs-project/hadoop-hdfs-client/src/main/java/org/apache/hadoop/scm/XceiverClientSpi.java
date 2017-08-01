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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * A Client for the storageContainer protocol.  */
end_comment

begin_class
DECL|class|XceiverClientSpi
specifier|public
specifier|abstract
class|class
name|XceiverClientSpi
implements|implements
name|Closeable
block|{
DECL|field|referenceCount
specifier|final
specifier|private
name|AtomicInteger
name|referenceCount
decl_stmt|;
DECL|field|isEvicted
specifier|private
name|boolean
name|isEvicted
decl_stmt|;
DECL|method|XceiverClientSpi ()
name|XceiverClientSpi
parameter_list|()
block|{
name|this
operator|.
name|referenceCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|isEvicted
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|incrementReference ()
name|void
name|incrementReference
parameter_list|()
block|{
name|this
operator|.
name|referenceCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|decrementReference ()
name|void
name|decrementReference
parameter_list|()
block|{
name|this
operator|.
name|referenceCount
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|cleanup
argument_list|()
expr_stmt|;
block|}
DECL|method|setEvicted ()
name|void
name|setEvicted
parameter_list|()
block|{
name|isEvicted
operator|=
literal|true
expr_stmt|;
name|cleanup
argument_list|()
expr_stmt|;
block|}
comment|// close the xceiverClient only if,
comment|// 1) there is no refcount on the client
comment|// 2) it has been evicted from the cache.
DECL|method|cleanup ()
specifier|private
name|void
name|cleanup
parameter_list|()
block|{
if|if
condition|(
name|referenceCount
operator|.
name|get
argument_list|()
operator|==
literal|0
operator|&&
name|isEvicted
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRefcount ()
specifier|public
name|int
name|getRefcount
parameter_list|()
block|{
return|return
name|referenceCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Connects to the leader in the pipeline.    */
DECL|method|connect ()
specifier|public
specifier|abstract
name|void
name|connect
parameter_list|()
throws|throws
name|Exception
function_decl|;
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Returns the pipeline of machines that host the container used by this    * client.    *    * @return pipeline of machines that host the container    */
DECL|method|getPipeline ()
specifier|public
specifier|abstract
name|Pipeline
name|getPipeline
parameter_list|()
function_decl|;
comment|/**    * Sends a given command to server and gets the reply back.    * @param request Request    * @return Response to the command    * @throws IOException    */
DECL|method|sendCommand ( ContainerCommandRequestProto request)
specifier|public
specifier|abstract
name|ContainerCommandResponseProto
name|sendCommand
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sends a given command to server gets a waitable future back.    * @param request Request    * @return Response to the command    * @throws IOException    */
specifier|public
specifier|abstract
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
DECL|method|sendCommandAsync (ContainerCommandRequestProto request)
name|sendCommandAsync
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
function_decl|;
block|}
end_class

end_unit

