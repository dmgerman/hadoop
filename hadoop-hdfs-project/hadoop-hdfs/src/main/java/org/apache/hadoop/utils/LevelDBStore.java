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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|LevelDBKeyFilters
operator|.
name|LevelDBKeyFilter
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
name|iq80
operator|.
name|leveldb
operator|.
name|WriteBatch
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
name|WriteOptions
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
name|Snapshot
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
name|ReadOptions
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
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|List
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_comment
comment|/**  * LevelDB interface.  */
end_comment

begin_class
DECL|class|LevelDBStore
specifier|public
class|class
name|LevelDBStore
implements|implements
name|Closeable
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
name|LevelDBStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|db
specifier|private
name|DB
name|db
decl_stmt|;
DECL|field|dbFile
specifier|private
specifier|final
name|File
name|dbFile
decl_stmt|;
DECL|field|dbOptions
specifier|private
specifier|final
name|Options
name|dbOptions
decl_stmt|;
DECL|field|writeOptions
specifier|private
specifier|final
name|WriteOptions
name|writeOptions
decl_stmt|;
comment|/**    * Opens a DB file.    *    * @param dbPath          - DB File path    * @param createIfMissing - Create if missing    * @throws IOException    */
DECL|method|LevelDBStore (File dbPath, boolean createIfMissing)
specifier|public
name|LevelDBStore
parameter_list|(
name|File
name|dbPath
parameter_list|,
name|boolean
name|createIfMissing
parameter_list|)
throws|throws
name|IOException
block|{
name|dbOptions
operator|=
operator|new
name|Options
argument_list|()
expr_stmt|;
name|dbOptions
operator|.
name|createIfMissing
argument_list|(
name|createIfMissing
argument_list|)
expr_stmt|;
name|db
operator|=
name|JniDBFactory
operator|.
name|factory
operator|.
name|open
argument_list|(
name|dbPath
argument_list|,
name|dbOptions
argument_list|)
expr_stmt|;
if|if
condition|(
name|db
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Db is null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|dbFile
operator|=
name|dbPath
expr_stmt|;
name|this
operator|.
name|writeOptions
operator|=
operator|new
name|WriteOptions
argument_list|()
operator|.
name|sync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Opens a DB file.    *    * @param dbPath          - DB File path    * @throws IOException    */
DECL|method|LevelDBStore (File dbPath, Options options)
specifier|public
name|LevelDBStore
parameter_list|(
name|File
name|dbPath
parameter_list|,
name|Options
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|dbOptions
operator|=
name|options
expr_stmt|;
name|db
operator|=
name|JniDBFactory
operator|.
name|factory
operator|.
name|open
argument_list|(
name|dbPath
argument_list|,
name|options
argument_list|)
expr_stmt|;
if|if
condition|(
name|db
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Db is null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|dbFile
operator|=
name|dbPath
expr_stmt|;
name|this
operator|.
name|writeOptions
operator|=
operator|new
name|WriteOptions
argument_list|()
operator|.
name|sync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Puts a Key into file.    *    * @param key   - key    * @param value - value    */
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
block|{
name|db
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|writeOptions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get Key.    *    * @param key key    * @return value    */
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
comment|/**    * Delete Key.    *    * @param key - Key    */
DECL|method|delete (byte[] key)
specifier|public
name|void
name|delete
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
block|{
name|db
operator|.
name|delete
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes the DB.    *    * @throws IOException    */
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
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns true if the DB is empty.    *    * @return boolean    * @throws IOException    */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|DBIterator
name|iter
init|=
name|db
operator|.
name|iterator
argument_list|()
decl_stmt|;
try|try
block|{
name|iter
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
return|return
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
finally|finally
block|{
name|iter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns Java File Object that points to the DB.    * @return File    */
DECL|method|getDbFile ()
specifier|public
name|File
name|getDbFile
parameter_list|()
block|{
return|return
name|dbFile
return|;
block|}
comment|/**    * Returns the actual levelDB object.    * @return DB handle.    */
DECL|method|getDB ()
specifier|public
name|DB
name|getDB
parameter_list|()
block|{
return|return
name|db
return|;
block|}
comment|/**    * Returns an iterator on all the key-value pairs in the DB.    * @return an iterator on DB entries.    */
DECL|method|getIterator ()
specifier|public
name|DBIterator
name|getIterator
parameter_list|()
block|{
return|return
name|db
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|IOException
block|{
name|JniDBFactory
operator|.
name|factory
operator|.
name|destroy
argument_list|(
name|dbFile
argument_list|,
name|dbOptions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a write batch for write multiple key-value pairs atomically.    * @return write batch that can be commit atomically.    */
DECL|method|createWriteBatch ()
specifier|public
name|WriteBatch
name|createWriteBatch
parameter_list|()
block|{
return|return
name|db
operator|.
name|createWriteBatch
argument_list|()
return|;
block|}
comment|/**    * Commit multiple writes of key-value pairs atomically.    * @param wb    */
DECL|method|commitWriteBatch (WriteBatch wb)
specifier|public
name|void
name|commitWriteBatch
parameter_list|(
name|WriteBatch
name|wb
parameter_list|)
block|{
name|db
operator|.
name|write
argument_list|(
name|wb
argument_list|,
name|writeOptions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Close a write batch of multiple writes to key-value pairs.    * @param wb - write batch.    * @throws IOException    */
DECL|method|closeWriteBatch (WriteBatch wb)
specifier|public
name|void
name|closeWriteBatch
parameter_list|(
name|WriteBatch
name|wb
parameter_list|)
throws|throws
name|IOException
block|{
name|wb
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Compacts the DB by removing deleted keys etc.    * @throws IOException if there is an error.    */
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
comment|// From LevelDB docs : begin == null and end == null means the whole DB.
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
block|}
comment|/**    * Returns a certain range of key value pairs as a list based on a startKey    * or count.    *    * @param keyPrefix start key.    * @param count number of entries to return.    * @return a range of entries or an empty list if nothing found.    * @throws IOException    *    * @see #getRangeKVs(byte[], int, LevelDBKeyFilter...)    */
DECL|method|getRangeKVs (byte[] keyPrefix, int count)
specifier|public
name|List
argument_list|<
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
name|keyPrefix
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|LevelDBKeyFilter
name|emptyFilter
init|=
parameter_list|(
name|preKey
parameter_list|,
name|currentKey
parameter_list|,
name|nextKey
parameter_list|)
lambda|->
literal|true
decl_stmt|;
return|return
name|getRangeKVs
argument_list|(
name|keyPrefix
argument_list|,
name|count
argument_list|,
name|emptyFilter
argument_list|)
return|;
block|}
comment|/**    * Returns a certain range of key value pairs as a list based on a    * startKey or count. Further a {@link LevelDBKeyFilter} can be added to    * filter keys if necessary. To prevent race conditions while listing    * entries, this implementation takes a snapshot and lists the entries from    * the snapshot. This may, on the other hand, cause the range result slight    * different with actual data if data is updating concurrently.    *<p>    * If the startKey is specified and found in levelDB, this key and the keys    * after this key will be included in the result. If the startKey is null    * all entries will be included as long as other conditions are satisfied.    * If the given startKey doesn't exist, an IOException will be thrown.    *<p>    * The count argument is to limit number of total entries to return,    * the value for count must be an integer greater than 0.    *<p>    * This method allows to specify one or more {@link LevelDBKeyFilter}    * to filter keys by certain condition. Once given, only the entries    * whose key passes all the filters will be included in the result.    *    * @param startKey a start key.    * @param count max number of entries to return.    * @param filters customized one or more {@link LevelDBKeyFilter}.    * @return a list of entries found in the database.    * @throws IOException if an invalid startKey is given or other I/O errors.    * @throws IllegalArgumentException if count is less than 0.    */
DECL|method|getRangeKVs (byte[] startKey, int count, LevelDBKeyFilter... filters)
specifier|public
name|List
argument_list|<
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
name|LevelDBKeyFilter
modifier|...
name|filters
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
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
name|Snapshot
name|snapShot
init|=
literal|null
decl_stmt|;
name|DBIterator
name|dbIter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|snapShot
operator|=
name|db
operator|.
name|getSnapshot
argument_list|()
expr_stmt|;
name|ReadOptions
name|readOptions
init|=
operator|new
name|ReadOptions
argument_list|()
operator|.
name|snapshot
argument_list|(
name|snapShot
argument_list|)
decl_stmt|;
name|dbIter
operator|=
name|db
operator|.
name|iterator
argument_list|(
name|readOptions
argument_list|)
expr_stmt|;
if|if
condition|(
name|startKey
operator|==
literal|null
condition|)
block|{
name|dbIter
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|db
operator|.
name|get
argument_list|(
name|startKey
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid start key, not found in current db."
argument_list|)
throw|;
block|}
name|dbIter
operator|.
name|seek
argument_list|(
name|startKey
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|dbIter
operator|.
name|hasNext
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
name|preKey
init|=
name|dbIter
operator|.
name|hasPrev
argument_list|()
condition|?
name|dbIter
operator|.
name|peekPrev
argument_list|()
operator|.
name|getKey
argument_list|()
else|:
literal|null
decl_stmt|;
name|byte
index|[]
name|nextKey
init|=
name|dbIter
operator|.
name|hasNext
argument_list|()
condition|?
name|dbIter
operator|.
name|peekNext
argument_list|()
operator|.
name|getKey
argument_list|()
else|:
literal|null
decl_stmt|;
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|current
init|=
name|dbIter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|filters
operator|==
literal|null
operator|||
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
name|preKey
argument_list|,
name|current
operator|.
name|getKey
argument_list|()
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
name|current
argument_list|)
block|;         }
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|snapShot
operator|!=
literal|null
condition|)
block|{
name|snapShot
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dbIter
operator|!=
literal|null
condition|)
block|{
name|dbIter
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Time consumed for getRangeKVs() is {},"
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
block|}
end_class

end_unit

