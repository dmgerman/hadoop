begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.select
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|select
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Scanner
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|FSDataInputStream
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
name|FutureDataInputStreamBuilder
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
name|impl
operator|.
name|FutureIOSupport
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
name|s3a
operator|.
name|S3AFileSystem
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
name|s3a
operator|.
name|s3guard
operator|.
name|S3GuardTool
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
name|shell
operator|.
name|CommandFormat
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
name|DurationInfo
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
name|ExitUtil
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
name|OperationDuration
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
operator|.
name|isNotEmpty
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|cleanupWithLogger
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|launcher
operator|.
name|LauncherExitCodes
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|select
operator|.
name|SelectConstants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This is a CLI tool for the select operation, which is available  * through the S3Guard command.  *  * Usage:  *<pre>  *   hadoop s3guard select [options] Path Statement  *</pre>  */
end_comment

begin_class
DECL|class|SelectTool
specifier|public
class|class
name|SelectTool
extends|extends
name|S3GuardTool
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
name|SelectTool
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"select"
decl_stmt|;
DECL|field|PURPOSE
specifier|public
specifier|static
specifier|final
name|String
name|PURPOSE
init|=
literal|"make an S3 Select call"
decl_stmt|;
DECL|field|USAGE
specifier|private
specifier|static
specifier|final
name|String
name|USAGE
init|=
name|NAME
operator|+
literal|" [OPTIONS]"
operator|+
literal|" [-limit rows]"
operator|+
literal|" [-header (use|none|ignore)]"
operator|+
literal|" [-out path]"
operator|+
literal|" [-expected rows]"
operator|+
literal|" [-compression (gzip|bzip2|none)]"
operator|+
literal|" [-inputformat csv]"
operator|+
literal|" [-outputformat csv]"
operator|+
literal|"<PATH><SELECT QUERY>\n"
operator|+
literal|"\t"
operator|+
name|PURPOSE
operator|+
literal|"\n\n"
decl_stmt|;
DECL|field|OPT_COMPRESSION
specifier|public
specifier|static
specifier|final
name|String
name|OPT_COMPRESSION
init|=
literal|"compression"
decl_stmt|;
DECL|field|OPT_EXPECTED
specifier|public
specifier|static
specifier|final
name|String
name|OPT_EXPECTED
init|=
literal|"expected"
decl_stmt|;
DECL|field|OPT_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|OPT_HEADER
init|=
literal|"header"
decl_stmt|;
DECL|field|OPT_INPUTFORMAT
specifier|public
specifier|static
specifier|final
name|String
name|OPT_INPUTFORMAT
init|=
literal|"inputformat"
decl_stmt|;
DECL|field|OPT_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|OPT_LIMIT
init|=
literal|"limit"
decl_stmt|;
DECL|field|OPT_OUTPUT
specifier|public
specifier|static
specifier|final
name|String
name|OPT_OUTPUT
init|=
literal|"out"
decl_stmt|;
DECL|field|OPT_OUTPUTFORMAT
specifier|public
specifier|static
specifier|final
name|String
name|OPT_OUTPUTFORMAT
init|=
literal|"inputformat"
decl_stmt|;
DECL|field|TOO_FEW_ARGUMENTS
specifier|static
specifier|final
name|String
name|TOO_FEW_ARGUMENTS
init|=
literal|"Too few arguments"
decl_stmt|;
DECL|field|WRONG_FILESYSTEM
specifier|static
specifier|final
name|String
name|WRONG_FILESYSTEM
init|=
literal|"Wrong filesystem for "
decl_stmt|;
DECL|field|SELECT_IS_DISABLED
specifier|static
specifier|final
name|String
name|SELECT_IS_DISABLED
init|=
literal|"S3 Select is disabled"
decl_stmt|;
DECL|field|selectDuration
specifier|private
name|OperationDuration
name|selectDuration
decl_stmt|;
DECL|field|bytesRead
specifier|private
name|long
name|bytesRead
decl_stmt|;
DECL|field|linesRead
specifier|private
name|long
name|linesRead
decl_stmt|;
DECL|method|SelectTool (Configuration conf)
specifier|public
name|SelectTool
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// read capacity.
name|getCommandFormat
argument_list|()
operator|.
name|addOptionWithValue
argument_list|(
name|OPT_COMPRESSION
argument_list|)
expr_stmt|;
name|getCommandFormat
argument_list|()
operator|.
name|addOptionWithValue
argument_list|(
name|OPT_EXPECTED
argument_list|)
expr_stmt|;
name|getCommandFormat
argument_list|()
operator|.
name|addOptionWithValue
argument_list|(
name|OPT_HEADER
argument_list|)
expr_stmt|;
name|getCommandFormat
argument_list|()
operator|.
name|addOptionWithValue
argument_list|(
name|OPT_INPUTFORMAT
argument_list|)
expr_stmt|;
name|getCommandFormat
argument_list|()
operator|.
name|addOptionWithValue
argument_list|(
name|OPT_LIMIT
argument_list|)
expr_stmt|;
name|getCommandFormat
argument_list|()
operator|.
name|addOptionWithValue
argument_list|(
name|OPT_OUTPUT
argument_list|)
expr_stmt|;
name|getCommandFormat
argument_list|()
operator|.
name|addOptionWithValue
argument_list|(
name|OPT_OUTPUTFORMAT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
return|return
name|USAGE
return|;
block|}
DECL|method|getSelectDuration ()
specifier|public
name|OperationDuration
name|getSelectDuration
parameter_list|()
block|{
return|return
name|selectDuration
return|;
block|}
DECL|method|getBytesRead ()
specifier|public
name|long
name|getBytesRead
parameter_list|()
block|{
return|return
name|bytesRead
return|;
block|}
comment|/**    * Number of lines read, when printing to the console.    * @return line count. 0 if writing direct to a file.    */
DECL|method|getLinesRead ()
specifier|public
name|long
name|getLinesRead
parameter_list|()
block|{
return|return
name|linesRead
return|;
block|}
DECL|method|parseNaturalInt (String option, String value)
specifier|private
name|int
name|parseNaturalInt
parameter_list|(
name|String
name|option
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|int
name|r
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|<
literal|0
condition|)
block|{
throw|throw
name|invalidArgs
argument_list|(
literal|"Negative value for option %s : %s"
argument_list|,
name|option
argument_list|,
name|value
argument_list|)
throw|;
block|}
return|return
name|r
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
name|invalidArgs
argument_list|(
literal|"Invalid number for option %s : %s"
argument_list|,
name|option
argument_list|,
name|value
argument_list|)
throw|;
block|}
block|}
DECL|method|getOptValue (String key)
specifier|private
name|Optional
argument_list|<
name|String
argument_list|>
name|getOptValue
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|String
name|value
init|=
name|getCommandFormat
argument_list|()
operator|.
name|getOptValue
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|isNotEmpty
argument_list|(
name|value
argument_list|)
condition|?
name|Optional
operator|.
name|of
argument_list|(
name|value
argument_list|)
else|:
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
DECL|method|getIntValue (String key)
specifier|private
name|Optional
argument_list|<
name|Integer
argument_list|>
name|getIntValue
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|Optional
argument_list|<
name|String
argument_list|>
name|v
init|=
name|getOptValue
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|v
operator|.
name|map
argument_list|(
name|i
lambda|->
name|parseNaturalInt
argument_list|(
name|key
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Execute the select operation.    * @param args argument list    * @param out output stream    * @return an exit code    * @throws IOException IO failure    * @throws ExitUtil.ExitException managed failure    */
DECL|method|run (String[] args, PrintStream out)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|PrintStream
name|out
parameter_list|)
throws|throws
name|IOException
throws|,
name|ExitUtil
operator|.
name|ExitException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|parsedArgs
decl_stmt|;
try|try
block|{
name|parsedArgs
operator|=
name|parseArgs
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommandFormat
operator|.
name|UnknownOptionException
name|e
parameter_list|)
block|{
name|errorln
argument_list|(
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ExitUtil
operator|.
name|ExitException
argument_list|(
name|EXIT_USAGE
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|parsedArgs
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
name|errorln
argument_list|(
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ExitUtil
operator|.
name|ExitException
argument_list|(
name|EXIT_USAGE
argument_list|,
name|TOO_FEW_ARGUMENTS
argument_list|)
throw|;
block|}
comment|// read mandatory arguments
specifier|final
name|String
name|file
init|=
name|parsedArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|String
name|expression
init|=
name|parsedArgs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|println
argument_list|(
name|out
argument_list|,
literal|"selecting file %s with query %s"
argument_list|,
name|path
argument_list|,
name|expression
argument_list|)
expr_stmt|;
comment|// and the optional arguments to adjust the configuration.
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|header
init|=
name|getOptValue
argument_list|(
name|OPT_HEADER
argument_list|)
decl_stmt|;
name|header
operator|.
name|ifPresent
argument_list|(
name|h
lambda|->
name|println
argument_list|(
name|out
argument_list|,
literal|"Using header option %s"
argument_list|,
name|h
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|destPath
init|=
name|getOptValue
argument_list|(
name|OPT_OUTPUT
argument_list|)
operator|.
name|map
argument_list|(
name|output
lambda|->
block|{
name|println
argument_list|(
name|out
argument_list|,
literal|"Saving output to %s"
argument_list|,
name|output
argument_list|)
expr_stmt|;
return|return
operator|new
name|Path
argument_list|(
name|output
argument_list|)
return|;
block|}
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|toConsole
init|=
name|destPath
operator|==
literal|null
decl_stmt|;
comment|// expected lines are only checked if empty
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|expectedLines
init|=
name|toConsole
condition|?
name|getIntValue
argument_list|(
name|OPT_EXPECTED
argument_list|)
else|:
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|limit
init|=
name|getIntValue
argument_list|(
name|OPT_LIMIT
argument_list|)
decl_stmt|;
if|if
condition|(
name|limit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
specifier|final
name|int
name|l
init|=
name|limit
operator|.
name|get
argument_list|()
decl_stmt|;
name|println
argument_list|(
name|out
argument_list|,
literal|"Using line limit %s"
argument_list|,
name|l
argument_list|)
expr_stmt|;
if|if
condition|(
name|expression
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|contains
argument_list|(
literal|" limit "
argument_list|)
condition|)
block|{
name|println
argument_list|(
name|out
argument_list|,
literal|"line limit already specified in SELECT expression"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|=
name|expression
operator|+
literal|" LIMIT "
operator|+
name|l
expr_stmt|;
block|}
block|}
comment|// now bind to the filesystem.
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|fs
operator|instanceof
name|S3AFileSystem
operator|)
condition|)
block|{
throw|throw
operator|new
name|ExitUtil
operator|.
name|ExitException
argument_list|(
name|EXIT_SERVICE_UNAVAILABLE
argument_list|,
name|WRONG_FILESYSTEM
operator|+
name|file
operator|+
literal|": got "
operator|+
name|fs
argument_list|)
throw|;
block|}
name|setFilesystem
argument_list|(
operator|(
name|S3AFileSystem
operator|)
name|fs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|getFilesystem
argument_list|()
operator|.
name|hasCapability
argument_list|(
name|S3_SELECT_CAPABILITY
argument_list|)
condition|)
block|{
comment|// capability disabled
throw|throw
operator|new
name|ExitUtil
operator|.
name|ExitException
argument_list|(
name|EXIT_SERVICE_UNAVAILABLE
argument_list|,
name|SELECT_IS_DISABLED
operator|+
literal|" for "
operator|+
name|file
argument_list|)
throw|;
block|}
name|linesRead
operator|=
literal|0
expr_stmt|;
name|selectDuration
operator|=
operator|new
name|OperationDuration
argument_list|()
expr_stmt|;
comment|// open and scan the stream.
specifier|final
name|FutureDataInputStreamBuilder
name|builder
init|=
name|fs
operator|.
name|openFile
argument_list|(
name|path
argument_list|)
operator|.
name|must
argument_list|(
name|SELECT_SQL
argument_list|,
name|expression
argument_list|)
decl_stmt|;
name|header
operator|.
name|ifPresent
argument_list|(
name|h
lambda|->
name|builder
operator|.
name|must
argument_list|(
name|CSV_INPUT_HEADER
argument_list|,
name|h
argument_list|)
argument_list|)
expr_stmt|;
name|getOptValue
argument_list|(
name|OPT_COMPRESSION
argument_list|)
operator|.
name|ifPresent
argument_list|(
name|compression
lambda|->
name|builder
operator|.
name|must
argument_list|(
name|SELECT_INPUT_COMPRESSION
argument_list|,
name|compression
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|getOptValue
argument_list|(
name|OPT_INPUTFORMAT
argument_list|)
operator|.
name|ifPresent
argument_list|(
name|opt
lambda|->
block|{
if|if
condition|(
operator|!
literal|"csv"
operator|.
name|equalsIgnoreCase
argument_list|(
name|opt
argument_list|)
condition|)
block|{
throw|throw
name|invalidArgs
argument_list|(
literal|"Unsupported input format %s"
argument_list|,
name|opt
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|getOptValue
argument_list|(
name|OPT_OUTPUTFORMAT
argument_list|)
operator|.
name|ifPresent
argument_list|(
name|opt
lambda|->
block|{
if|if
condition|(
operator|!
literal|"csv"
operator|.
name|equalsIgnoreCase
argument_list|(
name|opt
argument_list|)
condition|)
block|{
throw|throw
name|invalidArgs
argument_list|(
literal|"Unsupported output format %s"
argument_list|,
name|opt
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// turn on SQL error reporting.
name|builder
operator|.
name|opt
argument_list|(
name|SELECT_ERRORS_INCLUDE_SQL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|stream
decl_stmt|;
try|try
init|(
name|DurationInfo
name|ignored
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|"Selecting stream"
argument_list|)
init|)
block|{
name|stream
operator|=
name|FutureIOSupport
operator|.
name|awaitFuture
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// the source file is missing.
throw|throw
name|storeNotFound
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
if|if
condition|(
name|toConsole
condition|)
block|{
comment|// logging to console
name|bytesRead
operator|=
literal|0
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"IOResourceOpenedButNotSafelyClosed"
argument_list|)
name|Scanner
name|scanner
init|=
operator|new
name|Scanner
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|scanner
operator|.
name|useDelimiter
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
while|while
condition|(
name|scanner
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|linesRead
operator|++
expr_stmt|;
name|String
name|l
init|=
name|scanner
operator|.
name|nextLine
argument_list|()
decl_stmt|;
name|bytesRead
operator|+=
name|l
operator|.
name|length
argument_list|()
operator|+
literal|1
expr_stmt|;
name|println
argument_list|(
name|out
argument_list|,
literal|"%s"
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// straight dump of whole file; no line counting
name|FileSystem
name|destFS
init|=
name|destPath
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|DurationInfo
name|ignored
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|"Copying File"
argument_list|)
init|;             OutputStream destStream = destFS.createFile(destPath)
operator|.
name|overwrite
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
block|)
block|{
name|bytesRead
operator|=
name|IOUtils
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|destStream
argument_list|)
expr_stmt|;
block|}
block|}
comment|// close the stream.
comment|// this will take time if there's a lot of data remaining
try|try
init|(
name|DurationInfo
name|ignored
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|"Closing stream"
argument_list|)
init|)
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// generate a meaningful result depending on the operation
name|String
name|result
init|=
name|toConsole
condition|?
name|String
operator|.
name|format
argument_list|(
literal|"%s lines"
argument_list|,
name|linesRead
argument_list|)
else|:
name|String
operator|.
name|format
argument_list|(
literal|"%s bytes"
argument_list|,
name|bytesRead
argument_list|)
decl_stmt|;
comment|// print some statistics
name|selectDuration
operator|.
name|finished
argument_list|()
expr_stmt|;
name|println
argument_list|(
name|out
argument_list|,
literal|"Read %s in time %s"
argument_list|,
name|result
argument_list|,
name|selectDuration
operator|.
name|getDurationString
argument_list|()
argument_list|)
expr_stmt|;
name|println
argument_list|(
name|out
argument_list|,
literal|"Bytes Read: %,d bytes"
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|println
argument_list|(
name|out
argument_list|,
literal|"Bandwidth: %,.1f MiB/s"
argument_list|,
name|bandwidthMBs
argument_list|(
name|bytesRead
argument_list|,
name|selectDuration
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Statistics {}"
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|expectedLines
operator|.
name|ifPresent
argument_list|(
name|l
lambda|->
block|{
if|if
condition|(
name|l
operator|!=
name|linesRead
condition|)
block|{
throw|throw
name|exitException
argument_list|(
name|EXIT_FAIL
argument_list|,
literal|"Expected %d rows but the operation returned %d"
argument_list|,
name|l
argument_list|,
name|linesRead
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
parameter_list|()
constructor_decl|;
return|return
name|EXIT_SUCCESS
return|;
block|}
end_class

begin_comment
comment|/**    * Work out the bandwidth in MB/s.    * @param bytes bytes    * @param durationMillisNS duration in nanos    * @return the number of megabytes/second of the recorded operation    */
end_comment

begin_function
DECL|method|bandwidthMBs (long bytes, long durationMillisNS)
specifier|public
specifier|static
name|double
name|bandwidthMBs
parameter_list|(
name|long
name|bytes
parameter_list|,
name|long
name|durationMillisNS
parameter_list|)
block|{
return|return
name|durationMillisNS
operator|>
literal|0
condition|?
operator|(
name|bytes
operator|/
literal|1048576.0
operator|*
literal|1000
operator|/
name|durationMillisNS
operator|)
else|:
literal|0
return|;
block|}
end_function

unit|}
end_unit

