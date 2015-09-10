begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
package|;
end_package

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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|FSDataOutputStream
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
name|fs
operator|.
name|UnresolvedLinkException
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
name|DFSTestUtil
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
name|DistributedFileSystem
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
name|MiniDFSCluster
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
name|protocol
operator|.
name|HdfsConstants
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|SafeModeAction
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
name|protocol
operator|.
name|SnapshotAccessControlException
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
name|blockmanagement
operator|.
name|BlockInfo
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
name|blockmanagement
operator|.
name|BlockInfoStriped
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
name|namenode
operator|.
name|FSDirectory
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
name|namenode
operator|.
name|FSImageTestUtil
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
name|namenode
operator|.
name|INodeFile
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
name|BeforeClass
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

begin_class
DECL|class|TestOfflineImageViewerWithStripedBlocks
specifier|public
class|class
name|TestOfflineImageViewerWithStripedBlocks
block|{
DECL|field|dataBlocks
specifier|private
specifier|static
name|int
name|dataBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|parityBlocks
specifier|private
specifier|static
name|int
name|parityBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|static
specifier|final
name|int
name|cellSize
init|=
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
decl_stmt|;
DECL|field|stripesPerBlock
specifier|private
specifier|static
specifier|final
name|int
name|stripesPerBlock
init|=
literal|3
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|static
specifier|final
name|int
name|blockSize
init|=
name|cellSize
operator|*
name|stripesPerBlock
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDNs
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|setErasureCodingPolicy
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|Path
name|eczone
init|=
operator|new
name|Path
argument_list|(
literal|"/eczone"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|eczone
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFileEqualToOneStripe ()
specifier|public
name|void
name|testFileEqualToOneStripe
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numBytes
init|=
name|cellSize
decl_stmt|;
name|testFileSize
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFileLessThanOneStripe ()
specifier|public
name|void
name|testFileLessThanOneStripe
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numBytes
init|=
name|cellSize
operator|-
literal|100
decl_stmt|;
name|testFileSize
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFileHavingMultipleBlocks ()
specifier|public
name|void
name|testFileHavingMultipleBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numBytes
init|=
name|blockSize
operator|*
literal|3
decl_stmt|;
name|testFileSize
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFileLargerThanABlockGroup1 ()
specifier|public
name|void
name|testFileLargerThanABlockGroup1
parameter_list|()
throws|throws
name|IOException
block|{
name|testFileSize
argument_list|(
name|blockSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFileLargerThanABlockGroup2 ()
specifier|public
name|void
name|testFileLargerThanABlockGroup2
parameter_list|()
throws|throws
name|IOException
block|{
name|testFileSize
argument_list|(
name|blockSize
operator|*
name|dataBlocks
operator|*
literal|3
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFileFullBlockGroup ()
specifier|public
name|void
name|testFileFullBlockGroup
parameter_list|()
throws|throws
name|IOException
block|{
name|testFileSize
argument_list|(
name|blockSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFileMoreThanOneStripe ()
specifier|public
name|void
name|testFileMoreThanOneStripe
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numBytes
init|=
name|blockSize
operator|+
name|blockSize
operator|/
literal|2
decl_stmt|;
name|testFileSize
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
block|}
DECL|method|testFileSize (int numBytes)
specifier|private
name|void
name|testFileSize
parameter_list|(
name|int
name|numBytes
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
throws|,
name|SnapshotAccessControlException
block|{
name|fs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
name|File
name|orgFsimage
init|=
literal|null
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/eczone/striped"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|DFSTestUtil
operator|.
name|generateSequentialBytes
argument_list|(
literal|0
argument_list|,
name|numBytes
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Write results to the fsimage file
name|fs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
comment|// Determine location of fsimage file
name|orgFsimage
operator|=
name|FSImageTestUtil
operator|.
name|findLatestImageFile
argument_list|(
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|)
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|orgFsimage
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Didn't generate or can't find fsimage"
argument_list|)
throw|;
block|}
name|FSImageLoader
name|loader
init|=
name|FSImageLoader
operator|.
name|load
argument_list|(
name|orgFsimage
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|fileStatus
init|=
name|loader
operator|.
name|getFileStatus
argument_list|(
literal|"/eczone/striped"
argument_list|)
decl_stmt|;
name|long
name|expectedFileSize
init|=
name|bytes
operator|.
name|length
decl_stmt|;
comment|// Verify space consumed present in BlockInfoStriped
name|FSDirectory
name|fsdir
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
name|INodeFile
name|fileNode
init|=
name|fsdir
operator|.
name|getINode4Write
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Invalid block size"
argument_list|,
name|fileNode
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|long
name|actualFileSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BlockInfo
name|blockInfo
range|:
name|fileNode
operator|.
name|getBlocks
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"Didn't find block striped information"
argument_list|,
name|blockInfo
operator|instanceof
name|BlockInfoStriped
argument_list|)
expr_stmt|;
name|actualFileSize
operator|+=
name|blockInfo
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wrongly computed file size contains striped blocks"
argument_list|,
name|expectedFileSize
argument_list|,
name|actualFileSize
argument_list|)
expr_stmt|;
comment|// Verify space consumed present in filestatus
name|String
name|EXPECTED_FILE_SIZE
init|=
literal|"\"length\":"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|expectedFileSize
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Wrongly computed file size contains striped blocks, file status:"
operator|+
name|fileStatus
operator|+
literal|". Expected file size is : "
operator|+
name|EXPECTED_FILE_SIZE
argument_list|,
name|fileStatus
operator|.
name|contains
argument_list|(
name|EXPECTED_FILE_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

