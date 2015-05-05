begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
package|package
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
name|fsdataset
operator|.
name|impl
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
name|FileSystemTestHelper
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
name|StorageType
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
name|DFSConfigKeys
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
name|BlockScanner
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
name|fsdataset
operator|.
name|FsVolumeReference
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
name|fsdataset
operator|.
name|RoundRobinVolumeChoosingPolicy
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
name|fsdataset
operator|.
name|VolumeChoosingPolicy
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|assertNotEquals
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
name|assertNull
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
DECL|class|TestFsVolumeList
specifier|public
class|class
name|TestFsVolumeList
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|blockChooser
specifier|private
name|VolumeChoosingPolicy
argument_list|<
name|FsVolumeImpl
argument_list|>
name|blockChooser
init|=
operator|new
name|RoundRobinVolumeChoosingPolicy
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|dataset
specifier|private
name|FsDatasetImpl
name|dataset
init|=
literal|null
decl_stmt|;
DECL|field|baseDir
specifier|private
name|String
name|baseDir
decl_stmt|;
DECL|field|blockScanner
specifier|private
name|BlockScanner
name|blockScanner
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|dataset
operator|=
name|mock
argument_list|(
name|FsDatasetImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|baseDir
operator|=
operator|new
name|FileSystemTestHelper
argument_list|()
operator|.
name|getTestRootDir
argument_list|()
expr_stmt|;
name|Configuration
name|blockScannerConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|blockScannerConf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SCAN_PERIOD_HOURS_KEY
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|blockScanner
operator|=
operator|new
name|BlockScanner
argument_list|(
literal|null
argument_list|,
name|blockScannerConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNextVolumeWithClosedVolume ()
specifier|public
name|void
name|testGetNextVolumeWithClosedVolume
parameter_list|()
throws|throws
name|IOException
block|{
name|FsVolumeList
name|volumeList
init|=
operator|new
name|FsVolumeList
argument_list|(
name|Collections
operator|.
expr|<
name|VolumeFailureInfo
operator|>
name|emptyList
argument_list|()
argument_list|,
name|blockScanner
argument_list|,
name|blockChooser
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|volumes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|File
name|curDir
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"nextvolume-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|curDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FsVolumeImpl
name|volume
init|=
operator|new
name|FsVolumeImpl
argument_list|(
name|dataset
argument_list|,
literal|"storage-id"
argument_list|,
name|curDir
argument_list|,
name|conf
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|volume
operator|.
name|setCapacityForTesting
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|volumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|volumeList
operator|.
name|addVolume
argument_list|(
name|volume
operator|.
name|obtainReference
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Close the second volume.
name|volumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|closeAndWait
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
try|try
init|(
name|FsVolumeReference
name|ref
init|=
name|volumeList
operator|.
name|getNextVolume
argument_list|(
name|StorageType
operator|.
name|DEFAULT
argument_list|,
literal|128
argument_list|)
init|)
block|{
comment|// volume No.2 will not be chosen.
name|assertNotEquals
argument_list|(
name|ref
operator|.
name|getVolume
argument_list|()
argument_list|,
name|volumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testCheckDirsWithClosedVolume ()
specifier|public
name|void
name|testCheckDirsWithClosedVolume
parameter_list|()
throws|throws
name|IOException
block|{
name|FsVolumeList
name|volumeList
init|=
operator|new
name|FsVolumeList
argument_list|(
name|Collections
operator|.
expr|<
name|VolumeFailureInfo
operator|>
name|emptyList
argument_list|()
argument_list|,
name|blockScanner
argument_list|,
name|blockChooser
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|volumes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|File
name|curDir
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"volume-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|curDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FsVolumeImpl
name|volume
init|=
operator|new
name|FsVolumeImpl
argument_list|(
name|dataset
argument_list|,
literal|"storage-id"
argument_list|,
name|curDir
argument_list|,
name|conf
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|volumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|volumeList
operator|.
name|addVolume
argument_list|(
name|volume
operator|.
name|obtainReference
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Close the 2nd volume.
name|volumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|closeAndWait
argument_list|()
expr_stmt|;
comment|// checkDirs() should ignore the 2nd volume since it is closed.
name|volumeList
operator|.
name|checkDirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReleaseVolumeRefIfNoBlockScanner ()
specifier|public
name|void
name|testReleaseVolumeRefIfNoBlockScanner
parameter_list|()
throws|throws
name|IOException
block|{
name|FsVolumeList
name|volumeList
init|=
operator|new
name|FsVolumeList
argument_list|(
name|Collections
operator|.
expr|<
name|VolumeFailureInfo
operator|>
name|emptyList
argument_list|()
argument_list|,
literal|null
argument_list|,
name|blockChooser
argument_list|)
decl_stmt|;
name|File
name|volDir
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"volume-0"
argument_list|)
decl_stmt|;
name|volDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FsVolumeImpl
name|volume
init|=
operator|new
name|FsVolumeImpl
argument_list|(
name|dataset
argument_list|,
literal|"storage-id"
argument_list|,
name|volDir
argument_list|,
name|conf
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|FsVolumeReference
name|ref
init|=
name|volume
operator|.
name|obtainReference
argument_list|()
decl_stmt|;
name|volumeList
operator|.
name|addVolume
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ref
operator|.
name|getVolume
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

