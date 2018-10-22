begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
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
name|tuple
operator|.
name|ImmutablePair
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
name|DbPath
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|Options
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
name|RocksIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|WriteBatch
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
name|util
operator|.
name|AbstractMap
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
name|Arrays
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

begin_comment
comment|/**  * RocksDB implementation of ozone metadata store.  */
end_comment

begin_class
DECL|class|RocksDBStore
specifier|public
class|class
name|RocksDBStore
implements|implements
name|MetadataStore
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
name|RocksDBStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|db
specifier|private
name|RocksDB
name|db
init|=
literal|null
decl_stmt|;
DECL|field|dbLocation
specifier|private
name|File
name|dbLocation
decl_stmt|;
DECL|field|writeOptions
specifier|private
name|WriteOptions
name|writeOptions
decl_stmt|;
DECL|field|dbOptions
specifier|private
name|Options
name|dbOptions
decl_stmt|;
DECL|field|statMBeanName
specifier|private
name|ObjectName
name|statMBeanName
decl_stmt|;
DECL|method|RocksDBStore (File dbFile, Options options)
specifier|public
name|RocksDBStore
parameter_list|(
name|File
name|dbFile
parameter_list|,
name|Options
name|options
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
name|RocksDB
operator|.
name|loadLibrary
argument_list|()
expr_stmt|;
name|dbOptions
operator|=
name|options
expr_stmt|;
name|dbLocation
operator|=
name|dbFile
expr_stmt|;
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
argument_list|)
expr_stmt|;
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
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
block|}
catch|catch
parameter_list|(
name|RocksDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
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
literal|"[Option] compactionPriority= {}"
argument_list|,
name|options
operator|.
name|compactionStyle
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"[Option] compressionType= {}"
argument_list|,
name|options
operator|.
name|compressionType
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"[Option] writeBufferSize= {}"
argument_list|,
name|options
operator|.
name|writeBufferSize
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
DECL|method|put (byte[] key, byte[] value)
specifier|public
name|void
name|put
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|db
operator|.
name|put
argument_list|(
name|writeOptions
argument_list|,
name|key
argument_list|,
name|value
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
literal|"Failed to put key-value to metadata store"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|RocksIterator
name|it
init|=
literal|null
decl_stmt|;
try|try
block|{
name|it
operator|=
name|db
operator|.
name|newIterator
argument_list|()
expr_stmt|;
name|it
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
return|return
operator|!
name|it
operator|.
name|isValid
argument_list|()
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|it
operator|!=
literal|null
condition|)
block|{
name|it
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|get (byte[] key)
specifier|public
name|byte
index|[]
name|get
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|db
operator|.
name|get
argument_list|(
name|key
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
literal|"Failed to get the value for the given key"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|delete (byte[] key)
specifier|public
name|void
name|delete
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|db
operator|.
name|delete
argument_list|(
name|key
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
literal|"Failed to delete the given key"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getRangeKVs (byte[] startKey, int count, MetadataKeyFilters.MetadataKeyFilter... filters)
specifier|public
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|getRangeKVs
parameter_list|(
name|byte
index|[]
name|startKey
parameter_list|,
name|int
name|count
parameter_list|,
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
modifier|...
name|filters
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
return|return
name|getRangeKVs
argument_list|(
name|startKey
argument_list|,
name|count
argument_list|,
literal|false
argument_list|,
name|filters
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSequentialRangeKVs (byte[] startKey, int count, MetadataKeyFilters.MetadataKeyFilter... filters)
specifier|public
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|getSequentialRangeKVs
parameter_list|(
name|byte
index|[]
name|startKey
parameter_list|,
name|int
name|count
parameter_list|,
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
modifier|...
name|filters
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
return|return
name|getRangeKVs
argument_list|(
name|startKey
argument_list|,
name|count
argument_list|,
literal|true
argument_list|,
name|filters
argument_list|)
return|;
block|}
DECL|method|getRangeKVs (byte[] startKey, int count, boolean sequential, MetadataKeyFilters.MetadataKeyFilter... filters)
specifier|private
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|getRangeKVs
parameter_list|(
name|byte
index|[]
name|startKey
parameter_list|,
name|int
name|count
parameter_list|,
name|boolean
name|sequential
parameter_list|,
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
modifier|...
name|filters
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid count given "
operator|+
name|count
operator|+
literal|", count must be greater than 0"
argument_list|)
throw|;
block|}
name|RocksIterator
name|it
init|=
literal|null
decl_stmt|;
try|try
block|{
name|it
operator|=
name|db
operator|.
name|newIterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|startKey
operator|==
literal|null
condition|)
block|{
name|it
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|get
argument_list|(
name|startKey
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// Key not found, return empty list
return|return
name|result
return|;
block|}
name|it
operator|.
name|seek
argument_list|(
name|startKey
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|it
operator|.
name|isValid
argument_list|()
operator|&&
name|result
operator|.
name|size
argument_list|()
operator|<
name|count
condition|)
block|{
name|byte
index|[]
name|currentKey
init|=
name|it
operator|.
name|key
argument_list|()
decl_stmt|;
name|byte
index|[]
name|currentValue
init|=
name|it
operator|.
name|value
argument_list|()
decl_stmt|;
name|it
operator|.
name|prev
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|prevKey
init|=
name|it
operator|.
name|isValid
argument_list|()
condition|?
name|it
operator|.
name|key
argument_list|()
else|:
literal|null
decl_stmt|;
name|it
operator|.
name|seek
argument_list|(
name|currentKey
argument_list|)
expr_stmt|;
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|nextKey
init|=
name|it
operator|.
name|isValid
argument_list|()
condition|?
name|it
operator|.
name|key
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|filters
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|AbstractMap
operator|.
name|SimpleImmutableEntry
argument_list|<>
argument_list|(
name|currentKey
argument_list|,
name|currentValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Arrays
operator|.
name|asList
argument_list|(
name|filters
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|entry
lambda|->
name|entry
operator|.
name|filterKey
argument_list|(
name|prevKey
argument_list|,
name|currentKey
argument_list|,
name|nextKey
argument_list|)
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|AbstractMap
operator|.
name|SimpleImmutableEntry
argument_list|<>
argument_list|(
name|currentKey
argument_list|,
name|currentValue
argument_list|)
argument_list|)
block|;           }
else|else
block|{
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|sequential
condition|)
block|{
comment|// if the caller asks for a sequential range of results,
comment|// and we met a dis-match, abort iteration from here.
comment|// if result is empty, we continue to look for the first match.
break|break;
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|it
operator|!=
literal|null
condition|)
block|{
name|it
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|timeConsumed
init|=
name|end
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|filters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
name|filter
range|:
name|filters
control|)
block|{
name|int
name|scanned
init|=
name|filter
operator|.
name|getKeysScannedNum
argument_list|()
decl_stmt|;
name|int
name|hinted
init|=
name|filter
operator|.
name|getKeysHintedNum
argument_list|()
decl_stmt|;
if|if
condition|(
name|scanned
operator|>
literal|0
operator|||
name|hinted
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getRangeKVs ({}) numOfKeysScanned={}, numOfKeysHinted={}"
argument_list|,
name|filter
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|filter
operator|.
name|getKeysScannedNum
argument_list|()
argument_list|,
name|filter
operator|.
name|getKeysHintedNum
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Time consumed for getRangeKVs() is {}ms,"
operator|+
literal|" result length is {}."
argument_list|,
name|timeConsumed
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|writeBatch (BatchOperation operation)
specifier|public
name|void
name|writeBatch
parameter_list|(
name|BatchOperation
name|operation
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|BatchOperation
operator|.
name|SingleOperation
argument_list|>
name|operations
init|=
name|operation
operator|.
name|getOperations
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|operations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
init|(
name|WriteBatch
name|writeBatch
init|=
operator|new
name|WriteBatch
argument_list|()
init|)
block|{
for|for
control|(
name|BatchOperation
operator|.
name|SingleOperation
name|opt
range|:
name|operations
control|)
block|{
switch|switch
condition|(
name|opt
operator|.
name|getOpt
argument_list|()
condition|)
block|{
case|case
name|DELETE
case|:
name|writeBatch
operator|.
name|delete
argument_list|(
name|opt
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|PUT
case|:
name|writeBatch
operator|.
name|put
argument_list|(
name|opt
operator|.
name|getKey
argument_list|()
argument_list|,
name|opt
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid operation "
operator|+
name|opt
operator|.
name|getOpt
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|db
operator|.
name|write
argument_list|(
name|writeOptions
argument_list|,
name|writeBatch
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
literal|"Batch write operation failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
DECL|method|deleteQuietly (File fileOrDir)
specifier|private
name|void
name|deleteQuietly
parameter_list|(
name|File
name|fileOrDir
parameter_list|)
block|{
if|if
condition|(
name|fileOrDir
operator|!=
literal|null
operator|&&
name|fileOrDir
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
name|FileUtils
operator|.
name|forceDelete
argument_list|(
name|fileOrDir
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
name|warn
argument_list|(
literal|"Failed to delete dir {}"
argument_list|,
name|fileOrDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Make sure db is closed.
name|close
argument_list|()
expr_stmt|;
comment|// There is no destroydb java API available,
comment|// equivalently we can delete all db directories.
name|deleteQuietly
argument_list|(
name|dbLocation
argument_list|)
expr_stmt|;
name|deleteQuietly
argument_list|(
operator|new
name|File
argument_list|(
name|dbOptions
operator|.
name|dbLogDir
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|deleteQuietly
argument_list|(
operator|new
name|File
argument_list|(
name|dbOptions
operator|.
name|walDir
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DbPath
argument_list|>
name|dbPaths
init|=
name|dbOptions
operator|.
name|dbPaths
argument_list|()
decl_stmt|;
if|if
condition|(
name|dbPaths
operator|!=
literal|null
condition|)
block|{
name|dbPaths
operator|.
name|forEach
argument_list|(
name|dbPath
lambda|->
block|{
name|deleteQuietly
argument_list|(
operator|new
name|File
argument_list|(
name|dbPath
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|peekAround (int offset, byte[] from)
specifier|public
name|ImmutablePair
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|peekAround
parameter_list|(
name|int
name|offset
parameter_list|,
name|byte
index|[]
name|from
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
name|RocksIterator
name|it
init|=
literal|null
decl_stmt|;
try|try
block|{
name|it
operator|=
name|db
operator|.
name|newIterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|from
operator|==
literal|null
condition|)
block|{
name|it
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|it
operator|.
name|seek
argument_list|(
name|from
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|it
operator|.
name|isValid
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|offset
condition|)
block|{
case|case
literal|0
case|:
break|break;
case|case
literal|1
case|:
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
break|break;
case|case
operator|-
literal|1
case|:
name|it
operator|.
name|prev
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Position can only be -1, 0 "
operator|+
literal|"or 1, but found "
operator|+
name|offset
argument_list|)
throw|;
block|}
return|return
name|it
operator|.
name|isValid
argument_list|()
condition|?
operator|new
name|ImmutablePair
argument_list|<>
argument_list|(
name|it
operator|.
name|key
argument_list|()
argument_list|,
name|it
operator|.
name|value
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|it
operator|!=
literal|null
condition|)
block|{
name|it
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|iterate (byte[] from, EntryConsumer consumer)
specifier|public
name|void
name|iterate
parameter_list|(
name|byte
index|[]
name|from
parameter_list|,
name|EntryConsumer
name|consumer
parameter_list|)
throws|throws
name|IOException
block|{
name|RocksIterator
name|it
init|=
literal|null
decl_stmt|;
try|try
block|{
name|it
operator|=
name|db
operator|.
name|newIterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|from
operator|!=
literal|null
condition|)
block|{
name|it
operator|.
name|seek
argument_list|(
name|from
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|it
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|it
operator|.
name|isValid
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|consumer
operator|.
name|consume
argument_list|(
name|it
operator|.
name|key
argument_list|()
argument_list|,
name|it
operator|.
name|value
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|it
operator|!=
literal|null
condition|)
block|{
name|it
operator|.
name|close
argument_list|()
expr_stmt|;
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
DECL|method|iterator ()
specifier|public
name|MetaStoreIterator
argument_list|<
name|KeyValue
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|RocksDBStoreIterator
argument_list|(
name|db
operator|.
name|newIterator
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

