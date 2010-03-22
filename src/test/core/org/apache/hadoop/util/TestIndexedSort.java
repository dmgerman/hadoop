begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|Random
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|io
operator|.
name|DataInputBuffer
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
name|io
operator|.
name|DataOutputBuffer
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|WritableComparator
import|;
end_import

begin_class
DECL|class|TestIndexedSort
specifier|public
class|class
name|TestIndexedSort
extends|extends
name|TestCase
block|{
DECL|method|sortAllEqual (IndexedSorter sorter)
specifier|public
name|void
name|sortAllEqual
parameter_list|(
name|IndexedSorter
name|sorter
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|SAMPLE
init|=
literal|500
decl_stmt|;
name|int
index|[]
name|values
init|=
operator|new
name|int
index|[
name|SAMPLE
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|SampleSortable
name|s
init|=
operator|new
name|SampleSortable
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|SAMPLE
argument_list|)
expr_stmt|;
name|int
index|[]
name|check
init|=
name|s
operator|.
name|getSorted
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|values
argument_list|)
operator|+
literal|"\ndoesn't match\n"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|check
argument_list|)
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
name|check
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set random min/max, re-sort.
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|min
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|SAMPLE
argument_list|)
decl_stmt|;
name|int
name|max
init|=
operator|(
name|min
operator|+
literal|1
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|SAMPLE
operator|-
literal|2
argument_list|)
operator|)
operator|%
name|SAMPLE
decl_stmt|;
name|values
index|[
name|min
index|]
operator|=
literal|9
expr_stmt|;
name|values
index|[
name|max
index|]
operator|=
literal|11
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testAllEqual setting min/max at "
operator|+
name|min
operator|+
literal|"/"
operator|+
name|max
operator|+
literal|"("
operator|+
name|sorter
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|SampleSortable
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|SAMPLE
argument_list|)
expr_stmt|;
name|check
operator|=
name|s
operator|.
name|getSorted
argument_list|()
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|check
index|[
literal|0
index|]
operator|==
literal|9
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|check
index|[
name|SAMPLE
operator|-
literal|1
index|]
operator|==
literal|11
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|values
argument_list|)
operator|+
literal|"\ndoesn't match\n"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|check
argument_list|)
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
name|check
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sortSorted (IndexedSorter sorter)
specifier|public
name|void
name|sortSorted
parameter_list|(
name|IndexedSorter
name|sorter
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|SAMPLE
init|=
literal|500
decl_stmt|;
name|int
index|[]
name|values
init|=
operator|new
name|int
index|[
name|SAMPLE
index|]
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testSorted seed: "
operator|+
name|seed
operator|+
literal|"("
operator|+
name|sorter
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
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
name|SAMPLE
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|SampleSortable
name|s
init|=
operator|new
name|SampleSortable
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|SAMPLE
argument_list|)
expr_stmt|;
name|int
index|[]
name|check
init|=
name|s
operator|.
name|getSorted
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|values
argument_list|)
operator|+
literal|"\ndoesn't match\n"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|check
argument_list|)
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
name|check
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sortSequential (IndexedSorter sorter)
specifier|public
name|void
name|sortSequential
parameter_list|(
name|IndexedSorter
name|sorter
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|SAMPLE
init|=
literal|500
decl_stmt|;
name|int
index|[]
name|values
init|=
operator|new
name|int
index|[
name|SAMPLE
index|]
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
name|SAMPLE
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
name|SampleSortable
name|s
init|=
operator|new
name|SampleSortable
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|SAMPLE
argument_list|)
expr_stmt|;
name|int
index|[]
name|check
init|=
name|s
operator|.
name|getSorted
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|values
argument_list|)
operator|+
literal|"\ndoesn't match\n"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|check
argument_list|)
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
name|check
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sortSingleRecord (IndexedSorter sorter)
specifier|public
name|void
name|sortSingleRecord
parameter_list|(
name|IndexedSorter
name|sorter
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|SAMPLE
init|=
literal|1
decl_stmt|;
name|SampleSortable
name|s
init|=
operator|new
name|SampleSortable
argument_list|(
name|SAMPLE
argument_list|)
decl_stmt|;
name|int
index|[]
name|values
init|=
name|s
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|SAMPLE
argument_list|)
expr_stmt|;
name|int
index|[]
name|check
init|=
name|s
operator|.
name|getSorted
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|values
argument_list|)
operator|+
literal|"\ndoesn't match\n"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|check
argument_list|)
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
name|check
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sortRandom (IndexedSorter sorter)
specifier|public
name|void
name|sortRandom
parameter_list|(
name|IndexedSorter
name|sorter
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|SAMPLE
init|=
literal|256
operator|*
literal|1024
decl_stmt|;
name|SampleSortable
name|s
init|=
operator|new
name|SampleSortable
argument_list|(
name|SAMPLE
argument_list|)
decl_stmt|;
name|long
name|seed
init|=
name|s
operator|.
name|getSeed
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sortRandom seed: "
operator|+
name|seed
operator|+
literal|"("
operator|+
name|sorter
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|int
index|[]
name|values
init|=
name|s
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|SAMPLE
argument_list|)
expr_stmt|;
name|int
index|[]
name|check
init|=
name|s
operator|.
name|getSorted
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"seed: "
operator|+
name|seed
operator|+
literal|"\ndoesn't match\n"
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
name|check
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sortWritable (IndexedSorter sorter)
specifier|public
name|void
name|sortWritable
parameter_list|(
name|IndexedSorter
name|sorter
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|SAMPLE
init|=
literal|1000
decl_stmt|;
name|WritableSortable
name|s
init|=
operator|new
name|WritableSortable
argument_list|(
name|SAMPLE
argument_list|)
decl_stmt|;
name|long
name|seed
init|=
name|s
operator|.
name|getSeed
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sortWritable seed: "
operator|+
name|seed
operator|+
literal|"("
operator|+
name|sorter
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|String
index|[]
name|values
init|=
name|s
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|SAMPLE
argument_list|)
expr_stmt|;
name|String
index|[]
name|check
init|=
name|s
operator|.
name|getSorted
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"seed: "
operator|+
name|seed
operator|+
literal|"\ndoesn't match"
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
name|check
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testQuickSort ()
specifier|public
name|void
name|testQuickSort
parameter_list|()
throws|throws
name|Exception
block|{
name|QuickSort
name|sorter
init|=
operator|new
name|QuickSort
argument_list|()
decl_stmt|;
name|sortRandom
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortSingleRecord
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortSequential
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortSorted
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortAllEqual
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortWritable
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
comment|// test degenerate case for median-of-three partitioning
comment|// a_n, a_1, a_2, ..., a_{n-1}
specifier|final
name|int
name|DSAMPLE
init|=
literal|500
decl_stmt|;
name|int
index|[]
name|values
init|=
operator|new
name|int
index|[
name|DSAMPLE
index|]
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
name|DSAMPLE
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
name|values
index|[
literal|0
index|]
operator|=
name|values
index|[
name|DSAMPLE
operator|-
literal|1
index|]
operator|+
literal|1
expr_stmt|;
name|SampleSortable
name|s
init|=
operator|new
name|SampleSortable
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|values
operator|=
name|s
operator|.
name|getValues
argument_list|()
expr_stmt|;
specifier|final
name|int
name|DSS
init|=
operator|(
name|DSAMPLE
operator|/
literal|2
operator|)
operator|*
operator|(
name|DSAMPLE
operator|/
literal|2
operator|)
decl_stmt|;
comment|// Worst case is (N/2)^2 comparisons, not including those effecting
comment|// the median-of-three partitioning; impl should handle this case
name|MeasuredSortable
name|m
init|=
operator|new
name|MeasuredSortable
argument_list|(
name|s
argument_list|,
name|DSS
argument_list|)
decl_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|m
argument_list|,
literal|0
argument_list|,
name|DSAMPLE
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"QuickSort degen cmp/swp: "
operator|+
name|m
operator|.
name|getCmp
argument_list|()
operator|+
literal|"/"
operator|+
name|m
operator|.
name|getSwp
argument_list|()
operator|+
literal|"("
operator|+
name|sorter
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|int
index|[]
name|check
init|=
name|s
operator|.
name|getSorted
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
name|check
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHeapSort ()
specifier|public
name|void
name|testHeapSort
parameter_list|()
throws|throws
name|Exception
block|{
name|HeapSort
name|sorter
init|=
operator|new
name|HeapSort
argument_list|()
decl_stmt|;
name|sortRandom
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortSingleRecord
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortSequential
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortSorted
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortAllEqual
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
name|sortWritable
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
block|}
comment|// Sortables //
DECL|class|SampleSortable
specifier|private
specifier|static
class|class
name|SampleSortable
implements|implements
name|IndexedSortable
block|{
DECL|field|valindex
specifier|private
name|int
index|[]
name|valindex
decl_stmt|;
DECL|field|valindirect
specifier|private
name|int
index|[]
name|valindirect
decl_stmt|;
DECL|field|values
specifier|private
name|int
index|[]
name|values
decl_stmt|;
DECL|field|seed
specifier|private
specifier|final
name|long
name|seed
decl_stmt|;
DECL|method|SampleSortable ()
specifier|public
name|SampleSortable
parameter_list|()
block|{
name|this
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
DECL|method|SampleSortable (int j)
specifier|public
name|SampleSortable
parameter_list|(
name|int
name|j
parameter_list|)
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|seed
operator|=
name|r
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|int
index|[
name|j
index|]
expr_stmt|;
name|valindex
operator|=
operator|new
name|int
index|[
name|j
index|]
expr_stmt|;
name|valindirect
operator|=
operator|new
name|int
index|[
name|j
index|]
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
name|j
condition|;
operator|++
name|i
control|)
block|{
name|valindex
index|[
name|i
index|]
operator|=
name|valindirect
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|r
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|SampleSortable (int[] values)
specifier|public
name|SampleSortable
parameter_list|(
name|int
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|valindex
operator|=
operator|new
name|int
index|[
name|values
operator|.
name|length
index|]
expr_stmt|;
name|valindirect
operator|=
operator|new
name|int
index|[
name|values
operator|.
name|length
index|]
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|valindex
index|[
name|i
index|]
operator|=
name|valindirect
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
name|seed
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|getSeed ()
specifier|public
name|long
name|getSeed
parameter_list|()
block|{
return|return
name|seed
return|;
block|}
DECL|method|compare (int i, int j)
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
comment|// assume positive
return|return
name|values
index|[
name|valindirect
index|[
name|valindex
index|[
name|i
index|]
index|]
index|]
operator|-
name|values
index|[
name|valindirect
index|[
name|valindex
index|[
name|j
index|]
index|]
index|]
return|;
block|}
DECL|method|swap (int i, int j)
specifier|public
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|tmp
init|=
name|valindex
index|[
name|i
index|]
decl_stmt|;
name|valindex
index|[
name|i
index|]
operator|=
name|valindex
index|[
name|j
index|]
expr_stmt|;
name|valindex
index|[
name|j
index|]
operator|=
name|tmp
expr_stmt|;
block|}
DECL|method|getSorted ()
specifier|public
name|int
index|[]
name|getSorted
parameter_list|()
block|{
name|int
index|[]
name|ret
init|=
operator|new
name|int
index|[
name|values
operator|.
name|length
index|]
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
name|ret
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|ret
index|[
name|i
index|]
operator|=
name|values
index|[
name|valindirect
index|[
name|valindex
index|[
name|i
index|]
index|]
index|]
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|getValues ()
specifier|public
name|int
index|[]
name|getValues
parameter_list|()
block|{
name|int
index|[]
name|ret
init|=
operator|new
name|int
index|[
name|values
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|ret
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
DECL|class|MeasuredSortable
specifier|public
specifier|static
class|class
name|MeasuredSortable
implements|implements
name|IndexedSortable
block|{
DECL|field|comparisions
specifier|private
name|int
name|comparisions
decl_stmt|;
DECL|field|swaps
specifier|private
name|int
name|swaps
decl_stmt|;
DECL|field|maxcmp
specifier|private
specifier|final
name|int
name|maxcmp
decl_stmt|;
DECL|field|maxswp
specifier|private
specifier|final
name|int
name|maxswp
decl_stmt|;
DECL|field|s
specifier|private
name|IndexedSortable
name|s
decl_stmt|;
DECL|method|MeasuredSortable (IndexedSortable s)
specifier|public
name|MeasuredSortable
parameter_list|(
name|IndexedSortable
name|s
parameter_list|)
block|{
name|this
argument_list|(
name|s
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
DECL|method|MeasuredSortable (IndexedSortable s, int maxcmp)
specifier|public
name|MeasuredSortable
parameter_list|(
name|IndexedSortable
name|s
parameter_list|,
name|int
name|maxcmp
parameter_list|)
block|{
name|this
argument_list|(
name|s
argument_list|,
name|maxcmp
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
DECL|method|MeasuredSortable (IndexedSortable s, int maxcmp, int maxswp)
specifier|public
name|MeasuredSortable
parameter_list|(
name|IndexedSortable
name|s
parameter_list|,
name|int
name|maxcmp
parameter_list|,
name|int
name|maxswp
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|maxcmp
operator|=
name|maxcmp
expr_stmt|;
name|this
operator|.
name|maxswp
operator|=
name|maxswp
expr_stmt|;
block|}
DECL|method|getCmp ()
specifier|public
name|int
name|getCmp
parameter_list|()
block|{
return|return
name|comparisions
return|;
block|}
DECL|method|getSwp ()
specifier|public
name|int
name|getSwp
parameter_list|()
block|{
return|return
name|swaps
return|;
block|}
DECL|method|compare (int i, int j)
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Expected fewer than "
operator|+
name|maxcmp
operator|+
literal|" comparisons"
argument_list|,
operator|++
name|comparisions
operator|<
name|maxcmp
argument_list|)
expr_stmt|;
return|return
name|s
operator|.
name|compare
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
return|;
block|}
DECL|method|swap (int i, int j)
specifier|public
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Expected fewer than "
operator|+
name|maxswp
operator|+
literal|" swaps"
argument_list|,
operator|++
name|swaps
operator|<
name|maxswp
argument_list|)
expr_stmt|;
name|s
operator|.
name|swap
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|WritableSortable
specifier|private
specifier|static
class|class
name|WritableSortable
implements|implements
name|IndexedSortable
block|{
DECL|field|r
specifier|private
specifier|static
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|eob
specifier|private
specifier|final
name|int
name|eob
decl_stmt|;
DECL|field|indices
specifier|private
specifier|final
name|int
index|[]
name|indices
decl_stmt|;
DECL|field|offsets
specifier|private
specifier|final
name|int
index|[]
name|offsets
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|WritableComparator
name|comparator
decl_stmt|;
DECL|field|check
specifier|private
specifier|final
name|String
index|[]
name|check
decl_stmt|;
DECL|field|seed
specifier|private
specifier|final
name|long
name|seed
decl_stmt|;
DECL|method|WritableSortable ()
specifier|public
name|WritableSortable
parameter_list|()
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|WritableSortable (int j)
specifier|public
name|WritableSortable
parameter_list|(
name|int
name|j
parameter_list|)
throws|throws
name|IOException
block|{
name|seed
operator|=
name|r
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|Text
name|t
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|indices
operator|=
operator|new
name|int
index|[
name|j
index|]
expr_stmt|;
name|offsets
operator|=
operator|new
name|int
index|[
name|j
index|]
expr_stmt|;
name|check
operator|=
operator|new
name|String
index|[
name|j
index|]
expr_stmt|;
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
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
name|j
condition|;
operator|++
name|i
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
name|offsets
index|[
name|i
index|]
operator|=
name|dob
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|genRandom
argument_list|(
name|t
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|15
argument_list|)
operator|+
literal|1
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|t
operator|.
name|write
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|check
index|[
name|i
index|]
operator|=
name|t
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|eob
operator|=
name|dob
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|bytes
operator|=
name|dob
operator|.
name|getData
argument_list|()
expr_stmt|;
name|comparator
operator|=
name|WritableComparator
operator|.
name|get
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|getSeed ()
specifier|public
name|long
name|getSeed
parameter_list|()
block|{
return|return
name|seed
return|;
block|}
DECL|method|genRandom (Text t, int len, StringBuilder sb)
specifier|private
specifier|static
name|void
name|genRandom
parameter_list|(
name|Text
name|t
parameter_list|,
name|int
name|len
parameter_list|,
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|setLength
argument_list|(
literal|0
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
name|len
condition|;
operator|++
name|i
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
literal|26
argument_list|)
operator|+
literal|10
argument_list|,
literal|36
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|t
operator|.
name|set
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|compare (int i, int j)
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|ii
init|=
name|indices
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|int
name|ij
init|=
name|indices
index|[
name|j
index|]
decl_stmt|;
return|return
name|comparator
operator|.
name|compare
argument_list|(
name|bytes
argument_list|,
name|offsets
index|[
name|ii
index|]
argument_list|,
operator|(
operator|(
name|ii
operator|+
literal|1
operator|==
name|indices
operator|.
name|length
operator|)
condition|?
name|eob
else|:
name|offsets
index|[
name|ii
operator|+
literal|1
index|]
operator|)
operator|-
name|offsets
index|[
name|ii
index|]
argument_list|,
name|bytes
argument_list|,
name|offsets
index|[
name|ij
index|]
argument_list|,
operator|(
operator|(
name|ij
operator|+
literal|1
operator|==
name|indices
operator|.
name|length
operator|)
condition|?
name|eob
else|:
name|offsets
index|[
name|ij
operator|+
literal|1
index|]
operator|)
operator|-
name|offsets
index|[
name|ij
index|]
argument_list|)
return|;
block|}
DECL|method|swap (int i, int j)
specifier|public
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|tmp
init|=
name|indices
index|[
name|i
index|]
decl_stmt|;
name|indices
index|[
name|i
index|]
operator|=
name|indices
index|[
name|j
index|]
expr_stmt|;
name|indices
index|[
name|j
index|]
operator|=
name|tmp
expr_stmt|;
block|}
DECL|method|getValues ()
specifier|public
name|String
index|[]
name|getValues
parameter_list|()
block|{
return|return
name|check
return|;
block|}
DECL|method|getSorted ()
specifier|public
name|String
index|[]
name|getSorted
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|ret
init|=
operator|new
name|String
index|[
name|indices
operator|.
name|length
index|]
decl_stmt|;
name|Text
name|t
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
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
name|ret
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|int
name|ii
init|=
name|indices
index|[
name|i
index|]
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|bytes
argument_list|,
name|offsets
index|[
name|ii
index|]
argument_list|,
operator|(
operator|(
name|ii
operator|+
literal|1
operator|==
name|indices
operator|.
name|length
operator|)
condition|?
name|eob
else|:
name|offsets
index|[
name|ii
operator|+
literal|1
index|]
operator|)
operator|-
name|offsets
index|[
name|ii
index|]
argument_list|)
expr_stmt|;
name|t
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
name|ret
index|[
name|i
index|]
operator|=
name|t
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
block|}
end_class

end_unit

