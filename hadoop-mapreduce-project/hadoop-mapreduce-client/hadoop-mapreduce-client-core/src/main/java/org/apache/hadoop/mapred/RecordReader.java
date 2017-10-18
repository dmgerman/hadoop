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
comment|/**  *<code>RecordReader</code> reads&lt;key, value&gt; pairs from an   * {@link InputSplit}.  *     *<p><code>RecordReader</code>, typically, converts the byte-oriented view of   * the input, provided by the<code>InputSplit</code>, and presents a   * record-oriented view for the {@link Mapper} and {@link Reducer} tasks for  * processing. It thus assumes the responsibility of processing record   * boundaries and presenting the tasks with keys and values.</p>  *   * @see InputSplit  * @see InputFormat  */
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
DECL|interface|RecordReader
specifier|public
interface|interface
name|RecordReader
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Closeable
block|{
comment|/**     * Reads the next key/value pair from the input for processing.    *    * @param key the key to read data into    * @param value the value to read data into    * @return true iff a key/value was read, false if at EOF    */
DECL|method|next (K key, V value)
name|boolean
name|next
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create an object of the appropriate type to be used as a key.    *     * @return a new key object.    */
DECL|method|createKey ()
name|K
name|createKey
parameter_list|()
function_decl|;
comment|/**    * Create an object of the appropriate type to be used as a value.    *     * @return a new value object.    */
DECL|method|createValue ()
name|V
name|createValue
parameter_list|()
function_decl|;
comment|/**     * Returns the current position in the input.    *     * @return the current position in the input.    * @throws IOException    */
DECL|method|getPos ()
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Close this {@link InputSplit} to future operations.    *     * @throws IOException    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * How much of the input has the {@link RecordReader} consumed i.e.    * has been processed by?    *     * @return progress from<code>0.0</code> to<code>1.0</code>.    * @throws IOException    */
DECL|method|getProgress ()
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

