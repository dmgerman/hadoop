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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|OverlappingFileLockException
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
name|protocol
operator|.
name|ExtendedBlock
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
name|Storage
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
name|LocalReplica
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
name|ReplicaNotFoundException
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|assertNotNull
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
name|fail
import|;
end_import

begin_class
DECL|class|FsDatasetTestUtil
specifier|public
class|class
name|FsDatasetTestUtil
block|{
DECL|method|getFile (FsDatasetSpi<?> fsd, String bpid, long bid)
specifier|public
specifier|static
name|File
name|getFile
parameter_list|(
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
parameter_list|,
name|String
name|bpid
parameter_list|,
name|long
name|bid
parameter_list|)
block|{
name|ReplicaInfo
name|r
decl_stmt|;
try|try
block|{
name|r
operator|=
operator|(
operator|(
name|FsDatasetImpl
operator|)
name|fsd
operator|)
operator|.
name|getReplicaInfo
argument_list|(
name|bpid
argument_list|,
name|bid
argument_list|)
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
name|r
operator|.
name|getBlockURI
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ReplicaNotFoundException
name|e
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Replica with id %d was not found in block pool %s."
argument_list|,
name|bid
argument_list|,
name|bpid
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getBlockFile (FsDatasetSpi<?> fsd, String bpid, Block b )
specifier|public
specifier|static
name|File
name|getBlockFile
parameter_list|(
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
parameter_list|,
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|ReplicaInfo
name|r
init|=
operator|(
operator|(
name|FsDatasetImpl
operator|)
name|fsd
operator|)
operator|.
name|getReplicaInfo
argument_list|(
name|bpid
argument_list|,
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|r
operator|.
name|getBlockURI
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getMetaFile (FsDatasetSpi<?> fsd, String bpid, Block b)
specifier|public
specifier|static
name|File
name|getMetaFile
parameter_list|(
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
parameter_list|,
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FsDatasetUtil
operator|.
name|getMetaFile
argument_list|(
name|getBlockFile
argument_list|(
name|fsd
argument_list|,
name|bpid
argument_list|,
name|b
argument_list|)
argument_list|,
name|b
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
return|;
block|}
DECL|method|breakHardlinksIfNeeded (FsDatasetSpi<?> fsd, ExtendedBlock block)
specifier|public
specifier|static
name|boolean
name|breakHardlinksIfNeeded
parameter_list|(
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LocalReplica
name|info
init|=
call|(
name|LocalReplica
call|)
argument_list|(
operator|(
name|FsDatasetImpl
operator|)
name|fsd
argument_list|)
operator|.
name|getReplicaInfo
argument_list|(
name|block
argument_list|)
decl_stmt|;
return|return
name|info
operator|.
name|breakHardLinksIfNeeded
argument_list|()
return|;
block|}
DECL|method|fetchReplicaInfo (final FsDatasetSpi<?> fsd, final String bpid, final long blockId)
specifier|public
specifier|static
name|ReplicaInfo
name|fetchReplicaInfo
parameter_list|(
specifier|final
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
parameter_list|,
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|)
block|{
return|return
operator|(
operator|(
name|FsDatasetImpl
operator|)
name|fsd
operator|)
operator|.
name|fetchReplicaInfo
argument_list|(
name|bpid
argument_list|,
name|blockId
argument_list|)
return|;
block|}
DECL|method|getReplicas (FsDatasetSpi<?> fsd, String bpid)
specifier|public
specifier|static
name|Collection
argument_list|<
name|ReplicaInfo
argument_list|>
name|getReplicas
parameter_list|(
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
parameter_list|,
name|String
name|bpid
parameter_list|)
block|{
return|return
operator|(
operator|(
name|FsDatasetImpl
operator|)
name|fsd
operator|)
operator|.
name|volumeMap
operator|.
name|replicas
argument_list|(
name|bpid
argument_list|)
return|;
block|}
comment|/**    * Stop the lazy writer daemon that saves RAM disk files to persistent storage.    * @param dn    */
DECL|method|stopLazyWriter (DataNode dn)
specifier|public
specifier|static
name|void
name|stopLazyWriter
parameter_list|(
name|DataNode
name|dn
parameter_list|)
block|{
name|FsDatasetImpl
name|fsDataset
init|=
operator|(
operator|(
name|FsDatasetImpl
operator|)
name|dn
operator|.
name|getFSDataset
argument_list|()
operator|)
decl_stmt|;
operator|(
operator|(
name|FsDatasetImpl
operator|.
name|LazyWriter
operator|)
name|fsDataset
operator|.
name|lazyWriter
operator|.
name|getRunnable
argument_list|()
operator|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Asserts that the storage lock file in the given directory has been    * released.  This method works by trying to acquire the lock file itself.  If    * locking fails here, then the main code must have failed to release it.    *    * @param dir the storage directory to check    * @throws IOException if there is an unexpected I/O error    */
DECL|method|assertFileLockReleased (String dir)
specifier|public
specifier|static
name|void
name|assertFileLockReleased
parameter_list|(
name|String
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|StorageLocation
name|sl
init|=
name|StorageLocation
operator|.
name|parse
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|sl
operator|.
name|getUri
argument_list|()
argument_list|)
argument_list|,
name|Storage
operator|.
name|STORAGE_FILE_LOCK
argument_list|)
decl_stmt|;
try|try
init|(
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|lockFile
argument_list|,
literal|"rws"
argument_list|)
init|;
name|FileChannel
name|channel
operator|=
name|raf
operator|.
name|getChannel
argument_list|()
init|)
block|{
name|FileLock
name|lock
init|=
name|channel
operator|.
name|tryLock
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Lock file at %s appears to be held by a different process."
argument_list|,
name|lockFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|lock
argument_list|)
expr_stmt|;
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"I/O error releasing file lock %s."
argument_list|,
name|lockFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|OverlappingFileLockException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Must release lock file at %s."
argument_list|,
name|lockFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

