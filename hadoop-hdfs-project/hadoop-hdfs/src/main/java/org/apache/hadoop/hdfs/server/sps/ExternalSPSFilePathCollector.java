begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.hdfs.server.sps
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
name|sps
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|DFSConfigKeys
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
name|DFSUtilClient
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
name|protocol
operator|.
name|DirectoryListing
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
name|HdfsFileStatus
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
name|sps
operator|.
name|FileCollector
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
name|sps
operator|.
name|ItemInfo
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
name|sps
operator|.
name|SPSService
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

begin_comment
comment|/**  * This class is to scan the paths recursively. If file is directory, then it  * will scan for files recursively. If the file is non directory, then it will  * just submit the same file to process. This will use file string path  * representation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ExternalSPSFilePathCollector
specifier|public
class|class
name|ExternalSPSFilePathCollector
implements|implements
name|FileCollector
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
name|ExternalSPSFilePathCollector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|service
specifier|private
name|SPSService
name|service
decl_stmt|;
DECL|field|maxQueueLimitToScan
specifier|private
name|int
name|maxQueueLimitToScan
decl_stmt|;
DECL|method|ExternalSPSFilePathCollector (SPSService service)
specifier|public
name|ExternalSPSFilePathCollector
parameter_list|(
name|SPSService
name|service
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|maxQueueLimitToScan
operator|=
name|service
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_QUEUE_LIMIT_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_QUEUE_LIMIT_DEFAULT
argument_list|)
expr_stmt|;
try|try
block|{
comment|// TODO: probably we could get this dfs from external context? but this is
comment|// too specific to external.
name|dfs
operator|=
name|getFS
argument_list|(
name|service
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to get the filesystem. Make sure Namenode running and "
operator|+
literal|"configured namenode address is correct."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFS (Configuration conf)
specifier|private
name|DistributedFileSystem
name|getFS
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Recursively scan the given path and add the file info to SPS service for    * processing.    */
DECL|method|processPath (Long startID, String childPath)
specifier|private
name|long
name|processPath
parameter_list|(
name|Long
name|startID
parameter_list|,
name|String
name|childPath
parameter_list|)
block|{
name|long
name|pendingWorkCount
init|=
literal|0
decl_stmt|;
comment|// to be satisfied file counter
for|for
control|(
name|byte
index|[]
name|lastReturnedName
init|=
name|HdfsFileStatus
operator|.
name|EMPTY_NAME
init|;
condition|;
control|)
block|{
specifier|final
name|DirectoryListing
name|children
decl_stmt|;
try|try
block|{
name|children
operator|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|listPaths
argument_list|(
name|childPath
argument_list|,
name|lastReturnedName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to list directory "
operator|+
name|childPath
operator|+
literal|". Ignore the directory and continue."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|pendingWorkCount
return|;
block|}
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"The scanning start dir/sub dir "
operator|+
name|childPath
operator|+
literal|" does not have childrens."
argument_list|)
expr_stmt|;
block|}
return|return
name|pendingWorkCount
return|;
block|}
for|for
control|(
name|HdfsFileStatus
name|child
range|:
name|children
operator|.
name|getPartialListing
argument_list|()
control|)
block|{
if|if
condition|(
name|child
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|service
operator|.
name|addFileToProcess
argument_list|(
operator|new
name|ItemInfo
argument_list|(
name|startID
argument_list|,
name|child
operator|.
name|getFileId
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkProcessingQueuesFree
argument_list|()
expr_stmt|;
name|pendingWorkCount
operator|++
expr_stmt|;
comment|// increment to be satisfied file count
block|}
else|else
block|{
name|String
name|childFullPathName
init|=
name|child
operator|.
name|getFullName
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|childFullPathName
operator|.
name|endsWith
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
condition|)
block|{
name|childFullPathName
operator|=
name|childFullPathName
operator|+
name|Path
operator|.
name|SEPARATOR
expr_stmt|;
block|}
name|pendingWorkCount
operator|+=
name|processPath
argument_list|(
name|startID
argument_list|,
name|childFullPathName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|children
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|lastReturnedName
operator|=
name|children
operator|.
name|getLastName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
name|pendingWorkCount
return|;
block|}
block|}
block|}
DECL|method|checkProcessingQueuesFree ()
specifier|private
name|void
name|checkProcessingQueuesFree
parameter_list|()
block|{
name|int
name|remainingCapacity
init|=
name|remainingCapacity
argument_list|()
decl_stmt|;
comment|// wait for queue to be free
while|while
condition|(
name|remainingCapacity
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Waiting for storageMovementNeeded queue to be free!"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|remainingCapacity
operator|=
name|remainingCapacity
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns queue remaining capacity.    */
DECL|method|remainingCapacity ()
specifier|public
name|int
name|remainingCapacity
parameter_list|()
block|{
name|int
name|size
init|=
name|service
operator|.
name|processingQueueSize
argument_list|()
decl_stmt|;
name|int
name|remainingSize
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|size
operator|<
name|maxQueueLimitToScan
condition|)
block|{
name|remainingSize
operator|=
name|maxQueueLimitToScan
operator|-
name|size
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SPS processing Q -> maximum capacity:{}, current size:{},"
operator|+
literal|" remaining size:{}"
argument_list|,
name|maxQueueLimitToScan
argument_list|,
name|size
argument_list|,
name|remainingSize
argument_list|)
expr_stmt|;
block|}
return|return
name|remainingSize
return|;
block|}
annotation|@
name|Override
DECL|method|scanAndCollectFiles (long pathId)
specifier|public
name|void
name|scanAndCollectFiles
parameter_list|(
name|long
name|pathId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dfs
operator|==
literal|null
condition|)
block|{
name|dfs
operator|=
name|getFS
argument_list|(
name|service
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Path
name|filePath
init|=
name|DFSUtilClient
operator|.
name|makePathFromFileId
argument_list|(
name|pathId
argument_list|)
decl_stmt|;
name|long
name|pendingSatisfyItemsCount
init|=
name|processPath
argument_list|(
name|pathId
argument_list|,
name|filePath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// Check whether the given path contains any item to be tracked
comment|// or the no to be satisfied paths. In case of empty list, add the given
comment|// inodeId to the 'pendingWorkForDirectory' with empty list so that later
comment|// SPSPathIdProcessor#run function will remove the SPS hint considering that
comment|// this path is already satisfied the storage policy.
if|if
condition|(
name|pendingSatisfyItemsCount
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"There is no pending items to satisfy the given path "
operator|+
literal|"inodeId:{}"
argument_list|,
name|pathId
argument_list|)
expr_stmt|;
name|service
operator|.
name|addAllFilesToProcess
argument_list|(
name|pathId
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|service
operator|.
name|markScanCompletedForPath
argument_list|(
name|pathId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

