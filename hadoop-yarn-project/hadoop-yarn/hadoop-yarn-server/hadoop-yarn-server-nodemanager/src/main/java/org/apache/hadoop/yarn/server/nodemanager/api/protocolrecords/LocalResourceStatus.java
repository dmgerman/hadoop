begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords
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
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResource
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|URL
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
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|records
operator|.
name|SerializedException
import|;
end_import

begin_interface
DECL|interface|LocalResourceStatus
specifier|public
interface|interface
name|LocalResourceStatus
block|{
DECL|method|getResource ()
specifier|public
name|LocalResource
name|getResource
parameter_list|()
function_decl|;
DECL|method|getStatus ()
specifier|public
name|ResourceStatusType
name|getStatus
parameter_list|()
function_decl|;
DECL|method|getLocalPath ()
specifier|public
name|URL
name|getLocalPath
parameter_list|()
function_decl|;
DECL|method|getLocalSize ()
specifier|public
name|long
name|getLocalSize
parameter_list|()
function_decl|;
DECL|method|getException ()
specifier|public
name|SerializedException
name|getException
parameter_list|()
function_decl|;
DECL|method|setResource (LocalResource resource)
specifier|public
name|void
name|setResource
parameter_list|(
name|LocalResource
name|resource
parameter_list|)
function_decl|;
DECL|method|setStatus (ResourceStatusType status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|ResourceStatusType
name|status
parameter_list|)
function_decl|;
DECL|method|setLocalPath (URL localPath)
specifier|public
name|void
name|setLocalPath
parameter_list|(
name|URL
name|localPath
parameter_list|)
function_decl|;
DECL|method|setLocalSize (long size)
specifier|public
name|void
name|setLocalSize
parameter_list|(
name|long
name|size
parameter_list|)
function_decl|;
DECL|method|setException (SerializedException exception)
specifier|public
name|void
name|setException
parameter_list|(
name|SerializedException
name|exception
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

