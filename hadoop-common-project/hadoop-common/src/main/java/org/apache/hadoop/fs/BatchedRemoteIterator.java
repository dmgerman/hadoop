begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * A RemoteIterator that fetches elements in batches.  */
end_comment

begin_class
DECL|class|BatchedRemoteIterator
specifier|public
specifier|abstract
class|class
name|BatchedRemoteIterator
parameter_list|<
name|K
parameter_list|,
name|E
parameter_list|>
implements|implements
name|RemoteIterator
argument_list|<
name|E
argument_list|>
block|{
DECL|interface|BatchedEntries
specifier|public
interface|interface
name|BatchedEntries
parameter_list|<
name|E
parameter_list|>
block|{
DECL|method|get (int i)
specifier|public
name|E
name|get
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
DECL|method|hasMore ()
specifier|public
name|boolean
name|hasMore
parameter_list|()
function_decl|;
block|}
DECL|class|BatchedListEntries
specifier|public
specifier|static
class|class
name|BatchedListEntries
parameter_list|<
name|E
parameter_list|>
implements|implements
name|BatchedEntries
argument_list|<
name|E
argument_list|>
block|{
DECL|field|entries
specifier|private
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|entries
decl_stmt|;
DECL|field|hasMore
specifier|private
specifier|final
name|boolean
name|hasMore
decl_stmt|;
DECL|method|BatchedListEntries (List<E> entries, boolean hasMore)
specifier|public
name|BatchedListEntries
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|entries
parameter_list|,
name|boolean
name|hasMore
parameter_list|)
block|{
name|this
operator|.
name|entries
operator|=
name|entries
expr_stmt|;
name|this
operator|.
name|hasMore
operator|=
name|hasMore
expr_stmt|;
block|}
DECL|method|get (int i)
specifier|public
name|E
name|get
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|entries
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|entries
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|hasMore ()
specifier|public
name|boolean
name|hasMore
parameter_list|()
block|{
return|return
name|hasMore
return|;
block|}
block|}
DECL|field|prevKey
specifier|private
name|K
name|prevKey
decl_stmt|;
DECL|field|entries
specifier|private
name|BatchedEntries
argument_list|<
name|E
argument_list|>
name|entries
decl_stmt|;
DECL|field|idx
specifier|private
name|int
name|idx
decl_stmt|;
DECL|method|BatchedRemoteIterator (K prevKey)
specifier|public
name|BatchedRemoteIterator
parameter_list|(
name|K
name|prevKey
parameter_list|)
block|{
name|this
operator|.
name|prevKey
operator|=
name|prevKey
expr_stmt|;
name|this
operator|.
name|entries
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|idx
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**    * Perform the actual remote request.    *     * @param prevKey The key to send.    * @return A list of replies.    */
DECL|method|makeRequest (K prevKey)
specifier|public
specifier|abstract
name|BatchedEntries
argument_list|<
name|E
argument_list|>
name|makeRequest
parameter_list|(
name|K
name|prevKey
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|makeRequest ()
specifier|private
name|void
name|makeRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|idx
operator|=
literal|0
expr_stmt|;
name|entries
operator|=
literal|null
expr_stmt|;
name|entries
operator|=
name|makeRequest
argument_list|(
name|prevKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|entries
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|entries
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|makeRequestIfNeeded ()
specifier|private
name|void
name|makeRequestIfNeeded
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|idx
operator|==
operator|-
literal|1
condition|)
block|{
name|makeRequest
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|entries
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|idx
operator|>=
name|entries
operator|.
name|size
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|entries
operator|.
name|hasMore
argument_list|()
condition|)
block|{
comment|// Last time, we got fewer entries than requested.
comment|// So we should be at the end.
name|entries
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|makeRequest
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
throws|throws
name|IOException
block|{
name|makeRequestIfNeeded
argument_list|()
expr_stmt|;
return|return
operator|(
name|entries
operator|!=
literal|null
operator|)
return|;
block|}
comment|/**    * Return the next list key associated with an element.    */
DECL|method|elementToPrevKey (E element)
specifier|public
specifier|abstract
name|K
name|elementToPrevKey
parameter_list|(
name|E
name|element
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|E
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|makeRequestIfNeeded
argument_list|()
expr_stmt|;
if|if
condition|(
name|entries
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|E
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|idx
operator|++
argument_list|)
decl_stmt|;
name|prevKey
operator|=
name|elementToPrevKey
argument_list|(
name|entry
argument_list|)
expr_stmt|;
return|return
name|entry
return|;
block|}
block|}
end_class

end_unit

