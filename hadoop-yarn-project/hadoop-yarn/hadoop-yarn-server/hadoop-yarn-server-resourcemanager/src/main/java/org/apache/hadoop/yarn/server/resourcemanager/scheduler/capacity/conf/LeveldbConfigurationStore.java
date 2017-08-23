begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|conf
package|;
end_package

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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|util
operator|.
name|Time
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|leveldbjni
operator|.
name|JniDBFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|leveldbjni
operator|.
name|internal
operator|.
name|NativeDB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBComparator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|WriteBatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|ObjectInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|fusesource
operator|.
name|leveldbjni
operator|.
name|JniDBFactory
operator|.
name|bytes
import|;
end_import

begin_comment
comment|/**  * A LevelDB implementation of {@link YarnConfigurationStore}.  */
end_comment

begin_class
DECL|class|LeveldbConfigurationStore
specifier|public
class|class
name|LeveldbConfigurationStore
implements|implements
name|YarnConfigurationStore
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LeveldbConfigurationStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DB_NAME
specifier|private
specifier|static
specifier|final
name|String
name|DB_NAME
init|=
literal|"yarn-conf-store"
decl_stmt|;
DECL|field|LOG_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|LOG_PREFIX
init|=
literal|"log."
decl_stmt|;
DECL|field|LOG_COMMITTED_TXN
specifier|private
specifier|static
specifier|final
name|String
name|LOG_COMMITTED_TXN
init|=
literal|"committedTxn"
decl_stmt|;
DECL|field|db
specifier|private
name|DB
name|db
decl_stmt|;
comment|// Txnid for the last transaction logged to the store.
DECL|field|txnId
specifier|private
name|long
name|txnId
init|=
literal|0
decl_stmt|;
DECL|field|minTxn
specifier|private
name|long
name|minTxn
init|=
literal|0
decl_stmt|;
DECL|field|maxLogs
specifier|private
name|long
name|maxLogs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|pendingMutations
specifier|private
name|LinkedList
argument_list|<
name|LogMutation
argument_list|>
name|pendingMutations
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|compactionTimer
specifier|private
name|Timer
name|compactionTimer
decl_stmt|;
DECL|field|compactionIntervalMsec
specifier|private
name|long
name|compactionIntervalMsec
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (Configuration config, Configuration schedConf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|Configuration
name|schedConf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|config
expr_stmt|;
try|try
block|{
name|this
operator|.
name|db
operator|=
name|initDatabase
argument_list|(
name|schedConf
argument_list|)
expr_stmt|;
name|this
operator|.
name|txnId
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
operator|new
name|String
argument_list|(
name|db
operator|.
name|get
argument_list|(
name|bytes
argument_list|(
name|LOG_COMMITTED_TXN
argument_list|)
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|DBIterator
name|itr
init|=
name|db
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|itr
operator|.
name|seek
argument_list|(
name|bytes
argument_list|(
name|LOG_PREFIX
operator|+
name|txnId
argument_list|)
argument_list|)
expr_stmt|;
comment|// Seek to first uncommitted log
name|itr
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
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
name|entry
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|new
name|String
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|startsWith
argument_list|(
name|LOG_PREFIX
argument_list|)
condition|)
block|{
break|break;
block|}
name|pendingMutations
operator|.
name|add
argument_list|(
name|deserLogMutation
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|txnId
operator|++
expr_stmt|;
block|}
comment|// Get the earliest txnId stored in logs
name|itr
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
if|if
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
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
name|entry
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|byte
index|[]
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|logId
init|=
operator|new
name|String
argument_list|(
name|key
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
if|if
condition|(
name|logId
operator|.
name|startsWith
argument_list|(
name|LOG_PREFIX
argument_list|)
condition|)
block|{
name|minTxn
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|logId
operator|.
name|substring
argument_list|(
name|logId
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|maxLogs
operator|=
name|config
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDCONF_LEVELDB_MAX_LOGS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDCONF_LEVELDB_MAX_LOGS
argument_list|)
expr_stmt|;
name|this
operator|.
name|compactionIntervalMsec
operator|=
name|config
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDCONF_LEVELDB_COMPACTION_INTERVAL_SECS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDCONF_LEVELDB_COMPACTION_INTERVAL_SECS
argument_list|)
operator|*
literal|1000
expr_stmt|;
name|startCompactionTimer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|initDatabase (Configuration config)
specifier|private
name|DB
name|initDatabase
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|storeRoot
init|=
name|createStorageDir
argument_list|()
decl_stmt|;
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|createIfMissing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|options
operator|.
name|comparator
argument_list|(
operator|new
name|DBComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|key1
parameter_list|,
name|byte
index|[]
name|key2
parameter_list|)
block|{
name|String
name|key1Str
init|=
operator|new
name|String
argument_list|(
name|key1
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|String
name|key2Str
init|=
operator|new
name|String
argument_list|(
name|key2
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|int
name|key1Txn
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|key2Txn
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|key1Str
operator|.
name|startsWith
argument_list|(
name|LOG_PREFIX
argument_list|)
condition|)
block|{
name|key1Txn
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|key1Str
operator|.
name|substring
argument_list|(
name|key1Str
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key2Str
operator|.
name|startsWith
argument_list|(
name|LOG_PREFIX
argument_list|)
condition|)
block|{
name|key2Txn
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|key2Str
operator|.
name|substring
argument_list|(
name|key2Str
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO txnId could overflow, in theory
if|if
condition|(
name|key1Txn
operator|==
name|Integer
operator|.
name|MAX_VALUE
operator|&&
name|key2Txn
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
if|if
condition|(
name|key1Str
operator|.
name|equals
argument_list|(
name|key2Str
argument_list|)
operator|&&
name|key1Str
operator|.
name|equals
argument_list|(
name|LOG_COMMITTED_TXN
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|key1Str
operator|.
name|equals
argument_list|(
name|LOG_COMMITTED_TXN
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|key2Str
operator|.
name|equals
argument_list|(
name|LOG_COMMITTED_TXN
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|key1Str
operator|.
name|compareTo
argument_list|(
name|key2Str
argument_list|)
return|;
block|}
return|return
name|key1Txn
operator|-
name|key2Txn
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"logComparator"
return|;
block|}
specifier|public
name|byte
index|[]
name|findShortestSeparator
parameter_list|(
name|byte
index|[]
name|start
parameter_list|,
name|byte
index|[]
name|limit
parameter_list|)
block|{
return|return
name|start
return|;
block|}
specifier|public
name|byte
index|[]
name|findShortSuccessor
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
block|{
return|return
name|key
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using conf database at "
operator|+
name|storeRoot
argument_list|)
expr_stmt|;
name|File
name|dbfile
init|=
operator|new
name|File
argument_list|(
name|storeRoot
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|db
operator|=
name|JniDBFactory
operator|.
name|factory
operator|.
name|open
argument_list|(
name|dbfile
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NativeDB
operator|.
name|DBException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|isNotFound
argument_list|()
operator|||
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|" does not exist "
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating conf database at "
operator|+
name|dbfile
argument_list|)
expr_stmt|;
name|options
operator|.
name|createIfMissing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|db
operator|=
name|JniDBFactory
operator|.
name|factory
operator|.
name|open
argument_list|(
name|dbfile
argument_list|,
name|options
argument_list|)
expr_stmt|;
comment|// Write the initial scheduler configuration
name|WriteBatch
name|initBatch
init|=
name|db
operator|.
name|createWriteBatch
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|kv
range|:
name|config
control|)
block|{
name|initBatch
operator|.
name|put
argument_list|(
name|bytes
argument_list|(
name|kv
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|bytes
argument_list|(
name|kv
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|initBatch
operator|.
name|put
argument_list|(
name|bytes
argument_list|(
name|LOG_COMMITTED_TXN
argument_list|)
argument_list|,
name|bytes
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|write
argument_list|(
name|initBatch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|dbErr
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|dbErr
operator|.
name|getMessage
argument_list|()
argument_list|,
name|dbErr
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
return|return
name|db
return|;
block|}
DECL|method|createStorageDir ()
specifier|private
name|Path
name|createStorageDir
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|root
init|=
name|getStorageDir
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|root
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|root
return|;
block|}
DECL|method|getStorageDir ()
specifier|private
name|Path
name|getStorageDir
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|storePath
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDCONF_STORE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|storePath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No store location directory configured in "
operator|+
name|YarnConfiguration
operator|.
name|RM_SCHEDCONF_STORE_PATH
argument_list|)
throw|;
block|}
return|return
operator|new
name|Path
argument_list|(
name|storePath
argument_list|,
name|DB_NAME
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|logMutation (LogMutation logMutation)
specifier|public
specifier|synchronized
name|long
name|logMutation
parameter_list|(
name|LogMutation
name|logMutation
parameter_list|)
throws|throws
name|IOException
block|{
name|logMutation
operator|.
name|setId
argument_list|(
operator|++
name|txnId
argument_list|)
expr_stmt|;
name|WriteBatch
name|logBatch
init|=
name|db
operator|.
name|createWriteBatch
argument_list|()
decl_stmt|;
name|logBatch
operator|.
name|put
argument_list|(
name|bytes
argument_list|(
name|LOG_PREFIX
operator|+
name|txnId
argument_list|)
argument_list|,
name|serLogMutation
argument_list|(
name|logMutation
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|txnId
operator|-
name|minTxn
operator|>=
name|maxLogs
condition|)
block|{
name|logBatch
operator|.
name|delete
argument_list|(
name|bytes
argument_list|(
name|LOG_PREFIX
operator|+
name|minTxn
argument_list|)
argument_list|)
expr_stmt|;
name|minTxn
operator|++
expr_stmt|;
block|}
name|db
operator|.
name|write
argument_list|(
name|logBatch
argument_list|)
expr_stmt|;
name|pendingMutations
operator|.
name|add
argument_list|(
name|logMutation
argument_list|)
expr_stmt|;
return|return
name|txnId
return|;
block|}
annotation|@
name|Override
DECL|method|confirmMutation (long id, boolean isValid)
specifier|public
specifier|synchronized
name|boolean
name|confirmMutation
parameter_list|(
name|long
name|id
parameter_list|,
name|boolean
name|isValid
parameter_list|)
throws|throws
name|IOException
block|{
name|WriteBatch
name|updateBatch
init|=
name|db
operator|.
name|createWriteBatch
argument_list|()
decl_stmt|;
if|if
condition|(
name|isValid
condition|)
block|{
name|LogMutation
name|mutation
init|=
name|deserLogMutation
argument_list|(
name|db
operator|.
name|get
argument_list|(
name|bytes
argument_list|(
name|LOG_PREFIX
operator|+
name|id
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|changes
range|:
name|mutation
operator|.
name|getUpdates
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|changes
operator|.
name|getValue
argument_list|()
operator|==
literal|null
operator|||
name|changes
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|updateBatch
operator|.
name|delete
argument_list|(
name|bytes
argument_list|(
name|changes
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updateBatch
operator|.
name|put
argument_list|(
name|bytes
argument_list|(
name|changes
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|bytes
argument_list|(
name|changes
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|updateBatch
operator|.
name|put
argument_list|(
name|bytes
argument_list|(
name|LOG_COMMITTED_TXN
argument_list|)
argument_list|,
name|bytes
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|write
argument_list|(
name|updateBatch
argument_list|)
expr_stmt|;
comment|// Assumes logMutation and confirmMutation are done in the same
comment|// synchronized method. For example,
comment|// {@link MutableCSConfigurationProvider#mutateConfiguration(
comment|// UserGroupInformation user, SchedConfUpdateInfo confUpdate)}
name|pendingMutations
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|serLogMutation (LogMutation mutation)
specifier|private
name|byte
index|[]
name|serLogMutation
parameter_list|(
name|LogMutation
name|mutation
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
init|(
name|ObjectOutput
name|oos
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|baos
argument_list|)
init|)
block|{
name|oos
operator|.
name|writeObject
argument_list|(
name|mutation
argument_list|)
expr_stmt|;
name|oos
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
DECL|method|deserLogMutation (byte[] mutation)
specifier|private
name|LogMutation
name|deserLogMutation
parameter_list|(
name|byte
index|[]
name|mutation
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ObjectInput
name|input
init|=
operator|new
name|ObjectInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|mutation
argument_list|)
argument_list|)
init|)
block|{
return|return
operator|(
name|LogMutation
operator|)
name|input
operator|.
name|readObject
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|retrieve ()
specifier|public
specifier|synchronized
name|Configuration
name|retrieve
parameter_list|()
block|{
name|DBIterator
name|itr
init|=
name|db
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|itr
operator|.
name|seek
argument_list|(
name|bytes
argument_list|(
name|LOG_COMMITTED_TXN
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|itr
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
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
name|entry
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
operator|new
name|String
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
operator|new
name|String
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|config
return|;
block|}
annotation|@
name|Override
DECL|method|getPendingMutations ()
specifier|public
name|List
argument_list|<
name|LogMutation
argument_list|>
name|getPendingMutations
parameter_list|()
block|{
return|return
operator|new
name|LinkedList
argument_list|<>
argument_list|(
name|pendingMutations
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getConfirmedConfHistory (long fromId)
specifier|public
name|List
argument_list|<
name|LogMutation
argument_list|>
name|getConfirmedConfHistory
parameter_list|(
name|long
name|fromId
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// unimplemented
block|}
comment|// TODO below was taken from LeveldbRMStateStore, it can probably be
comment|// refactored
DECL|method|startCompactionTimer ()
specifier|private
name|void
name|startCompactionTimer
parameter_list|()
block|{
if|if
condition|(
name|compactionIntervalMsec
operator|>
literal|0
condition|)
block|{
name|compactionTimer
operator|=
operator|new
name|Timer
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" compaction timer"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|compactionTimer
operator|.
name|schedule
argument_list|(
operator|new
name|CompactionTimerTask
argument_list|()
argument_list|,
name|compactionIntervalMsec
argument_list|,
name|compactionIntervalMsec
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CompactionTimerTask
specifier|private
class|class
name|CompactionTimerTask
extends|extends
name|TimerTask
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|start
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting full compaction cycle"
argument_list|)
expr_stmt|;
try|try
block|{
name|db
operator|.
name|compactRange
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error compacting database"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|long
name|duration
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|start
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Full compaction cycle completed in "
operator|+
name|duration
operator|+
literal|" msec"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

