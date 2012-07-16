begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
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
name|PrintStream
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
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|conf
operator|.
name|Configured
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
name|hdfs
operator|.
name|DFSUtil
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
name|DistributedFileSystem
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
name|HAUtil
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
name|HdfsConfiguration
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
name|namenode
operator|.
name|NamenodeFsck
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
name|security
operator|.
name|SecurityUtil
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
name|security
operator|.
name|UserGroupInformation
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
name|StringUtils
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
comment|/**  * This class provides rudimentary checking of DFS volumes for errors and  * sub-optimal conditions.  *<p>The tool scans all files and directories, starting from an indicated  *  root path. The following abnormal conditions are detected and handled:</p>  *<ul>  *<li>files with blocks that are completely missing from all datanodes.<br/>  * In this case the tool can perform one of the following actions:  *<ul>  *<li>none ({@link org.apache.hadoop.hdfs.server.namenode.NamenodeFsck#FIXING_NONE})</li>  *<li>move corrupted files to /lost+found directory on DFS  *      ({@link org.apache.hadoop.hdfs.server.namenode.NamenodeFsck#FIXING_MOVE}). Remaining data blocks are saved as a  *      block chains, representing longest consecutive series of valid blocks.</li>  *<li>delete corrupted files ({@link org.apache.hadoop.hdfs.server.namenode.NamenodeFsck#FIXING_DELETE})</li>  *</ul>  *</li>  *<li>detect files with under-replicated or over-replicated blocks</li>  *</ul>  *  Additionally, the tool collects a detailed overall DFS statistics, and  *  optionally can print detailed statistics on block locations and replication  *  factors of each file.  *  The tool also provides and option to filter open files during the scan.  *    */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DFSck
specifier|public
class|class
name|DFSck
extends|extends
name|Configured
implements|implements
name|Tool
block|{
static|static
block|{
name|HdfsConfiguration
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
DECL|field|ugi
specifier|private
specifier|final
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|out
specifier|private
specifier|final
name|PrintStream
name|out
decl_stmt|;
comment|/**    * Filesystem checker.    * @param conf current Configuration    */
DECL|method|DFSck (Configuration conf)
specifier|public
name|DFSck
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|DFSck (Configuration conf, PrintStream out)
specifier|public
name|DFSck
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|PrintStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
comment|/**    * Print fsck usage information    */
DECL|method|printUsage ()
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: DFSck<path> [-list-corruptfileblocks | "
operator|+
literal|"[-move | -delete | -openforwrite] "
operator|+
literal|"[-files [-blocks [-locations | -racks]]]]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t<path>\tstart checking from this path"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-move\tmove corrupted files to /lost+found"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-delete\tdelete corrupted files"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-files\tprint out files being checked"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-openforwrite\tprint out files opened for write"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-list-corruptfileblocks\tprint out list of missing "
operator|+
literal|"blocks and files they belong to"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-blocks\tprint out block report"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-locations\tprint out locations for every block"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t-racks\tprint out network topology for data-node locations"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t\tBy default fsck ignores files opened for write, "
operator|+
literal|"use -openforwrite to report such files. They are usually "
operator|+
literal|" tagged CORRUPT or HEALTHY depending on their block "
operator|+
literal|"allocation status"
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param args    */
annotation|@
name|Override
DECL|method|run (final String[] args)
specifier|public
name|int
name|run
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
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
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|doWork
argument_list|(
name|args
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/*    * To get the list, we need to call iteratively until the server says    * there is no more left.    */
DECL|method|listCorruptFileBlocks (String dir, String baseUrl)
specifier|private
name|Integer
name|listCorruptFileBlocks
parameter_list|(
name|String
name|dir
parameter_list|,
name|String
name|baseUrl
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|errCode
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|numCorrupt
init|=
literal|0
decl_stmt|;
name|int
name|cookie
init|=
literal|0
decl_stmt|;
specifier|final
name|String
name|noCorruptLine
init|=
literal|"has no CORRUPT files"
decl_stmt|;
specifier|final
name|String
name|noMoreCorruptLine
init|=
literal|"has no more CORRUPT files"
decl_stmt|;
specifier|final
name|String
name|cookiePrefix
init|=
literal|"Cookie:"
decl_stmt|;
name|boolean
name|allDone
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|allDone
condition|)
block|{
specifier|final
name|StringBuffer
name|url
init|=
operator|new
name|StringBuffer
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
if|if
condition|(
name|cookie
operator|>
literal|0
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|"&startblockafter="
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|cookie
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|URL
name|path
init|=
operator|new
name|URL
argument_list|(
name|url
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|URLConnection
name|connection
init|=
name|SecurityUtil
operator|.
name|openSecureHttpConnection
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|connection
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|BufferedReader
name|input
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|input
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|cookiePrefix
argument_list|)
condition|)
block|{
try|try
block|{
name|cookie
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|allDone
operator|=
literal|true
expr_stmt|;
break|break;
block|}
continue|continue;
block|}
if|if
condition|(
operator|(
name|line
operator|.
name|endsWith
argument_list|(
name|noCorruptLine
argument_list|)
operator|)
operator|||
operator|(
name|line
operator|.
name|endsWith
argument_list|(
name|noMoreCorruptLine
argument_list|)
operator|)
operator|||
operator|(
name|line
operator|.
name|endsWith
argument_list|(
name|NamenodeFsck
operator|.
name|NONEXISTENT_STATUS
argument_list|)
operator|)
condition|)
block|{
name|allDone
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|(
name|line
operator|.
name|isEmpty
argument_list|()
operator|)
operator|||
operator|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"FSCK started by"
argument_list|)
operator|)
operator|||
operator|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"The filesystem under path"
argument_list|)
operator|)
condition|)
continue|continue;
name|numCorrupt
operator|++
expr_stmt|;
if|if
condition|(
name|numCorrupt
operator|==
literal|1
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"The list of corrupt files under path '"
operator|+
name|dir
operator|+
literal|"' are:"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|out
operator|.
name|println
argument_list|(
literal|"The filesystem under path '"
operator|+
name|dir
operator|+
literal|"' has "
operator|+
name|numCorrupt
operator|+
literal|" CORRUPT files"
argument_list|)
expr_stmt|;
if|if
condition|(
name|numCorrupt
operator|==
literal|0
condition|)
name|errCode
operator|=
literal|0
expr_stmt|;
return|return
name|errCode
return|;
block|}
comment|/**    * Derive the namenode http address from the current file system,    * either default or as set by "-fs" in the generic options.    * @return Returns http address or null if failure.    * @throws IOException if we can't determine the active NN address    */
DECL|method|getCurrentNamenodeAddress ()
specifier|private
name|String
name|getCurrentNamenodeAddress
parameter_list|()
throws|throws
name|IOException
block|{
comment|//String nnAddress = null;
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
comment|//get the filesystem object to verify it is an HDFS system
name|FileSystem
name|fs
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"FileSystem is inaccessible due to:\n"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ioe
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|fs
operator|instanceof
name|DistributedFileSystem
operator|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"FileSystem is "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|DFSUtil
operator|.
name|getInfoServer
argument_list|(
name|HAUtil
operator|.
name|getAddressOfActive
argument_list|(
name|fs
argument_list|)
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|doWork (final String[] args)
specifier|private
name|int
name|doWork
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StringBuilder
name|url
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"http://"
argument_list|)
decl_stmt|;
name|String
name|namenodeAddress
init|=
name|getCurrentNamenodeAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|namenodeAddress
operator|==
literal|null
condition|)
block|{
comment|//Error message already output in {@link #getCurrentNamenodeAddress()}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"DFSck exiting."
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
name|url
operator|.
name|append
argument_list|(
name|namenodeAddress
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Connecting to namenode via "
operator|+
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|url
operator|.
name|append
argument_list|(
literal|"/fsck?ugi="
argument_list|)
operator|.
name|append
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|dir
init|=
literal|null
decl_stmt|;
name|boolean
name|doListCorruptFileBlocks
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|args
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
literal|"-move"
argument_list|)
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|"&move=1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
literal|"-delete"
argument_list|)
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|"&delete=1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
literal|"-files"
argument_list|)
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|"&files=1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
literal|"-openforwrite"
argument_list|)
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|"&openforwrite=1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
literal|"-blocks"
argument_list|)
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|"&blocks=1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
literal|"-locations"
argument_list|)
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|"&locations=1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
literal|"-racks"
argument_list|)
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|"&racks=1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
literal|"-list-corruptfileblocks"
argument_list|)
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|"&listcorruptfileblocks=1"
argument_list|)
expr_stmt|;
name|doListCorruptFileBlocks
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|args
index|[
name|idx
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|dir
condition|)
block|{
name|dir
operator|=
name|args
index|[
name|idx
index|]
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"fsck: can only operate on one path at a time '"
operator|+
name|args
index|[
name|idx
index|]
operator|+
literal|"'"
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
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"fsck: Illegal option '"
operator|+
name|args
index|[
name|idx
index|]
operator|+
literal|"'"
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
block|}
if|if
condition|(
literal|null
operator|==
name|dir
condition|)
block|{
name|dir
operator|=
literal|"/"
expr_stmt|;
block|}
name|url
operator|.
name|append
argument_list|(
literal|"&path="
argument_list|)
operator|.
name|append
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|dir
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|doListCorruptFileBlocks
condition|)
block|{
return|return
name|listCorruptFileBlocks
argument_list|(
name|dir
argument_list|,
name|url
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
name|URL
name|path
init|=
operator|new
name|URL
argument_list|(
name|url
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|URLConnection
name|connection
init|=
name|SecurityUtil
operator|.
name|openSecureHttpConnection
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|connection
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|BufferedReader
name|input
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|String
name|lastLine
init|=
literal|null
decl_stmt|;
name|int
name|errCode
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|line
operator|=
name|input
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|lastLine
operator|=
name|line
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|lastLine
operator|.
name|endsWith
argument_list|(
name|NamenodeFsck
operator|.
name|HEALTHY_STATUS
argument_list|)
condition|)
block|{
name|errCode
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lastLine
operator|.
name|endsWith
argument_list|(
name|NamenodeFsck
operator|.
name|CORRUPT_STATUS
argument_list|)
condition|)
block|{
name|errCode
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lastLine
operator|.
name|endsWith
argument_list|(
name|NamenodeFsck
operator|.
name|NONEXISTENT_STATUS
argument_list|)
condition|)
block|{
name|errCode
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|errCode
return|;
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
comment|// -files option is also used by GenericOptionsParser
comment|// Make sure that is not the first argument for fsck
name|int
name|res
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
operator|(
name|args
operator|.
name|length
operator|==
literal|0
operator|)
operator|||
operator|(
literal|"-files"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
operator|)
condition|)
name|printUsage
argument_list|()
expr_stmt|;
else|else
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|DFSck
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|)
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

