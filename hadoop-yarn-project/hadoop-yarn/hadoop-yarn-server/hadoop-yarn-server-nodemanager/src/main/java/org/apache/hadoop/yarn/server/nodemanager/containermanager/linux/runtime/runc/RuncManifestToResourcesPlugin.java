begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.runc
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
name|containermanager
operator|.
name|linux
operator|.
name|runtime
operator|.
name|runc
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
name|service
operator|.
name|Service
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * This class is a plugin interface for the  * {@link org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.RuncContainerRuntime}  * that maps OCI Image Manifests into associated config and layers.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|RuncManifestToResourcesPlugin
specifier|public
interface|interface
name|RuncManifestToResourcesPlugin
extends|extends
name|Service
block|{
comment|//The layers should be returned in the order in which they
comment|// appear in the manifest
DECL|method|getLayerResources (ImageManifest manifest)
name|List
argument_list|<
name|LocalResource
argument_list|>
name|getLayerResources
parameter_list|(
name|ImageManifest
name|manifest
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getConfigResource (ImageManifest manifest)
name|LocalResource
name|getConfigResource
parameter_list|(
name|ImageManifest
name|manifest
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

