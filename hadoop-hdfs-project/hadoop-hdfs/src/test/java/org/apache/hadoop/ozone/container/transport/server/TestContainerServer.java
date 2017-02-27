begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.transport.server
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
name|transport
operator|.
name|server
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|embedded
operator|.
name|EmbeddedChannel
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|ozone
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|container
operator|.
name|ContainerTestHelper
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
name|common
operator|.
name|interfaces
operator|.
name|ContainerDispatcher
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerManager
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|Dispatcher
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
name|XceiverClient
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServer
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServerHandler
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
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_class
DECL|class|TestContainerServer
specifier|public
class|class
name|TestContainerServer
block|{
annotation|@
name|Test
DECL|method|testPipeline ()
specifier|public
name|void
name|testPipeline
parameter_list|()
throws|throws
name|IOException
block|{
name|EmbeddedChannel
name|channel
init|=
literal|null
decl_stmt|;
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
try|try
block|{
name|channel
operator|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|XceiverServerHandler
argument_list|(
operator|new
name|TestContainerDispatcher
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|channel
operator|.
name|finish
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|channel
operator|.
name|readOutbound
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|channel
operator|!=
literal|null
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testClientServer ()
specifier|public
name|void
name|testClientServer
parameter_list|()
throws|throws
name|Exception
block|{
name|XceiverServer
name|server
init|=
literal|null
decl_stmt|;
name|XceiverClient
name|client
init|=
literal|null
decl_stmt|;
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
try|try
block|{
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getContainerPort
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|XceiverServer
argument_list|(
name|conf
argument_list|,
operator|new
name|TestContainerDispatcher
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|XceiverClient
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testClientServerWithContainerDispatcher ()
specifier|public
name|void
name|testClientServerWithContainerDispatcher
parameter_list|()
throws|throws
name|Exception
block|{
name|XceiverServer
name|server
init|=
literal|null
decl_stmt|;
name|XceiverClient
name|client
init|=
literal|null
decl_stmt|;
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
try|try
block|{
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getContainerPort
argument_list|()
argument_list|)
expr_stmt|;
name|Dispatcher
name|dispatcher
init|=
operator|new
name|Dispatcher
argument_list|(
name|mock
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|init
argument_list|()
expr_stmt|;
name|server
operator|=
operator|new
name|XceiverServer
argument_list|(
name|conf
argument_list|,
name|dispatcher
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|XceiverClient
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|response
operator|.
name|getResult
argument_list|()
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dispatcher
operator|.
name|getContainerMetrics
argument_list|()
operator|.
name|getContainerOpsMetrics
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|CreateContainer
argument_list|)
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|TestContainerDispatcher
specifier|private
class|class
name|TestContainerDispatcher
implements|implements
name|ContainerDispatcher
block|{
comment|/**      * Dispatches commands to container layer.      *      * @param msg - Command Request      * @return Command Response      * @throws IOException      */
annotation|@
name|Override
specifier|public
name|ContainerCommandResponseProto
DECL|method|dispatch (ContainerCommandRequestProto msg)
name|dispatch
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ContainerTestHelper
operator|.
name|getCreateContainerResponse
argument_list|(
name|msg
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

