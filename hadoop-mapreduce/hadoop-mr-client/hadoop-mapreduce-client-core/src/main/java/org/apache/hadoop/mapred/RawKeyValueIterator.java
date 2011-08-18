begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|util
operator|.
name|Progress
import|;
end_import

begin_comment
comment|/**  *<code>RawKeyValueIterator</code> is an iterator used to iterate over  * the raw keys and values during sort/merge of intermediate data.   */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|RawKeyValueIterator
specifier|public
interface|interface
name|RawKeyValueIterator
block|{
comment|/**     * Gets the current raw key.    *     * @return Gets the current raw key as a DataInputBuffer    * @throws IOException    */
DECL|method|getKey ()
name|DataInputBuffer
name|getKey
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Gets the current raw value.    *     * @return Gets the current raw value as a DataInputBuffer     * @throws IOException    */
DECL|method|getValue ()
name|DataInputBuffer
name|getValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Sets up the current key and value (for getKey and getValue).    *     * @return<code>true</code> if there exists a key/value,     *<code>false</code> otherwise.     * @throws IOException    */
DECL|method|next ()
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Closes the iterator so that the underlying streams can be closed.    *     * @throws IOException    */
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Gets the Progress object; this has a float (0.0 - 1.0)     * indicating the bytes processed by the iterator so far    */
DECL|method|getProgress ()
name|Progress
name|getProgress
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

