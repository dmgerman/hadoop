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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * This interface is intended for allowing schedulers to   * communicate with the queue share management implementation.  * Schedulers can periodically poll this interface to  * obtain the latest queue allocations.  */
end_comment

begin_interface
DECL|interface|QueueAllocator
specifier|public
interface|interface
name|QueueAllocator
block|{
comment|/**    * Used by schedulers to obtain queue allocations periodically    * @return hashtable of queue names and their allocations (shares)    */
DECL|method|getAllocation ()
name|Map
argument_list|<
name|String
argument_list|,
name|QueueAllocation
argument_list|>
name|getAllocation
parameter_list|()
function_decl|;
comment|/**    * Used by schedulers to push queue usage info for    * accounting purposes.    * @param queue the queue name    * @param used of slots currently used    * @param pending number of tasks pending    */
DECL|method|setUsage (String queue, int used, int pending)
name|void
name|setUsage
parameter_list|(
name|String
name|queue
parameter_list|,
name|int
name|used
parameter_list|,
name|int
name|pending
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

