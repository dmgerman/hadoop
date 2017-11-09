begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLineParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|HelpFormatter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|PosixParser
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|blockaliasmap
operator|.
name|BlockAliasMap
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Tool
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
name|ToolRunner
import|;
end_import

begin_comment
comment|/**  * Create FSImage from an external namespace.  */
end_comment

begin_class
DECL|class|FileSystemImage
specifier|public
class|class
name|FileSystemImage
implements|implements
name|Tool
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
comment|// require absolute URI to write anywhere but local
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|printUsage ()
specifier|protected
name|void
name|printUsage
parameter_list|()
block|{
name|HelpFormatter
name|formatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|formatter
operator|.
name|printHelp
argument_list|(
literal|"fs2img [OPTIONS] URI"
argument_list|,
operator|new
name|Options
argument_list|()
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|setSyntaxPrefix
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|printHelp
argument_list|(
literal|"Options"
argument_list|,
name|options
argument_list|()
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|options ()
specifier|static
name|Options
name|options
parameter_list|()
block|{
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"o"
argument_list|,
literal|"outdir"
argument_list|,
literal|true
argument_list|,
literal|"Output directory"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"u"
argument_list|,
literal|"ugiclass"
argument_list|,
literal|true
argument_list|,
literal|"UGI resolver class"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"b"
argument_list|,
literal|"blockclass"
argument_list|,
literal|true
argument_list|,
literal|"Block output class"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"i"
argument_list|,
literal|"blockidclass"
argument_list|,
literal|true
argument_list|,
literal|"Block resolver class"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"c"
argument_list|,
literal|"cachedirs"
argument_list|,
literal|true
argument_list|,
literal|"Max active dirents"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"cid"
argument_list|,
literal|"clusterID"
argument_list|,
literal|true
argument_list|,
literal|"Cluster ID"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"h"
argument_list|,
literal|"help"
argument_list|,
literal|false
argument_list|,
literal|"Print usage"
argument_list|)
expr_stmt|;
return|return
name|options
return|;
block|}
annotation|@
name|Override
DECL|method|run (String[] argv)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|Options
name|options
init|=
name|options
argument_list|()
decl_stmt|;
name|CommandLineParser
name|parser
init|=
operator|new
name|PosixParser
argument_list|()
decl_stmt|;
name|CommandLine
name|cmd
decl_stmt|;
try|try
block|{
name|cmd
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|options
argument_list|,
name|argv
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Error parsing command-line options: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|printUsage
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
literal|"h"
argument_list|)
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|ImageWriter
operator|.
name|Options
name|opts
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|ImageWriter
operator|.
name|Options
operator|.
name|class
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Option
name|o
range|:
name|cmd
operator|.
name|getOptions
argument_list|()
control|)
block|{
switch|switch
condition|(
name|o
operator|.
name|getOpt
argument_list|()
condition|)
block|{
case|case
literal|"o"
case|:
name|opts
operator|.
name|output
argument_list|(
name|o
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"u"
case|:
name|opts
operator|.
name|ugi
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|o
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|UGIResolver
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"b"
case|:
name|opts
operator|.
name|blocks
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|o
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|BlockAliasMap
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"i"
case|:
name|opts
operator|.
name|blockIds
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|o
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|BlockResolver
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"c"
case|:
name|opts
operator|.
name|cache
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|o
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"cid"
case|:
name|opts
operator|.
name|clusterID
argument_list|(
name|o
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Internal error"
argument_list|)
throw|;
block|}
block|}
name|String
index|[]
name|rem
init|=
name|cmd
operator|.
name|getArgs
argument_list|()
decl_stmt|;
if|if
condition|(
name|rem
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
try|try
init|(
name|ImageWriter
name|w
init|=
operator|new
name|ImageWriter
argument_list|(
name|opts
argument_list|)
init|)
block|{
for|for
control|(
name|TreePath
name|e
range|:
operator|new
name|FSTreeWalk
argument_list|(
operator|new
name|Path
argument_list|(
name|rem
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|)
control|)
block|{
name|w
operator|.
name|accept
argument_list|(
name|e
argument_list|)
expr_stmt|;
comment|// add and continue
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|ret
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|FileSystemImage
argument_list|()
argument_list|,
name|argv
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

