begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.client
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
name|client
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|test
operator|.
name|TestHdfsHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestHttpFSWithHttpFSFileSystem
specifier|public
class|class
name|TestHttpFSWithHttpFSFileSystem
extends|extends
name|BaseTestHttpFSWith
block|{
DECL|method|TestHttpFSWithHttpFSFileSystem (Operation operation)
specifier|public
name|TestHttpFSWithHttpFSFileSystem
parameter_list|(
name|Operation
name|operation
parameter_list|)
block|{
name|super
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFileSystemClass ()
specifier|protected
name|Class
name|getFileSystemClass
parameter_list|()
block|{
return|return
name|HttpFSFileSystem
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|getProxiedFSTestDir ()
specifier|protected
name|Path
name|getProxiedFSTestDir
parameter_list|()
block|{
return|return
name|TestHdfsHelper
operator|.
name|getHdfsTestDir
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getProxiedFSURI ()
specifier|protected
name|String
name|getProxiedFSURI
parameter_list|()
block|{
return|return
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProxiedFSConf ()
specifier|protected
name|Configuration
name|getProxiedFSConf
parameter_list|()
block|{
return|return
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
return|;
block|}
block|}
end_class

end_unit

