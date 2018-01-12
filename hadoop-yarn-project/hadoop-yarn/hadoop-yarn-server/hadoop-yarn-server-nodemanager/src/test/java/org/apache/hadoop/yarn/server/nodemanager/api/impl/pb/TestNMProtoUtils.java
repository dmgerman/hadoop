begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api.impl.pb
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
name|nodemanager
operator|.
name|api
operator|.
name|impl
operator|.
name|pb
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
name|fs
operator|.
name|Path
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
name|proto
operator|.
name|YarnServerNodemanagerRecoveryProtos
operator|.
name|DeletionServiceDeleteTaskProto
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
name|server
operator|.
name|nodemanager
operator|.
name|DeletionService
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|recovery
operator|.
name|DeletionTaskRecoveryInfo
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|task
operator|.
name|DeletionTask
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|task
operator|.
name|DeletionTaskType
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|task
operator|.
name|DockerContainerDeletionTask
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|task
operator|.
name|FileDeletionTask
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
name|util
operator|.
name|Arrays
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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

begin_comment
comment|/**  * Test conversion to {@link DeletionTask}.  */
end_comment

begin_class
DECL|class|TestNMProtoUtils
specifier|public
class|class
name|TestNMProtoUtils
block|{
annotation|@
name|Test
DECL|method|testConvertProtoToDeletionTask ()
specifier|public
name|void
name|testConvertProtoToDeletionTask
parameter_list|()
throws|throws
name|Exception
block|{
name|DeletionService
name|deletionService
init|=
name|mock
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
decl_stmt|;
name|DeletionServiceDeleteTaskProto
operator|.
name|Builder
name|protoBuilder
init|=
name|DeletionServiceDeleteTaskProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|int
name|id
init|=
literal|0
decl_stmt|;
name|protoBuilder
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|DeletionServiceDeleteTaskProto
name|proto
init|=
name|protoBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|DeletionTask
name|deletionTask
init|=
name|NMProtoUtils
operator|.
name|convertProtoToDeletionTask
argument_list|(
name|proto
argument_list|,
name|deletionService
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DeletionTaskType
operator|.
name|FILE
argument_list|,
name|deletionTask
operator|.
name|getDeletionTaskType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|id
argument_list|,
name|deletionTask
operator|.
name|getTaskId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertProtoToFileDeletionTask ()
specifier|public
name|void
name|testConvertProtoToFileDeletionTask
parameter_list|()
throws|throws
name|Exception
block|{
name|DeletionService
name|deletionService
init|=
name|mock
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|id
init|=
literal|0
decl_stmt|;
name|String
name|user
init|=
literal|"user"
decl_stmt|;
name|Path
name|subdir
init|=
operator|new
name|Path
argument_list|(
literal|"subdir"
argument_list|)
decl_stmt|;
name|Path
name|basedir
init|=
operator|new
name|Path
argument_list|(
literal|"basedir"
argument_list|)
decl_stmt|;
name|DeletionServiceDeleteTaskProto
operator|.
name|Builder
name|protoBuilder
init|=
name|DeletionServiceDeleteTaskProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|protoBuilder
operator|.
name|setId
argument_list|(
name|id
argument_list|)
operator|.
name|setUser
argument_list|(
literal|"user"
argument_list|)
operator|.
name|setSubdir
argument_list|(
name|subdir
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|addBasedirs
argument_list|(
name|basedir
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DeletionServiceDeleteTaskProto
name|proto
init|=
name|protoBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|DeletionTask
name|deletionTask
init|=
name|NMProtoUtils
operator|.
name|convertProtoToFileDeletionTask
argument_list|(
name|proto
argument_list|,
name|deletionService
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DeletionTaskType
operator|.
name|FILE
operator|.
name|name
argument_list|()
argument_list|,
name|deletionTask
operator|.
name|getDeletionTaskType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|id
argument_list|,
name|deletionTask
operator|.
name|getTaskId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|subdir
argument_list|,
operator|(
operator|(
name|FileDeletionTask
operator|)
name|deletionTask
operator|)
operator|.
name|getSubDir
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|basedir
argument_list|,
operator|(
operator|(
name|FileDeletionTask
operator|)
name|deletionTask
operator|)
operator|.
name|getBaseDirs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertProtoToDockerContainerDeletionTask ()
specifier|public
name|void
name|testConvertProtoToDockerContainerDeletionTask
parameter_list|()
throws|throws
name|Exception
block|{
name|DeletionService
name|deletionService
init|=
name|mock
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|id
init|=
literal|0
decl_stmt|;
name|String
name|user
init|=
literal|"user"
decl_stmt|;
name|String
name|dockerContainerId
init|=
literal|"container_e123_12321231_00001"
decl_stmt|;
name|DeletionServiceDeleteTaskProto
operator|.
name|Builder
name|protoBuilder
init|=
name|DeletionServiceDeleteTaskProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|protoBuilder
operator|.
name|setId
argument_list|(
name|id
argument_list|)
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
operator|.
name|setDockerContainerId
argument_list|(
name|dockerContainerId
argument_list|)
expr_stmt|;
name|DeletionServiceDeleteTaskProto
name|proto
init|=
name|protoBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|DeletionTask
name|deletionTask
init|=
name|NMProtoUtils
operator|.
name|convertProtoToDockerContainerDeletionTask
argument_list|(
name|proto
argument_list|,
name|deletionService
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DeletionTaskType
operator|.
name|DOCKER_CONTAINER
operator|.
name|name
argument_list|()
argument_list|,
name|deletionTask
operator|.
name|getDeletionTaskType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|id
argument_list|,
name|deletionTask
operator|.
name|getTaskId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dockerContainerId
argument_list|,
operator|(
operator|(
name|DockerContainerDeletionTask
operator|)
name|deletionTask
operator|)
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertProtoToDeletionTaskRecoveryInfo ()
specifier|public
name|void
name|testConvertProtoToDeletionTaskRecoveryInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|delTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|successorTaskIds
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|DeletionTask
name|deletionTask
init|=
name|mock
argument_list|(
name|DeletionTask
operator|.
name|class
argument_list|)
decl_stmt|;
name|DeletionTaskRecoveryInfo
name|info
init|=
operator|new
name|DeletionTaskRecoveryInfo
argument_list|(
name|deletionTask
argument_list|,
name|successorTaskIds
argument_list|,
name|delTime
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deletionTask
argument_list|,
name|info
operator|.
name|getTask
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|successorTaskIds
argument_list|,
name|info
operator|.
name|getSuccessorTaskIds
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|delTime
argument_list|,
name|info
operator|.
name|getDeletionTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

