begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|LinkedBlockingQueue
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
name|atomic
operator|.
name|AtomicInteger
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
name|mapreduce
operator|.
name|server
operator|.
name|tasktracker
operator|.
name|TTConfig
import|;
end_import

begin_class
DECL|class|IndexCache
class|class
name|IndexCache
block|{
DECL|field|conf
specifier|private
specifier|final
name|JobConf
name|conf
decl_stmt|;
DECL|field|totalMemoryAllowed
specifier|private
specifier|final
name|int
name|totalMemoryAllowed
decl_stmt|;
DECL|field|totalMemoryUsed
specifier|private
name|AtomicInteger
name|totalMemoryUsed
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|IndexCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|IndexInformation
argument_list|>
name|cache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|IndexInformation
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|LinkedBlockingQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|IndexCache (JobConf conf)
specifier|public
name|IndexCache
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|totalMemoryAllowed
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|TTConfig
operator|.
name|TT_INDEX_CACHE
argument_list|,
literal|10
argument_list|)
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"IndexCache created with max memory = "
operator|+
name|totalMemoryAllowed
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method gets the index information for the given mapId and reduce.    * It reads the index file into cache if it is not already present.    * @param mapId    * @param reduce    * @param fileName The file to read the index information from if it is not    *                 already present in the cache    * @param expectedIndexOwner The expected owner of the index file    * @return The Index Information    * @throws IOException    */
DECL|method|getIndexInformation (String mapId, int reduce, Path fileName, String expectedIndexOwner)
specifier|public
name|IndexRecord
name|getIndexInformation
parameter_list|(
name|String
name|mapId
parameter_list|,
name|int
name|reduce
parameter_list|,
name|Path
name|fileName
parameter_list|,
name|String
name|expectedIndexOwner
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInformation
name|info
init|=
name|cache
operator|.
name|get
argument_list|(
name|mapId
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|info
operator|=
name|readIndexFileToCache
argument_list|(
name|fileName
argument_list|,
name|mapId
argument_list|,
name|expectedIndexOwner
argument_list|)
expr_stmt|;
block|}
else|else
block|{
synchronized|synchronized
init|(
name|info
init|)
block|{
while|while
condition|(
name|isUnderConstruction
argument_list|(
name|info
argument_list|)
condition|)
block|{
try|try
block|{
name|info
operator|.
name|wait
argument_list|()
expr_stmt|;
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
literal|"Interrupted waiting for construction"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"IndexCache HIT: MapId "
operator|+
name|mapId
operator|+
literal|" found"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|mapSpillRecord
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
name|info
operator|.
name|mapSpillRecord
operator|.
name|size
argument_list|()
operator|<=
name|reduce
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid request "
operator|+
literal|" Map Id = "
operator|+
name|mapId
operator|+
literal|" Reducer = "
operator|+
name|reduce
operator|+
literal|" Index Info Length = "
operator|+
name|info
operator|.
name|mapSpillRecord
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|info
operator|.
name|mapSpillRecord
operator|.
name|getIndex
argument_list|(
name|reduce
argument_list|)
return|;
block|}
DECL|method|isUnderConstruction (IndexInformation info)
specifier|private
name|boolean
name|isUnderConstruction
parameter_list|(
name|IndexInformation
name|info
parameter_list|)
block|{
synchronized|synchronized
init|(
name|info
init|)
block|{
return|return
operator|(
literal|null
operator|==
name|info
operator|.
name|mapSpillRecord
operator|)
return|;
block|}
block|}
DECL|method|readIndexFileToCache (Path indexFileName, String mapId, String expectedIndexOwner)
specifier|private
name|IndexInformation
name|readIndexFileToCache
parameter_list|(
name|Path
name|indexFileName
parameter_list|,
name|String
name|mapId
parameter_list|,
name|String
name|expectedIndexOwner
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInformation
name|info
decl_stmt|;
name|IndexInformation
name|newInd
init|=
operator|new
name|IndexInformation
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|info
operator|=
name|cache
operator|.
name|putIfAbsent
argument_list|(
name|mapId
argument_list|,
name|newInd
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|info
init|)
block|{
while|while
condition|(
name|isUnderConstruction
argument_list|(
name|info
argument_list|)
condition|)
block|{
try|try
block|{
name|info
operator|.
name|wait
argument_list|()
expr_stmt|;
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
literal|"Interrupted waiting for construction"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"IndexCache HIT: MapId "
operator|+
name|mapId
operator|+
literal|" found"
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"IndexCache MISS: MapId "
operator|+
name|mapId
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
name|SpillRecord
name|tmp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|tmp
operator|=
operator|new
name|SpillRecord
argument_list|(
name|indexFileName
argument_list|,
name|conf
argument_list|,
name|expectedIndexOwner
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|tmp
operator|=
operator|new
name|SpillRecord
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cache
operator|.
name|remove
argument_list|(
name|mapId
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error Reading IndexFile"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|newInd
init|)
block|{
name|newInd
operator|.
name|mapSpillRecord
operator|=
name|tmp
expr_stmt|;
name|newInd
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
name|queue
operator|.
name|add
argument_list|(
name|mapId
argument_list|)
expr_stmt|;
if|if
condition|(
name|totalMemoryUsed
operator|.
name|addAndGet
argument_list|(
name|newInd
operator|.
name|getSize
argument_list|()
argument_list|)
operator|>
name|totalMemoryAllowed
condition|)
block|{
name|freeIndexInformation
argument_list|()
expr_stmt|;
block|}
return|return
name|newInd
return|;
block|}
comment|/**    * This method removes the map from the cache if index information for this    * map is loaded(size>0), index information entry in cache will not be     * removed if it is in the loading phrase(size=0), this prevents corruption      * of totalMemoryUsed. It should be called when a map output on this tracker     * is discarded.    * @param mapId The taskID of this map.    */
DECL|method|removeMap (String mapId)
specifier|public
name|void
name|removeMap
parameter_list|(
name|String
name|mapId
parameter_list|)
block|{
name|IndexInformation
name|info
init|=
name|cache
operator|.
name|get
argument_list|(
name|mapId
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
operator|||
operator|(
operator|(
name|info
operator|!=
literal|null
operator|)
operator|&&
name|isUnderConstruction
argument_list|(
name|info
argument_list|)
operator|)
condition|)
block|{
return|return;
block|}
name|info
operator|=
name|cache
operator|.
name|remove
argument_list|(
name|mapId
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|totalMemoryUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|info
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|queue
operator|.
name|remove
argument_list|(
name|mapId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Map ID"
operator|+
name|mapId
operator|+
literal|" not found in queue!!"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Map ID "
operator|+
name|mapId
operator|+
literal|" not found in cache"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This method checks if cache and totolMemoryUsed is consistent.    * It is only used for unit test.    * @return True if cache and totolMemoryUsed is consistent    */
DECL|method|checkTotalMemoryUsed ()
name|boolean
name|checkTotalMemoryUsed
parameter_list|()
block|{
name|int
name|totalSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IndexInformation
name|info
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|totalSize
operator|+=
name|info
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
return|return
name|totalSize
operator|==
name|totalMemoryUsed
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Bring memory usage below totalMemoryAllowed.    */
DECL|method|freeIndexInformation ()
specifier|private
specifier|synchronized
name|void
name|freeIndexInformation
parameter_list|()
block|{
while|while
condition|(
name|totalMemoryUsed
operator|.
name|get
argument_list|()
operator|>
name|totalMemoryAllowed
condition|)
block|{
name|String
name|s
init|=
name|queue
operator|.
name|remove
argument_list|()
decl_stmt|;
name|IndexInformation
name|info
init|=
name|cache
operator|.
name|remove
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|totalMemoryUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|info
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|IndexInformation
specifier|private
specifier|static
class|class
name|IndexInformation
block|{
DECL|field|mapSpillRecord
name|SpillRecord
name|mapSpillRecord
decl_stmt|;
DECL|method|getSize ()
name|int
name|getSize
parameter_list|()
block|{
return|return
name|mapSpillRecord
operator|==
literal|null
condition|?
literal|0
else|:
name|mapSpillRecord
operator|.
name|size
argument_list|()
operator|*
name|MapTask
operator|.
name|MAP_OUTPUT_INDEX_RECORD_LENGTH
return|;
block|}
block|}
block|}
end_class

end_unit

