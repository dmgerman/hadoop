begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.extensions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|extensions
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
operator|.
name|FsAction
import|;
end_import

begin_comment
comment|/**  * Interface to support authorization in Azure Blob File System.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"authorization-subsystems"
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|AbfsAuthorizer
specifier|public
interface|interface
name|AbfsAuthorizer
block|{
comment|/**    * Initialize authorizer for Azure Blob File System.    *    * @throws AbfsAuthorizationException if unable to initialize the authorizer.    * @throws IOException network problems or similar.    * @throws IllegalArgumentException if the required parameters are not provided.    */
DECL|method|init ()
name|void
name|init
parameter_list|()
throws|throws
name|AbfsAuthorizationException
throws|,
name|IOException
function_decl|;
comment|/**    * Checks if the provided {@link FsAction} is allowed on the provided {@link Path}s.    *    * @param action the {@link FsAction} being requested on the provided {@link Path}s.    * @param absolutePaths The absolute paths of the storage being accessed.    * @return true if authorized, otherwise false.    * @throws AbfsAuthorizationException on authorization failure.    * @throws IOException network problems or similar.    * @throws IllegalArgumentException if the required parameters are not provided.    */
DECL|method|isAuthorized (FsAction action, Path... absolutePaths)
name|boolean
name|isAuthorized
parameter_list|(
name|FsAction
name|action
parameter_list|,
name|Path
modifier|...
name|absolutePaths
parameter_list|)
throws|throws
name|AbfsAuthorizationException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

