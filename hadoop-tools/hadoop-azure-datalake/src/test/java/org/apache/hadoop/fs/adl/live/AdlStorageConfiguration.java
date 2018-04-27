begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.adl.live
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|adl
operator|.
name|live
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
name|FileSystem
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
name|adl
operator|.
name|AdlFileSystem
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
name|util
operator|.
name|ReflectionUtils
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_comment
comment|/**  * Configure Adl storage file system.  */
end_comment

begin_class
DECL|class|AdlStorageConfiguration
specifier|public
specifier|final
class|class
name|AdlStorageConfiguration
block|{
DECL|field|CONTRACT_XML
specifier|static
specifier|final
name|String
name|CONTRACT_XML
init|=
literal|"adls.xml"
decl_stmt|;
DECL|field|CONTRACT_ENABLE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|CONTRACT_ENABLE_KEY
init|=
literal|"fs.adl.test.contract.enable"
decl_stmt|;
DECL|field|CONTRACT_ENABLE_DEFAULT
specifier|private
specifier|static
specifier|final
name|boolean
name|CONTRACT_ENABLE_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|FILE_SYSTEM_KEY
specifier|private
specifier|static
specifier|final
name|String
name|FILE_SYSTEM_KEY
init|=
name|String
operator|.
name|format
argument_list|(
literal|"test.fs.%s.name"
argument_list|,
name|AdlFileSystem
operator|.
name|SCHEME
argument_list|)
decl_stmt|;
DECL|field|FILE_SYSTEM_IMPL_KEY
specifier|private
specifier|static
specifier|final
name|String
name|FILE_SYSTEM_IMPL_KEY
init|=
name|String
operator|.
name|format
argument_list|(
literal|"fs.%s.impl"
argument_list|,
name|AdlFileSystem
operator|.
name|SCHEME
argument_list|)
decl_stmt|;
DECL|field|FILE_SYSTEM_IMPL_DEFAULT
specifier|private
specifier|static
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|FILE_SYSTEM_IMPL_DEFAULT
init|=
name|AdlFileSystem
operator|.
name|class
decl_stmt|;
DECL|field|isContractTestEnabled
specifier|private
specifier|static
name|boolean
name|isContractTestEnabled
init|=
literal|false
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
static|static
block|{
name|Configuration
operator|.
name|addDeprecation
argument_list|(
literal|"dfs.adl.test.contract.enable"
argument_list|,
name|CONTRACT_ENABLE_KEY
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|reloadExistingConfigurations
argument_list|()
expr_stmt|;
block|}
DECL|method|AdlStorageConfiguration ()
specifier|private
name|AdlStorageConfiguration
parameter_list|()
block|{   }
DECL|method|getConfiguration ()
specifier|public
specifier|synchronized
specifier|static
name|Configuration
name|getConfiguration
parameter_list|()
block|{
name|Configuration
name|newConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|newConf
operator|.
name|addResource
argument_list|(
name|CONTRACT_XML
argument_list|)
expr_stmt|;
return|return
name|newConf
return|;
block|}
DECL|method|isContractTestEnabled ()
specifier|public
specifier|synchronized
specifier|static
name|boolean
name|isContractTestEnabled
parameter_list|()
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|conf
operator|=
name|getConfiguration
argument_list|()
expr_stmt|;
block|}
name|isContractTestEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|CONTRACT_ENABLE_KEY
argument_list|,
name|CONTRACT_ENABLE_DEFAULT
argument_list|)
expr_stmt|;
return|return
name|isContractTestEnabled
return|;
block|}
DECL|method|createStorageConnector ()
specifier|public
specifier|synchronized
specifier|static
name|FileSystem
name|createStorageConnector
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|conf
operator|=
name|getConfiguration
argument_list|()
expr_stmt|;
block|}
return|return
name|createStorageConnector
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|createStorageConnector ( Configuration fsConfig)
specifier|public
specifier|synchronized
specifier|static
name|FileSystem
name|createStorageConnector
parameter_list|(
name|Configuration
name|fsConfig
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|isContractTestEnabled
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|fileSystem
init|=
name|fsConfig
operator|.
name|get
argument_list|(
name|FILE_SYSTEM_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileSystem
operator|==
literal|null
operator|||
name|fileSystem
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Default file system not configured."
argument_list|)
throw|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|fsConfig
operator|.
name|getClass
argument_list|(
name|FILE_SYSTEM_IMPL_KEY
argument_list|,
name|FILE_SYSTEM_IMPL_DEFAULT
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|(
name|FileSystem
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|clazz
argument_list|,
name|fsConfig
argument_list|)
decl_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
operator|new
name|URI
argument_list|(
name|fileSystem
argument_list|)
argument_list|,
name|fsConfig
argument_list|)
expr_stmt|;
return|return
name|fs
return|;
block|}
block|}
end_class

end_unit

