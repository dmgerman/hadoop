begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.recovery
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|recovery
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * A wrapper for a Iterator to translate the raw RuntimeExceptions that  * can be thrown into IOException.  */
end_comment

begin_interface
DECL|interface|RecoveryIterator
specifier|public
interface|interface
name|RecoveryIterator
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Closeable
block|{
comment|/**    * Returns true if the iteration has more elements.    */
DECL|method|hasNext ()
name|boolean
name|hasNext
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the next element in the iteration.    */
DECL|method|next ()
name|T
name|next
parameter_list|()
throws|throws
name|IOException
throws|,
name|NoSuchElementException
function_decl|;
block|}
end_interface

end_unit

