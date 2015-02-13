begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SCANNER_VOLUME_BYTES_PER_SECOND
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SCANNER_VOLUME_BYTES_PER_SECOND_DEFAULT
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SCAN_PERIOD_HOURS_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SCAN_PERIOD_HOURS_DEFAULT
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|TimeUnit
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
name|ExtendedBlock
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
name|datanode
operator|.
name|VolumeScanner
operator|.
name|ScanResultHandler
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
name|base
operator|.
name|Preconditions
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
name|util
operator|.
name|concurrent
operator|.
name|Uninterruptibles
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeReference
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
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|io
operator|.
name|IOUtils
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockScanner
specifier|public
class|class
name|BlockScanner
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
name|BlockScanner
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The DataNode that this scanner is associated with.    */
DECL|field|datanode
specifier|private
specifier|final
name|DataNode
name|datanode
decl_stmt|;
comment|/**    * Maps Storage IDs to VolumeScanner objects.    */
DECL|field|scanners
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|VolumeScanner
argument_list|>
name|scanners
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|VolumeScanner
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * The scanner configuration.    */
DECL|field|conf
specifier|private
specifier|final
name|Conf
name|conf
decl_stmt|;
comment|/**    * The cached scanner configuration.    */
DECL|class|Conf
specifier|static
class|class
name|Conf
block|{
comment|// These are a few internal configuration keys used for unit tests.
comment|// They can't be set unless the static boolean allowUnitTestSettings has
comment|// been set to true.
annotation|@
name|VisibleForTesting
DECL|field|INTERNAL_DFS_DATANODE_SCAN_PERIOD_MS
specifier|static
specifier|final
name|String
name|INTERNAL_DFS_DATANODE_SCAN_PERIOD_MS
init|=
literal|"internal.dfs.datanode.scan.period.ms.key"
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER
specifier|static
specifier|final
name|String
name|INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER
init|=
literal|"internal.volume.scanner.scan.result.handler"
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|INTERNAL_DFS_BLOCK_SCANNER_MAX_STALENESS_MS
specifier|static
specifier|final
name|String
name|INTERNAL_DFS_BLOCK_SCANNER_MAX_STALENESS_MS
init|=
literal|"internal.dfs.block.scanner.max_staleness.ms"
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|INTERNAL_DFS_BLOCK_SCANNER_MAX_STALENESS_MS_DEFAULT
specifier|static
specifier|final
name|long
name|INTERNAL_DFS_BLOCK_SCANNER_MAX_STALENESS_MS_DEFAULT
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS
specifier|static
specifier|final
name|String
name|INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS
init|=
literal|"dfs.block.scanner.cursor.save.interval.ms"
decl_stmt|;
annotation|@
name|VisibleForTesting
specifier|static
specifier|final
name|long
DECL|field|INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS_DEFAULT
name|INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS_DEFAULT
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
decl_stmt|;
DECL|field|allowUnitTestSettings
specifier|static
name|boolean
name|allowUnitTestSettings
init|=
literal|false
decl_stmt|;
DECL|field|targetBytesPerSec
specifier|final
name|long
name|targetBytesPerSec
decl_stmt|;
DECL|field|maxStalenessMs
specifier|final
name|long
name|maxStalenessMs
decl_stmt|;
DECL|field|scanPeriodMs
specifier|final
name|long
name|scanPeriodMs
decl_stmt|;
DECL|field|cursorSaveMs
specifier|final
name|long
name|cursorSaveMs
decl_stmt|;
DECL|field|resultHandler
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ScanResultHandler
argument_list|>
name|resultHandler
decl_stmt|;
DECL|method|getUnitTestLong (Configuration conf, String key, long defVal)
specifier|private
specifier|static
name|long
name|getUnitTestLong
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|long
name|defVal
parameter_list|)
block|{
if|if
condition|(
name|allowUnitTestSettings
condition|)
block|{
return|return
name|conf
operator|.
name|getLong
argument_list|(
name|key
argument_list|,
name|defVal
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|defVal
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|Conf (Configuration conf)
name|Conf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|targetBytesPerSec
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0L
argument_list|,
name|conf
operator|.
name|getLong
argument_list|(
name|DFS_BLOCK_SCANNER_VOLUME_BYTES_PER_SECOND
argument_list|,
name|DFS_BLOCK_SCANNER_VOLUME_BYTES_PER_SECOND_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxStalenessMs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0L
argument_list|,
name|getUnitTestLong
argument_list|(
name|conf
argument_list|,
name|INTERNAL_DFS_BLOCK_SCANNER_MAX_STALENESS_MS
argument_list|,
name|INTERNAL_DFS_BLOCK_SCANNER_MAX_STALENESS_MS_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|scanPeriodMs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0L
argument_list|,
name|getUnitTestLong
argument_list|(
name|conf
argument_list|,
name|INTERNAL_DFS_DATANODE_SCAN_PERIOD_MS
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|conf
operator|.
name|getLong
argument_list|(
name|DFS_DATANODE_SCAN_PERIOD_HOURS_KEY
argument_list|,
name|DFS_DATANODE_SCAN_PERIOD_HOURS_DEFAULT
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|cursorSaveMs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0L
argument_list|,
name|getUnitTestLong
argument_list|(
name|conf
argument_list|,
name|INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS
argument_list|,
name|INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|allowUnitTestSettings
condition|)
block|{
name|this
operator|.
name|resultHandler
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|ScanResultHandler
argument_list|>
operator|)
name|conf
operator|.
name|getClass
argument_list|(
name|INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER
argument_list|,
name|ScanResultHandler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|resultHandler
operator|=
name|ScanResultHandler
operator|.
name|class
expr_stmt|;
block|}
block|}
block|}
DECL|method|BlockScanner (DataNode datanode, Configuration conf)
specifier|public
name|BlockScanner
parameter_list|(
name|DataNode
name|datanode
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
name|this
operator|.
name|conf
operator|=
operator|new
name|Conf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|isEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialized block scanner with targetBytesPerSec {}"
argument_list|,
name|this
operator|.
name|conf
operator|.
name|targetBytesPerSec
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Disabled block scanner."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns true if the block scanner is enabled.<p/>    *    * If the block scanner is disabled, no volume scanners will be created, and    * no threads will start.    */
DECL|method|isEnabled ()
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
operator|(
name|conf
operator|.
name|scanPeriodMs
operator|)
operator|>
literal|0
operator|&&
operator|(
name|conf
operator|.
name|targetBytesPerSec
operator|>
literal|0
operator|)
return|;
block|}
comment|/**   * Set up a scanner for the given block pool and volume.   *   * @param ref              A reference to the volume.   */
DECL|method|addVolumeScanner (FsVolumeReference ref)
specifier|public
specifier|synchronized
name|void
name|addVolumeScanner
parameter_list|(
name|FsVolumeReference
name|ref
parameter_list|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FsVolumeSpi
name|volume
init|=
name|ref
operator|.
name|getVolume
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Not adding volume scanner for {}, because the block "
operator|+
literal|"scanner is disabled."
argument_list|,
name|volume
operator|.
name|getBasePath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|VolumeScanner
name|scanner
init|=
name|scanners
operator|.
name|get
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scanner
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Already have a scanner for volume {}."
argument_list|,
name|volume
operator|.
name|getBasePath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding scanner for volume {} (StorageID {})"
argument_list|,
name|volume
operator|.
name|getBasePath
argument_list|()
argument_list|,
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
name|scanner
operator|=
operator|new
name|VolumeScanner
argument_list|(
name|conf
argument_list|,
name|datanode
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|scanner
operator|.
name|start
argument_list|()
expr_stmt|;
name|scanners
operator|.
name|put
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|scanner
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// If we didn't create a new VolumeScanner object, we don't
comment|// need this reference to the volume.
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Stops and removes a volume scanner.<p/>    *    * This function will block until the volume scanner has stopped.    *    * @param volume           The volume to remove.    */
DECL|method|removeVolumeScanner (FsVolumeSpi volume)
specifier|public
specifier|synchronized
name|void
name|removeVolumeScanner
parameter_list|(
name|FsVolumeSpi
name|volume
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Not removing volume scanner for {}, because the block "
operator|+
literal|"scanner is disabled."
argument_list|,
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|VolumeScanner
name|scanner
init|=
name|scanners
operator|.
name|get
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scanner
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No scanner found to remove for volumeId {}"
argument_list|,
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing scanner for volume {} (StorageID {})"
argument_list|,
name|volume
operator|.
name|getBasePath
argument_list|()
argument_list|,
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
name|scanner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|scanners
operator|.
name|remove
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
name|Uninterruptibles
operator|.
name|joinUninterruptibly
argument_list|(
name|scanner
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
comment|/**    * Stops and removes all volume scanners.<p/>    *    * This function will block until all the volume scanners have stopped.    */
DECL|method|removeAllVolumeScanners ()
specifier|public
specifier|synchronized
name|void
name|removeAllVolumeScanners
parameter_list|()
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|VolumeScanner
argument_list|>
name|entry
range|:
name|scanners
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|VolumeScanner
argument_list|>
name|entry
range|:
name|scanners
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Uninterruptibles
operator|.
name|joinUninterruptibly
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
name|scanners
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Enable scanning a given block pool id.    *    * @param bpid        The block pool id to enable scanning for.    */
DECL|method|enableBlockPoolId (String bpid)
specifier|synchronized
name|void
name|enableBlockPoolId
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
for|for
control|(
name|VolumeScanner
name|scanner
range|:
name|scanners
operator|.
name|values
argument_list|()
control|)
block|{
name|scanner
operator|.
name|enableBlockPoolId
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Disable scanning a given block pool id.    *    * @param bpid        The block pool id to disable scanning for.    */
DECL|method|disableBlockPoolId (String bpid)
specifier|synchronized
name|void
name|disableBlockPoolId
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
for|for
control|(
name|VolumeScanner
name|scanner
range|:
name|scanners
operator|.
name|values
argument_list|()
control|)
block|{
name|scanner
operator|.
name|disableBlockPoolId
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getVolumeStats (String volumeId)
specifier|synchronized
name|VolumeScanner
operator|.
name|Statistics
name|getVolumeStats
parameter_list|(
name|String
name|volumeId
parameter_list|)
block|{
name|VolumeScanner
name|scanner
init|=
name|scanners
operator|.
name|get
argument_list|(
name|volumeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|scanner
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|scanner
operator|.
name|getStatistics
argument_list|()
return|;
block|}
DECL|method|printStats (StringBuilder p)
specifier|synchronized
name|void
name|printStats
parameter_list|(
name|StringBuilder
name|p
parameter_list|)
block|{
comment|// print out all bpids that we're scanning ?
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|VolumeScanner
argument_list|>
name|entry
range|:
name|scanners
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|printStats
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Mark a block as "suspect."    *    * This means that we should try to rescan it soon.  Note that the    * VolumeScanner keeps a list of recently suspicious blocks, which    * it uses to avoid rescanning the same block over and over in a short    * time frame.    *    * @param storageId     The ID of the storage where the block replica    *                      is being stored.    * @param block         The block's ID and block pool id.    */
DECL|method|markSuspectBlock (String storageId, ExtendedBlock block)
specifier|synchronized
name|void
name|markSuspectBlock
parameter_list|(
name|String
name|storageId
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Not scanning suspicious block {} on {}, because the block "
operator|+
literal|"scanner is disabled."
argument_list|,
name|block
argument_list|,
name|storageId
argument_list|)
expr_stmt|;
return|return;
block|}
name|VolumeScanner
name|scanner
init|=
name|scanners
operator|.
name|get
argument_list|(
name|storageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|scanner
operator|==
literal|null
condition|)
block|{
comment|// This could happen if the volume is in the process of being removed.
comment|// The removal process shuts down the VolumeScanner, but the volume
comment|// object stays around as long as there are references to it (which
comment|// should not be that long.)
name|LOG
operator|.
name|info
argument_list|(
literal|"Not scanning suspicious block {} on {}, because there is no "
operator|+
literal|"volume scanner for that storageId."
argument_list|,
name|block
argument_list|,
name|storageId
argument_list|)
expr_stmt|;
return|return;
block|}
name|scanner
operator|.
name|markSuspectBlock
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Servlet
specifier|public
specifier|static
class|class
name|Servlet
extends|extends
name|HttpServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response)
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|DataNode
name|datanode
init|=
operator|(
name|DataNode
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"datanode"
argument_list|)
decl_stmt|;
name|BlockScanner
name|blockScanner
init|=
name|datanode
operator|.
name|getBlockScanner
argument_list|()
decl_stmt|;
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|8
operator|*
literal|1024
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|blockScanner
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Periodic block scanner is not running"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Periodic block scanner is not running. "
operator|+
literal|"Please check the datanode log if this is unexpected."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"Block Scanner Statistics\n\n"
argument_list|)
expr_stmt|;
name|blockScanner
operator|.
name|printStats
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|String
name|resp
init|=
name|buffer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Returned Servlet info {}"
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|response
operator|.
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

