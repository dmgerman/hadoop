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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|FileSystem
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
name|HdfsConfiguration
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
name|Block
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|ReplicaState
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
name|DataNode
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
name|DataNodeTestUtils
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
name|DatanodeUtil
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
name|ReplicaInfo
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
name|FsVolumeSpi
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
name|io
operator|.
name|IOUtils
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

begin_comment
comment|/** Test if a datanode can correctly upgrade itself */
end_comment

begin_class
DECL|class|TestDatanodeRestart
specifier|public
class|class
name|TestDatanodeRestart
block|{
comment|// test finalized replicas persist across DataNode restarts
DECL|method|testFinalizedReplicas ()
annotation|@
name|Test
specifier|public
name|void
name|testFinalizedReplicas
parameter_list|()
throws|throws
name|Exception
block|{
comment|// bring up a cluster of 3
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
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
literal|1024L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
comment|// test finalized replicas
specifier|final
name|String
name|TopDir
init|=
literal|"/test"
decl_stmt|;
name|DFSTestUtil
name|util
init|=
operator|new
name|DFSTestUtil
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"TestDatanodeRestart"
argument_list|)
operator|.
name|setNumFiles
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|util
operator|.
name|createFiles
argument_list|(
name|fs
argument_list|,
name|TopDir
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|util
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|TopDir
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|util
operator|.
name|checkFiles
argument_list|(
name|fs
argument_list|,
name|TopDir
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|util
operator|.
name|checkFiles
argument_list|(
name|fs
argument_list|,
name|TopDir
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|// test rbw replicas persist across DataNode restarts
DECL|method|testRbwReplicas ()
specifier|public
name|void
name|testRbwReplicas
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
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
literal|1024L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
try|try
block|{
name|testRbwReplicas
argument_list|(
name|cluster
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testRbwReplicas
argument_list|(
name|cluster
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testRbwReplicas (MiniDFSCluster cluster, boolean isCorrupt)
specifier|private
name|void
name|testRbwReplicas
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|boolean
name|isCorrupt
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|src
init|=
operator|new
name|Path
argument_list|(
literal|"/test.txt"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|fileLen
init|=
literal|515
decl_stmt|;
comment|// create some rbw replicas on disk
name|byte
index|[]
name|writeBuf
init|=
operator|new
name|byte
index|[
name|fileLen
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|writeBuf
argument_list|)
expr_stmt|;
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|writeBuf
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|FsVolumeSpi
name|v
range|:
name|dataset
argument_list|(
name|dn
argument_list|)
operator|.
name|getVolumes
argument_list|()
control|)
block|{
specifier|final
name|FsVolumeImpl
name|volume
init|=
operator|(
name|FsVolumeImpl
operator|)
name|v
decl_stmt|;
name|File
name|currentDir
init|=
name|volume
operator|.
name|getCurrentDir
argument_list|()
operator|.
name|getParentFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|File
name|rbwDir
init|=
operator|new
name|File
argument_list|(
name|currentDir
argument_list|,
literal|"rbw"
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|rbwDir
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|isCorrupt
operator|&&
name|Block
operator|.
name|isBlockFilename
argument_list|(
name|file
argument_list|)
condition|)
block|{
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
operator|.
name|setLength
argument_list|(
name|fileLen
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// corrupt
block|}
block|}
block|}
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|dn
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// check volumeMap: one rwr replica
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|ReplicaMap
name|replicas
init|=
name|dataset
argument_list|(
name|dn
argument_list|)
operator|.
name|volumeMap
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|replicas
operator|.
name|size
argument_list|(
name|bpid
argument_list|)
argument_list|)
expr_stmt|;
name|ReplicaInfo
name|replica
init|=
name|replicas
operator|.
name|replicas
argument_list|(
name|bpid
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ReplicaState
operator|.
name|RWR
argument_list|,
name|replica
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCorrupt
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
name|fileLen
operator|-
literal|1
operator|)
operator|/
literal|512
operator|*
literal|512
argument_list|,
name|replica
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fileLen
argument_list|,
name|replica
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dataset
argument_list|(
name|dn
argument_list|)
operator|.
name|invalidate
argument_list|(
name|bpid
argument_list|,
operator|new
name|Block
index|[]
block|{
name|replica
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|src
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|src
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// test recovering unlinked tmp replicas
DECL|method|testRecoverReplicas ()
annotation|@
name|Test
specifier|public
name|void
name|testRecoverReplicas
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
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
literal|1024L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
try|try
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
literal|1
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|ReplicaInfo
argument_list|>
name|replicasItor
init|=
name|dataset
argument_list|(
name|dn
argument_list|)
operator|.
name|volumeMap
operator|.
name|replicas
argument_list|(
name|bpid
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ReplicaInfo
name|replica
init|=
name|replicasItor
operator|.
name|next
argument_list|()
decl_stmt|;
name|createUnlinkTmpFile
argument_list|(
name|replica
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// rename block file
name|createUnlinkTmpFile
argument_list|(
name|replica
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// rename meta file
name|replica
operator|=
name|replicasItor
operator|.
name|next
argument_list|()
expr_stmt|;
name|createUnlinkTmpFile
argument_list|(
name|replica
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// copy block file
name|createUnlinkTmpFile
argument_list|(
name|replica
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// copy meta file
name|replica
operator|=
name|replicasItor
operator|.
name|next
argument_list|()
expr_stmt|;
name|createUnlinkTmpFile
argument_list|(
name|replica
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// rename block file
name|createUnlinkTmpFile
argument_list|(
name|replica
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// copy meta file
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|dn
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// check volumeMap: 4 finalized replica
name|Collection
argument_list|<
name|ReplicaInfo
argument_list|>
name|replicas
init|=
name|dataset
argument_list|(
name|dn
argument_list|)
operator|.
name|volumeMap
operator|.
name|replicas
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|replicas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|replicasItor
operator|=
name|replicas
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|replicasItor
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ReplicaState
operator|.
name|FINALIZED
argument_list|,
name|replicasItor
operator|.
name|next
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|dataset (DataNode dn)
specifier|private
specifier|static
name|FsDatasetImpl
name|dataset
parameter_list|(
name|DataNode
name|dn
parameter_list|)
block|{
return|return
operator|(
name|FsDatasetImpl
operator|)
name|DataNodeTestUtils
operator|.
name|getFSDataset
argument_list|(
name|dn
argument_list|)
return|;
block|}
DECL|method|createUnlinkTmpFile (ReplicaInfo replicaInfo, boolean changeBlockFile, boolean isRename)
specifier|private
specifier|static
name|void
name|createUnlinkTmpFile
parameter_list|(
name|ReplicaInfo
name|replicaInfo
parameter_list|,
name|boolean
name|changeBlockFile
parameter_list|,
name|boolean
name|isRename
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|src
decl_stmt|;
if|if
condition|(
name|changeBlockFile
condition|)
block|{
name|src
operator|=
name|replicaInfo
operator|.
name|getBlockFile
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|src
operator|=
name|replicaInfo
operator|.
name|getMetaFile
argument_list|()
expr_stmt|;
block|}
name|File
name|dst
init|=
name|DatanodeUtil
operator|.
name|getUnlinkTmpFile
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|isRename
condition|)
block|{
name|src
operator|.
name|renameTo
argument_list|(
name|dst
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|src
argument_list|)
decl_stmt|;
try|try
block|{
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|dst
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

