begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements. See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership. The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License. You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
name|resources
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
name|conf
operator|.
name|Configuration
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
import|;
end_import

begin_comment
comment|/**  * Base interface for network tag mapping manager.  */
end_comment

begin_interface
DECL|interface|NetworkTagMappingManager
specifier|public
interface|interface
name|NetworkTagMappingManager
block|{
comment|/**    * Initialize the networkTagMapping manager.    */
DECL|method|initialize (Configuration conf)
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**    * Get networkTagHexID for the given container.    * @param container    * @return the networkTagID.    */
DECL|method|getNetworkTagHexID (Container container)
name|String
name|getNetworkTagHexID
parameter_list|(
name|Container
name|container
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

