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
name|HashMap
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
name|Map
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
name|CompletionService
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
name|ExecutionException
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
name|Future
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|StoragePolicySatisfyWorker
operator|.
name|BlockMovementResult
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
name|StoragePolicySatisfyWorker
operator|.
name|BlocksMovementsCompletionHandler
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
comment|/**  * This class is used to track the completion of block movement future tasks.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|BlockStorageMovementTracker
specifier|public
class|class
name|BlockStorageMovementTracker
implements|implements
name|Runnable
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
name|BlockStorageMovementTracker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|moverCompletionService
specifier|private
specifier|final
name|CompletionService
argument_list|<
name|BlockMovementResult
argument_list|>
name|moverCompletionService
decl_stmt|;
DECL|field|blksMovementscompletionHandler
specifier|private
specifier|final
name|BlocksMovementsCompletionHandler
name|blksMovementscompletionHandler
decl_stmt|;
comment|// Keeps the information - trackID vs its list of blocks
DECL|field|moverTaskFutures
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Future
argument_list|<
name|BlockMovementResult
argument_list|>
argument_list|>
argument_list|>
name|moverTaskFutures
decl_stmt|;
DECL|field|movementResults
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|BlockMovementResult
argument_list|>
argument_list|>
name|movementResults
decl_stmt|;
comment|/**    * BlockStorageMovementTracker constructor.    *    * @param moverCompletionService    *          completion service.    * @param handler    *          blocks movements completion handler    */
DECL|method|BlockStorageMovementTracker ( CompletionService<BlockMovementResult> moverCompletionService, BlocksMovementsCompletionHandler handler)
specifier|public
name|BlockStorageMovementTracker
parameter_list|(
name|CompletionService
argument_list|<
name|BlockMovementResult
argument_list|>
name|moverCompletionService
parameter_list|,
name|BlocksMovementsCompletionHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|moverCompletionService
operator|=
name|moverCompletionService
expr_stmt|;
name|this
operator|.
name|moverTaskFutures
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|blksMovementscompletionHandler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|movementResults
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|moverTaskFutures
operator|.
name|size
argument_list|()
operator|<=
literal|0
condition|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|moverTaskFutures
init|)
block|{
comment|// Waiting for mover tasks.
name|moverTaskFutures
operator|.
name|wait
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{
comment|// ignore
block|}
block|}
try|try
block|{
name|Future
argument_list|<
name|BlockMovementResult
argument_list|>
name|future
init|=
name|moverCompletionService
operator|.
name|take
argument_list|()
decl_stmt|;
if|if
condition|(
name|future
operator|!=
literal|null
condition|)
block|{
name|BlockMovementResult
name|result
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Completed block movement. {}"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|long
name|trackId
init|=
name|result
operator|.
name|getTrackId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|BlockMovementResult
argument_list|>
argument_list|>
name|blocksMoving
init|=
name|moverTaskFutures
operator|.
name|get
argument_list|(
name|trackId
argument_list|)
decl_stmt|;
name|blocksMoving
operator|.
name|remove
argument_list|(
name|future
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|BlockMovementResult
argument_list|>
name|resultPerTrackIdList
init|=
name|addMovementResultToTrackIdList
argument_list|(
name|result
argument_list|)
decl_stmt|;
comment|// Completed all the scheduled blocks movement under this 'trackId'.
if|if
condition|(
name|blocksMoving
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|moverTaskFutures
init|)
block|{
name|moverTaskFutures
operator|.
name|remove
argument_list|(
name|trackId
argument_list|)
expr_stmt|;
block|}
comment|// handle completed blocks movements per trackId.
name|blksMovementscompletionHandler
operator|.
name|handle
argument_list|(
name|resultPerTrackIdList
argument_list|)
expr_stmt|;
name|movementResults
operator|.
name|remove
argument_list|(
name|trackId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// TODO: Do we need failure retries and implement the same if required.
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while moving block replica to target storage type"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addMovementResultToTrackIdList ( BlockMovementResult result)
specifier|private
name|List
argument_list|<
name|BlockMovementResult
argument_list|>
name|addMovementResultToTrackIdList
parameter_list|(
name|BlockMovementResult
name|result
parameter_list|)
block|{
name|long
name|trackId
init|=
name|result
operator|.
name|getTrackId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BlockMovementResult
argument_list|>
name|perTrackIdList
decl_stmt|;
synchronized|synchronized
init|(
name|movementResults
init|)
block|{
name|perTrackIdList
operator|=
name|movementResults
operator|.
name|get
argument_list|(
name|trackId
argument_list|)
expr_stmt|;
if|if
condition|(
name|perTrackIdList
operator|==
literal|null
condition|)
block|{
name|perTrackIdList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|movementResults
operator|.
name|put
argument_list|(
name|trackId
argument_list|,
name|perTrackIdList
argument_list|)
expr_stmt|;
block|}
name|perTrackIdList
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|perTrackIdList
return|;
block|}
comment|/**    * Add future task to the tracking list to check the completion status of the    * block movement.    *    * @param trackID    *          tracking Id    * @param futureTask    *          future task used for moving the respective block    */
DECL|method|addBlock (long trackID, Future<BlockMovementResult> futureTask)
name|void
name|addBlock
parameter_list|(
name|long
name|trackID
parameter_list|,
name|Future
argument_list|<
name|BlockMovementResult
argument_list|>
name|futureTask
parameter_list|)
block|{
synchronized|synchronized
init|(
name|moverTaskFutures
init|)
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|BlockMovementResult
argument_list|>
argument_list|>
name|futures
init|=
name|moverTaskFutures
operator|.
name|get
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|trackID
argument_list|)
argument_list|)
decl_stmt|;
comment|// null for the first task
if|if
condition|(
name|futures
operator|==
literal|null
condition|)
block|{
name|futures
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|moverTaskFutures
operator|.
name|put
argument_list|(
name|trackID
argument_list|,
name|futures
argument_list|)
expr_stmt|;
block|}
name|futures
operator|.
name|add
argument_list|(
name|futureTask
argument_list|)
expr_stmt|;
comment|// Notify waiting tracker thread about the newly added tasks.
name|moverTaskFutures
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

