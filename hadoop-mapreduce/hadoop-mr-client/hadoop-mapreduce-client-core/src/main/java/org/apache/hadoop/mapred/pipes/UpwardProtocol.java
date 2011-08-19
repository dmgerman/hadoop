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

begin_comment
comment|/**  * The interface for the messages that can come up from the child. All of these  * calls are asynchronous and return before the message has been processed.  */
end_comment

begin_interface
DECL|interface|UpwardProtocol
interface|interface
name|UpwardProtocol
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
comment|/**    * Output a record from the child.    * @param key the record's key    * @param value the record's value    * @throws IOException    */
DECL|method|output (K key, V value)
name|void
name|output
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
comment|/**    * Map functions where the application has defined a partition function    * output records along with their partition.    * @param reduce the reduce to send this record to    * @param key the record's key    * @param value the record's value    * @throws IOException    */
DECL|method|partitionedOutput (int reduce, K key, V value)
name|void
name|partitionedOutput
parameter_list|(
name|int
name|reduce
parameter_list|,
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Update the task's status message    * @param msg the string to display to the user    * @throws IOException    */
DECL|method|status (String msg)
name|void
name|status
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Report making progress (and the current progress)    * @param progress the current progress (0.0 to 1.0)    * @throws IOException    */
DECL|method|progress (float progress)
name|void
name|progress
parameter_list|(
name|float
name|progress
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Report that the application has finished processing all inputs     * successfully.    * @throws IOException    */
DECL|method|done ()
name|void
name|done
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Report that the application or more likely communication failed.    * @param e    */
DECL|method|failed (Throwable e)
name|void
name|failed
parameter_list|(
name|Throwable
name|e
parameter_list|)
function_decl|;
comment|/**    * Register a counter with the given id and group/name.    * @param group counter group    * @param name counter name    * @throws IOException    */
DECL|method|registerCounter (int id, String group, String name)
name|void
name|registerCounter
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|group
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Increment the value of a registered counter.    * @param id counter id of the registered counter    * @param amount increment for the counter value    * @throws IOException    */
DECL|method|incrementCounter (int id, long amount)
name|void
name|incrementCounter
parameter_list|(
name|int
name|id
parameter_list|,
name|long
name|amount
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Handles authentication response from client.    * It must notify the threads waiting for authentication response.    * @param digest    * @return true if authentication is successful    * @throws IOException    */
DECL|method|authenticate (String digest)
name|boolean
name|authenticate
parameter_list|(
name|String
name|digest
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

