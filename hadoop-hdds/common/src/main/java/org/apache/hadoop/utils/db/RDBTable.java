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
name|charset
operator|.
name|StandardCharsets
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
name|hdfs
operator|.
name|DFSUtil
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
name|ReadOptions
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
comment|/**  * RocksDB implementation of ozone metadata store. This class should be only  * used as part of TypedTable as it's underlying implementation to access the  * metadata store content. All other user's using Table should use TypedTable.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RDBTable
class|class
name|RDBTable
implements|implements
name|Table
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
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
name|RDBTable
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
DECL|field|handle
specifier|private
specifier|final
name|ColumnFamilyHandle
name|handle
decl_stmt|;
DECL|field|writeOptions
specifier|private
specifier|final
name|WriteOptions
name|writeOptions
decl_stmt|;
comment|/**    * Constructs a TableStore.    *    * @param db - DBstore that we are using.    * @param handle - ColumnFamily Handle.    * @param writeOptions - RocksDB write Options.    */
DECL|method|RDBTable (RocksDB db, ColumnFamilyHandle handle, WriteOptions writeOptions)
name|RDBTable
parameter_list|(
name|RocksDB
name|db
parameter_list|,
name|ColumnFamilyHandle
name|handle
parameter_list|,
name|WriteOptions
name|writeOptions
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|handle
operator|=
name|handle
expr_stmt|;
name|this
operator|.
name|writeOptions
operator|=
name|writeOptions
expr_stmt|;
block|}
comment|/**    * Converts RocksDB exception to IOE.    * @param msg  - Message to add to exception.    * @param e - Original Exception.    * @return  IOE.    */
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
comment|/**    * Returns the Column family Handle.    *    * @return ColumnFamilyHandle.    */
DECL|method|getHandle ()
specifier|public
name|ColumnFamilyHandle
name|getHandle
parameter_list|()
block|{
return|return
name|handle
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
name|handle
argument_list|,
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to write to DB. Key: {}"
argument_list|,
operator|new
name|String
argument_list|(
name|key
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|toIOException
argument_list|(
literal|"Failed to put key-value to metadata "
operator|+
literal|"store"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|putWithBatch (BatchOperation batch, byte[] key, byte[] value)
specifier|public
name|void
name|putWithBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|,
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
if|if
condition|(
name|batch
operator|instanceof
name|RDBBatchOperation
condition|)
block|{
operator|(
operator|(
name|RDBBatchOperation
operator|)
name|batch
operator|)
operator|.
name|put
argument_list|(
name|getHandle
argument_list|()
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"batch should be RDBBatchOperation"
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
try|try
init|(
name|TableIterator
argument_list|<
name|byte
index|[]
argument_list|,
name|ByteArrayKeyValue
argument_list|>
name|keyIter
init|=
name|iterator
argument_list|()
init|)
block|{
name|keyIter
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
return|return
operator|!
name|keyIter
operator|.
name|hasNext
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|isExist (byte[] key)
specifier|public
name|boolean
name|isExist
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
name|handle
argument_list|,
name|key
argument_list|)
operator|!=
literal|null
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
literal|"Error in accessing DB. "
argument_list|,
name|e
argument_list|)
throw|;
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
name|handle
argument_list|,
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
name|handle
argument_list|,
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
DECL|method|deleteWithBatch (BatchOperation batch, byte[] key)
specifier|public
name|void
name|deleteWithBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|,
name|byte
index|[]
name|key
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|batch
operator|instanceof
name|RDBBatchOperation
condition|)
block|{
operator|(
operator|(
name|RDBBatchOperation
operator|)
name|batch
operator|)
operator|.
name|delete
argument_list|(
name|getHandle
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"batch should be RDBBatchOperation"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|TableIterator
argument_list|<
name|byte
index|[]
argument_list|,
name|ByteArrayKeyValue
argument_list|>
name|iterator
parameter_list|()
block|{
name|ReadOptions
name|readOptions
init|=
operator|new
name|ReadOptions
argument_list|()
decl_stmt|;
name|readOptions
operator|.
name|setFillCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
operator|new
name|RDBStoreIterator
argument_list|(
name|db
operator|.
name|newIterator
argument_list|(
name|handle
argument_list|,
name|readOptions
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|this
operator|.
name|getHandle
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RocksDBException
name|rdbEx
parameter_list|)
block|{
throw|throw
name|toIOException
argument_list|(
literal|"Unable to get the table name."
argument_list|,
name|rdbEx
argument_list|)
throw|;
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
name|Exception
block|{
comment|// Nothing do for a Column Family.
block|}
block|}
end_class

end_unit

