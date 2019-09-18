begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|tasks
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
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
name|lang3
operator|.
name|tuple
operator|.
name|ImmutablePair
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
name|lang3
operator|.
name|tuple
operator|.
name|Pair
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
name|ozone
operator|.
name|om
operator|.
name|OMMetadataManager
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyInfo
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
name|hdds
operator|.
name|utils
operator|.
name|db
operator|.
name|Table
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
name|hdds
operator|.
name|utils
operator|.
name|db
operator|.
name|TableIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|schema
operator|.
name|tables
operator|.
name|daos
operator|.
name|FileCountBySizeDao
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|schema
operator|.
name|tables
operator|.
name|pojos
operator|.
name|FileCountBySize
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|Configuration
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
name|Collection
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
name|Iterator
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OmMetadataManagerImpl
operator|.
name|KEY_TABLE
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
name|ozone
operator|.
name|recon
operator|.
name|tasks
operator|.
name|OMDBUpdateEvent
operator|.
name|OMDBUpdateAction
operator|.
name|DELETE
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
name|ozone
operator|.
name|recon
operator|.
name|tasks
operator|.
name|OMDBUpdateEvent
operator|.
name|OMDBUpdateAction
operator|.
name|PUT
import|;
end_import

begin_comment
comment|/**  * Class to iterate over the OM DB and store the counts of existing/new  * files binned into ranges (1KB, 2Kb..,4MB,.., 1TB,..1PB) to the Recon  * fileSize DB.  */
end_comment

