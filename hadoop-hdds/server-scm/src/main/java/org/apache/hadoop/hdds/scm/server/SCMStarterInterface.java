begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license  * agreements. See the NOTICE file distributed with this work for additional  * information regarding  * copyright ownership. The ASF licenses this file to you under the Apache  * License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the  * License. You may obtain a  * copy of the License at  *  *<p>http://www.apache.org/licenses/LICENSE-2.0  *  *<p>Unless required by applicable law or agreed to in writing, software  * distributed under the  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR  * CONDITIONS OF ANY KIND, either  * express or implied. See the License for the specific language governing  * permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|server
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
comment|/**  * This interface is used by the StorageContainerManager to allow the  * dependencies to be injected to the CLI class.  */
end_comment

begin_interface
DECL|interface|SCMStarterInterface
specifier|public
interface|interface
name|SCMStarterInterface
block|{
DECL|method|start (OzoneConfiguration conf)
name|void
name|start
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|init (OzoneConfiguration conf, String clusterId)
name|boolean
name|init
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|String
name|clusterId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|generateClusterId ()
name|String
name|generateClusterId
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

