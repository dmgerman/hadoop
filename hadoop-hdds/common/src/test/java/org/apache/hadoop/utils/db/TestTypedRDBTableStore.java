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
name|HashSet
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
name|Set
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
name|Optional
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
name|test
operator|.
name|GenericTestUtils
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
name|db
operator|.
name|Table
operator|.
name|KeyValue
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
name|RandomStringUtils
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
name|db
operator|.
name|cache
operator|.
name|CacheKey
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
name|db
operator|.
name|cache
operator|.
name|CacheValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|ColumnFamilyOptions
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
name|Statistics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|StatsLevel
import|;
end_import

begin_comment
comment|/**  * Tests for RocksDBTable Store.  */
end_comment

begin_class
DECL|class|TestTypedRDBTableStore
specifier|public
class|class
name|TestTypedRDBTableStore
block|{
DECL|field|count
specifier|private
specifier|static
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|families
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|families
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|RocksDB
operator|.
name|DEFAULT_COLUMN_FAMILY
argument_list|)
argument_list|,
literal|"First"
argument_list|,
literal|"Second"
argument_list|,
literal|"Third"
argument_list|,
literal|"Fourth"
argument_list|,
literal|"Fifth"
argument_list|,
literal|"Sixth"
argument_list|,
literal|"Seven"
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
DECL|field|rdbStore
specifier|private
name|RDBStore
name|rdbStore
init|=
literal|null
decl_stmt|;
DECL|field|options
specifier|private
name|DBOptions
name|options
init|=
literal|null
decl_stmt|;
DECL|field|codecRegistry
specifier|private
name|CodecRegistry
name|codecRegistry
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|options
operator|=
operator|new
name|DBOptions
argument_list|()
expr_stmt|;
name|options
operator|.
name|setCreateIfMissing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|options
operator|.
name|setCreateMissingColumnFamilies
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Statistics
name|statistics
init|=
operator|new
name|Statistics
argument_list|()
decl_stmt|;
name|statistics
operator|.
name|setStatsLevel
argument_list|(
name|StatsLevel
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|options
operator|=
name|options
operator|.
name|setStatistics
argument_list|(
name|statistics
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|TableConfig
argument_list|>
name|configSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|families
control|)
block|{
name|TableConfig
name|newConfig
init|=
operator|new
name|TableConfig
argument_list|(
name|name
argument_list|,
operator|new
name|ColumnFamilyOptions
argument_list|()
argument_list|)
decl_stmt|;
name|configSet
operator|.
name|add
argument_list|(
name|newConfig
argument_list|)
expr_stmt|;
block|}
name|rdbStore
operator|=
operator|new
name|RDBStore
argument_list|(
name|folder
operator|.
name|newFolder
argument_list|()
argument_list|,
name|options
argument_list|,
name|configSet
argument_list|)
expr_stmt|;
name|codecRegistry
operator|=
operator|new
name|CodecRegistry
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|rdbStore
operator|!=
literal|null
condition|)
block|{
name|rdbStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|toIOException ()
specifier|public
name|void
name|toIOException
parameter_list|()
block|{   }
annotation|@
name|Test
DECL|method|putGetAndEmpty ()
specifier|public
name|void
name|putGetAndEmpty
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|testTable
init|=
name|createTypedTable
argument_list|(
literal|"First"
argument_list|)
init|)
block|{
name|String
name|key
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|testTable
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|testTable
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|readValue
init|=
name|testTable
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
argument_list|,
name|readValue
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|Table
name|secondTable
init|=
name|rdbStore
operator|.
name|getTable
argument_list|(
literal|"Second"
argument_list|)
init|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|secondTable
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createTypedTable (String name)
specifier|private
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|createTypedTable
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TypedTable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|rdbStore
operator|.
name|getTable
argument_list|(
name|name
argument_list|)
argument_list|,
name|codecRegistry
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|delete ()
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|deletedKeys
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|validKeys
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|100
condition|;
name|x
operator|++
control|)
block|{
name|deletedKeys
operator|.
name|add
argument_list|(
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|100
condition|;
name|x
operator|++
control|)
block|{
name|validKeys
operator|.
name|add
argument_list|(
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Write all the keys and delete the keys scheduled for delete.
comment|//Assert we find only expected keys in the Table.
try|try
init|(
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|testTable
init|=
name|createTypedTable
argument_list|(
literal|"Fourth"
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|deletedKeys
operator|.
name|size
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|testTable
operator|.
name|put
argument_list|(
name|deletedKeys
operator|.
name|get
argument_list|(
name|x
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|testTable
operator|.
name|delete
argument_list|(
name|deletedKeys
operator|.
name|get
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|validKeys
operator|.
name|size
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|testTable
operator|.
name|put
argument_list|(
name|validKeys
operator|.
name|get
argument_list|(
name|x
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|validKeys
operator|.
name|size
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|testTable
operator|.
name|get
argument_list|(
name|validKeys
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|deletedKeys
operator|.
name|size
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|Assert
operator|.
name|assertNull
argument_list|(
name|testTable
operator|.
name|get
argument_list|(
name|deletedKeys
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|batchPut ()
specifier|public
name|void
name|batchPut
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|testTable
init|=
name|createTypedTable
argument_list|(
literal|"Fourth"
argument_list|)
init|;
name|BatchOperation
name|batch
operator|=
name|rdbStore
operator|.
name|initBatchOperation
argument_list|()
init|)
block|{
comment|//given
name|String
name|key
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|//when
name|testTable
operator|.
name|putWithBatch
argument_list|(
name|batch
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|rdbStore
operator|.
name|commitBatchOperation
argument_list|(
name|batch
argument_list|)
expr_stmt|;
comment|//then
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|testTable
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|batchDelete ()
specifier|public
name|void
name|batchDelete
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|testTable
init|=
name|createTypedTable
argument_list|(
literal|"Fourth"
argument_list|)
init|;
name|BatchOperation
name|batch
operator|=
name|rdbStore
operator|.
name|initBatchOperation
argument_list|()
init|)
block|{
comment|//given
name|String
name|key
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|testTable
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|//when
name|testTable
operator|.
name|deleteWithBatch
argument_list|(
name|batch
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|rdbStore
operator|.
name|commitBatchOperation
argument_list|(
name|batch
argument_list|)
expr_stmt|;
comment|//then
name|Assert
operator|.
name|assertNull
argument_list|(
name|testTable
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|consume (Table.KeyValue keyValue)
specifier|private
specifier|static
name|boolean
name|consume
parameter_list|(
name|Table
operator|.
name|KeyValue
name|keyValue
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
try|try
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|keyValue
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Test
DECL|method|forEachAndIterator ()
specifier|public
name|void
name|forEachAndIterator
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iterCount
init|=
literal|100
decl_stmt|;
try|try
init|(
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|testTable
init|=
name|createTypedTable
argument_list|(
literal|"Sixth"
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|iterCount
condition|;
name|x
operator|++
control|)
block|{
name|String
name|key
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|testTable
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|int
name|localCount
init|=
literal|0
decl_stmt|;
try|try
init|(
name|TableIterator
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|KeyValue
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|iter
init|=
name|testTable
operator|.
name|iterator
argument_list|()
init|)
block|{
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Table
operator|.
name|KeyValue
name|keyValue
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|localCount
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|iterCount
argument_list|,
name|localCount
argument_list|)
expr_stmt|;
name|iter
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
name|iter
operator|.
name|forEachRemaining
argument_list|(
name|TestTypedRDBTableStore
operator|::
name|consume
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|iterCount
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testTypedTableWithCache ()
specifier|public
name|void
name|testTypedTableWithCache
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterCount
init|=
literal|10
decl_stmt|;
try|try
init|(
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|testTable
init|=
name|createTypedTable
argument_list|(
literal|"Seven"
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|iterCount
condition|;
name|x
operator|++
control|)
block|{
name|String
name|key
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|testTable
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|key
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|value
argument_list|)
argument_list|,
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// As we have added to cache, so get should return value even if it
comment|// does not exist in DB.
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|iterCount
condition|;
name|x
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|testTable
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testTypedTableWithCacheWithFewDeletedOperationType ()
specifier|public
name|void
name|testTypedTableWithCacheWithFewDeletedOperationType
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterCount
init|=
literal|10
decl_stmt|;
try|try
init|(
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|testTable
init|=
name|createTypedTable
argument_list|(
literal|"Seven"
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|iterCount
condition|;
name|x
operator|++
control|)
block|{
name|String
name|key
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
decl_stmt|;
if|if
condition|(
name|x
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|testTable
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|key
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|value
argument_list|)
argument_list|,
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|testTable
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|key
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|absent
argument_list|()
argument_list|,
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// As we have added to cache, so get should return value even if it
comment|// does not exist in DB.
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|iterCount
condition|;
name|x
operator|++
control|)
block|{
if|if
condition|(
name|x
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
argument_list|,
name|testTable
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertNull
argument_list|(
name|testTable
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|testTable
operator|.
name|cleanupCache
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
operator|(
operator|(
name|TypedTable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|testTable
operator|)
operator|.
name|getCache
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|,
literal|100
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
comment|//Check remaining values
for|for
control|(
name|int
name|x
init|=
literal|6
init|;
name|x
operator|<
name|iterCount
condition|;
name|x
operator|++
control|)
block|{
if|if
condition|(
name|x
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
argument_list|,
name|testTable
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertNull
argument_list|(
name|testTable
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

