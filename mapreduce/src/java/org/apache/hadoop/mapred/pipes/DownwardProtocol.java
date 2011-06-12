begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.pipes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|pipes
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
name|mapred
operator|.
name|InputSplit
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
name|mapred
operator|.
name|JobConf
import|;
end_import

begin_comment
comment|/**  * The abstract description of the downward (from Java to C++) Pipes protocol.  * All of these calls are asynchronous and return before the message has been   * processed.  */
end_comment

begin_interface
DECL|interface|DownwardProtocol
interface|interface
name|DownwardProtocol
parameter_list|<
name|K
extends|extends
name|WritableComparable
parameter_list|,
name|V
extends|extends
name|Writable
parameter_list|>
block|{
comment|/**    * request authentication    * @throws IOException    */
DECL|method|authenticate (String digest, String challenge)
name|void
name|authenticate
parameter_list|(
name|String
name|digest
parameter_list|,
name|String
name|challenge
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Start communication    * @throws IOException    */
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the JobConf for the task.    * @param conf    * @throws IOException    */
DECL|method|setJobConf (JobConf conf)
name|void
name|setJobConf
parameter_list|(
name|JobConf
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the input types for Maps.    * @param keyType the name of the key's type    * @param valueType the name of the value's type    * @throws IOException    */
DECL|method|setInputTypes (String keyType, String valueType)
name|void
name|setInputTypes
parameter_list|(
name|String
name|keyType
parameter_list|,
name|String
name|valueType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Run a map task in the child.    * @param split The input split for this map.    * @param numReduces The number of reduces for this job.    * @param pipedInput Is the input coming from Java?    * @throws IOException    */
DECL|method|runMap (InputSplit split, int numReduces, boolean pipedInput)
name|void
name|runMap
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|int
name|numReduces
parameter_list|,
name|boolean
name|pipedInput
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * For maps with pipedInput, the key/value pairs are sent via this messaage.    * @param key The record's key    * @param value The record's value    * @throws IOException    */
DECL|method|mapItem (K key, V value)
name|void
name|mapItem
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
comment|/**    * Run a reduce task in the child    * @param reduce the index of the reduce (0 .. numReduces - 1)    * @param pipedOutput is the output being sent to Java?    * @throws IOException    */
DECL|method|runReduce (int reduce, boolean pipedOutput)
name|void
name|runReduce
parameter_list|(
name|int
name|reduce
parameter_list|,
name|boolean
name|pipedOutput
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * The reduce should be given a new key    * @param key the new key    * @throws IOException    */
DECL|method|reduceKey (K key)
name|void
name|reduceKey
parameter_list|(
name|K
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * The reduce should be given a new value    * @param value the new value    * @throws IOException    */
DECL|method|reduceValue (V value)
name|void
name|reduceValue
parameter_list|(
name|V
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * The task has no more input coming, but it should finish processing it's     * input.    * @throws IOException    */
DECL|method|endOfInput ()
name|void
name|endOfInput
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * The task should stop as soon as possible, because something has gone wrong.    * @throws IOException    */
DECL|method|abort ()
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Flush the data through any buffers.    */
DECL|method|flush ()
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the connection.    */
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

