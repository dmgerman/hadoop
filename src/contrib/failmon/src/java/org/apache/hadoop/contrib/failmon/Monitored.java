begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.failmon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|failmon
package|;
end_package

begin_comment
comment|/**********************************************************  * Represents objects that monitor specific hardware resources and  * can query them to get EventRecords describing the state of these  * resources.  *  **********************************************************/
end_comment

begin_interface
DECL|interface|Monitored
specifier|public
interface|interface
name|Monitored
block|{
comment|/**    * Get an array of all EventRecords that can be extracted for    * the represented hardware component.    *     * @return The array of EventRecords    */
DECL|method|monitor ()
specifier|public
name|EventRecord
index|[]
name|monitor
parameter_list|()
function_decl|;
comment|/**    * Inserts all EventRecords that can be extracted for    * the represented hardware component into a LocalStore.    *     * @param ls the LocalStore into which the EventRecords     * are to be stored.    */
DECL|method|monitor (LocalStore ls)
specifier|public
name|void
name|monitor
parameter_list|(
name|LocalStore
name|ls
parameter_list|)
function_decl|;
comment|/**    * Return a String with information about the implementing    * class     *     * @return A String describing the implementing class    */
DECL|method|getInfo ()
specifier|public
name|String
name|getInfo
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

