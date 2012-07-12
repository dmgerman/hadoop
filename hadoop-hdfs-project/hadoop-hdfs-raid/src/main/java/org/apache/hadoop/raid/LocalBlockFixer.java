begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.raid
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|raid
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
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|hdfs
operator|.
name|RaidDFSUtil
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
name|Time
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
name|net
operator|.
name|NetUtils
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
name|raid
operator|.
name|RaidNode
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
name|raid
operator|.
name|RaidUtils
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
name|raid
operator|.
name|protocol
operator|.
name|PolicyInfo
operator|.
name|ErasureCodeType
import|;
end_import

begin_comment
comment|/**  * This class fixes source file blocks using the parity file,  * and parity file blocks using the source file.  * It periodically fetches the list of corrupt files from the namenode,  * and figures out the location of the bad block by reading through  * the corrupt file.  */
end_comment

begin_class
DECL|class|LocalBlockFixer
specifier|public
class|class
name|LocalBlockFixer
extends|extends
name|BlockFixer
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LocalBlockFixer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|history
specifier|private
name|java
operator|.
name|util
operator|.
name|HashMap
argument_list|<
name|String
argument_list|,
name|java
operator|.
name|util
operator|.
name|Date
argument_list|>
name|history
decl_stmt|;
DECL|field|helper
specifier|private
name|BlockFixerHelper
name|helper
decl_stmt|;
DECL|method|LocalBlockFixer (Configuration conf)
specifier|public
name|LocalBlockFixer
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|history
operator|=
operator|new
name|java
operator|.
name|util
operator|.
name|HashMap
argument_list|<
name|String
argument_list|,
name|java
operator|.
name|util
operator|.
name|Date
argument_list|>
argument_list|()
expr_stmt|;
name|helper
operator|=
operator|new
name|BlockFixerHelper
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|running
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LocalBlockFixer continuing to run..."
argument_list|)
expr_stmt|;
name|doFix
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|err
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exiting after encountering "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|err
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|err
throw|;
block|}
block|}
block|}
DECL|method|doFix ()
name|void
name|doFix
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
while|while
condition|(
name|running
condition|)
block|{
comment|// Sleep before proceeding to fix files.
name|Thread
operator|.
name|sleep
argument_list|(
name|blockFixInterval
argument_list|)
expr_stmt|;
comment|// Purge history older than the history interval.
name|purgeHistory
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|corruptFiles
init|=
name|getCorruptFiles
argument_list|()
decl_stmt|;
name|filterUnfixableSourceFiles
argument_list|(
name|corruptFiles
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|corruptFiles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// If there are no corrupt files, retry after some time.
continue|continue;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Found "
operator|+
name|corruptFiles
operator|.
name|size
argument_list|()
operator|+
literal|" corrupt files."
argument_list|)
expr_stmt|;
name|helper
operator|.
name|sortCorruptFiles
argument_list|(
name|corruptFiles
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|srcPath
range|:
name|corruptFiles
control|)
block|{
if|if
condition|(
operator|!
name|running
condition|)
break|break;
try|try
block|{
name|boolean
name|fixed
init|=
name|helper
operator|.
name|fixFile
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding "
operator|+
name|srcPath
operator|+
literal|" to history"
argument_list|)
expr_stmt|;
name|history
operator|.
name|put
argument_list|(
name|srcPath
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fixed
condition|)
block|{
name|incrFilesFixed
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Hit error while processing "
operator|+
name|srcPath
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ie
argument_list|)
argument_list|)
expr_stmt|;
comment|// Do nothing, move on to the next file.
block|}
block|}
block|}
block|}
comment|/**    * We maintain history of fixed files because a fixed file may appear in    * the list of corrupt files if we loop around too quickly.    * This function removes the old items in the history so that we can    * recognize files that have actually become corrupt since being fixed.    */
DECL|method|purgeHistory ()
name|void
name|purgeHistory
parameter_list|()
block|{
name|java
operator|.
name|util
operator|.
name|Date
name|cutOff
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|(
name|Time
operator|.
name|now
argument_list|()
operator|-
name|historyInterval
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|toRemove
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|history
operator|.
name|keySet
argument_list|()
control|)
block|{
name|java
operator|.
name|util
operator|.
name|Date
name|item
init|=
name|history
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|before
argument_list|(
name|cutOff
argument_list|)
condition|)
block|{
name|toRemove
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|key
range|:
name|toRemove
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing "
operator|+
name|key
operator|+
literal|" from history"
argument_list|)
expr_stmt|;
name|history
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return A list of corrupt files as obtained from the namenode    */
DECL|method|getCorruptFiles ()
name|List
argument_list|<
name|Path
argument_list|>
name|getCorruptFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|DistributedFileSystem
name|dfs
init|=
name|helper
operator|.
name|getDFS
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|files
init|=
name|RaidDFSUtil
operator|.
name|getCorruptFiles
argument_list|(
name|dfs
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|corruptFiles
init|=
operator|new
name|LinkedList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|files
control|)
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|history
operator|.
name|containsKey
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|corruptFiles
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|RaidUtils
operator|.
name|filterTrash
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|corruptFiles
argument_list|)
expr_stmt|;
return|return
name|corruptFiles
return|;
block|}
block|}
end_class

end_unit

