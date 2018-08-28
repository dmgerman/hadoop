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
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
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
specifier|final
name|RocksDB
name|db
decl_stmt|;
DECL|field|dbLocation
specifier|private
specifier|final
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
specifier|final
name|List
argument_list|<
name|ColumnFamilyHandle
argument_list|>
name|columnFamilyHandles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
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
name|MBeans
operator|.
name|register
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
DECL|method|move (byte[] key, Table source, Table dest)
specifier|public
name|void
name|move
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|Table
name|source
parameter_list|,
name|Table
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|RDBTable
name|sourceTable
decl_stmt|;
name|RDBTable
name|destTable
decl_stmt|;
if|if
condition|(
name|source
operator|instanceof
name|RDBTable
condition|)
block|{
name|sourceTable
operator|=
operator|(
name|RDBTable
operator|)
name|source
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected Table type. Expected RocksTable Store for Source."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected TableStore Type in source. Expected "
operator|+
literal|"RocksDBTable."
argument_list|)
throw|;
block|}
if|if
condition|(
name|dest
operator|instanceof
name|RDBTable
condition|)
block|{
name|destTable
operator|=
operator|(
name|RDBTable
operator|)
name|dest
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected Table type. Expected RocksTable Store for Dest."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected TableStore Type in dest. Expected "
operator|+
literal|"RocksDBTable."
argument_list|)
throw|;
block|}
try|try
init|(
name|WriteBatch
name|batch
init|=
operator|new
name|WriteBatch
argument_list|()
init|)
block|{
name|byte
index|[]
name|value
init|=
name|sourceTable
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|destTable
operator|.
name|getHandle
argument_list|()
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|batch
operator|.
name|delete
argument_list|(
name|sourceTable
operator|.
name|getHandle
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|db
operator|.
name|write
argument_list|(
name|writeOptions
argument_list|,
name|batch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RocksDBException
name|rockdbException
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Move of key failed. Key:{}"
argument_list|,
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|toIOException
argument_list|(
literal|"Unable to move key: "
operator|+
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|key
argument_list|)
argument_list|,
name|rockdbException
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|move (byte[] key, byte[] value, Table source, Table dest)
specifier|public
name|void
name|move
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|Table
name|source
parameter_list|,
name|Table
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|RDBTable
name|sourceTable
decl_stmt|;
name|RDBTable
name|destTable
decl_stmt|;
if|if
condition|(
name|source
operator|instanceof
name|RDBTable
condition|)
block|{
name|sourceTable
operator|=
operator|(
name|RDBTable
operator|)
name|source
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected Table type. Expected RocksTable Store for Source."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected TableStore Type in source. Expected "
operator|+
literal|"RocksDBTable."
argument_list|)
throw|;
block|}
if|if
condition|(
name|dest
operator|instanceof
name|RDBTable
condition|)
block|{
name|destTable
operator|=
operator|(
name|RDBTable
operator|)
name|dest
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected Table type. Expected RocksTable Store for Dest."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected TableStore Type in dest. Expected "
operator|+
literal|"RocksDBTable."
argument_list|)
throw|;
block|}
try|try
init|(
name|WriteBatch
name|batch
init|=
operator|new
name|WriteBatch
argument_list|()
init|)
block|{
name|batch
operator|.
name|put
argument_list|(
name|destTable
operator|.
name|getHandle
argument_list|()
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|batch
operator|.
name|delete
argument_list|(
name|sourceTable
operator|.
name|getHandle
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|db
operator|.
name|write
argument_list|(
name|writeOptions
argument_list|,
name|batch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RocksDBException
name|rockdbException
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Move of key failed. Key:{}"
argument_list|,
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|toIOException
argument_list|(
literal|"Unable to move key: "
operator|+
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|key
argument_list|)
argument_list|,
name|rockdbException
argument_list|)
throw|;
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
block|}
end_class

end_unit

