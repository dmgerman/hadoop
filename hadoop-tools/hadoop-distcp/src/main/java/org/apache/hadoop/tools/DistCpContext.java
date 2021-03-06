begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

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
name|tools
operator|.
name|DistCpOptions
operator|.
name|FileAttribute
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
name|Set
import|;
end_import

begin_comment
comment|/**  * This is the context of the distcp at runtime.  *  * It has the immutable {@link DistCpOptions} and mutable runtime status.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DistCpContext
specifier|public
class|class
name|DistCpContext
block|{
DECL|field|options
specifier|private
specifier|final
name|DistCpOptions
name|options
decl_stmt|;
comment|/** The source paths can be set at runtime via snapshots. */
DECL|field|sourcePaths
specifier|private
name|List
argument_list|<
name|Path
argument_list|>
name|sourcePaths
decl_stmt|;
comment|/** This is a derived field, it's initialized in the beginning of distcp. */
DECL|field|targetPathExists
specifier|private
name|boolean
name|targetPathExists
init|=
literal|true
decl_stmt|;
comment|/** Indicate that raw.* xattrs should be preserved if true. */
DECL|field|preserveRawXattrs
specifier|private
name|boolean
name|preserveRawXattrs
init|=
literal|false
decl_stmt|;
DECL|method|DistCpContext (DistCpOptions options)
specifier|public
name|DistCpContext
parameter_list|(
name|DistCpOptions
name|options
parameter_list|)
block|{
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
name|this
operator|.
name|sourcePaths
operator|=
name|options
operator|.
name|getSourcePaths
argument_list|()
expr_stmt|;
block|}
DECL|method|setSourcePaths (List<Path> sourcePaths)
specifier|public
name|void
name|setSourcePaths
parameter_list|(
name|List
argument_list|<
name|Path
argument_list|>
name|sourcePaths
parameter_list|)
block|{
name|this
operator|.
name|sourcePaths
operator|=
name|sourcePaths
expr_stmt|;
block|}
comment|/**    * @return the sourcePaths. Please note this method does not directly delegate    * to the {@link #options}.    */
DECL|method|getSourcePaths ()
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|getSourcePaths
parameter_list|()
block|{
return|return
name|sourcePaths
return|;
block|}
DECL|method|getSourceFileListing ()
specifier|public
name|Path
name|getSourceFileListing
parameter_list|()
block|{
return|return
name|options
operator|.
name|getSourceFileListing
argument_list|()
return|;
block|}
DECL|method|getTargetPath ()
specifier|public
name|Path
name|getTargetPath
parameter_list|()
block|{
return|return
name|options
operator|.
name|getTargetPath
argument_list|()
return|;
block|}
DECL|method|shouldAtomicCommit ()
specifier|public
name|boolean
name|shouldAtomicCommit
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldAtomicCommit
argument_list|()
return|;
block|}
DECL|method|shouldSyncFolder ()
specifier|public
name|boolean
name|shouldSyncFolder
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldSyncFolder
argument_list|()
return|;
block|}
DECL|method|shouldDeleteMissing ()
specifier|public
name|boolean
name|shouldDeleteMissing
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldDeleteMissing
argument_list|()
return|;
block|}
DECL|method|shouldIgnoreFailures ()
specifier|public
name|boolean
name|shouldIgnoreFailures
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldIgnoreFailures
argument_list|()
return|;
block|}
DECL|method|shouldOverwrite ()
specifier|public
name|boolean
name|shouldOverwrite
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldOverwrite
argument_list|()
return|;
block|}
DECL|method|shouldAppend ()
specifier|public
name|boolean
name|shouldAppend
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldAppend
argument_list|()
return|;
block|}
DECL|method|shouldSkipCRC ()
specifier|public
name|boolean
name|shouldSkipCRC
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldSkipCRC
argument_list|()
return|;
block|}
DECL|method|shouldBlock ()
specifier|public
name|boolean
name|shouldBlock
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldBlock
argument_list|()
return|;
block|}
DECL|method|shouldUseDiff ()
specifier|public
name|boolean
name|shouldUseDiff
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldUseDiff
argument_list|()
return|;
block|}
DECL|method|shouldUseRdiff ()
specifier|public
name|boolean
name|shouldUseRdiff
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldUseRdiff
argument_list|()
return|;
block|}
DECL|method|shouldUseSnapshotDiff ()
specifier|public
name|boolean
name|shouldUseSnapshotDiff
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldUseSnapshotDiff
argument_list|()
return|;
block|}
DECL|method|getFromSnapshot ()
specifier|public
name|String
name|getFromSnapshot
parameter_list|()
block|{
return|return
name|options
operator|.
name|getFromSnapshot
argument_list|()
return|;
block|}
DECL|method|getToSnapshot ()
specifier|public
name|String
name|getToSnapshot
parameter_list|()
block|{
return|return
name|options
operator|.
name|getToSnapshot
argument_list|()
return|;
block|}
DECL|method|getFiltersFile ()
specifier|public
specifier|final
name|String
name|getFiltersFile
parameter_list|()
block|{
return|return
name|options
operator|.
name|getFiltersFile
argument_list|()
return|;
block|}
DECL|method|getNumListstatusThreads ()
specifier|public
name|int
name|getNumListstatusThreads
parameter_list|()
block|{
return|return
name|options
operator|.
name|getNumListstatusThreads
argument_list|()
return|;
block|}
DECL|method|getMaxMaps ()
specifier|public
name|int
name|getMaxMaps
parameter_list|()
block|{
return|return
name|options
operator|.
name|getMaxMaps
argument_list|()
return|;
block|}
DECL|method|getMapBandwidth ()
specifier|public
name|float
name|getMapBandwidth
parameter_list|()
block|{
return|return
name|options
operator|.
name|getMapBandwidth
argument_list|()
return|;
block|}
DECL|method|getPreserveAttributes ()
specifier|public
name|Set
argument_list|<
name|FileAttribute
argument_list|>
name|getPreserveAttributes
parameter_list|()
block|{
return|return
name|options
operator|.
name|getPreserveAttributes
argument_list|()
return|;
block|}
DECL|method|shouldPreserve (FileAttribute attribute)
specifier|public
name|boolean
name|shouldPreserve
parameter_list|(
name|FileAttribute
name|attribute
parameter_list|)
block|{
return|return
name|options
operator|.
name|shouldPreserve
argument_list|(
name|attribute
argument_list|)
return|;
block|}
DECL|method|shouldPreserveRawXattrs ()
specifier|public
name|boolean
name|shouldPreserveRawXattrs
parameter_list|()
block|{
return|return
name|preserveRawXattrs
return|;
block|}
DECL|method|setPreserveRawXattrs (boolean preserveRawXattrs)
specifier|public
name|void
name|setPreserveRawXattrs
parameter_list|(
name|boolean
name|preserveRawXattrs
parameter_list|)
block|{
name|this
operator|.
name|preserveRawXattrs
operator|=
name|preserveRawXattrs
expr_stmt|;
block|}
DECL|method|getAtomicWorkPath ()
specifier|public
name|Path
name|getAtomicWorkPath
parameter_list|()
block|{
return|return
name|options
operator|.
name|getAtomicWorkPath
argument_list|()
return|;
block|}
DECL|method|getLogPath ()
specifier|public
name|Path
name|getLogPath
parameter_list|()
block|{
return|return
name|options
operator|.
name|getLogPath
argument_list|()
return|;
block|}
DECL|method|getCopyStrategy ()
specifier|public
name|String
name|getCopyStrategy
parameter_list|()
block|{
return|return
name|options
operator|.
name|getCopyStrategy
argument_list|()
return|;
block|}
DECL|method|getBlocksPerChunk ()
specifier|public
name|int
name|getBlocksPerChunk
parameter_list|()
block|{
return|return
name|options
operator|.
name|getBlocksPerChunk
argument_list|()
return|;
block|}
DECL|method|splitLargeFile ()
specifier|public
specifier|final
name|boolean
name|splitLargeFile
parameter_list|()
block|{
return|return
name|options
operator|.
name|getBlocksPerChunk
argument_list|()
operator|>
literal|0
return|;
block|}
DECL|method|getCopyBufferSize ()
specifier|public
name|int
name|getCopyBufferSize
parameter_list|()
block|{
return|return
name|options
operator|.
name|getCopyBufferSize
argument_list|()
return|;
block|}
DECL|method|shouldDirectWrite ()
specifier|public
name|boolean
name|shouldDirectWrite
parameter_list|()
block|{
return|return
name|options
operator|.
name|shouldDirectWrite
argument_list|()
return|;
block|}
DECL|method|setTargetPathExists (boolean targetPathExists)
specifier|public
name|void
name|setTargetPathExists
parameter_list|(
name|boolean
name|targetPathExists
parameter_list|)
block|{
name|this
operator|.
name|targetPathExists
operator|=
name|targetPathExists
expr_stmt|;
block|}
DECL|method|isTargetPathExists ()
specifier|public
name|boolean
name|isTargetPathExists
parameter_list|()
block|{
return|return
name|targetPathExists
return|;
block|}
DECL|method|appendToConf (Configuration conf)
specifier|public
name|void
name|appendToConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|options
operator|.
name|appendToConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
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
name|options
operator|.
name|toString
argument_list|()
operator|+
literal|", sourcePaths="
operator|+
name|sourcePaths
operator|+
literal|", targetPathExists="
operator|+
name|targetPathExists
operator|+
literal|", preserveRawXattrs"
operator|+
name|preserveRawXattrs
return|;
block|}
block|}
end_class

end_unit

