begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
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
name|cblock
operator|.
name|meta
operator|.
name|VolumeDescriptor
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
name|cblock
operator|.
name|storage
operator|.
name|IStorageClient
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
name|cblock
operator|.
name|util
operator|.
name|MockStorageClient
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SERVICE_LEVELDB_PATH_KEY
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test the CBlock server state is maintained in persistent storage and can be  * recovered on CBlock server restart.  */
end_comment

begin_class
DECL|class|TestCBlockServerPersistence
specifier|public
class|class
name|TestCBlockServerPersistence
block|{
comment|/**    * Test when cblock server fails with volume meta data, the meta data can be    * restored correctly.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testWriteToPersistentStore ()
specifier|public
name|void
name|testWriteToPersistentStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|userName
init|=
literal|"testWriteToPersistentStore"
decl_stmt|;
name|String
name|volumeName1
init|=
literal|"testVolume1"
decl_stmt|;
name|String
name|volumeName2
init|=
literal|"testVolume2"
decl_stmt|;
name|long
name|volumeSize1
init|=
literal|30L
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|long
name|volumeSize2
init|=
literal|15L
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|int
name|blockSize
init|=
literal|4096
decl_stmt|;
name|CBlockManager
name|cBlockManager
init|=
literal|null
decl_stmt|;
name|CBlockManager
name|cBlockManager1
init|=
literal|null
decl_stmt|;
name|String
name|dbPath
init|=
literal|"/tmp/testCblockPersistence.dat"
decl_stmt|;
try|try
block|{
name|IStorageClient
name|storageClient
init|=
operator|new
name|MockStorageClient
argument_list|()
decl_stmt|;
name|CBlockConfiguration
name|conf
init|=
operator|new
name|CBlockConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_CBLOCK_SERVICE_LEVELDB_PATH_KEY
argument_list|,
name|dbPath
argument_list|)
expr_stmt|;
name|cBlockManager
operator|=
operator|new
name|CBlockManager
argument_list|(
name|conf
argument_list|,
name|storageClient
argument_list|)
expr_stmt|;
name|cBlockManager
operator|.
name|createVolume
argument_list|(
name|userName
argument_list|,
name|volumeName1
argument_list|,
name|volumeSize1
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|cBlockManager
operator|.
name|createVolume
argument_list|(
name|userName
argument_list|,
name|volumeName2
argument_list|,
name|volumeSize2
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|VolumeDescriptor
argument_list|>
name|allVolumes
init|=
name|cBlockManager
operator|.
name|getAllVolumes
argument_list|()
decl_stmt|;
comment|// close the cblock server. Since meta data is written to disk on volume
comment|// creation, closing server here is the same as a cblock server crash.
name|cBlockManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|cBlockManager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|cBlockManager
operator|.
name|join
argument_list|()
expr_stmt|;
name|cBlockManager
operator|=
literal|null
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allVolumes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|VolumeDescriptor
name|volumeDescriptor1
init|=
name|allVolumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|VolumeDescriptor
name|volumeDescriptor2
init|=
name|allVolumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// create a new cblock server instance. This is just the
comment|// same as restarting cblock server.
name|IStorageClient
name|storageClient1
init|=
operator|new
name|MockStorageClient
argument_list|()
decl_stmt|;
name|CBlockConfiguration
name|conf1
init|=
operator|new
name|CBlockConfiguration
argument_list|()
decl_stmt|;
name|conf1
operator|.
name|set
argument_list|(
name|DFS_CBLOCK_SERVICE_LEVELDB_PATH_KEY
argument_list|,
name|dbPath
argument_list|)
expr_stmt|;
name|cBlockManager1
operator|=
operator|new
name|CBlockManager
argument_list|(
name|conf1
argument_list|,
name|storageClient1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|VolumeDescriptor
argument_list|>
name|allVolumes1
init|=
name|cBlockManager1
operator|.
name|getAllVolumes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allVolumes1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|VolumeDescriptor
name|newvolumeDescriptor1
init|=
name|allVolumes1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|VolumeDescriptor
name|newvolumeDescriptor2
init|=
name|allVolumes1
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// It seems levelDB iterator gets keys in the same order as keys
comment|// are inserted, in which case the else clause should never happen.
comment|// But still kept the second clause if it is possible to get different
comment|// key ordering from leveldb. And we do not rely on the ordering of keys
comment|// here.
if|if
condition|(
name|volumeDescriptor1
operator|.
name|getVolumeName
argument_list|()
operator|.
name|equals
argument_list|(
name|newvolumeDescriptor1
operator|.
name|getVolumeName
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|volumeDescriptor1
argument_list|,
name|newvolumeDescriptor1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|volumeDescriptor2
argument_list|,
name|newvolumeDescriptor2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|volumeDescriptor1
argument_list|,
name|newvolumeDescriptor2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|volumeDescriptor2
argument_list|,
name|newvolumeDescriptor1
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cBlockManager
operator|!=
literal|null
condition|)
block|{
name|cBlockManager
operator|.
name|clean
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cBlockManager1
operator|!=
literal|null
condition|)
block|{
name|cBlockManager1
operator|.
name|clean
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

