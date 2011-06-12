begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.join
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|join
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
name|classification
operator|.
name|InterfaceStability
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
name|Writable
import|;
end_import

begin_comment
comment|/**  * This defines an interface to a stateful Iterator that can replay elements  * added to it directly.  * Note that this does not extend {@link java.util.Iterator}.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|ResetableIterator
specifier|public
interface|interface
name|ResetableIterator
parameter_list|<
name|T
extends|extends
name|Writable
parameter_list|>
block|{
DECL|class|EMPTY
specifier|public
specifier|static
class|class
name|EMPTY
parameter_list|<
name|U
extends|extends
name|Writable
parameter_list|>
implements|implements
name|ResetableIterator
argument_list|<
name|U
argument_list|>
block|{
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{ }
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{ }
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{ }
DECL|method|next (U val)
specifier|public
name|boolean
name|next
parameter_list|(
name|U
name|val
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
DECL|method|replay (U val)
specifier|public
name|boolean
name|replay
parameter_list|(
name|U
name|val
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
DECL|method|add (U item)
specifier|public
name|void
name|add
parameter_list|(
name|U
name|item
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|/**    * True if a call to next may return a value. This is permitted false    * positives, but not false negatives.    */
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
function_decl|;
comment|/**    * Assign next value to actual.    * It is required that elements added to a ResetableIterator be returned in    * the same order after a call to {@link #reset} (FIFO).    *    * Note that a call to this may fail for nested joins (i.e. more elements    * available, but none satisfying the constraints of the join)    */
DECL|method|next (T val)
specifier|public
name|boolean
name|next
parameter_list|(
name|T
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Assign last value returned to actual.    */
DECL|method|replay (T val)
specifier|public
name|boolean
name|replay
parameter_list|(
name|T
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set iterator to return to the start of its range. Must be called after    * calling {@link #add} to avoid a ConcurrentModificationException.    */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**    * Add an element to the collection of elements to iterate over.    */
DECL|method|add (T item)
specifier|public
name|void
name|add
parameter_list|(
name|T
name|item
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close datasources and release resources. Calling methods on the iterator    * after calling close has undefined behavior.    */
comment|// XXX is this necessary?
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Close datasources, but do not release internal resources. Calling this    * method should permit the object to be reused with a different datasource.    */
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

