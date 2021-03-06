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
name|BufferedReader
import|;
end_import

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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|util
operator|.
name|Shell
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/** Filesystem disk space usage statistics.  * Uses the unix 'df' program to get mount points, and java.io.File for  * space utilization. Tested on Linux, FreeBSD, Windows. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DF
specifier|public
class|class
name|DF
extends|extends
name|Shell
block|{
DECL|field|dirPath
specifier|private
specifier|final
name|String
name|dirPath
decl_stmt|;
DECL|field|dirFile
specifier|private
specifier|final
name|File
name|dirFile
decl_stmt|;
DECL|field|filesystem
specifier|private
name|String
name|filesystem
decl_stmt|;
DECL|field|mount
specifier|private
name|String
name|mount
decl_stmt|;
DECL|field|output
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|output
decl_stmt|;
DECL|method|DF (File path, Configuration conf)
specifier|public
name|DF
parameter_list|(
name|File
name|path
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|path
argument_list|,
name|conf
operator|.
name|getLong
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_DF_INTERVAL_KEY
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|FS_DF_INTERVAL_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|DF (File path, long dfInterval)
specifier|public
name|DF
parameter_list|(
name|File
name|path
parameter_list|,
name|long
name|dfInterval
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dfInterval
argument_list|)
expr_stmt|;
name|this
operator|.
name|dirPath
operator|=
name|path
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
name|this
operator|.
name|dirFile
operator|=
operator|new
name|File
argument_list|(
name|this
operator|.
name|dirPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|output
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/// ACCESSORS
comment|/** @return the canonical path to the volume we're checking. */
DECL|method|getDirPath ()
specifier|public
name|String
name|getDirPath
parameter_list|()
block|{
return|return
name|dirPath
return|;
block|}
comment|/** @return a string indicating which filesystem volume we're checking. */
DECL|method|getFilesystem ()
specifier|public
name|String
name|getFilesystem
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|this
operator|.
name|filesystem
operator|=
name|dirFile
operator|.
name|getCanonicalPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|filesystem
return|;
block|}
else|else
block|{
name|run
argument_list|()
expr_stmt|;
name|verifyExitCode
argument_list|()
expr_stmt|;
name|parseOutput
argument_list|()
expr_stmt|;
return|return
name|filesystem
return|;
block|}
block|}
comment|/** @return the capacity of the measured filesystem in bytes. */
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
block|{
return|return
name|dirFile
operator|.
name|getTotalSpace
argument_list|()
return|;
block|}
comment|/** @return the total used space on the filesystem in bytes. */
DECL|method|getUsed ()
specifier|public
name|long
name|getUsed
parameter_list|()
block|{
return|return
name|dirFile
operator|.
name|getTotalSpace
argument_list|()
operator|-
name|dirFile
operator|.
name|getFreeSpace
argument_list|()
return|;
block|}
comment|/** @return the usable space remaining on the filesystem in bytes. */
DECL|method|getAvailable ()
specifier|public
name|long
name|getAvailable
parameter_list|()
block|{
return|return
name|dirFile
operator|.
name|getUsableSpace
argument_list|()
return|;
block|}
comment|/** @return the amount of the volume full, as a percent. */
DECL|method|getPercentUsed ()
specifier|public
name|int
name|getPercentUsed
parameter_list|()
block|{
name|double
name|cap
init|=
operator|(
name|double
operator|)
name|getCapacity
argument_list|()
decl_stmt|;
name|double
name|used
init|=
operator|(
name|cap
operator|-
operator|(
name|double
operator|)
name|getAvailable
argument_list|()
operator|)
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|used
operator|*
literal|100.0
operator|/
name|cap
argument_list|)
return|;
block|}
comment|/** @return the filesystem mount point for the indicated volume */
DECL|method|getMount ()
specifier|public
name|String
name|getMount
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Abort early if specified path does not exist
if|if
condition|(
operator|!
name|dirFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Specified path "
operator|+
name|dirFile
operator|.
name|getPath
argument_list|()
operator|+
literal|"does not exist"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
comment|// Assume a drive letter for a mount point
name|this
operator|.
name|mount
operator|=
name|dirFile
operator|.
name|getCanonicalPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|run
argument_list|()
expr_stmt|;
name|verifyExitCode
argument_list|()
expr_stmt|;
name|parseOutput
argument_list|()
expr_stmt|;
block|}
return|return
name|mount
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"df -k "
operator|+
name|mount
operator|+
literal|"\n"
operator|+
name|filesystem
operator|+
literal|"\t"
operator|+
name|getCapacity
argument_list|()
operator|/
literal|1024
operator|+
literal|"\t"
operator|+
name|getUsed
argument_list|()
operator|/
literal|1024
operator|+
literal|"\t"
operator|+
name|getAvailable
argument_list|()
operator|/
literal|1024
operator|+
literal|"\t"
operator|+
name|getPercentUsed
argument_list|()
operator|+
literal|"%\t"
operator|+
name|mount
return|;
block|}
annotation|@
name|Override
DECL|method|getExecString ()
specifier|protected
name|String
index|[]
name|getExecString
parameter_list|()
block|{
comment|// ignoring the error since the exit code it enough
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"DF.getExecString() should never be called on Windows"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"bash"
block|,
literal|"-c"
block|,
literal|"exec 'df' '-k' '-P' '"
operator|+
name|dirPath
operator|+
literal|"' 2>/dev/null"
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|parseExecResult (BufferedReader lines)
specifier|protected
name|void
name|parseExecResult
parameter_list|(
name|BufferedReader
name|lines
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|clear
argument_list|()
expr_stmt|;
name|String
name|line
init|=
name|lines
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|lines
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|parseOutput ()
specifier|protected
name|void
name|parseOutput
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|output
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"Fewer lines of output than expected"
argument_list|)
decl_stmt|;
if|if
condition|(
name|output
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|": "
operator|+
name|output
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|line
init|=
name|output
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|StringTokenizer
name|tokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|,
literal|" \t\n\r\f%"
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|filesystem
operator|=
name|tokens
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected empty line"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|tokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
comment|// for long filesystem name
if|if
condition|(
name|output
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
name|line
operator|=
name|output
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expecting additional output after line: "
operator|+
name|line
argument_list|)
throw|;
block|}
name|tokens
operator|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|,
literal|" \t\n\r\f%"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Long
operator|.
name|parseLong
argument_list|(
name|tokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
comment|// capacity
name|Long
operator|.
name|parseLong
argument_list|(
name|tokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
comment|// used
name|Long
operator|.
name|parseLong
argument_list|(
name|tokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
comment|// available
name|Integer
operator|.
name|parseInt
argument_list|(
name|tokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
comment|// pct used
name|this
operator|.
name|mount
operator|=
name|tokens
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not parse line: "
operator|+
name|line
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not parse line: "
operator|+
name|line
argument_list|)
throw|;
block|}
block|}
DECL|method|verifyExitCode ()
specifier|private
name|void
name|verifyExitCode
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|getExitCode
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"df could not be run successfully: "
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|output
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"."
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
name|path
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|DF
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|FS_DF_INTERVAL_DEFAULT
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

