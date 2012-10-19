begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertNotNull
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
name|assertNull
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
name|assertSame
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
name|assertTrue
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
name|Iterator
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
name|Random
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
name|util
operator|.
name|Time
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

begin_class
DECL|class|TestLightWeightHashSet
specifier|public
class|class
name|TestLightWeightHashSet
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.hdfs.TestLightWeightHashSet"
argument_list|)
decl_stmt|;
DECL|field|list
specifier|private
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|NUM
specifier|private
specifier|final
name|int
name|NUM
init|=
literal|100
decl_stmt|;
DECL|field|set
specifier|private
name|LightWeightHashSet
argument_list|<
name|Integer
argument_list|>
name|set
decl_stmt|;
DECL|field|rand
specifier|private
name|Random
name|rand
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|float
name|maxF
init|=
name|LightWeightHashSet
operator|.
name|DEFAULT_MAX_LOAD_FACTOR
decl_stmt|;
name|float
name|minF
init|=
name|LightWeightHashSet
operator|.
name|DEFAUT_MIN_LOAD_FACTOR
decl_stmt|;
name|int
name|initCapacity
init|=
name|LightWeightHashSet
operator|.
name|MINIMUM_CAPACITY
decl_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|set
operator|=
operator|new
name|LightWeightHashSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|initCapacity
argument_list|,
name|maxF
argument_list|,
name|minF
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyBasic ()
specifier|public
name|void
name|testEmptyBasic
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test empty basic"
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// iterator should not have next
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test empty - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneElementBasic ()
specifier|public
name|void
name|testOneElementBasic
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test one element basic"
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// set should be non-empty
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterator should have next
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterator should not have next
name|assertEquals
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test one element basic - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiBasic ()
specifier|public
name|void
name|testMultiBasic
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test multi element basic"
argument_list|)
expr_stmt|;
comment|// add once
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|add
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// check if the elements are in the set
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// add again - should return false each time
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
name|assertFalse
argument_list|(
name|set
operator|.
name|add
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check again if the elements are there
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|num
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Integer
name|next
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|next
argument_list|)
argument_list|)
expr_stmt|;
name|num
operator|++
expr_stmt|;
block|}
comment|// check the number of element from the iterator
name|assertEquals
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test multi element basic - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveOne ()
specifier|public
name|void
name|testRemoveOne
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test remove one"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|add
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove from the head/tail
name|assertTrue
argument_list|(
name|set
operator|.
name|remove
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the iterator
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// add the element back to the set
name|assertTrue
argument_list|(
name|set
operator|.
name|add
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|iter
operator|=
name|set
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test remove one - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveMulti ()
specifier|public
name|void
name|testRemoveMulti
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test remove multi"
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|add
argument_list|(
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
name|NUM
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|remove
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// the deleted elements should not be there
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// the rest should be there
for|for
control|(
name|int
name|i
init|=
name|NUM
operator|/
literal|2
init|;
name|i
operator|<
name|NUM
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Test remove multi - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveAll ()
specifier|public
name|void
name|testRemoveAll
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test remove all"
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|add
argument_list|(
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
name|NUM
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|remove
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// the deleted elements should not be there
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// iterator should not have next
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test remove all - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPollAll ()
specifier|public
name|void
name|testPollAll
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test poll all"
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|add
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// remove all elements by polling
name|List
argument_list|<
name|Integer
argument_list|>
name|poll
init|=
name|set
operator|.
name|pollAll
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// the deleted elements should not be there
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// we should get all original items
for|for
control|(
name|Integer
name|i
range|:
name|poll
control|)
block|{
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test poll all - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPollNMulti ()
specifier|public
name|void
name|testPollNMulti
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test pollN multi"
argument_list|)
expr_stmt|;
comment|// use addAll
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
comment|// poll zero
name|List
argument_list|<
name|Integer
argument_list|>
name|poll
init|=
name|set
operator|.
name|pollN
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|poll
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// poll existing elements (less than size)
name|poll
operator|=
name|set
operator|.
name|pollN
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|poll
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|poll
control|)
block|{
comment|// should be in original items
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// should not be in the set anymore
name|assertFalse
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// poll more elements than present
name|poll
operator|=
name|set
operator|.
name|pollN
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM
operator|-
literal|10
argument_list|,
name|poll
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|poll
control|)
block|{
comment|// should be in original items
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// set is empty
name|assertTrue
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test pollN multi - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPollNMultiArray ()
specifier|public
name|void
name|testPollNMultiArray
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test pollN multi array"
argument_list|)
expr_stmt|;
comment|// use addAll
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
comment|// poll existing elements (less than size)
name|Integer
index|[]
name|poll
init|=
operator|new
name|Integer
index|[
literal|10
index|]
decl_stmt|;
name|poll
operator|=
name|set
operator|.
name|pollToArray
argument_list|(
name|poll
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|poll
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|poll
control|)
block|{
comment|// should be in original items
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// should not be in the set anymore
name|assertFalse
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// poll other elements (more than size)
name|poll
operator|=
operator|new
name|Integer
index|[
name|NUM
index|]
expr_stmt|;
name|poll
operator|=
name|set
operator|.
name|pollToArray
argument_list|(
name|poll
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM
operator|-
literal|10
argument_list|,
name|poll
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM
operator|-
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|poll
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// set is empty
name|assertTrue
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// //////
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
comment|// poll existing elements (exactly the size)
name|poll
operator|=
operator|new
name|Integer
index|[
name|NUM
index|]
expr_stmt|;
name|poll
operator|=
name|set
operator|.
name|pollToArray
argument_list|(
name|poll
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM
argument_list|,
name|poll
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|poll
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// //////
comment|// //////
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
comment|// poll existing elements (exactly the size)
name|poll
operator|=
operator|new
name|Integer
index|[
literal|0
index|]
expr_stmt|;
name|poll
operator|=
name|set
operator|.
name|pollToArray
argument_list|(
name|poll
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|poll
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// //////
name|LOG
operator|.
name|info
argument_list|(
literal|"Test pollN multi array- DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClear ()
specifier|public
name|void
name|testClear
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test clear"
argument_list|)
expr_stmt|;
comment|// use addAll
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// clear the set
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterator should be empty
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test clear - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCapacity ()
specifier|public
name|void
name|testCapacity
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test capacity"
argument_list|)
expr_stmt|;
name|float
name|maxF
init|=
name|LightWeightHashSet
operator|.
name|DEFAULT_MAX_LOAD_FACTOR
decl_stmt|;
name|float
name|minF
init|=
name|LightWeightHashSet
operator|.
name|DEFAUT_MIN_LOAD_FACTOR
decl_stmt|;
comment|// capacity lower than min_capacity
name|set
operator|=
operator|new
name|LightWeightHashSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|1
argument_list|,
name|maxF
argument_list|,
name|minF
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LightWeightHashSet
operator|.
name|MINIMUM_CAPACITY
argument_list|,
name|set
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
comment|// capacity not a power of two
name|set
operator|=
operator|new
name|LightWeightHashSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|30
argument_list|,
name|maxF
argument_list|,
name|minF
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|LightWeightHashSet
operator|.
name|MINIMUM_CAPACITY
argument_list|,
literal|32
argument_list|)
argument_list|,
name|set
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
comment|// capacity valid
name|set
operator|=
operator|new
name|LightWeightHashSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|64
argument_list|,
name|maxF
argument_list|,
name|minF
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|LightWeightHashSet
operator|.
name|MINIMUM_CAPACITY
argument_list|,
literal|64
argument_list|)
argument_list|,
name|set
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
comment|// add NUM elements
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|int
name|expCap
init|=
name|LightWeightHashSet
operator|.
name|MINIMUM_CAPACITY
decl_stmt|;
while|while
condition|(
name|expCap
operator|<
name|NUM
operator|&&
name|maxF
operator|*
name|expCap
operator|<
name|NUM
condition|)
name|expCap
operator|<<=
literal|1
expr_stmt|;
name|assertEquals
argument_list|(
name|expCap
argument_list|,
name|set
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
comment|// see if the set shrinks if we remove elements by removing
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|int
name|toRemove
init|=
name|set
operator|.
name|size
argument_list|()
operator|-
call|(
name|int
call|)
argument_list|(
name|set
operator|.
name|getCapacity
argument_list|()
operator|*
name|minF
argument_list|)
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|toRemove
condition|;
name|i
operator|++
control|)
block|{
name|set
operator|.
name|remove
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|LightWeightHashSet
operator|.
name|MINIMUM_CAPACITY
argument_list|,
name|expCap
operator|/
literal|2
argument_list|)
argument_list|,
name|set
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test capacity - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOther ()
specifier|public
name|void
name|testOther
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test other"
argument_list|)
expr_stmt|;
comment|// remove all
name|assertTrue
argument_list|(
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|removeAll
argument_list|(
name|list
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove sublist
name|List
argument_list|<
name|Integer
argument_list|>
name|sub
init|=
operator|new
name|LinkedList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
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
name|sub
operator|.
name|add
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|removeAll
argument_list|(
name|sub
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM
operator|-
literal|10
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|sub
control|)
block|{
name|assertFalse
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|set
operator|.
name|containsAll
argument_list|(
name|sub
argument_list|)
argument_list|)
expr_stmt|;
comment|// the rest of the elements should be there
name|List
argument_list|<
name|Integer
argument_list|>
name|sub2
init|=
operator|new
name|LinkedList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
name|NUM
condition|;
name|i
operator|++
control|)
block|{
name|sub2
operator|.
name|add
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|set
operator|.
name|containsAll
argument_list|(
name|sub2
argument_list|)
argument_list|)
expr_stmt|;
comment|// to array
name|Integer
index|[]
name|array
init|=
name|set
operator|.
name|toArray
argument_list|(
operator|new
name|Integer
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM
operator|-
literal|10
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|sub2
operator|.
name|contains
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|NUM
operator|-
literal|10
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// to array
name|Object
index|[]
name|array2
init|=
name|set
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM
operator|-
literal|10
argument_list|,
name|array2
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|sub2
operator|.
name|contains
argument_list|(
operator|(
name|Integer
operator|)
name|array2
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Test other - DONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetElement ()
specifier|public
name|void
name|testGetElement
parameter_list|()
block|{
name|LightWeightHashSet
argument_list|<
name|TestObject
argument_list|>
name|objSet
init|=
operator|new
name|LightWeightHashSet
argument_list|<
name|TestObject
argument_list|>
argument_list|()
decl_stmt|;
name|TestObject
name|objA
init|=
operator|new
name|TestObject
argument_list|(
literal|"object A"
argument_list|)
decl_stmt|;
name|TestObject
name|equalToObjA
init|=
operator|new
name|TestObject
argument_list|(
literal|"object A"
argument_list|)
decl_stmt|;
name|TestObject
name|objB
init|=
operator|new
name|TestObject
argument_list|(
literal|"object B"
argument_list|)
decl_stmt|;
name|objSet
operator|.
name|add
argument_list|(
name|objA
argument_list|)
expr_stmt|;
name|objSet
operator|.
name|add
argument_list|(
name|objB
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|objA
argument_list|,
name|objSet
operator|.
name|getElement
argument_list|(
name|objA
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|objA
argument_list|,
name|objSet
operator|.
name|getElement
argument_list|(
name|equalToObjA
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|objB
argument_list|,
name|objSet
operator|.
name|getElement
argument_list|(
name|objB
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|objSet
operator|.
name|getElement
argument_list|(
operator|new
name|TestObject
argument_list|(
literal|"not in set"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wrapper class which is used in    * {@link TestLightWeightHashSet#testGetElement()}    */
DECL|class|TestObject
specifier|private
specifier|static
class|class
name|TestObject
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|TestObject (String value)
specifier|public
name|TestObject
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|TestObject
name|other
init|=
operator|(
name|TestObject
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|value
operator|.
name|equals
argument_list|(
name|other
operator|.
name|value
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

