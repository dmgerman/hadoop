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
name|InputStream
import|;
end_import

begin_comment
comment|/**  *<code>RamManager</code> manages a memory pool of a configured limit.  */
end_comment

begin_interface
DECL|interface|RamManager
interface|interface
name|RamManager
block|{
comment|/**    * Reserve memory for data coming through the given input-stream.    *     * @param requestedSize size of memory requested    * @param in input stream    * @throws InterruptedException    * @return<code>true</code> if memory was allocated immediately,     *         else<code>false</code>    */
DECL|method|reserve (int requestedSize, InputStream in)
name|boolean
name|reserve
parameter_list|(
name|int
name|requestedSize
parameter_list|,
name|InputStream
name|in
parameter_list|)
throws|throws
name|InterruptedException
function_decl|;
comment|/**    * Return memory to the pool.    *     * @param requestedSize size of memory returned to the pool    */
DECL|method|unreserve (int requestedSize)
name|void
name|unreserve
parameter_list|(
name|int
name|requestedSize
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

