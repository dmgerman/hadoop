begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.node
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|node
package|;
end_package

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
name|DatanodeID
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
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * Command Queue is queue of commands for the datanode.  *<p>  * Node manager, container Manager and key space managers can queue commands for  * datanodes into this queue. These commands will be send in the order in which  * there where queued.  */
end_comment

begin_class
DECL|class|CommandQueue
specifier|public
class|class
name|CommandQueue
block|{
comment|// This list is used as default return value.
DECL|field|DEFAULT_LIST
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|DEFAULT_LIST
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|commandMap
specifier|private
specifier|final
name|Map
argument_list|<
name|DatanodeID
argument_list|,
name|Commands
argument_list|>
name|commandMap
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
DECL|field|commandsInQueue
specifier|private
name|long
name|commandsInQueue
decl_stmt|;
comment|/**    * Returns number of commands in queue.    * @return Command Count.    */
DECL|method|getCommandsInQueue ()
specifier|public
name|long
name|getCommandsInQueue
parameter_list|()
block|{
return|return
name|commandsInQueue
return|;
block|}
comment|/**    * Constructs a Command Queue.    * TODO : Add a flusher thread that throws away commands older than a certain    * time period.    */
DECL|method|CommandQueue ()
specifier|public
name|CommandQueue
parameter_list|()
block|{
name|commandMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|lock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
name|commandsInQueue
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * This function is used only for test purposes.    */
annotation|@
name|VisibleForTesting
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|commandMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|commandsInQueue
operator|=
literal|0
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns  a list of Commands for the datanode to execute, if we have no    * commands returns a empty list otherwise the current set of    * commands are returned and command map set to empty list again.    *    * @param datanodeID DatanodeID    * @return List of SCM Commands.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getCommand (final DatanodeID datanodeID)
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|getCommand
parameter_list|(
specifier|final
name|DatanodeID
name|datanodeID
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Commands
name|cmds
init|=
name|commandMap
operator|.
name|remove
argument_list|(
name|datanodeID
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|cmdList
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cmds
operator|!=
literal|null
condition|)
block|{
name|cmdList
operator|=
name|cmds
operator|.
name|getCommands
argument_list|()
expr_stmt|;
name|commandsInQueue
operator|-=
name|cmdList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
name|cmdList
operator|.
name|size
argument_list|()
else|:
literal|0
expr_stmt|;
comment|// A post condition really.
name|Preconditions
operator|.
name|checkState
argument_list|(
name|commandsInQueue
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|cmds
operator|==
literal|null
condition|?
name|DEFAULT_LIST
else|:
name|cmdList
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Adds a Command to the SCM Queue to send the command to container.    *    * @param datanodeID DatanodeID    * @param command    - Command    */
DECL|method|addCommand (final DatanodeID datanodeID, final SCMCommand command)
specifier|public
name|void
name|addCommand
parameter_list|(
specifier|final
name|DatanodeID
name|datanodeID
parameter_list|,
specifier|final
name|SCMCommand
name|command
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|commandMap
operator|.
name|containsKey
argument_list|(
name|datanodeID
argument_list|)
condition|)
block|{
name|commandMap
operator|.
name|get
argument_list|(
name|datanodeID
argument_list|)
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|commandMap
operator|.
name|put
argument_list|(
name|datanodeID
argument_list|,
operator|new
name|Commands
argument_list|(
name|command
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|commandsInQueue
operator|++
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Class that stores commands for a datanode.    */
DECL|class|Commands
specifier|private
specifier|static
class|class
name|Commands
block|{
DECL|field|updateTime
specifier|private
name|long
name|updateTime
decl_stmt|;
DECL|field|readTime
specifier|private
name|long
name|readTime
decl_stmt|;
DECL|field|commands
specifier|private
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|commands
decl_stmt|;
comment|/**      * Constructs a Commands class.      */
DECL|method|Commands ()
name|Commands
parameter_list|()
block|{
name|commands
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|updateTime
operator|=
literal|0
expr_stmt|;
name|readTime
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Creates the object and populates with the command.      * @param command command to add to queue.      */
DECL|method|Commands (SCMCommand command)
name|Commands
parameter_list|(
name|SCMCommand
name|command
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the last time the commands for this node was updated.      * @return Time stamp      */
DECL|method|getUpdateTime ()
specifier|public
name|long
name|getUpdateTime
parameter_list|()
block|{
return|return
name|updateTime
return|;
block|}
comment|/**      * Gets the last read time.      * @return last time when these commands were read from this queue.      */
DECL|method|getReadTime ()
specifier|public
name|long
name|getReadTime
parameter_list|()
block|{
return|return
name|readTime
return|;
block|}
comment|/**      * Adds a command to the list.      *      * @param command SCMCommand      */
DECL|method|add (SCMCommand command)
specifier|public
name|void
name|add
parameter_list|(
name|SCMCommand
name|command
parameter_list|)
block|{
name|this
operator|.
name|commands
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|updateTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the commands for this datanode.      * @return command list.      */
DECL|method|getCommands ()
specifier|public
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|getCommands
parameter_list|()
block|{
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|temp
init|=
name|this
operator|.
name|commands
decl_stmt|;
name|this
operator|.
name|commands
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|readTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
return|return
name|temp
return|;
block|}
block|}
block|}
end_class

end_unit