begin_class
DECL|class|FileSizeCountTask
specifier|public
class|class
name|FileSizeCountTask
implements|implements
name|ReconDBUpdateTask
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
name|FileSizeCountTask
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxBinSize
specifier|private
name|int
name|maxBinSize
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxFileSizeUpperBound
specifier|private
name|long
name|maxFileSizeUpperBound
init|=
literal|1125899906842624L
decl_stmt|;
comment|// 1 PB
DECL|field|upperBoundCount
specifier|private
name|long
index|[]
name|upperBoundCount
decl_stmt|;
DECL|field|oneKb
specifier|private
name|long
name|oneKb
init|=
literal|1024L
decl_stmt|;
DECL|field|fileCountBySizeDao
specifier|private
name|FileCountBySizeDao
name|fileCountBySizeDao
decl_stmt|;
annotation|@
name|Inject
DECL|method|FileSizeCountTask (Configuration sqlConfiguration)
specifier|public
name|FileSizeCountTask
parameter_list|(
name|Configuration
name|sqlConfiguration
parameter_list|)
block|{
name|fileCountBySizeDao
operator|=
operator|new
name|FileCountBySizeDao
argument_list|(
name|sqlConfiguration
argument_list|)
expr_stmt|;
name|upperBoundCount
operator|=
operator|new
name|long
index|[
name|getMaxBinSize
argument_list|()
index|]
expr_stmt|;
block|}
DECL|method|getOneKB ()
name|long
name|getOneKB
parameter_list|()
block|{
return|return
name|oneKb
return|;
block|}
DECL|method|getMaxFileSizeUpperBound ()
name|long
name|getMaxFileSizeUpperBound
parameter_list|()
block|{
return|return
name|maxFileSizeUpperBound
return|;
block|}
DECL|method|getMaxBinSize ()
name|int
name|getMaxBinSize
parameter_list|()
block|{
if|if
condition|(
name|maxBinSize
operator|==
operator|-
literal|1
condition|)
block|{
comment|// extra bin to add files> 1PB.
comment|// 1 KB (2 ^ 10) is the smallest tracked file.
name|maxBinSize
operator|=
name|nextClosestPowerIndexOfTwo
argument_list|(
name|maxFileSizeUpperBound
argument_list|)
operator|-
literal|10
operator|+
literal|1
expr_stmt|;
block|}
return|return
name|maxBinSize
return|;
block|}
comment|/**    * Read the Keys from OM snapshot DB and calculate the upper bound of    * File Size it belongs to.    *    * @param omMetadataManager OM Metadata instance.    * @return Pair    */
annotation|@
name|Override
DECL|method|reprocess (OMMetadataManager omMetadataManager)
specifier|public
name|Pair
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|reprocess
parameter_list|(
name|OMMetadataManager
name|omMetadataManager
parameter_list|)
block|{
name|Table
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|omKeyInfoTable
init|=
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
decl_stmt|;
try|try
init|(
name|TableIterator
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|Table
operator|.
name|KeyValue
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
argument_list|>
name|keyIter
init|=
name|omKeyInfoTable
operator|.
name|iterator
argument_list|()
init|)
block|{
while|while
condition|(
name|keyIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Table
operator|.
name|KeyValue
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|kv
init|=
name|keyIter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// reprocess() is a PUT operation on the DB.
name|updateUpperBoundCount
argument_list|(
name|kv
operator|.
name|getValue
argument_list|()
argument_list|,
name|PUT
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioEx
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to populate File Size Count in Recon DB. "
argument_list|,
name|ioEx
argument_list|)
expr_stmt|;
return|return
operator|new
name|ImmutablePair
argument_list|<>
argument_list|(
name|getTaskName
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
name|populateFileCountBySizeDB
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Completed a 'reprocess' run of FileSizeCountTask."
argument_list|)
expr_stmt|;
return|return
operator|new
name|ImmutablePair
argument_list|<>
argument_list|(
name|getTaskName
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskName ()
specifier|public
name|String
name|getTaskName
parameter_list|()
block|{
return|return
literal|"FileSizeCountTask"
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskTables ()
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getTaskTables
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|KEY_TABLE
argument_list|)
return|;
block|}
DECL|method|updateCountFromDB ()
specifier|private
name|void
name|updateCountFromDB
parameter_list|()
block|{
comment|// Read - Write operations to DB are in ascending order
comment|// of file size upper bounds.
name|List
argument_list|<
name|FileCountBySize
argument_list|>
name|resultSet
init|=
name|fileCountBySizeDao
operator|.
name|findAll
argument_list|()
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|resultSet
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|FileCountBySize
name|row
range|:
name|resultSet
control|)
block|{
name|upperBoundCount
index|[
name|index
index|]
operator|=
name|row
operator|.
name|getCount
argument_list|()
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Read the Keys from update events and update the count of files    * pertaining to a certain upper bound.    *    * @param events Update events - PUT/DELETE.    * @return Pair    */
annotation|@
name|Override
DECL|method|process (OMUpdateEventBatch events)
specifier|public
name|Pair
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|process
parameter_list|(
name|OMUpdateEventBatch
name|events
parameter_list|)
block|{
name|Iterator
argument_list|<
name|OMDBUpdateEvent
argument_list|>
name|eventIterator
init|=
name|events
operator|.
name|getIterator
argument_list|()
decl_stmt|;
comment|//update array with file size count from DB
name|updateCountFromDB
argument_list|()
expr_stmt|;
while|while
condition|(
name|eventIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|OMDBUpdateEvent
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|omdbUpdateEvent
init|=
name|eventIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|updatedKey
init|=
name|omdbUpdateEvent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|OmKeyInfo
name|omKeyInfo
init|=
name|omdbUpdateEvent
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|omdbUpdateEvent
operator|.
name|getAction
argument_list|()
condition|)
block|{
case|case
name|PUT
case|:
name|updateUpperBoundCount
argument_list|(
name|omKeyInfo
argument_list|,
name|PUT
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|updateUpperBoundCount
argument_list|(
name|omKeyInfo
argument_list|,
name|DELETE
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|trace
argument_list|(
literal|"Skipping DB update event : "
operator|+
name|omdbUpdateEvent
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
literal|"Unexpected exception while updating key data : {} {}"
argument_list|,
name|updatedKey
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|ImmutablePair
argument_list|<>
argument_list|(
name|getTaskName
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
name|populateFileCountBySizeDB
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Completed a 'process' run of FileSizeCountTask."
argument_list|)
expr_stmt|;
return|return
operator|new
name|ImmutablePair
argument_list|<>
argument_list|(
name|getTaskName
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Calculate the bin index based on size of the Key.    * index is calculated as the number of right shifts    * needed until dataSize becomes zero.    *    * @param dataSize Size of the key.    * @return int bin index in upperBoundCount    */
DECL|method|calculateBinIndex (long dataSize)
specifier|public
name|int
name|calculateBinIndex
parameter_list|(
name|long
name|dataSize
parameter_list|)
block|{
if|if
condition|(
name|dataSize
operator|>=
name|getMaxFileSizeUpperBound
argument_list|()
condition|)
block|{
return|return
name|getMaxBinSize
argument_list|()
operator|-
literal|1
return|;
block|}
name|int
name|index
init|=
name|nextClosestPowerIndexOfTwo
argument_list|(
name|dataSize
argument_list|)
decl_stmt|;
comment|// The smallest file size being tracked for count
comment|// is 1 KB i.e. 1024 = 2 ^ 10.
return|return
name|index
operator|<
literal|10
condition|?
literal|0
else|:
name|index
operator|-
literal|10
return|;
block|}
DECL|method|nextClosestPowerIndexOfTwo (long dataSize)
name|int
name|nextClosestPowerIndexOfTwo
parameter_list|(
name|long
name|dataSize
parameter_list|)
block|{
name|int
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|dataSize
operator|!=
literal|0
condition|)
block|{
name|dataSize
operator|>>=
literal|1
expr_stmt|;
name|index
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
comment|/**    * Populate DB with the counts of file sizes calculated    * using the dao.    *    */
DECL|method|populateFileCountBySizeDB ()
name|void
name|populateFileCountBySizeDB
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|upperBoundCount
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
name|fileSizeUpperBound
init|=
operator|(
name|i
operator|==
name|upperBoundCount
operator|.
name|length
operator|-
literal|1
operator|)
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
operator|(
name|long
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
operator|(
literal|10
operator|+
name|i
operator|)
argument_list|)
decl_stmt|;
name|FileCountBySize
name|fileCountRecord
init|=
name|fileCountBySizeDao
operator|.
name|findById
argument_list|(
name|fileSizeUpperBound
argument_list|)
decl_stmt|;
name|FileCountBySize
name|newRecord
init|=
operator|new
name|FileCountBySize
argument_list|(
name|fileSizeUpperBound
argument_list|,
name|upperBoundCount
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileCountRecord
operator|==
literal|null
condition|)
block|{
name|fileCountBySizeDao
operator|.
name|insert
argument_list|(
name|newRecord
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fileCountBySizeDao
operator|.
name|update
argument_list|(
name|newRecord
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Calculate and update the count of files being tracked by    * upperBoundCount[].    * Used by reprocess() and process().    *    * @param omKeyInfo OmKey being updated for count    * @param operation (PUT, DELETE)    */
DECL|method|updateUpperBoundCount (OmKeyInfo omKeyInfo, OMDBUpdateEvent.OMDBUpdateAction operation)
name|void
name|updateUpperBoundCount
parameter_list|(
name|OmKeyInfo
name|omKeyInfo
parameter_list|,
name|OMDBUpdateEvent
operator|.
name|OMDBUpdateAction
name|operation
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|binIndex
init|=
name|calculateBinIndex
argument_list|(
name|omKeyInfo
operator|.
name|getDataSize
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|operation
operator|==
name|PUT
condition|)
block|{
name|upperBoundCount
index|[
name|binIndex
index|]
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|DELETE
condition|)
block|{
if|if
condition|(
name|upperBoundCount
index|[
name|binIndex
index|]
operator|!=
literal|0
condition|)
block|{
comment|//decrement only if it had files before, default DB value is 0
name|upperBoundCount
index|[
name|binIndex
index|]
operator|--
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected error while updating bin count. Found 0 count "
operator|+
literal|"for index : "
operator|+
name|binIndex
operator|+
literal|" while processing DELETE event for "
operator|+
name|omKeyInfo
operator|.
name|getKeyName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

