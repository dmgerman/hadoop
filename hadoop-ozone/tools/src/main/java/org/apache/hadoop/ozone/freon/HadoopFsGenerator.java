begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.freon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|freon
package|;
end_package

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
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|FSDataOutputStream
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
name|hdds
operator|.
name|cli
operator|.
name|HddsVersionProvider
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Option
import|;
end_import

begin_comment
comment|/**  * Data generator tool test om performance.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"dfsg"
argument_list|,
name|aliases
operator|=
literal|"dfs-file-generator"
argument_list|,
name|description
operator|=
literal|"Create random files to the any dfs compatible file system."
argument_list|,
name|versionProvider
operator|=
name|HddsVersionProvider
operator|.
name|class
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|,
name|showDefaultValues
operator|=
literal|true
argument_list|)
DECL|class|HadoopFsGenerator
specifier|public
class|class
name|HadoopFsGenerator
extends|extends
name|BaseFreonGenerator
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HadoopFsGenerator
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--path"
block|}
argument_list|,
name|description
operator|=
literal|"Hadoop FS file system path"
argument_list|,
name|defaultValue
operator|=
literal|"o3fs://bucket1.vol1"
argument_list|)
DECL|field|rootPath
specifier|private
name|String
name|rootPath
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-s"
block|,
literal|"--size"
block|}
argument_list|,
name|description
operator|=
literal|"Size of the generated files (in bytes)"
argument_list|,
name|defaultValue
operator|=
literal|"10240"
argument_list|)
DECL|field|fileSize
specifier|private
name|int
name|fileSize
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--buffer"
block|}
argument_list|,
name|description
operator|=
literal|"Size of buffer used to generated the key content."
argument_list|,
name|defaultValue
operator|=
literal|"4096"
argument_list|)
DECL|field|bufferSize
specifier|private
name|int
name|bufferSize
decl_stmt|;
DECL|field|contentGenerator
specifier|private
name|ContentGenerator
name|contentGenerator
decl_stmt|;
DECL|field|timer
specifier|private
name|Timer
name|timer
decl_stmt|;
DECL|field|fileSystem
specifier|private
name|FileSystem
name|fileSystem
decl_stmt|;
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|()
expr_stmt|;
name|OzoneConfiguration
name|configuration
init|=
name|createOzoneConfiguration
argument_list|()
decl_stmt|;
name|fileSystem
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|rootPath
argument_list|)
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
name|contentGenerator
operator|=
operator|new
name|ContentGenerator
argument_list|(
name|fileSize
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|timer
operator|=
name|getMetrics
argument_list|()
operator|.
name|timer
argument_list|(
literal|"file-create"
argument_list|)
expr_stmt|;
name|runTests
argument_list|(
name|this
operator|::
name|createFile
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|createFile (long counter)
specifier|private
name|void
name|createFile
parameter_list|(
name|long
name|counter
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|rootPath
operator|+
literal|"/"
operator|+
name|generateObjectName
argument_list|(
name|counter
argument_list|)
argument_list|)
decl_stmt|;
name|fileSystem
operator|.
name|mkdirs
argument_list|(
name|file
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|timer
operator|.
name|time
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
init|(
name|FSDataOutputStream
name|output
init|=
name|fileSystem
operator|.
name|create
argument_list|(
name|file
argument_list|)
init|)
block|{
name|contentGenerator
operator|.
name|write
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

