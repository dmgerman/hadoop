begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
package|;
end_package

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
name|Map
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

begin_comment
comment|/**  * Random algorithms.  */
end_comment

begin_class
DECL|class|RandomAlgorithms
specifier|public
class|class
name|RandomAlgorithms
block|{
DECL|interface|IndexMapper
specifier|private
interface|interface
name|IndexMapper
block|{
DECL|method|get (int pos)
name|int
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
DECL|method|swap (int a, int b)
name|void
name|swap
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
function_decl|;
DECL|method|getSize ()
name|int
name|getSize
parameter_list|()
function_decl|;
DECL|method|reset ()
name|void
name|reset
parameter_list|()
function_decl|;
block|}
comment|/**    * A sparse index mapping table - useful when we want to    * non-destructively permute a small fraction of a large array.    */
DECL|class|SparseIndexMapper
specifier|private
specifier|static
class|class
name|SparseIndexMapper
implements|implements
name|IndexMapper
block|{
DECL|field|mapping
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|mapping
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|size
name|int
name|size
decl_stmt|;
DECL|method|SparseIndexMapper (int size)
name|SparseIndexMapper
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
DECL|method|get (int pos)
specifier|public
name|int
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|Integer
name|mapped
init|=
name|mapping
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapped
operator|==
literal|null
condition|)
return|return
name|pos
return|;
return|return
name|mapped
return|;
block|}
DECL|method|swap (int a, int b)
specifier|public
name|void
name|swap
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
name|b
condition|)
return|return;
name|int
name|valA
init|=
name|get
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|int
name|valB
init|=
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|==
name|valA
condition|)
block|{
name|mapping
operator|.
name|remove
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapping
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|valA
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|==
name|valB
condition|)
block|{
name|mapping
operator|.
name|remove
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapping
operator|.
name|put
argument_list|(
name|a
argument_list|,
name|valB
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getSize ()
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|mapping
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * A dense index mapping table - useful when we want to    * non-destructively permute a large fraction of an array.    */
DECL|class|DenseIndexMapper
specifier|private
specifier|static
class|class
name|DenseIndexMapper
implements|implements
name|IndexMapper
block|{
DECL|field|mapping
name|int
index|[]
name|mapping
decl_stmt|;
DECL|method|DenseIndexMapper (int size)
name|DenseIndexMapper
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|mapping
operator|=
operator|new
name|int
index|[
name|size
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
name|size
condition|;
operator|++
name|i
control|)
block|{
name|mapping
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
DECL|method|get (int pos)
specifier|public
name|int
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
operator|(
name|pos
operator|<
literal|0
operator|)
operator|||
operator|(
name|pos
operator|>=
name|mapping
operator|.
name|length
operator|)
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
return|return
name|mapping
index|[
name|pos
index|]
return|;
block|}
DECL|method|swap (int a, int b)
specifier|public
name|void
name|swap
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
name|b
condition|)
return|return;
name|int
name|valA
init|=
name|get
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|int
name|valB
init|=
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|mapping
index|[
name|a
index|]
operator|=
name|valB
expr_stmt|;
name|mapping
index|[
name|b
index|]
operator|=
name|valA
expr_stmt|;
block|}
DECL|method|getSize ()
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|mapping
operator|.
name|length
return|;
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
return|return;
block|}
block|}
comment|/**    * Iteratively pick random numbers from pool 0..n-1. Each number can only be    * picked once.    */
DECL|class|Selector
specifier|public
specifier|static
class|class
name|Selector
block|{
DECL|field|mapping
specifier|private
name|IndexMapper
name|mapping
decl_stmt|;
DECL|field|n
specifier|private
name|int
name|n
decl_stmt|;
DECL|field|rand
specifier|private
name|Random
name|rand
decl_stmt|;
comment|/**      * Constructor.      *       * @param n      *          The pool of integers: 0..n-1.      * @param selPcnt      *          Percentage of selected numbers. This is just a hint for internal      *          memory optimization.      * @param rand      *          Random number generator.      */
DECL|method|Selector (int n, double selPcnt, Random rand)
specifier|public
name|Selector
parameter_list|(
name|int
name|n
parameter_list|,
name|double
name|selPcnt
parameter_list|,
name|Random
name|rand
parameter_list|)
block|{
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"n should be positive"
argument_list|)
throw|;
block|}
name|boolean
name|sparse
init|=
operator|(
name|n
operator|>
literal|200
operator|)
operator|&&
operator|(
name|selPcnt
operator|<
literal|0.1
operator|)
decl_stmt|;
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
name|mapping
operator|=
operator|(
name|sparse
operator|)
condition|?
operator|new
name|SparseIndexMapper
argument_list|(
name|n
argument_list|)
else|:
operator|new
name|DenseIndexMapper
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|this
operator|.
name|rand
operator|=
name|rand
expr_stmt|;
block|}
comment|/**      * Select the next random number.      * @return Random number selected. Or -1 if the remaining pool is empty.      */
DECL|method|next ()
specifier|public
name|int
name|next
parameter_list|()
block|{
switch|switch
condition|(
name|n
condition|)
block|{
case|case
literal|0
case|:
return|return
operator|-
literal|1
return|;
case|case
literal|1
case|:
block|{
name|int
name|index
init|=
name|mapping
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
operator|--
name|n
expr_stmt|;
return|return
name|index
return|;
block|}
default|default:
block|{
name|int
name|pos
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|mapping
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
name|mapping
operator|.
name|swap
argument_list|(
name|pos
argument_list|,
operator|--
name|n
argument_list|)
expr_stmt|;
return|return
name|index
return|;
block|}
block|}
block|}
comment|/**      * Get the remaining random number pool size.      */
DECL|method|getPoolSize ()
specifier|public
name|int
name|getPoolSize
parameter_list|()
block|{
return|return
name|n
return|;
block|}
comment|/**      * Reset the selector for reuse usage.      */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|mapping
operator|.
name|reset
argument_list|()
expr_stmt|;
name|n
operator|=
name|mapping
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Selecting m random integers from 0..n-1.    * @return An array of selected integers.    */
DECL|method|select (int m, int n, Random rand)
specifier|public
specifier|static
name|int
index|[]
name|select
parameter_list|(
name|int
name|m
parameter_list|,
name|int
name|n
parameter_list|,
name|Random
name|rand
parameter_list|)
block|{
if|if
condition|(
name|m
operator|>=
name|n
condition|)
block|{
name|int
index|[]
name|ret
init|=
operator|new
name|int
index|[
name|n
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
name|n
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
name|i
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
name|Selector
name|selector
init|=
operator|new
name|Selector
argument_list|(
name|n
argument_list|,
operator|(
name|float
operator|)
name|m
operator|/
name|n
argument_list|,
name|rand
argument_list|)
decl_stmt|;
name|int
index|[]
name|selected
init|=
operator|new
name|int
index|[
name|m
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
name|m
condition|;
operator|++
name|i
control|)
block|{
name|selected
index|[
name|i
index|]
operator|=
name|selector
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|selected
return|;
block|}
block|}
end_class

end_unit

