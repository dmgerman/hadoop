begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils.db.cache
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
operator|.
name|cache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CompletableFuture
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
name|test
operator|.
name|GenericTestUtils
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
name|Test
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

begin_comment
comment|/**  * Class tests partial table cache.  */
end_comment

begin_class
DECL|class|TestPartialTableCache
specifier|public
class|class
name|TestPartialTableCache
block|{
DECL|field|tableCache
specifier|private
name|TableCache
argument_list|<
name|CacheKey
argument_list|<
name|String
argument_list|>
argument_list|,
name|CacheValue
argument_list|<
name|String
argument_list|>
argument_list|>
name|tableCache
decl_stmt|;
annotation|@
name|Before
DECL|method|create ()
specifier|public
name|void
name|create
parameter_list|()
block|{
name|tableCache
operator|=
operator|new
name|PartialTableCache
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPartialTableCache ()
specifier|public
name|void
name|testPartialTableCache
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|tableCache
operator|.
name|put
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
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
name|i
argument_list|)
argument_list|,
name|tableCache
operator|.
name|get
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// On a full table cache if some one calls cleanup it is a no-op.
name|tableCache
operator|.
name|cleanup
argument_list|(
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|<
literal|10
condition|;
name|i
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
name|i
argument_list|)
argument_list|,
name|tableCache
operator|.
name|get
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPartialTableCacheParallel ()
specifier|public
name|void
name|testPartialTableCacheParallel
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|totalCount
init|=
literal|0
decl_stmt|;
name|CompletableFuture
argument_list|<
name|Integer
argument_list|>
name|future
init|=
name|CompletableFuture
operator|.
name|supplyAsync
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
return|return
name|writeToCache
argument_list|(
literal|10
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"writeToCache got interrupt exception"
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
argument_list|)
decl_stmt|;
name|int
name|value
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|totalCount
operator|+=
name|value
expr_stmt|;
name|future
operator|=
name|CompletableFuture
operator|.
name|supplyAsync
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
return|return
name|writeToCache
argument_list|(
literal|10
argument_list|,
literal|11
argument_list|,
literal|100
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"writeToCache got interrupt exception"
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
argument_list|)
expr_stmt|;
comment|// Check we have first 10 entries in cache.
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10
condition|;
name|i
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
name|i
argument_list|)
argument_list|,
name|tableCache
operator|.
name|get
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|deleted
init|=
literal|5
decl_stmt|;
comment|// cleanup first 5 entires
name|tableCache
operator|.
name|cleanup
argument_list|(
name|deleted
argument_list|)
expr_stmt|;
name|value
operator|=
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|totalCount
operator|+=
name|value
expr_stmt|;
comment|// We should totalCount - deleted entries in cache.
specifier|final
name|int
name|tc
init|=
name|totalCount
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
operator|(
name|tc
operator|-
name|deleted
operator|==
name|tableCache
operator|.
name|size
argument_list|()
operator|)
argument_list|,
literal|100
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
comment|// Check if we have remaining entries.
for|for
control|(
name|int
name|i
init|=
literal|6
init|;
name|i
operator|<=
name|totalCount
condition|;
name|i
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
name|i
argument_list|)
argument_list|,
name|tableCache
operator|.
name|get
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tableCache
operator|.
name|cleanup
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|tableCache
operator|.
name|cleanup
argument_list|(
name|totalCount
argument_list|)
expr_stmt|;
comment|// Cleaned up all entries, so cache size should be zero.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
operator|(
literal|0
operator|==
name|tableCache
operator|.
name|size
argument_list|()
operator|)
argument_list|,
literal|100
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
block|}
DECL|method|writeToCache (int count, int startVal, long sleep)
specifier|private
name|int
name|writeToCache
parameter_list|(
name|int
name|count
parameter_list|,
name|int
name|startVal
parameter_list|,
name|long
name|sleep
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|counter
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|counter
operator|<=
name|count
condition|)
block|{
name|tableCache
operator|.
name|put
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|startVal
argument_list|)
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
name|Integer
operator|.
name|toString
argument_list|(
name|startVal
argument_list|)
argument_list|)
argument_list|,
name|startVal
argument_list|)
argument_list|)
expr_stmt|;
name|startVal
operator|++
expr_stmt|;
name|counter
operator|++
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

