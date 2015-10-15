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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|FsDatasetTestUtils
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
name|FileNotFoundException
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
name|file
operator|.
name|Files
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

begin_comment
comment|/**  * Test-related utilities to access blocks in {@link FsDatasetImpl}.  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FsDatasetImplTestUtils
specifier|public
class|class
name|FsDatasetImplTestUtils
implements|implements
name|FsDatasetTestUtils
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FsDatasetImplTestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dataset
specifier|private
specifier|final
name|FsDatasetImpl
name|dataset
decl_stmt|;
comment|/**    * A reference to the replica that is used to corrupt block / meta later.    */
DECL|class|FsDatasetImplMaterializedReplica
specifier|private
specifier|static
class|class
name|FsDatasetImplMaterializedReplica
implements|implements
name|MaterializedReplica
block|{
comment|/** Block file of the replica. */
DECL|field|blockFile
specifier|private
specifier|final
name|File
name|blockFile
decl_stmt|;
DECL|field|metaFile
specifier|private
specifier|final
name|File
name|metaFile
decl_stmt|;
comment|/** Check the existence of the file. */
DECL|method|checkFile (File file)
specifier|private
specifier|static
name|void
name|checkFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
if|if
condition|(
name|file
operator|==
literal|null
operator|||
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"The block file or metadata file "
operator|+
name|file
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
block|}
comment|/** Corrupt a block / crc file by truncating it to a newSize */
DECL|method|truncate (File file, long newSize)
specifier|private
specifier|static
name|void
name|truncate
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|newSize
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|newSize
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|checkFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
try|try
init|(
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
init|)
block|{
name|raf
operator|.
name|setLength
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Corrupt a block / crc file by deleting it. */
DECL|method|delete (File file)
specifier|private
specifier|static
name|void
name|delete
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|checkFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|FsDatasetImplMaterializedReplica (File blockFile, File metaFile)
name|FsDatasetImplMaterializedReplica
parameter_list|(
name|File
name|blockFile
parameter_list|,
name|File
name|metaFile
parameter_list|)
block|{
name|this
operator|.
name|blockFile
operator|=
name|blockFile
expr_stmt|;
name|this
operator|.
name|metaFile
operator|=
name|metaFile
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|corruptData ()
specifier|public
name|void
name|corruptData
parameter_list|()
throws|throws
name|IOException
block|{
name|checkFile
argument_list|(
name|blockFile
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Corrupting block file: "
operator|+
name|blockFile
argument_list|)
expr_stmt|;
specifier|final
name|int
name|BUF_SIZE
init|=
literal|32
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|BUF_SIZE
index|]
decl_stmt|;
try|try
init|(
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|blockFile
argument_list|,
literal|"rw"
argument_list|)
init|)
block|{
name|int
name|nread
init|=
name|raf
operator|.
name|read
argument_list|(
name|buf
argument_list|)
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
name|nread
condition|;
name|i
operator|++
control|)
block|{
name|buf
index|[
name|i
index|]
operator|++
expr_stmt|;
block|}
name|raf
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|raf
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|corruptData (byte[] newContent)
specifier|public
name|void
name|corruptData
parameter_list|(
name|byte
index|[]
name|newContent
parameter_list|)
throws|throws
name|IOException
block|{
name|checkFile
argument_list|(
name|blockFile
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Corrupting block file with new content: "
operator|+
name|blockFile
argument_list|)
expr_stmt|;
try|try
init|(
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|blockFile
argument_list|,
literal|"rw"
argument_list|)
init|)
block|{
name|raf
operator|.
name|write
argument_list|(
name|newContent
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|truncateData (long newSize)
specifier|public
name|void
name|truncateData
parameter_list|(
name|long
name|newSize
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Truncating block file: "
operator|+
name|blockFile
argument_list|)
expr_stmt|;
name|truncate
argument_list|(
name|blockFile
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteData ()
specifier|public
name|void
name|deleteData
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting block file: "
operator|+
name|blockFile
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|blockFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|corruptMeta ()
specifier|public
name|void
name|corruptMeta
parameter_list|()
throws|throws
name|IOException
block|{
name|checkFile
argument_list|(
name|metaFile
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Corrupting meta file: "
operator|+
name|metaFile
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
try|try
init|(
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|metaFile
argument_list|,
literal|"rw"
argument_list|)
init|)
block|{
name|FileChannel
name|channel
init|=
name|raf
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|random
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|channel
operator|.
name|size
argument_list|()
operator|/
literal|2
argument_list|)
decl_stmt|;
name|raf
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|raf
operator|.
name|write
argument_list|(
literal|"BADBAD"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteMeta ()
specifier|public
name|void
name|deleteMeta
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting metadata file: "
operator|+
name|metaFile
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|metaFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|truncateMeta (long newSize)
specifier|public
name|void
name|truncateMeta
parameter_list|(
name|long
name|newSize
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Truncating metadata file: "
operator|+
name|metaFile
argument_list|)
expr_stmt|;
name|truncate
argument_list|(
name|metaFile
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"MaterializedReplica: file=%s"
argument_list|,
name|blockFile
argument_list|)
return|;
block|}
block|}
DECL|method|FsDatasetImplTestUtils (DataNode datanode)
specifier|public
name|FsDatasetImplTestUtils
parameter_list|(
name|DataNode
name|datanode
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|datanode
operator|.
name|getFSDataset
argument_list|()
operator|instanceof
name|FsDatasetImpl
argument_list|)
expr_stmt|;
name|dataset
operator|=
operator|(
name|FsDatasetImpl
operator|)
name|datanode
operator|.
name|getFSDataset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Return a materialized replica from the FsDatasetImpl.    */
annotation|@
name|Override
DECL|method|getMaterializedReplica (ExtendedBlock block)
specifier|public
name|MaterializedReplica
name|getMaterializedReplica
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|ReplicaNotFoundException
block|{
name|File
name|blockFile
decl_stmt|;
try|try
block|{
name|blockFile
operator|=
name|dataset
operator|.
name|getBlockFile
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Block file for "
operator|+
name|block
operator|+
literal|" does not existed:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ReplicaNotFoundException
argument_list|(
name|block
argument_list|)
throw|;
block|}
name|File
name|metaFile
init|=
name|FsDatasetUtil
operator|.
name|getMetaFile
argument_list|(
name|blockFile
argument_list|,
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|FsDatasetImplMaterializedReplica
argument_list|(
name|blockFile
argument_list|,
name|metaFile
argument_list|)
return|;
block|}
block|}
end_class

end_unit

