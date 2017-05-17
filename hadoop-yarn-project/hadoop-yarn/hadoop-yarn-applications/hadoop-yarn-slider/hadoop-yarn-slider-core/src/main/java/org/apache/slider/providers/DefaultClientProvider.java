begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
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
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Artifact
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|ConfigFile
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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_class
DECL|class|DefaultClientProvider
specifier|public
class|class
name|DefaultClientProvider
extends|extends
name|AbstractClientProvider
block|{
DECL|method|DefaultClientProvider ()
specifier|public
name|DefaultClientProvider
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|validateArtifact (Artifact artifact, FileSystem fileSystem)
specifier|public
name|void
name|validateArtifact
parameter_list|(
name|Artifact
name|artifact
parameter_list|,
name|FileSystem
name|fileSystem
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|validateConfigFile (ConfigFile configFile, FileSystem fileSystem)
specifier|protected
name|void
name|validateConfigFile
parameter_list|(
name|ConfigFile
name|configFile
parameter_list|,
name|FileSystem
name|fileSystem
parameter_list|)
throws|throws
name|IOException
block|{
comment|// validate dest_file is not absolute
if|if
condition|(
name|Paths
operator|.
name|get
argument_list|(
name|configFile
operator|.
name|getDestFile
argument_list|()
argument_list|)
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Dest_file must not be absolute path: "
operator|+
name|configFile
operator|.
name|getDestFile
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

