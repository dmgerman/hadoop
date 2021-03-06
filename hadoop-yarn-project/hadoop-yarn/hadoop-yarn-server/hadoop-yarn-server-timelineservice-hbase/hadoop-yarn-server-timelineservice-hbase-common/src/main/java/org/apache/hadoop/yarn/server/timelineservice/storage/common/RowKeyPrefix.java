begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
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
name|timelineservice
operator|.
name|storage
operator|.
name|common
package|;
end_package

begin_comment
comment|/**  * In queries where a single result is needed, an exact rowkey can be used  * through the corresponding rowkey#getRowKey() method. For queries that need to  * scan over a range of rowkeys, a partial (the initial part) of rowkeys are  * used. Classes implementing RowKeyPrefix indicate that they are the initial  * part of rowkeys, with different constructors with fewer number of argument to  * form a partial rowkey, a prefix.  *  * @param<R> indicating the type of rowkey that a particular implementation is  *          a prefix for.  */
end_comment

begin_interface
DECL|interface|RowKeyPrefix
specifier|public
interface|interface
name|RowKeyPrefix
parameter_list|<
name|R
parameter_list|>
block|{
comment|/**    * Create a row key prefix, meaning a partial rowkey that can be used in range    * scans. Which fields are included in the prefix will depend on the    * constructor of the specific instance that was used. Output depends on which    * constructor was used.    * @return a prefix of the following form {@code fist!second!...!last!}    */
DECL|method|getRowKeyPrefix ()
name|byte
index|[]
name|getRowKeyPrefix
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

