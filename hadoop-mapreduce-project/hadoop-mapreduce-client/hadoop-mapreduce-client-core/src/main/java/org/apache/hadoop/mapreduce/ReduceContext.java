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
comment|/**  * The context passed to the {@link Reducer}.  * @param<KEYIN> the class of the input keys  * @param<VALUEIN> the class of the input values  * @param<KEYOUT> the class of the output keys  * @param<VALUEOUT> the class of the output values  */
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
DECL|interface|ReduceContext
specifier|public
interface|interface
name|ReduceContext
parameter_list|<
name|KEYIN
parameter_list|,
name|VALUEIN
parameter_list|,
name|KEYOUT
parameter_list|,
name|VALUEOUT
parameter_list|>
extends|extends
name|TaskInputOutputContext
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
block|{
comment|/** Start processing next unique key. */
DECL|method|nextKey ()
specifier|public
name|boolean
name|nextKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Iterate through the values for the current key, reusing the same value     * object, which is stored in the context.    * @return the series of values associated with the current key. All of the     * objects returned directly and indirectly from this method are reused.    */
DECL|method|getValues ()
specifier|public
name|Iterable
argument_list|<
name|VALUEIN
argument_list|>
name|getValues
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * {@link Iterator} to iterate over values for a given group of records.    */
DECL|interface|ValueIterator
interface|interface
name|ValueIterator
parameter_list|<
name|VALUEIN
parameter_list|>
extends|extends
name|MarkableIteratorInterface
argument_list|<
name|VALUEIN
argument_list|>
block|{
comment|/**      * This method is called when the reducer moves from one key to       * another.      * @throws IOException      */
DECL|method|resetBackupStore ()
name|void
name|resetBackupStore
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_interface

end_unit

