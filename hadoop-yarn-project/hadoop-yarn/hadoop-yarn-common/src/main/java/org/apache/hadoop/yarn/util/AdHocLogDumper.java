begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|AppenderSkeleton
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
name|Layout
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
name|Level
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
name|Logger
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
name|LogManager
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
name|PatternLayout
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
name|Priority
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRuntimeException
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
name|Enumeration
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|AdHocLogDumper
specifier|public
class|class
name|AdHocLogDumper
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|AdHocLogDumper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|targetFilename
specifier|private
name|String
name|targetFilename
decl_stmt|;
DECL|field|appenderLevels
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Priority
argument_list|>
name|appenderLevels
decl_stmt|;
DECL|field|currentLogLevel
specifier|private
name|Level
name|currentLogLevel
decl_stmt|;
DECL|field|AD_HOC_DUMPER_APPENDER
specifier|public
specifier|static
specifier|final
name|String
name|AD_HOC_DUMPER_APPENDER
init|=
literal|"ad-hoc-dumper-appender"
decl_stmt|;
DECL|field|logFlag
specifier|private
specifier|static
specifier|volatile
name|boolean
name|logFlag
init|=
literal|false
decl_stmt|;
DECL|field|lock
specifier|private
specifier|static
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|AdHocLogDumper (String name, String targetFilename)
specifier|public
name|AdHocLogDumper
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|targetFilename
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|targetFilename
operator|=
name|targetFilename
expr_stmt|;
name|appenderLevels
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|dumpLogs (String level, int timePeriod)
specifier|public
name|void
name|dumpLogs
parameter_list|(
name|String
name|level
parameter_list|,
name|int
name|timePeriod
parameter_list|)
throws|throws
name|YarnRuntimeException
throws|,
name|IOException
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
if|if
condition|(
name|logFlag
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempt to dump logs when appender is already running"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Appender is already dumping logs"
argument_list|)
throw|;
block|}
name|Level
name|targetLevel
init|=
name|Level
operator|.
name|toLevel
argument_list|(
name|level
argument_list|)
decl_stmt|;
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|appenderLevels
operator|.
name|clear
argument_list|()
expr_stmt|;
name|currentLogLevel
operator|=
name|logger
operator|.
name|getLevel
argument_list|()
expr_stmt|;
name|Level
name|currentEffectiveLevel
init|=
name|logger
operator|.
name|getEffectiveLevel
argument_list|()
decl_stmt|;
comment|// make sure we can create the appender first
name|Layout
name|layout
init|=
operator|new
name|PatternLayout
argument_list|(
literal|"%d{ISO8601} %p %c: %m%n"
argument_list|)
decl_stmt|;
name|FileAppender
name|fApp
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"yarn.log.dir"
argument_list|)
argument_list|,
name|targetFilename
argument_list|)
decl_stmt|;
try|try
block|{
name|fApp
operator|=
operator|new
name|FileAppender
argument_list|(
name|layout
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error creating file, can't dump logs to "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ie
argument_list|)
expr_stmt|;
throw|throw
name|ie
throw|;
block|}
name|fApp
operator|.
name|setName
argument_list|(
name|AdHocLogDumper
operator|.
name|AD_HOC_DUMPER_APPENDER
argument_list|)
expr_stmt|;
name|fApp
operator|.
name|setThreshold
argument_list|(
name|targetLevel
argument_list|)
expr_stmt|;
comment|// get current threshold of all appenders and set it to the effective
comment|// level
for|for
control|(
name|Enumeration
name|appenders
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|getAllAppenders
argument_list|()
init|;
name|appenders
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|Object
name|obj
init|=
name|appenders
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|AppenderSkeleton
condition|)
block|{
name|AppenderSkeleton
name|appender
init|=
operator|(
name|AppenderSkeleton
operator|)
name|obj
decl_stmt|;
name|appenderLevels
operator|.
name|put
argument_list|(
name|appender
operator|.
name|getName
argument_list|()
argument_list|,
name|appender
operator|.
name|getThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|appender
operator|.
name|setThreshold
argument_list|(
name|currentEffectiveLevel
argument_list|)
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|addAppender
argument_list|(
name|fApp
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Dumping adhoc logs for "
operator|+
name|name
operator|+
literal|" to "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" for "
operator|+
name|timePeriod
operator|+
literal|" milliseconds"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|setLevel
argument_list|(
name|targetLevel
argument_list|)
expr_stmt|;
name|logFlag
operator|=
literal|true
expr_stmt|;
name|TimerTask
name|restoreLogLevel
init|=
operator|new
name|RestoreLogLevel
argument_list|()
decl_stmt|;
name|Timer
name|restoreLogLevelTimer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|restoreLogLevelTimer
operator|.
name|schedule
argument_list|(
name|restoreLogLevel
argument_list|,
name|timePeriod
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getState ()
specifier|public
specifier|static
name|boolean
name|getState
parameter_list|()
block|{
return|return
name|logFlag
return|;
block|}
DECL|class|RestoreLogLevel
class|class
name|RestoreLogLevel
extends|extends
name|TimerTask
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|logger
operator|.
name|removeAppender
argument_list|(
name|AD_HOC_DUMPER_APPENDER
argument_list|)
expr_stmt|;
name|logger
operator|.
name|setLevel
argument_list|(
name|currentLogLevel
argument_list|)
expr_stmt|;
for|for
control|(
name|Enumeration
name|appenders
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|getAllAppenders
argument_list|()
init|;
name|appenders
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|Object
name|obj
init|=
name|appenders
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|AppenderSkeleton
condition|)
block|{
name|AppenderSkeleton
name|appender
init|=
operator|(
name|AppenderSkeleton
operator|)
name|obj
decl_stmt|;
name|appender
operator|.
name|setThreshold
argument_list|(
name|appenderLevels
operator|.
name|get
argument_list|(
name|appender
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|logFlag
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Done dumping adhoc logs for "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

