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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OM_DB_CHECKPOINTS_DIR_NAME
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|Set
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
name|hdds
operator|.
name|HddsUtils
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
name|RocksDBStoreMBean
import|;
end_import

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
name|ratis
operator|.
name|thirdparty
operator|.
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|ColumnFamilyDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|ColumnFamilyHandle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|DBOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|FlushOptions
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
name|rocksdb
operator|.
name|WriteOptions
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
comment|/**  * RocksDB Store that supports creating Tables in DB.  */
end_comment

begin_class
DECL|class|RDBStore
specifier|public
class|class
name|RDBStore
implements|implements
name|DBStore
block|{
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
name|RDBStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|db
specifier|private
name|RocksDB
name|db
decl_stmt|;
DECL|field|dbLocation
specifier|private
name|File
name|dbLocation
decl_stmt|;
DECL|field|writeOptions
specifier|private
specifier|final
name|WriteOptions
name|writeOptions
decl_stmt|;
DECL|field|dbOptions
specifier|private
specifier|final
name|DBOptions
name|dbOptions
decl_stmt|;
DECL|field|codecRegistry
specifier|private
specifier|final
name|CodecRegistry
name|codecRegistry
decl_stmt|;
DECL|field|handleTable
specifier|private
specifier|final
name|Hashtable
argument_list|<
name|String
argument_list|,
name|ColumnFamilyHandle
argument_list|>
name|handleTable
decl_stmt|;
DECL|field|statMBeanName
specifier|private
name|ObjectName
name|statMBeanName
decl_stmt|;
DECL|field|checkPointManager
specifier|private
name|RDBCheckpointManager
name|checkPointManager
decl_stmt|;
DECL|field|checkpointsParentDir
specifier|private
name|String
name|checkpointsParentDir
decl_stmt|;
DECL|field|columnFamilyHandles
specifier|private
name|List
argument_list|<
name|ColumnFamilyHandle
argument_list|>
name|columnFamilyHandles
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|RDBStore (File dbFile, DBOptions options, Set<TableConfig> families)
specifier|public
name|RDBStore
parameter_list|(
name|File
name|dbFile
parameter_list|,
name|DBOptions
name|options
parameter_list|,
name|Set
argument_list|<
name|TableConfig
argument_list|>
name|families
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dbFile
argument_list|,
name|options
argument_list|,
name|families
argument_list|,
operator|new
name|CodecRegistry
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|RDBStore (File dbFile, DBOptions options, Set<TableConfig> families, CodecRegistry registry)
specifier|public
name|RDBStore
parameter_list|(
name|File
name|dbFile
parameter_list|,
name|DBOptions
name|options
parameter_list|,
name|Set
argument_list|<
name|TableConfig
argument_list|>
name|families
parameter_list|,
name|CodecRegistry
name|registry
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dbFile
argument_list|,
literal|"DB file location cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|families
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|families
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|handleTable
operator|=
operator|new
name|Hashtable
argument_list|<>
argument_list|()
expr_stmt|;
name|codecRegistry
operator|=
name|registry
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ColumnFamilyDescriptor
argument_list|>
name|columnFamilyDescriptors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|columnFamilyHandles
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|TableConfig
name|family
range|:
name|families
control|)
block|{
name|columnFamilyDescriptors
operator|.
name|add
argument_list|(
name|family
operator|.
name|getDescriptor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dbOptions
operator|=
name|options
expr_stmt|;
name|dbLocation
operator|=
name|dbFile
expr_stmt|;
comment|// TODO: Read from the next Config.
name|writeOptions
operator|=
operator|new
name|WriteOptions
argument_list|()
expr_stmt|;
try|try
block|{
name|db
operator|=
name|RocksDB
operator|.
name|open
argument_list|(
name|dbOptions
argument_list|,
name|dbLocation
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|columnFamilyDescriptors
argument_list|,
name|columnFamilyHandles
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|columnFamilyHandles
operator|.
name|size
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|handleTable
operator|.
name|put
argument_list|(
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|columnFamilyHandles
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|columnFamilyHandles
operator|.
name|get
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dbOptions
operator|.
name|statistics
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jmxProperties
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|jmxProperties
operator|.
name|put
argument_list|(
literal|"dbName"
argument_list|,
name|dbFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|statMBeanName
operator|=
name|HddsUtils
operator|.
name|registerWithJmxProperties
argument_list|(
literal|"Ozone"
argument_list|,
literal|"RocksDbStore"
argument_list|,
name|jmxProperties
argument_list|,
operator|new
name|RocksDBStoreMBean
argument_list|(
name|dbOptions
operator|.
name|statistics
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|statMBeanName
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"jmx registration failed during RocksDB init, db path :{}"
argument_list|,
name|dbFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//create checkpoints directory if not exists.
name|checkpointsParentDir
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|dbLocation
operator|.
name|getParent
argument_list|()
argument_list|,
name|OM_DB_CHECKPOINTS_DIR_NAME
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|File
name|checkpointsDir
init|=
operator|new
name|File
argument_list|(
name|checkpointsParentDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|checkpointsDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|boolean
name|success
init|=
name|checkpointsDir
operator|.
name|mkdir
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to create RocksDB checkpoint directory"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Initialize checkpoint manager
name|checkPointManager
operator|=
operator|new
name|RDBCheckpointManager
argument_list|(
name|db
argument_list|,
literal|"om"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RocksDBException
name|e
parameter_list|)
block|{
throw|throw
name|toIOException
argument_list|(
literal|"Failed init RocksDB, db path : "
operator|+
name|dbFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"RocksDB successfully opened."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"[Option] dbLocation= {}"
argument_list|,
name|dbLocation
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"[Option] createIfMissing = {}"
argument_list|,
name|options
operator|.
name|createIfMissing
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"[Option] maxOpenFiles= {}"
argument_list|,
name|options
operator|.
name|maxOpenFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toIOException (String msg, RocksDBException e)
specifier|public
specifier|static
name|IOException
name|toIOException
parameter_list|(
name|String
name|msg
parameter_list|,
name|RocksDBException
name|e
parameter_list|)
block|{
name|String
name|statusCode
init|=
name|e
operator|.
name|getStatus
argument_list|()
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|e
operator|.
name|getStatus
argument_list|()
operator|.
name|getCodeString
argument_list|()
decl_stmt|;
name|String
name|errMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
operator|==
literal|null
condition|?
literal|"Unknown error"
else|:
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|String
name|output
init|=
name|msg
operator|+
literal|"; status : "
operator|+
name|statusCode
operator|+
literal|"; message : "
operator|+
name|errMessage
decl_stmt|;
return|return
operator|new
name|IOException
argument_list|(
name|output
argument_list|,
name|e
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compactDB ()
specifier|public
name|void
name|compactDB
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|db
operator|.
name|compactRange
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RocksDBException
name|e
parameter_list|)
block|{
throw|throw
name|toIOException
argument_list|(
literal|"Failed to compact db"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
specifier|final
name|ColumnFamilyHandle
name|handle
range|:
name|handleTable
operator|.
name|values
argument_list|()
control|)
block|{
name|handle
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|statMBeanName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|statMBeanName
argument_list|)
expr_stmt|;
name|statMBeanName
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dbOptions
operator|!=
literal|null
condition|)
block|{
name|dbOptions
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|writeOptions
operator|!=
literal|null
condition|)
block|{
name|writeOptions
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|move (KEY key, Table<KEY, VALUE> source, Table<KEY, VALUE> dest)
specifier|public
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|void
name|move
parameter_list|(
name|KEY
name|key
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|source
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BatchOperation
name|batchOperation
init|=
name|initBatchOperation
argument_list|()
init|)
block|{
name|VALUE
name|value
init|=
name|source
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|dest
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|source
operator|.
name|deleteWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|commitBatchOperation
argument_list|(
name|batchOperation
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|move (KEY key, VALUE value, Table<KEY, VALUE> source, Table<KEY, VALUE> dest)
specifier|public
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|void
name|move
parameter_list|(
name|KEY
name|key
parameter_list|,
name|VALUE
name|value
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|source
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|move
argument_list|(
name|key
argument_list|,
name|key
argument_list|,
name|value
argument_list|,
name|source
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|move (KEY sourceKey, KEY destKey, VALUE value, Table<KEY, VALUE> source, Table<KEY, VALUE> dest)
specifier|public
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|void
name|move
parameter_list|(
name|KEY
name|sourceKey
parameter_list|,
name|KEY
name|destKey
parameter_list|,
name|VALUE
name|value
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|source
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BatchOperation
name|batchOperation
init|=
name|initBatchOperation
argument_list|()
init|)
block|{
name|dest
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|destKey
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|source
operator|.
name|deleteWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|sourceKey
argument_list|)
expr_stmt|;
name|commitBatchOperation
argument_list|(
name|batchOperation
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEstimatedKeyCount ()
specifier|public
name|long
name|getEstimatedKeyCount
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|db
operator|.
name|getLongProperty
argument_list|(
literal|"rocksdb.estimate-num-keys"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RocksDBException
name|e
parameter_list|)
block|{
throw|throw
name|toIOException
argument_list|(
literal|"Unable to get the estimated count."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|initBatchOperation ()
specifier|public
name|BatchOperation
name|initBatchOperation
parameter_list|()
block|{
return|return
operator|new
name|RDBBatchOperation
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|commitBatchOperation (BatchOperation operation)
specifier|public
name|void
name|commitBatchOperation
parameter_list|(
name|BatchOperation
name|operation
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|RDBBatchOperation
operator|)
name|operation
operator|)
operator|.
name|commit
argument_list|(
name|db
argument_list|,
name|writeOptions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getStatMBeanName ()
specifier|protected
name|ObjectName
name|getStatMBeanName
parameter_list|()
block|{
return|return
name|statMBeanName
return|;
block|}
annotation|@
name|Override
DECL|method|getTable (String name)
specifier|public
name|Table
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ColumnFamilyHandle
name|handle
init|=
name|handleTable
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|handle
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No such table in this DB. TableName : "
operator|+
name|name
argument_list|)
throw|;
block|}
return|return
operator|new
name|RDBTable
argument_list|(
name|this
operator|.
name|db
argument_list|,
name|handle
argument_list|,
name|this
operator|.
name|writeOptions
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTable (String name, Class<KEY> keyType, Class<VALUE> valueType)
specifier|public
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|getTable
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|KEY
argument_list|>
name|keyType
parameter_list|,
name|Class
argument_list|<
name|VALUE
argument_list|>
name|valueType
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TypedTable
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
argument_list|(
name|getTable
argument_list|(
name|name
argument_list|)
argument_list|,
name|codecRegistry
argument_list|,
name|keyType
argument_list|,
name|valueType
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listTables ()
specifier|public
name|ArrayList
argument_list|<
name|Table
argument_list|>
name|listTables
parameter_list|()
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|Table
argument_list|>
name|returnList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ColumnFamilyHandle
name|handle
range|:
name|handleTable
operator|.
name|values
argument_list|()
control|)
block|{
name|returnList
operator|.
name|add
argument_list|(
operator|new
name|RDBTable
argument_list|(
name|db
argument_list|,
name|handle
argument_list|,
name|writeOptions
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|returnList
return|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|FlushOptions
name|flushOptions
init|=
operator|new
name|FlushOptions
argument_list|()
operator|.
name|setWaitForFlush
argument_list|(
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|db
operator|.
name|flush
argument_list|(
name|flushOptions
argument_list|)
expr_stmt|;
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
literal|"Unable to Flush RocksDB data"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|toIOException
argument_list|(
literal|"Unable to Flush RocksDB data"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCheckpoint (boolean flush)
specifier|public
name|DBCheckpoint
name|getCheckpoint
parameter_list|(
name|boolean
name|flush
parameter_list|)
block|{
specifier|final
name|FlushOptions
name|flushOptions
init|=
operator|new
name|FlushOptions
argument_list|()
operator|.
name|setWaitForFlush
argument_list|(
name|flush
argument_list|)
decl_stmt|;
try|try
block|{
name|db
operator|.
name|flush
argument_list|(
name|flushOptions
argument_list|)
expr_stmt|;
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
literal|"Unable to Flush RocksDB data before creating snapshot"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|checkPointManager
operator|.
name|createCheckpoint
argument_list|(
name|checkpointsParentDir
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDbLocation ()
specifier|public
name|File
name|getDbLocation
parameter_list|()
block|{
return|return
name|dbLocation
return|;
block|}
annotation|@
name|Override
DECL|method|getTableNames ()
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|getTableNames
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|tableNames
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|StringCodec
name|stringCodec
init|=
operator|new
name|StringCodec
argument_list|()
decl_stmt|;
for|for
control|(
name|ColumnFamilyHandle
name|columnFamilyHandle
range|:
name|columnFamilyHandles
control|)
block|{
try|try
block|{
name|tableNames
operator|.
name|put
argument_list|(
name|columnFamilyHandle
operator|.
name|getID
argument_list|()
argument_list|,
name|stringCodec
operator|.
name|fromPersistedFormat
argument_list|(
name|columnFamilyHandle
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RocksDBException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception while reading column family handle "
operator|+
literal|"name"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tableNames
return|;
block|}
annotation|@
name|Override
DECL|method|getCodecRegistry ()
specifier|public
name|CodecRegistry
name|getCodecRegistry
parameter_list|()
block|{
return|return
name|codecRegistry
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDb ()
specifier|public
name|RocksDB
name|getDb
parameter_list|()
block|{
return|return
name|db
return|;
block|}
block|}
end_class

end_unit

