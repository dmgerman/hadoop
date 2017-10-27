begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api
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
name|fs
operator|.
name|Path
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
comment|/** An Interface that can retrieve local directories to read from or write to.  *  Components can implement this interface to link it to  *  their own Directory Handler Service  */
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
DECL|interface|AuxiliaryLocalPathHandler
specifier|public
interface|interface
name|AuxiliaryLocalPathHandler
block|{
comment|/**    * Get a path from the local FS for reading for a given Auxiliary Service.    * @param path the requested path    * @return the complete path to the file on a local disk    * @throws IOException if the file read encounters a problem    */
DECL|method|getLocalPathForRead (String path)
name|Path
name|getLocalPathForRead
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a path from the local FS for writing for a given Auxiliary Service.    * @param path the requested path    * @return the complete path to the file on a local disk    * @throws IOException if the path creations fails    */
DECL|method|getLocalPathForWrite (String path)
name|Path
name|getLocalPathForWrite
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a path from the local FS for writing a file of an estimated size    * for a given Auxiliary Service.    * @param path the requested path    * @param size the size of the file that is going to be written    * @return the complete path to the file on a local disk    * @throws IOException if the path creations fails    */
DECL|method|getLocalPathForWrite (String path, long size)
name|Path
name|getLocalPathForWrite
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

