begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
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
name|HddsIdFactory
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Test the JMX interface for the rocksdb metastore implementation.  */
end_comment

begin_class
DECL|class|TestHddsIdFactory
specifier|public
class|class
name|TestHddsIdFactory
block|{
DECL|field|ID_SET
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Long
argument_list|>
name|ID_SET
init|=
name|ConcurrentHashMap
operator|.
name|newKeySet
argument_list|()
decl_stmt|;
DECL|field|IDS_PER_THREAD
specifier|private
specifier|static
specifier|final
name|int
name|IDS_PER_THREAD
init|=
literal|10000
decl_stmt|;
DECL|field|NUM_OF_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_OF_THREADS
init|=
literal|5
decl_stmt|;
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|ID_SET
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetLongId ()
specifier|public
name|void
name|testGetLongId
parameter_list|()
throws|throws
name|Exception
block|{
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|addTasks
argument_list|(
name|tasks
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|result
init|=
name|executor
operator|.
name|invokeAll
argument_list|(
name|tasks
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|IDS_PER_THREAD
operator|*
name|NUM_OF_THREADS
argument_list|,
name|ID_SET
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Future
argument_list|<
name|Integer
argument_list|>
name|r
range|:
name|result
control|)
block|{
name|assertEquals
argument_list|(
name|IDS_PER_THREAD
argument_list|,
name|r
operator|.
name|get
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addTasks (List<Callable<Integer>> tasks)
specifier|private
name|void
name|addTasks
parameter_list|(
name|List
argument_list|<
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|tasks
parameter_list|)
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
name|NUM_OF_THREADS
condition|;
name|i
operator|++
control|)
block|{
name|Callable
argument_list|<
name|Integer
argument_list|>
name|task
init|=
parameter_list|()
lambda|->
block|{
for|for
control|(
name|int
name|idNum
init|=
literal|0
init|;
name|idNum
operator|<
name|IDS_PER_THREAD
condition|;
name|idNum
operator|++
control|)
block|{
name|long
name|var
init|=
name|HddsIdFactory
operator|.
name|getLongId
argument_list|()
decl_stmt|;
if|if
condition|(
name|ID_SET
operator|.
name|contains
argument_list|(
name|var
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Duplicate id found"
argument_list|)
expr_stmt|;
block|}
name|ID_SET
operator|.
name|add
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
return|return
name|IDS_PER_THREAD
return|;
block|}
decl_stmt|;
name|tasks
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

