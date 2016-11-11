begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.adl
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
name|DelegateToFileSystem
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
comment|/**  * Expose adl:// scheme to access ADL file system.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Adl
specifier|public
class|class
name|Adl
extends|extends
name|DelegateToFileSystem
block|{
DECL|method|Adl (URI theUri, Configuration conf)
name|Adl
parameter_list|(
name|URI
name|theUri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|super
argument_list|(
name|theUri
argument_list|,
name|createDataLakeFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|,
name|AdlFileSystem
operator|.
name|SCHEME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|createDataLakeFileSystem (Configuration conf)
specifier|private
specifier|static
name|AdlFileSystem
name|createDataLakeFileSystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|AdlFileSystem
name|fs
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|fs
return|;
block|}
comment|/**    * @return Default port for ADL File system to communicate    */
annotation|@
name|Override
DECL|method|getUriDefaultPort ()
specifier|public
specifier|final
name|int
name|getUriDefaultPort
parameter_list|()
block|{
return|return
name|AdlFileSystem
operator|.
name|DEFAULT_PORT
return|;
block|}
block|}
end_class

end_unit

