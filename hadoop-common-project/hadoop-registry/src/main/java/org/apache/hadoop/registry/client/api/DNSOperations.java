begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|api
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
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
name|service
operator|.
name|Service
import|;
end_import

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
comment|/**  * DNS Operations.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|DNSOperations
specifier|public
interface|interface
name|DNSOperations
extends|extends
name|Service
block|{
comment|/**    * Register a service based on a service record.    *    * @param path the ZK path.    * @param record record providing DNS registration info.    * @throws IOException Any other IO Exception.    */
DECL|method|register (String path, ServiceRecord record)
name|void
name|register
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete a service's registered endpoints.    *    * If the operation returns without an error then the entry has been    * deleted.    *    * @param path the ZK path.    * @param record service record    * @throws IOException Any other IO Exception    *    */
DECL|method|delete (String path, ServiceRecord record)
name|void
name|delete
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

