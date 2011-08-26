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
name|WritableComparable
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
name|mapreduce
operator|.
name|RecordReader
import|;
end_import

begin_comment
comment|/**  * Additional operations required of a RecordReader to participate in a join.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ComposableRecordReader
specifier|public
specifier|abstract
class|class
name|ComposableRecordReader
parameter_list|<
name|K
extends|extends
name|WritableComparable
parameter_list|<
name|?
parameter_list|>
parameter_list|,
name|V
extends|extends
name|Writable
parameter_list|>
extends|extends
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|implements
name|Comparable
argument_list|<
name|ComposableRecordReader
argument_list|<
name|K
argument_list|,
name|?
argument_list|>
argument_list|>
block|{
comment|/**    * Return the position in the collector this class occupies.    */
DECL|method|id ()
specifier|abstract
name|int
name|id
parameter_list|()
function_decl|;
comment|/**    * Return the key this RecordReader would supply on a call to next(K,V)    */
DECL|method|key ()
specifier|abstract
name|K
name|key
parameter_list|()
function_decl|;
comment|/**    * Clone the key at the head of this RecordReader into the object provided.    */
DECL|method|key (K key)
specifier|abstract
name|void
name|key
parameter_list|(
name|K
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create instance of key.    */
DECL|method|createKey ()
specifier|abstract
name|K
name|createKey
parameter_list|()
function_decl|;
comment|/**    * Create instance of value.    */
DECL|method|createValue ()
specifier|abstract
name|V
name|createValue
parameter_list|()
function_decl|;
comment|/**    * Returns true if the stream is not empty, but provides no guarantee that    * a call to next(K,V) will succeed.    */
DECL|method|hasNext ()
specifier|abstract
name|boolean
name|hasNext
parameter_list|()
function_decl|;
comment|/**    * Skip key-value pairs with keys less than or equal to the key provided.    */
DECL|method|skip (K key)
specifier|abstract
name|void
name|skip
parameter_list|(
name|K
name|key
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * While key-value pairs from this RecordReader match the given key, register    * them with the JoinCollector provided.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|accept (CompositeRecordReader.JoinCollector jc, K key)
specifier|abstract
name|void
name|accept
parameter_list|(
name|CompositeRecordReader
operator|.
name|JoinCollector
name|jc
parameter_list|,
name|K
name|key
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
block|}
end_class

end_unit

