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
name|EnumSet
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|hadoop
operator|.
name|fs
operator|.
name|FileUtil
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
name|Storage
operator|.
name|StorageDirectory
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
name|NNStorage
operator|.
name|NameNodeDirType
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
name|NNStorage
operator|.
name|NameNodeFile
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
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_class
DECL|class|FSImageTransactionalStorageInspector
class|class
name|FSImageTransactionalStorageInspector
extends|extends
name|FSImageStorageInspector
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FSImageTransactionalStorageInspector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|needToSave
specifier|private
name|boolean
name|needToSave
init|=
literal|false
decl_stmt|;
DECL|field|isUpgradeFinalized
specifier|private
name|boolean
name|isUpgradeFinalized
init|=
literal|true
decl_stmt|;
DECL|field|foundImages
specifier|final
name|List
argument_list|<
name|FSImageFile
argument_list|>
name|foundImages
init|=
operator|new
name|ArrayList
argument_list|<
name|FSImageFile
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|maxSeenTxId
specifier|private
name|long
name|maxSeenTxId
init|=
literal|0
decl_stmt|;
DECL|field|namePatterns
specifier|private
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|namePatterns
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|method|FSImageTransactionalStorageInspector ()
name|FSImageTransactionalStorageInspector
parameter_list|()
block|{
name|this
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|NameNodeFile
operator|.
name|IMAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|FSImageTransactionalStorageInspector (EnumSet<NameNodeFile> nnfs)
name|FSImageTransactionalStorageInspector
parameter_list|(
name|EnumSet
argument_list|<
name|NameNodeFile
argument_list|>
name|nnfs
parameter_list|)
block|{
for|for
control|(
name|NameNodeFile
name|nnf
range|:
name|nnfs
control|)
block|{
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|nnf
operator|.
name|getName
argument_list|()
operator|+
literal|"_(\\d+)"
argument_list|)
decl_stmt|;
name|namePatterns
operator|.
name|add
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|matchPattern (String name)
specifier|private
name|Matcher
name|matchPattern
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Pattern
name|p
range|:
name|namePatterns
control|)
block|{
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|m
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|inspectDirectory (StorageDirectory sd)
specifier|public
name|void
name|inspectDirectory
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Was the directory just formatted?
if|if
condition|(
operator|!
name|sd
operator|.
name|getVersionFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No version file in "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|needToSave
operator||=
literal|true
expr_stmt|;
return|return;
block|}
comment|// Check for a seen_txid file, which marks a minimum transaction ID that
comment|// must be included in our load plan.
try|try
block|{
name|maxSeenTxId
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxSeenTxId
argument_list|,
name|NNStorage
operator|.
name|readTransactionIdFile
argument_list|(
name|sd
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to determine the max transaction ID seen by "
operator|+
name|sd
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
return|return;
block|}
name|File
name|currentDir
init|=
name|sd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|File
name|filesInStorage
index|[]
decl_stmt|;
try|try
block|{
name|filesInStorage
operator|=
name|FileUtil
operator|.
name|listFiles
argument_list|(
name|currentDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to inspect storage directory "
operator|+
name|currentDir
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|File
name|f
range|:
name|filesInStorage
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Checking file "
operator|+
name|f
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|f
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// Check for fsimage_*
name|Matcher
name|imageMatch
init|=
name|this
operator|.
name|matchPattern
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|imageMatch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sd
operator|.
name|getStorageDirType
argument_list|()
operator|.
name|isOfType
argument_list|(
name|NameNodeDirType
operator|.
name|IMAGE
argument_list|)
condition|)
block|{
try|try
block|{
name|long
name|txid
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|imageMatch
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|foundImages
operator|.
name|add
argument_list|(
operator|new
name|FSImageFile
argument_list|(
name|sd
argument_list|,
name|f
argument_list|,
name|txid
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Image file "
operator|+
name|f
operator|+
literal|" has improperly formatted "
operator|+
literal|"transaction ID"
argument_list|)
expr_stmt|;
comment|// skip
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Found image file at "
operator|+
name|f
operator|+
literal|" but storage directory is "
operator|+
literal|"not configured to contain images."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// set finalized flag
name|isUpgradeFinalized
operator|=
name|isUpgradeFinalized
operator|&&
operator|!
name|sd
operator|.
name|getPreviousDir
argument_list|()
operator|.
name|exists
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isUpgradeFinalized ()
specifier|public
name|boolean
name|isUpgradeFinalized
parameter_list|()
block|{
return|return
name|isUpgradeFinalized
return|;
block|}
comment|/**    * @return the image files that have the most recent associated     * transaction IDs.  If there are multiple storage directories which     * contain equal images, we'll return them all.    *     * @throws FileNotFoundException if not images are found.    */
annotation|@
name|Override
DECL|method|getLatestImages ()
name|List
argument_list|<
name|FSImageFile
argument_list|>
name|getLatestImages
parameter_list|()
throws|throws
name|IOException
block|{
name|LinkedList
argument_list|<
name|FSImageFile
argument_list|>
name|ret
init|=
operator|new
name|LinkedList
argument_list|<
name|FSImageFile
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FSImageFile
name|img
range|:
name|foundImages
control|)
block|{
if|if
condition|(
name|ret
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|img
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FSImageFile
name|cur
init|=
name|ret
operator|.
name|getFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|cur
operator|.
name|txId
operator|==
name|img
operator|.
name|txId
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|img
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cur
operator|.
name|txId
operator|<
name|img
operator|.
name|txId
condition|)
block|{
name|ret
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|img
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|ret
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"No valid image files found"
argument_list|)
throw|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|getFoundImages ()
specifier|public
name|List
argument_list|<
name|FSImageFile
argument_list|>
name|getFoundImages
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|foundImages
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|needToSave ()
specifier|public
name|boolean
name|needToSave
parameter_list|()
block|{
return|return
name|needToSave
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxSeenTxId ()
name|long
name|getMaxSeenTxId
parameter_list|()
block|{
return|return
name|maxSeenTxId
return|;
block|}
block|}
end_class

end_unit

