begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.jscsiHelper.cache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
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

begin_comment
comment|/**  * Defines the interface for cache implementations. The cache will be called  * by cblock storage module when it performs IO operations.  */
end_comment

begin_interface
DECL|interface|CacheModule
specifier|public
interface|interface
name|CacheModule
block|{
comment|/**    * check if the key is cached, if yes, returned the cached object.    * otherwise, load from data source. Then put it into cache.    *    * @param blockID    * @return the target block.    */
DECL|method|get (long blockID)
name|LogicalBlock
name|get
parameter_list|(
name|long
name|blockID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * put the value of the key into cache.    * @param blockID    * @param value    */
DECL|method|put (long blockID, byte[] value)
name|void
name|put
parameter_list|(
name|long
name|blockID
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|flush ()
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|isDirtyCache ()
name|boolean
name|isDirtyCache
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

