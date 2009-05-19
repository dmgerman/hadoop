begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * An implementation of the core algorithm of HeapSort.  */
end_comment

begin_class
DECL|class|HeapSort
specifier|public
specifier|final
class|class
name|HeapSort
implements|implements
name|IndexedSorter
block|{
DECL|method|HeapSort ()
specifier|public
name|HeapSort
parameter_list|()
block|{ }
DECL|method|downHeap (final IndexedSortable s, final int b, int i, final int N)
specifier|private
specifier|static
name|void
name|downHeap
parameter_list|(
specifier|final
name|IndexedSortable
name|s
parameter_list|,
specifier|final
name|int
name|b
parameter_list|,
name|int
name|i
parameter_list|,
specifier|final
name|int
name|N
parameter_list|)
block|{
for|for
control|(
name|int
name|idx
init|=
name|i
operator|<<
literal|1
init|;
name|idx
operator|<
name|N
condition|;
name|idx
operator|=
name|i
operator|<<
literal|1
control|)
block|{
if|if
condition|(
name|idx
operator|+
literal|1
operator|<
name|N
operator|&&
name|s
operator|.
name|compare
argument_list|(
name|b
operator|+
name|idx
argument_list|,
name|b
operator|+
name|idx
operator|+
literal|1
argument_list|)
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|compare
argument_list|(
name|b
operator|+
name|i
argument_list|,
name|b
operator|+
name|idx
operator|+
literal|1
argument_list|)
operator|<
literal|0
condition|)
block|{
name|s
operator|.
name|swap
argument_list|(
name|b
operator|+
name|i
argument_list|,
name|b
operator|+
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
return|return;
name|i
operator|=
name|idx
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|compare
argument_list|(
name|b
operator|+
name|i
argument_list|,
name|b
operator|+
name|idx
argument_list|)
operator|<
literal|0
condition|)
block|{
name|s
operator|.
name|swap
argument_list|(
name|b
operator|+
name|i
argument_list|,
name|b
operator|+
name|idx
argument_list|)
expr_stmt|;
name|i
operator|=
name|idx
expr_stmt|;
block|}
else|else
return|return;
block|}
block|}
comment|/**    * Sort the given range of items using heap sort.    * {@inheritDoc}    */
DECL|method|sort (IndexedSortable s, int p, int r)
specifier|public
name|void
name|sort
parameter_list|(
name|IndexedSortable
name|s
parameter_list|,
name|int
name|p
parameter_list|,
name|int
name|r
parameter_list|)
block|{
name|sort
argument_list|(
name|s
argument_list|,
name|p
argument_list|,
name|r
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|sort (final IndexedSortable s, final int p, final int r, final Progressable rep)
specifier|public
name|void
name|sort
parameter_list|(
specifier|final
name|IndexedSortable
name|s
parameter_list|,
specifier|final
name|int
name|p
parameter_list|,
specifier|final
name|int
name|r
parameter_list|,
specifier|final
name|Progressable
name|rep
parameter_list|)
block|{
specifier|final
name|int
name|N
init|=
name|r
operator|-
name|p
decl_stmt|;
comment|// build heap w/ reverse comparator, then write in-place from end
specifier|final
name|int
name|t
init|=
name|Integer
operator|.
name|highestOneBit
argument_list|(
name|N
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|t
init|;
name|i
operator|>
literal|1
condition|;
name|i
operator|>>>=
literal|1
control|)
block|{
for|for
control|(
name|int
name|j
init|=
name|i
operator|>>>
literal|1
init|;
name|j
operator|<
name|i
condition|;
operator|++
name|j
control|)
block|{
name|downHeap
argument_list|(
name|s
argument_list|,
name|p
operator|-
literal|1
argument_list|,
name|j
argument_list|,
name|N
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|rep
condition|)
block|{
name|rep
operator|.
name|progress
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|r
operator|-
literal|1
init|;
name|i
operator|>
name|p
condition|;
operator|--
name|i
control|)
block|{
name|s
operator|.
name|swap
argument_list|(
name|p
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|downHeap
argument_list|(
name|s
argument_list|,
name|p
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
name|i
operator|-
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

