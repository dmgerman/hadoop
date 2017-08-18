begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|container
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|FileUtil
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
name|container
operator|.
name|common
operator|.
name|SCMTestUtils
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
name|XceiverClientManager
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * Tests for Container Mapping.  */
end_comment

begin_class
DECL|class|TestContainerMapping
specifier|public
class|class
name|TestContainerMapping
block|{
DECL|field|mapping
specifier|private
specifier|static
name|ContainerMapping
name|mapping
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|static
name|MockNodeManager
name|nodeManager
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
specifier|static
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|testDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestContainerMapping
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|folderExisted
init|=
name|testDir
operator|.
name|exists
argument_list|()
operator|||
name|testDir
operator|.
name|mkdirs
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|folderExisted
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create test directory path"
argument_list|)
throw|;
block|}
name|nodeManager
operator|=
operator|new
name|MockNodeManager
argument_list|(
literal|true
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ContainerMapping
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
literal|128
argument_list|)
expr_stmt|;
name|xceiverClientManager
operator|=
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|mapping
operator|!=
literal|null
condition|)
block|{
name|mapping
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|clearChillMode ()
specifier|public
name|void
name|clearChillMode
parameter_list|()
block|{
name|nodeManager
operator|.
name|setChillmode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testallocateContainer ()
specifier|public
name|void
name|testallocateContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|Pipeline
name|pipeline
init|=
name|mapping
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testallocateContainerDistributesAllocation ()
specifier|public
name|void
name|testallocateContainerDistributesAllocation
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* This is a lame test, we should really be testing something like     z-score or make sure that we don't have 3sigma kind of events. Too lazy     to write all that code. This test very lamely tests if we have more than     5 separate nodes  from the list of 10 datanodes that got allocated a     container.      */
name|Set
argument_list|<
name|String
argument_list|>
name|pipelineList
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|30
condition|;
name|x
operator|++
control|)
block|{
name|Pipeline
name|pipeline
init|=
name|mapping
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|pipelineList
operator|.
name|add
argument_list|(
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|pipelineList
operator|.
name|size
argument_list|()
operator|>
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetContainer ()
specifier|public
name|void
name|testGetContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|containerName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|mapping
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Pipeline
name|newPipeline
init|=
name|mapping
operator|.
name|getContainer
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|newPipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDuplicateAllocateContainerFails ()
specifier|public
name|void
name|testDuplicateAllocateContainerFails
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|containerName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|mapping
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Specified container already exists."
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testgetNoneExistentContainer ()
specifier|public
name|void
name|testgetNoneExistentContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|containerName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Specified key does not exist."
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|getContainer
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChillModeAllocateContainerFails ()
specifier|public
name|void
name|testChillModeAllocateContainerFails
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|containerName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|nodeManager
operator|.
name|setChillmode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Unable to create container while in chill mode"
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

