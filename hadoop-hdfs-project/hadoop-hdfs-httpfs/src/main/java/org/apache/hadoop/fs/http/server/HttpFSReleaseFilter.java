begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
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
name|lib
operator|.
name|service
operator|.
name|FileSystemAccess
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
name|lib
operator|.
name|servlet
operator|.
name|FileSystemReleaseFilter
import|;
end_import

begin_comment
comment|/**  * Filter that releases FileSystemAccess filesystem instances upon HTTP request  * completion.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HttpFSReleaseFilter
specifier|public
class|class
name|HttpFSReleaseFilter
extends|extends
name|FileSystemReleaseFilter
block|{
comment|/**    * Returns the {@link FileSystemAccess} service to return the FileSystemAccess filesystem    * instance to.    *    * @return the FileSystemAccess service.    */
annotation|@
name|Override
DECL|method|getFileSystemAccess ()
specifier|protected
name|FileSystemAccess
name|getFileSystemAccess
parameter_list|()
block|{
return|return
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

