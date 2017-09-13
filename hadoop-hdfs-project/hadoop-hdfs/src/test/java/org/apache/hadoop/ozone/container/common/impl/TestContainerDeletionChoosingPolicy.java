begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
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
name|impl
package|;
end_package

begin_import
import|import static
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
operator|.
name|createSingleNodePipeline
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|DFSUtil
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
name|server
operator|.
name|datanode
operator|.
name|StorageLocation
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
name|OzoneConsts
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
name|helpers
operator|.
name|ContainerData
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
name|helpers
operator|.
name|KeyUtils
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
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
name|utils
operator|.
name|MetadataStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_comment
comment|/**  * The class for testing container deletion choosing policy.  */
end_comment

begin_class
DECL|class|TestContainerDeletionChoosingPolicy
specifier|public
class|class
name|TestContainerDeletionChoosingPolicy
block|{
DECL|field|path
specifier|private
specifier|static
name|String
name|path
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|static
name|ContainerManagerImpl
name|containerManager
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Throwable
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|path
operator|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestContainerDeletionChoosingPolicy
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|path
operator|+=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT_DEFAULT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|containerManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|containerManager
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRandomChoosingPolicy ()
specifier|public
name|void
name|testRandomChoosingPolicy
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|containerDir
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_DELETION_CHOOSING_POLICY
argument_list|,
name|RandomContainerDeletionChoosingPolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|pathLists
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|pathLists
operator|.
name|add
argument_list|(
name|StorageLocation
operator|.
name|parse
argument_list|(
name|containerDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|containerManager
operator|=
operator|new
name|ContainerManagerImpl
argument_list|()
expr_stmt|;
name|containerManager
operator|.
name|init
argument_list|(
name|conf
argument_list|,
name|pathLists
argument_list|)
expr_stmt|;
name|int
name|numContainers
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numContainers
condition|;
name|i
operator|++
control|)
block|{
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
name|ContainerData
name|data
init|=
operator|new
name|ContainerData
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|createContainer
argument_list|(
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerManager
operator|.
name|getContainerMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ContainerData
argument_list|>
name|result0
init|=
name|containerManager
operator|.
name|chooseContainerForBlockDeletion
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|result0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// test random choosing
name|List
argument_list|<
name|ContainerData
argument_list|>
name|result1
init|=
name|containerManager
operator|.
name|chooseContainerForBlockDeletion
argument_list|(
name|numContainers
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContainerData
argument_list|>
name|result2
init|=
name|containerManager
operator|.
name|chooseContainerForBlockDeletion
argument_list|(
name|numContainers
argument_list|)
decl_stmt|;
name|boolean
name|hasShuffled
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numContainers
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|result1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getContainerName
argument_list|()
operator|.
name|equals
argument_list|(
name|result2
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getContainerName
argument_list|()
argument_list|)
condition|)
block|{
name|hasShuffled
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Chosen container results were same"
argument_list|,
name|hasShuffled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTopNOrderedChoosingPolicy ()
specifier|public
name|void
name|testTopNOrderedChoosingPolicy
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|containerDir
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_DELETION_CHOOSING_POLICY
argument_list|,
name|TopNOrderedContainerDeletionChoosingPolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|pathLists
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|pathLists
operator|.
name|add
argument_list|(
name|StorageLocation
operator|.
name|parse
argument_list|(
name|containerDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|containerManager
operator|=
operator|new
name|ContainerManagerImpl
argument_list|()
expr_stmt|;
name|containerManager
operator|.
name|init
argument_list|(
name|conf
argument_list|,
name|pathLists
argument_list|)
expr_stmt|;
name|int
name|numContainers
init|=
literal|10
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|name2Count
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// create [numContainers + 1] containers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|numContainers
condition|;
name|i
operator|++
control|)
block|{
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
name|ContainerData
name|data
init|=
operator|new
name|ContainerData
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|createContainer
argument_list|(
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerManager
operator|.
name|getContainerMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
comment|// don't create deletion blocks in the last container.
if|if
condition|(
name|i
operator|==
name|numContainers
condition|)
block|{
break|break;
block|}
comment|// create random number of deletion blocks and write to container db
name|int
name|deletionBlocks
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numContainers
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// record<ContainerName, DeletionCount> value
name|name2Count
operator|.
name|put
argument_list|(
name|containerName
argument_list|,
name|deletionBlocks
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|deletionBlocks
condition|;
name|j
operator|++
control|)
block|{
name|MetadataStore
name|metadata
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|data
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|String
name|blk
init|=
literal|"blk"
operator|+
name|i
operator|+
literal|"-"
operator|+
name|j
decl_stmt|;
name|byte
index|[]
name|blkBytes
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|blk
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
operator|+
name|blk
argument_list|)
argument_list|,
name|blkBytes
argument_list|)
expr_stmt|;
block|}
block|}
name|containerManager
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|containerManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|containerManager
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
name|containerManager
operator|.
name|init
argument_list|(
name|conf
argument_list|,
name|pathLists
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerData
argument_list|>
name|result0
init|=
name|containerManager
operator|.
name|chooseContainerForBlockDeletion
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|result0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerData
argument_list|>
name|result1
init|=
name|containerManager
operator|.
name|chooseContainerForBlockDeletion
argument_list|(
name|numContainers
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// the empty deletion blocks container should not be chosen
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numContainers
argument_list|,
name|result1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify the order of return list
name|int
name|lastCount
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|ContainerData
name|data
range|:
name|result1
control|)
block|{
name|int
name|currentCount
init|=
name|name2Count
operator|.
name|remove
argument_list|(
name|data
operator|.
name|getContainerName
argument_list|()
argument_list|)
decl_stmt|;
comment|// previous count should not smaller than next one
name|Assert
operator|.
name|assertTrue
argument_list|(
name|currentCount
operator|>
literal|0
operator|&&
name|currentCount
operator|<=
name|lastCount
argument_list|)
expr_stmt|;
name|lastCount
operator|=
name|currentCount
expr_stmt|;
block|}
comment|// ensure all the container data are compared
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|name2Count
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

