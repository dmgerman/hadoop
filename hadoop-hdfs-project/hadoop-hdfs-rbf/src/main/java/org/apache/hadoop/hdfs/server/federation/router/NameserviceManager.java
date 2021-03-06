begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|DisableNameserviceRequest
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|DisableNameserviceResponse
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|EnableNameserviceRequest
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|EnableNameserviceResponse
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetDisabledNameservicesRequest
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetDisabledNameservicesResponse
import|;
end_import

begin_comment
comment|/**  * Interface for enable/disable name service.  */
end_comment

begin_interface
DECL|interface|NameserviceManager
specifier|public
interface|interface
name|NameserviceManager
block|{
comment|/**    * Disable a name service.    * @param request Request to disable a name service.    * @return Response to disable a name service.    * @throws IOException If it cannot perform the operation.    */
DECL|method|disableNameservice ( DisableNameserviceRequest request)
name|DisableNameserviceResponse
name|disableNameservice
parameter_list|(
name|DisableNameserviceRequest
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Enable a name service.    * @param request Request to enable a name service.    * @return Response to disable a name service.    * @throws IOException If it cannot perform the operation.    */
DECL|method|enableNameservice (EnableNameserviceRequest request)
name|EnableNameserviceResponse
name|enableNameservice
parameter_list|(
name|EnableNameserviceRequest
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the list of disabled name service.    * @param request Request to get the disabled name services.    * @return Response to get the disabled name services.    * @throws IOException If it cannot perform the operation.    */
DECL|method|getDisabledNameservices ( GetDisabledNameservicesRequest request)
name|GetDisabledNameservicesResponse
name|getDisabledNameservices
parameter_list|(
name|GetDisabledNameservicesRequest
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

