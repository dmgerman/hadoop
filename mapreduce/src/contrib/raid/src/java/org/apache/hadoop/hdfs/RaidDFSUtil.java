begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

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
name|util
operator|.
name|LinkedList
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|FileStatus
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
name|RemoteIterator
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
name|protocol
operator|.
name|LocatedBlock
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
name|protocol
operator|.
name|LocatedBlocks
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
name|tools
operator|.
name|DFSck
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

begin_class
DECL|class|RaidDFSUtil
specifier|public
specifier|abstract
class|class
name|RaidDFSUtil
block|{
comment|/**    * Returns the corrupt blocks in a file.    */
DECL|method|corruptBlocksInFile ( DistributedFileSystem dfs, String path, long offset, long length)
specifier|public
specifier|static
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|corruptBlocksInFile
parameter_list|(
name|DistributedFileSystem
name|dfs
parameter_list|,
name|String
name|path
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|corrupt
init|=
operator|new
name|LinkedList
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|()
decl_stmt|;
name|LocatedBlocks
name|locatedBlocks
init|=
name|getBlockLocations
argument_list|(
name|dfs
argument_list|,
name|path
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|b
range|:
name|locatedBlocks
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
if|if
condition|(
name|b
operator|.
name|isCorrupt
argument_list|()
operator|||
operator|(
name|b
operator|.
name|getLocations
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|&&
name|b
operator|.
name|getBlockSize
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|corrupt
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|corrupt
return|;
block|}
DECL|method|getBlockLocations ( DistributedFileSystem dfs, String path, long offset, long length)
specifier|public
specifier|static
name|LocatedBlocks
name|getBlockLocations
parameter_list|(
name|DistributedFileSystem
name|dfs
parameter_list|,
name|String
name|path
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|namenode
operator|.
name|getBlockLocations
argument_list|(
name|path
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * Make successive calls to listCorruptFiles to obtain all     * corrupt files.    */
DECL|method|getCorruptFiles (DistributedFileSystem dfs)
specifier|public
specifier|static
name|String
index|[]
name|getCorruptFiles
parameter_list|(
name|DistributedFileSystem
name|dfs
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|corruptFiles
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|RemoteIterator
argument_list|<
name|Path
argument_list|>
name|cfb
init|=
name|dfs
operator|.
name|listCorruptFileBlocks
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|cfb
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|corruptFiles
operator|.
name|add
argument_list|(
name|cfb
operator|.
name|next
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|corruptFiles
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|corruptFiles
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

