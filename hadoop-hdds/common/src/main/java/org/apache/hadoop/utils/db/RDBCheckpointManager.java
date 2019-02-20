begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|db
package|;
end_package

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
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|Checkpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|RocksDB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|RocksDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * RocksDB Checkpoint Manager, used to create and cleanup checkpoints.  */
end_comment

begin_class
DECL|class|RDBCheckpointManager
specifier|public
class|class
name|RDBCheckpointManager
block|{
DECL|field|checkpoint
specifier|private
specifier|final
name|Checkpoint
name|checkpoint
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|RocksDB
name|db
decl_stmt|;
DECL|field|RDB_CHECKPOINT_DIR_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|RDB_CHECKPOINT_DIR_PREFIX
init|=
literal|"rdb_checkpoint_"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RDBCheckpointManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|JAVA_TMP_DIR
specifier|public
specifier|static
specifier|final
name|String
name|JAVA_TMP_DIR
init|=
literal|"java.io.tmpdir"
decl_stmt|;
DECL|field|checkpointNamePrefix
specifier|private
name|String
name|checkpointNamePrefix
init|=
literal|""
decl_stmt|;
DECL|method|RDBCheckpointManager (RocksDB rocksDB)
specifier|public
name|RDBCheckpointManager
parameter_list|(
name|RocksDB
name|rocksDB
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|rocksDB
expr_stmt|;
name|this
operator|.
name|checkpoint
operator|=
name|Checkpoint
operator|.
name|create
argument_list|(
name|rocksDB
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a checkpoint manager with a prefix to be added to the    * snapshots created.    *    * @param rocksDB          DB instance    * @param checkpointPrefix prefix string.    */
DECL|method|RDBCheckpointManager (RocksDB rocksDB, String checkpointPrefix)
specifier|public
name|RDBCheckpointManager
parameter_list|(
name|RocksDB
name|rocksDB
parameter_list|,
name|String
name|checkpointPrefix
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|rocksDB
expr_stmt|;
name|this
operator|.
name|checkpointNamePrefix
operator|=
name|checkpointPrefix
expr_stmt|;
name|this
operator|.
name|checkpoint
operator|=
name|Checkpoint
operator|.
name|create
argument_list|(
name|rocksDB
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create RocksDB snapshot by saving a checkpoint to a directory.    *    * @param parentDir The directory where the checkpoint needs to be created.    * @return RocksDB specific Checkpoint information object.    */
DECL|method|createCheckpointSnapshot (String parentDir)
specifier|public
name|RocksDBCheckpointSnapshot
name|createCheckpointSnapshot
parameter_list|(
name|String
name|parentDir
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|checkpointDir
init|=
name|StringUtils
operator|.
name|EMPTY
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|checkpointNamePrefix
argument_list|)
condition|)
block|{
name|checkpointDir
operator|+=
name|checkpointNamePrefix
expr_stmt|;
block|}
name|checkpointDir
operator|+=
literal|"_"
operator|+
name|RDB_CHECKPOINT_DIR_PREFIX
operator|+
name|currentTime
expr_stmt|;
name|Path
name|checkpointPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|parentDir
argument_list|,
name|checkpointDir
argument_list|)
decl_stmt|;
name|checkpoint
operator|.
name|createCheckpoint
argument_list|(
name|checkpointPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|RocksDBCheckpointSnapshot
argument_list|(
name|checkpointPath
argument_list|,
name|currentTime
argument_list|,
name|db
operator|.
name|getLatestSequenceNumber
argument_list|()
argument_list|)
return|;
comment|//Best guesstimate here. Not accurate.
block|}
catch|catch
parameter_list|(
name|RocksDBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create RocksDB Snapshot."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|class|RocksDBCheckpointSnapshot
specifier|static
class|class
name|RocksDBCheckpointSnapshot
implements|implements
name|DBCheckpointSnapshot
block|{
DECL|field|checkpointLocation
specifier|private
name|Path
name|checkpointLocation
decl_stmt|;
DECL|field|checkpointTimestamp
specifier|private
name|long
name|checkpointTimestamp
decl_stmt|;
DECL|field|latestSequenceNumber
specifier|private
name|long
name|latestSequenceNumber
decl_stmt|;
DECL|method|RocksDBCheckpointSnapshot (Path checkpointLocation, long snapshotTimestamp, long latestSequenceNumber)
name|RocksDBCheckpointSnapshot
parameter_list|(
name|Path
name|checkpointLocation
parameter_list|,
name|long
name|snapshotTimestamp
parameter_list|,
name|long
name|latestSequenceNumber
parameter_list|)
block|{
name|this
operator|.
name|checkpointLocation
operator|=
name|checkpointLocation
expr_stmt|;
name|this
operator|.
name|checkpointTimestamp
operator|=
name|snapshotTimestamp
expr_stmt|;
name|this
operator|.
name|latestSequenceNumber
operator|=
name|latestSequenceNumber
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCheckpointLocation ()
specifier|public
name|Path
name|getCheckpointLocation
parameter_list|()
block|{
return|return
name|this
operator|.
name|checkpointLocation
return|;
block|}
annotation|@
name|Override
DECL|method|getCheckpointTimestamp ()
specifier|public
name|long
name|getCheckpointTimestamp
parameter_list|()
block|{
return|return
name|this
operator|.
name|checkpointTimestamp
return|;
block|}
annotation|@
name|Override
DECL|method|getLatestSequenceNumber ()
specifier|public
name|long
name|getLatestSequenceNumber
parameter_list|()
block|{
return|return
name|this
operator|.
name|latestSequenceNumber
return|;
block|}
annotation|@
name|Override
DECL|method|cleanupCheckpoint ()
specifier|public
name|void
name|cleanupCheckpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|checkpointLocation
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

