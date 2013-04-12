begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api
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
operator|.
name|Private
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_interface
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|interface|ResourceLocalizationSpec
specifier|public
interface|interface
name|ResourceLocalizationSpec
block|{
DECL|method|setResource (LocalResource rsrc)
specifier|public
name|void
name|setResource
parameter_list|(
name|LocalResource
name|rsrc
parameter_list|)
function_decl|;
DECL|method|getResource ()
specifier|public
name|LocalResource
name|getResource
parameter_list|()
function_decl|;
DECL|method|setDestinationDirectory (URL destinationDirectory)
specifier|public
name|void
name|setDestinationDirectory
parameter_list|(
name|URL
name|destinationDirectory
parameter_list|)
function_decl|;
DECL|method|getDestinationDirectory ()
specifier|public
name|URL
name|getDestinationDirectory
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

