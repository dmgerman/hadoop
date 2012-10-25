begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
operator|.
name|server
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
name|IOException
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
name|HdfsServerConstants
operator|.
name|NodeType
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
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|StorageErrorReporter
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
name|FileJournalManager
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
name|protocol
operator|.
name|NamespaceInfo
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

begin_comment
comment|/**  * A {@link Storage} implementation for the {@link JournalNode}.  *   * The JN has a storage directory for each namespace for which it stores  * metadata. There is only a single directory per JN in the current design.  */
end_comment

begin_class
DECL|class|JNStorage
class|class
name|JNStorage
extends|extends
name|Storage
block|{
DECL|field|fjm
specifier|private
specifier|final
name|FileJournalManager
name|fjm
decl_stmt|;
DECL|field|sd
specifier|private
specifier|final
name|StorageDirectory
name|sd
decl_stmt|;
DECL|field|state
specifier|private
name|StorageState
name|state
decl_stmt|;
DECL|field|CURRENT_DIR_PURGE_REGEXES
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|CURRENT_DIR_PURGE_REGEXES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"edits_\\d+-(\\d+)"
argument_list|)
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"edits_inprogress_(\\d+)(?:\\..*)?"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|PAXOS_DIR_PURGE_REGEXES
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|PAXOS_DIR_PURGE_REGEXES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\d+)"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * @param logDir the path to the directory in which data will be stored    * @param errorReporter a callback to report errors    * @throws IOException     */
DECL|method|JNStorage (File logDir, StorageErrorReporter errorReporter)
specifier|protected
name|JNStorage
parameter_list|(
name|File
name|logDir
parameter_list|,
name|StorageErrorReporter
name|errorReporter
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|NodeType
operator|.
name|JOURNAL_NODE
argument_list|)
expr_stmt|;
name|sd
operator|=
operator|new
name|StorageDirectory
argument_list|(
name|logDir
argument_list|)
expr_stmt|;
name|this
operator|.
name|addStorageDir
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|this
operator|.
name|fjm
operator|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|,
name|errorReporter
argument_list|)
expr_stmt|;
name|analyzeStorage
argument_list|()
expr_stmt|;
block|}
DECL|method|getJournalManager ()
name|FileJournalManager
name|getJournalManager
parameter_list|()
block|{
return|return
name|fjm
return|;
block|}
annotation|@
name|Override
DECL|method|isPreUpgradableLayout (StorageDirectory sd)
specifier|public
name|boolean
name|isPreUpgradableLayout
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Find an edits file spanning the given transaction ID range.    * If no such file exists, an exception is thrown.    */
DECL|method|findFinalizedEditsFile (long startTxId, long endTxId)
name|File
name|findFinalizedEditsFile
parameter_list|(
name|long
name|startTxId
parameter_list|,
name|long
name|endTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|ret
init|=
operator|new
name|File
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
name|startTxId
argument_list|,
name|endTxId
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ret
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No edits file for range "
operator|+
name|startTxId
operator|+
literal|"-"
operator|+
name|endTxId
argument_list|)
throw|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * @return the path for an in-progress edits file starting at the given    * transaction ID. This does not verify existence of the file.     */
DECL|method|getInProgressEditLog (long startTxId)
name|File
name|getInProgressEditLog
parameter_list|(
name|long
name|startTxId
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
name|startTxId
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @param segmentTxId the first txid of the segment    * @param epoch the epoch number of the writer which is coordinating    * recovery    * @return the temporary path in which an edits log should be stored    * while it is being downloaded from a remote JournalNode    */
DECL|method|getSyncLogTemporaryFile (long segmentTxId, long epoch)
name|File
name|getSyncLogTemporaryFile
parameter_list|(
name|long
name|segmentTxId
parameter_list|,
name|long
name|epoch
parameter_list|)
block|{
name|String
name|name
init|=
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
name|segmentTxId
argument_list|)
operator|+
literal|".epoch="
operator|+
name|epoch
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/**    * @return the path for the file which contains persisted data for the    * paxos-like recovery process for the given log segment.    */
DECL|method|getPaxosFile (long segmentTxId)
name|File
name|getPaxosFile
parameter_list|(
name|long
name|segmentTxId
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|getPaxosDir
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|segmentTxId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getPaxosDir ()
name|File
name|getPaxosDir
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
argument_list|,
literal|"paxos"
argument_list|)
return|;
block|}
comment|/**    * Remove any log files and associated paxos files which are older than    * the given txid.    */
DECL|method|purgeDataOlderThan (long minTxIdToKeep)
name|void
name|purgeDataOlderThan
parameter_list|(
name|long
name|minTxIdToKeep
parameter_list|)
throws|throws
name|IOException
block|{
name|purgeMatching
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
argument_list|,
name|CURRENT_DIR_PURGE_REGEXES
argument_list|,
name|minTxIdToKeep
argument_list|)
expr_stmt|;
name|purgeMatching
argument_list|(
name|getPaxosDir
argument_list|()
argument_list|,
name|PAXOS_DIR_PURGE_REGEXES
argument_list|,
name|minTxIdToKeep
argument_list|)
expr_stmt|;
block|}
comment|/**    * Purge files in the given directory which match any of the set of patterns.    * The patterns must have a single numeric capture group which determines    * the associated transaction ID of the file. Only those files for which    * the transaction ID is less than the<code>minTxIdToKeep</code> parameter    * are removed.    */
DECL|method|purgeMatching (File dir, List<Pattern> patterns, long minTxIdToKeep)
specifier|private
specifier|static
name|void
name|purgeMatching
parameter_list|(
name|File
name|dir
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|patterns
parameter_list|,
name|long
name|minTxIdToKeep
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|File
name|f
range|:
name|FileUtil
operator|.
name|listFiles
argument_list|(
name|dir
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|isFile
argument_list|()
condition|)
continue|continue;
for|for
control|(
name|Pattern
name|p
range|:
name|patterns
control|)
block|{
name|Matcher
name|matcher
init|=
name|p
operator|.
name|matcher
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|// This parsing will always succeed since the group(1) is
comment|// /\d+/ in the regex itself.
name|long
name|txid
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|txid
operator|<
name|minTxIdToKeep
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Purging no-longer needed file "
operator|+
name|txid
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to delete no-longer-needed data "
operator|+
name|f
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
block|}
block|}
DECL|method|format (NamespaceInfo nsInfo)
name|void
name|format
parameter_list|(
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|setStorageInfo
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Formatting journal "
operator|+
name|sd
operator|+
literal|" with nsid: "
operator|+
name|getNamespaceID
argument_list|()
argument_list|)
expr_stmt|;
comment|// Unlock the directory before formatting, because we will
comment|// re-analyze it after format(). The analyzeStorage() call
comment|// below is reponsible for re-locking it. This is a no-op
comment|// if the storage is not currently locked.
name|unlockAll
argument_list|()
expr_stmt|;
name|sd
operator|.
name|clearDirectory
argument_list|()
expr_stmt|;
name|writeProperties
argument_list|(
name|sd
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|getPaxosDir
argument_list|()
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not create paxos dir: "
operator|+
name|getPaxosDir
argument_list|()
argument_list|)
throw|;
block|}
name|analyzeStorage
argument_list|()
expr_stmt|;
block|}
DECL|method|analyzeStorage ()
name|void
name|analyzeStorage
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|state
operator|=
name|sd
operator|.
name|analyzeStorage
argument_list|(
name|StartupOption
operator|.
name|REGULAR
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|StorageState
operator|.
name|NORMAL
condition|)
block|{
name|readProperties
argument_list|(
name|sd
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkConsistentNamespace (NamespaceInfo nsInfo)
name|void
name|checkConsistentNamespace
parameter_list|(
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nsInfo
operator|.
name|getNamespaceID
argument_list|()
operator|!=
name|getNamespaceID
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incompatible namespaceID for journal "
operator|+
name|this
operator|.
name|sd
operator|+
literal|": NameNode has nsId "
operator|+
name|nsInfo
operator|.
name|getNamespaceID
argument_list|()
operator|+
literal|" but storage has nsId "
operator|+
name|getNamespaceID
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|nsInfo
operator|.
name|getClusterID
argument_list|()
operator|.
name|equals
argument_list|(
name|getClusterID
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incompatible clusterID for journal "
operator|+
name|this
operator|.
name|sd
operator|+
literal|": NameNode has clusterId '"
operator|+
name|nsInfo
operator|.
name|getClusterID
argument_list|()
operator|+
literal|"' but storage has clusterId '"
operator|+
name|getClusterID
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing journal storage for "
operator|+
name|sd
argument_list|)
expr_stmt|;
name|unlockAll
argument_list|()
expr_stmt|;
block|}
DECL|method|isFormatted ()
specifier|public
name|boolean
name|isFormatted
parameter_list|()
block|{
return|return
name|state
operator|==
name|StorageState
operator|.
name|NORMAL
return|;
block|}
block|}
end_class

end_unit

