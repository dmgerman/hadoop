begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.checker
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
name|datanode
operator|.
name|checker
package|;
end_package

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
comment|/**  * A Checkable is an object whose health can be probed by invoking its  * {@link #check} method.  *  * e.g. a {@link Checkable} instance may represent a single hardware  * resource.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|Checkable
specifier|public
interface|interface
name|Checkable
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**    * Query the health of this object. This method may hang    * indefinitely depending on the status of the target resource.    *    * @param context for the probe operation. May be null depending    *                on the implementation.    *    * @return result of the check operation.    *    * @throws Exception encountered during the check operation. An    *                   exception indicates that the check failed.    */
DECL|method|check (K context)
name|V
name|check
parameter_list|(
name|K
name|context
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

