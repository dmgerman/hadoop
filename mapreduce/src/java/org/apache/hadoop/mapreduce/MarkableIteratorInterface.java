begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|Iterator
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

begin_comment
comment|/**  *<code>MarkableIteratorInterface</code> is an interface for a iterator that   * supports mark-reset functionality.   *  *<p>Mark can be called at any point during the iteration process and a reset  * will go back to the last record before the call to the previous mark.  *   */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|MarkableIteratorInterface
interface|interface
name|MarkableIteratorInterface
parameter_list|<
name|VALUE
parameter_list|>
extends|extends
name|Iterator
argument_list|<
name|VALUE
argument_list|>
block|{
comment|/**    * Mark the current record. A subsequent call to reset will rewind    * the iterator to this record.    * @throws IOException    */
DECL|method|mark ()
name|void
name|mark
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Reset the iterator to the last record before a call to the previous mark    * @throws IOException    */
DECL|method|reset ()
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Clear any previously set mark    * @throws IOException    */
DECL|method|clearMark ()
name|void
name|clearMark
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

