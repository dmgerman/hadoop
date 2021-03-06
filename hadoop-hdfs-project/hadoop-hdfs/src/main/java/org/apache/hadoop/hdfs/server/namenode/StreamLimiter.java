begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_comment
comment|/**  * An object that allows you to set a limit on a stream.  This limit  * represents the number of bytes that can be read without getting an  * exception.  */
end_comment

begin_interface
DECL|interface|StreamLimiter
interface|interface
name|StreamLimiter
block|{
comment|/**    * Set a limit.  Calling this function clears any existing limit.    */
DECL|method|setLimit (long limit)
specifier|public
name|void
name|setLimit
parameter_list|(
name|long
name|limit
parameter_list|)
function_decl|;
comment|/**    * Disable limit.    */
DECL|method|clearLimit ()
specifier|public
name|void
name|clearLimit
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

