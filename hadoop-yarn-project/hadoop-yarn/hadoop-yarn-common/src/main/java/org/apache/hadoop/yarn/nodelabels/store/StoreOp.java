begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels.store
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
operator|.
name|store
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
comment|/**  * Define the interface for store activity.  * Used by for FileSystem based operation.  *  * @param<W> write to be done to  * @param<R> read to be done from  * @param<M> manager used  */
end_comment

begin_interface
DECL|interface|StoreOp
specifier|public
interface|interface
name|StoreOp
parameter_list|<
name|W
parameter_list|,
name|R
parameter_list|,
name|M
parameter_list|>
block|{
comment|/**    * Write operation to persistent storage    *    * @param write write to be done to    * @param mgr manager used by store    * @throws IOException    */
DECL|method|write (W write, M mgr)
name|void
name|write
parameter_list|(
name|W
name|write
parameter_list|,
name|M
name|mgr
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read and populate StoreOp    *    * @param read read to be done from    * @param mgr  manager used by store    * @throws IOException    */
DECL|method|recover (R read, M mgr)
name|void
name|recover
parameter_list|(
name|R
name|read
parameter_list|,
name|M
name|mgr
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

