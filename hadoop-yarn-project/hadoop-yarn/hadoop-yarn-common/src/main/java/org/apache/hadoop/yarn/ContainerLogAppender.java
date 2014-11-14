begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
name|InterfaceAudience
operator|.
name|Public
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
operator|.
name|Unstable
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
comment|/**  * A simple log4j-appender for container's logs.  *   */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ContainerLogAppender
specifier|public
class|class
name|ContainerLogAppender
extends|extends
name|FileAppender
implements|implements
name|Flushable
block|{
DECL|field|containerLogDir
specifier|private
name|String
name|containerLogDir
decl_stmt|;
comment|//so that log4j can configure it from the configuration(log4j.properties).
DECL|field|maxEvents
specifier|private
name|int
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
DECL|field|closing
specifier|private
name|boolean
name|closing
init|=
literal|false
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
operator|new
name|File
argument_list|(
name|this
operator|.
name|containerLogDir
argument_list|,
literal|"syslog"
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
name|closing
condition|)
block|{
comment|// When closing drop any new/transitive CLA appending
return|return;
block|}
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
name|closing
operator|=
literal|true
expr_stmt|;
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
DECL|method|getContainerLogDir ()
specifier|public
name|String
name|getContainerLogDir
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerLogDir
return|;
block|}
DECL|method|setContainerLogDir (String containerLogDir)
specifier|public
name|void
name|setContainerLogDir
parameter_list|(
name|String
name|containerLogDir
parameter_list|)
block|{
name|this
operator|.
name|containerLogDir
operator|=
name|containerLogDir
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
block|}
end_class

end_unit

