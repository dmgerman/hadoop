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
name|Flushable
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
name|Queue
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
name|log4j
operator|.
name|FileAppender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
import|;
end_import

begin_comment
comment|/**  * A simple log4j-appender for the task child's   * map-reduce system logs.  *   */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TaskLogAppender
specifier|public
class|class
name|TaskLogAppender
extends|extends
name|FileAppender
implements|implements
name|Flushable
block|{
DECL|field|taskId
specifier|private
name|String
name|taskId
decl_stmt|;
comment|//taskId should be managed as String rather than TaskID object
comment|//so that log4j can configure it from the configuration(log4j.properties).
DECL|field|maxEvents
specifier|private
name|Integer
name|maxEvents
decl_stmt|;
DECL|field|tail
specifier|private
name|Queue
argument_list|<
name|LoggingEvent
argument_list|>
name|tail
init|=
literal|null
decl_stmt|;
DECL|field|isCleanup
specifier|private
name|Boolean
name|isCleanup
decl_stmt|;
comment|// System properties passed in from JVM runner
DECL|field|ISCLEANUP_PROPERTY
specifier|static
specifier|final
name|String
name|ISCLEANUP_PROPERTY
init|=
literal|"hadoop.tasklog.iscleanup"
decl_stmt|;
DECL|field|LOGSIZE_PROPERTY
specifier|static
specifier|final
name|String
name|LOGSIZE_PROPERTY
init|=
literal|"hadoop.tasklog.totalLogFileSize"
decl_stmt|;
DECL|field|TASKID_PROPERTY
specifier|static
specifier|final
name|String
name|TASKID_PROPERTY
init|=
literal|"hadoop.tasklog.taskid"
decl_stmt|;
annotation|@
name|Override
DECL|method|activateOptions ()
specifier|public
name|void
name|activateOptions
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|setOptionsFromSystemProperties
argument_list|()
expr_stmt|;
if|if
condition|(
name|maxEvents
operator|>
literal|0
condition|)
block|{
name|tail
operator|=
operator|new
name|LinkedList
argument_list|<
name|LoggingEvent
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|setFile
argument_list|(
name|TaskLog
operator|.
name|getTaskLogFile
argument_list|(
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|taskId
argument_list|)
argument_list|,
name|isCleanup
argument_list|,
name|TaskLog
operator|.
name|LogName
operator|.
name|SYSLOG
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|setAppend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|activateOptions
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * The Task Runner passes in the options as system properties. Set    * the options if the setters haven't already been called.    */
DECL|method|setOptionsFromSystemProperties ()
specifier|private
specifier|synchronized
name|void
name|setOptionsFromSystemProperties
parameter_list|()
block|{
if|if
condition|(
name|isCleanup
operator|==
literal|null
condition|)
block|{
name|String
name|propValue
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|ISCLEANUP_PROPERTY
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
name|isCleanup
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|propValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|taskId
operator|==
literal|null
condition|)
block|{
name|taskId
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|TASKID_PROPERTY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxEvents
operator|==
literal|null
condition|)
block|{
name|String
name|propValue
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|LOGSIZE_PROPERTY
argument_list|,
literal|"0"
argument_list|)
decl_stmt|;
name|setTotalLogFileSize
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|propValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|append (LoggingEvent event)
specifier|public
name|void
name|append
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|tail
operator|==
literal|null
condition|)
block|{
name|super
operator|.
name|append
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|tail
operator|.
name|size
argument_list|()
operator|>=
name|maxEvents
condition|)
block|{
name|tail
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|tail
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
block|{
if|if
condition|(
name|qw
operator|!=
literal|null
condition|)
block|{
name|qw
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|tail
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|LoggingEvent
name|event
range|:
name|tail
control|)
block|{
name|super
operator|.
name|append
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Getter/Setter methods for log4j.    */
DECL|method|getTaskId ()
specifier|public
specifier|synchronized
name|String
name|getTaskId
parameter_list|()
block|{
return|return
name|taskId
return|;
block|}
DECL|method|setTaskId (String taskId)
specifier|public
specifier|synchronized
name|void
name|setTaskId
parameter_list|(
name|String
name|taskId
parameter_list|)
block|{
name|this
operator|.
name|taskId
operator|=
name|taskId
expr_stmt|;
block|}
DECL|field|EVENT_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|EVENT_SIZE
init|=
literal|100
decl_stmt|;
DECL|method|getTotalLogFileSize ()
specifier|public
specifier|synchronized
name|long
name|getTotalLogFileSize
parameter_list|()
block|{
return|return
name|maxEvents
operator|*
name|EVENT_SIZE
return|;
block|}
DECL|method|setTotalLogFileSize (long logSize)
specifier|public
specifier|synchronized
name|void
name|setTotalLogFileSize
parameter_list|(
name|long
name|logSize
parameter_list|)
block|{
name|maxEvents
operator|=
operator|(
name|int
operator|)
name|logSize
operator|/
name|EVENT_SIZE
expr_stmt|;
block|}
comment|/**    * Set whether the task is a cleanup attempt or not.    *     * @param isCleanup    *          true if the task is cleanup attempt, false otherwise.    */
DECL|method|setIsCleanup (boolean isCleanup)
specifier|public
specifier|synchronized
name|void
name|setIsCleanup
parameter_list|(
name|boolean
name|isCleanup
parameter_list|)
block|{
name|this
operator|.
name|isCleanup
operator|=
name|isCleanup
expr_stmt|;
block|}
comment|/**    * Get whether task is cleanup attempt or not.    *     * @return true if the task is cleanup attempt, false otherwise.    */
DECL|method|getIsCleanup ()
specifier|public
specifier|synchronized
name|boolean
name|getIsCleanup
parameter_list|()
block|{
return|return
name|isCleanup
return|;
block|}
block|}
end_class

end_unit

