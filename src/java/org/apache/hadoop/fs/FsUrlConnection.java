begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|java
operator|.
name|io
operator|.
name|InputStream
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
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

begin_comment
comment|/**  * Representation of a URL connection to open InputStreams.  */
end_comment

begin_class
DECL|class|FsUrlConnection
class|class
name|FsUrlConnection
extends|extends
name|URLConnection
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|is
specifier|private
name|InputStream
name|is
decl_stmt|;
DECL|method|FsUrlConnection (Configuration conf, URL url)
name|FsUrlConnection
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URL
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|connect ()
specifier|public
name|void
name|connect
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|url
operator|.
name|toURI
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|is
operator|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|getInputStream ()
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|connect
argument_list|()
expr_stmt|;
block|}
return|return
name|is
return|;
block|}
block|}
end_class

end_unit

