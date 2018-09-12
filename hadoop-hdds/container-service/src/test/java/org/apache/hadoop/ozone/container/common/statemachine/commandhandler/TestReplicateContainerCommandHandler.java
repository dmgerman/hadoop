begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.statemachine.commandhandler
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
name|common
operator|.
name|statemachine
operator|.
name|commandhandler
package|;
end_package

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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|TimeoutException
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
name|conf
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|ContainerSet
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
name|statemachine
operator|.
name|StateContext
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
name|replication
operator|.
name|ContainerDownloader
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
name|protocol
operator|.
name|commands
operator|.
name|ReplicateContainerCommand
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
name|test
operator|.
name|GenericTestUtils
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
name|test
operator|.
name|TestGenericTestUtils
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
name|Before
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * Test replication command handler.  */
end_comment

begin_class
DECL|class|TestReplicateContainerCommandHandler
specifier|public
class|class
name|TestReplicateContainerCommandHandler
block|{
DECL|field|EXCEPTION_MESSAGE
specifier|private
specifier|static
specifier|final
name|String
name|EXCEPTION_MESSAGE
init|=
literal|"Oh my god"
decl_stmt|;
DECL|field|handler
specifier|private
name|ReplicateContainerCommandHandler
name|handler
decl_stmt|;
DECL|field|downloader
specifier|private
name|StubDownloader
name|downloader
decl_stmt|;
DECL|field|command
specifier|private
name|ReplicateContainerCommand
name|command
decl_stmt|;
DECL|field|importedContainerIds
specifier|private
name|List
argument_list|<
name|Long
argument_list|>
name|importedContainerIds
decl_stmt|;
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
block|{
name|importedContainerIds
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ContainerSet
name|containerSet
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ContainerSet
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerDispatcher
name|containerDispatcher
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ContainerDispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
name|downloader
operator|=
operator|new
name|StubDownloader
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|ReplicateContainerCommandHandler
argument_list|(
name|conf
argument_list|,
name|containerSet
argument_list|,
name|containerDispatcher
argument_list|,
name|downloader
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|importContainer
parameter_list|(
name|long
name|containerID
parameter_list|,
name|Path
name|tarFilePath
parameter_list|)
block|{
name|importedContainerIds
operator|.
name|add
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
comment|//the command
name|ArrayList
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodeDetails
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|datanodeDetails
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeDetails
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|datanodeDetails
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeDetails
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|command
operator|=
operator|new
name|ReplicateContainerCommand
argument_list|(
literal|1L
argument_list|,
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|handle ()
specifier|public
name|void
name|handle
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
comment|//GIVEN
comment|//WHEN
name|handler
operator|.
name|handle
argument_list|(
name|command
argument_list|,
literal|null
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|TestGenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|downloader
operator|.
name|futureByContainers
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|,
literal|100
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|downloader
operator|.
name|futureByContainers
operator|.
name|get
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|downloader
operator|.
name|futureByContainers
operator|.
name|get
argument_list|(
literal|1L
argument_list|)
operator|.
name|complete
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"/tmp/test"
argument_list|)
argument_list|)
expr_stmt|;
name|TestGenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|importedContainerIds
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|,
literal|100
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|handleWithErrors ()
specifier|public
name|void
name|handleWithErrors
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
comment|//GIVEN
name|GenericTestUtils
operator|.
name|LogCapturer
name|logCapturer
init|=
name|GenericTestUtils
operator|.
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|ReplicateContainerCommandHandler
operator|.
name|LOG
argument_list|)
decl_stmt|;
comment|//WHEN
name|handler
operator|.
name|handle
argument_list|(
name|command
argument_list|,
literal|null
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//THEN
name|TestGenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|downloader
operator|.
name|futureByContainers
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|,
literal|100
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|downloader
operator|.
name|futureByContainers
operator|.
name|get
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|downloader
operator|.
name|futureByContainers
operator|.
name|get
argument_list|(
literal|1L
argument_list|)
operator|.
name|completeExceptionally
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
name|EXCEPTION_MESSAGE
argument_list|)
argument_list|)
expr_stmt|;
name|TestGenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
name|String
name|output
init|=
name|logCapturer
operator|.
name|getOutput
argument_list|()
decl_stmt|;
return|return
name|output
operator|.
name|contains
argument_list|(
literal|"unsuccessful"
argument_list|)
operator|&&
name|output
operator|.
name|contains
argument_list|(
name|EXCEPTION_MESSAGE
argument_list|)
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Can't handle a command if there are no source replicas.    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|handleWithoutReplicas ()
specifier|public
name|void
name|handleWithoutReplicas
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
comment|//GIVEN
name|ReplicateContainerCommand
name|commandWithoutReplicas
init|=
operator|new
name|ReplicateContainerCommand
argument_list|(
literal|1L
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
comment|//WHEN
name|handler
operator|.
name|handle
argument_list|(
name|commandWithoutReplicas
argument_list|,
literal|null
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|class|StubDownloader
specifier|private
specifier|static
class|class
name|StubDownloader
implements|implements
name|ContainerDownloader
block|{
DECL|field|futureByContainers
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|CompletableFuture
argument_list|<
name|Path
argument_list|>
argument_list|>
name|futureByContainers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{      }
annotation|@
name|Override
DECL|method|getContainerDataFromReplicas ( long containerId, List<DatanodeDetails> sources)
specifier|public
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
block|{
name|CompletableFuture
argument_list|<
name|Path
argument_list|>
name|future
init|=
operator|new
name|CompletableFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|futureByContainers
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|future
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
block|}
block|}
end_class

end_unit

