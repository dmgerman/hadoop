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
name|org
operator|.
name|slf4j
operator|.
name|Marker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|MarkerFactory
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
name|Level
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
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_class
DECL|class|TestLog4jWarningErrorMetricsAppender
specifier|public
class|class
name|TestLog4jWarningErrorMetricsAppender
block|{
DECL|field|appender
name|Log4jWarningErrorMetricsAppender
name|appender
decl_stmt|;
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
name|TestLog4jWarningErrorMetricsAppender
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FATAL
specifier|private
specifier|static
specifier|final
name|Marker
name|FATAL
init|=
name|MarkerFactory
operator|.
name|getMarker
argument_list|(
literal|"FATAL"
argument_list|)
decl_stmt|;
DECL|field|cutoff
name|List
argument_list|<
name|Long
argument_list|>
name|cutoff
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|setupAppender (int cleanupIntervalSeconds, long messageAgeLimitSeconds, int maxUniqueMessages)
name|void
name|setupAppender
parameter_list|(
name|int
name|cleanupIntervalSeconds
parameter_list|,
name|long
name|messageAgeLimitSeconds
parameter_list|,
name|int
name|maxUniqueMessages
parameter_list|)
block|{
name|removeAppender
argument_list|()
expr_stmt|;
name|appender
operator|=
operator|new
name|Log4jWarningErrorMetricsAppender
argument_list|(
name|cleanupIntervalSeconds
argument_list|,
name|messageAgeLimitSeconds
argument_list|,
name|maxUniqueMessages
argument_list|)
expr_stmt|;
name|LogManager
operator|.
name|getRootLogger
argument_list|()
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
DECL|method|removeAppender ()
name|void
name|removeAppender
parameter_list|()
block|{
name|LogManager
operator|.
name|getRootLogger
argument_list|()
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
DECL|method|logMessages (Level level, String message, int count)
name|void
name|logMessages
parameter_list|(
name|Level
name|level
parameter_list|,
name|String
name|message
parameter_list|,
name|int
name|count
parameter_list|)
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
name|count
condition|;
operator|++
name|i
control|)
block|{
switch|switch
condition|(
name|level
operator|.
name|toInt
argument_list|()
condition|)
block|{
case|case
name|Level
operator|.
name|FATAL_INT
case|:
name|LOG
operator|.
name|error
argument_list|(
name|FATAL
argument_list|,
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|Level
operator|.
name|ERROR_INT
case|:
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|Level
operator|.
name|WARN_INT
case|:
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|Level
operator|.
name|INFO_INT
case|:
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|Level
operator|.
name|DEBUG_INT
case|:
name|LOG
operator|.
name|debug
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|Level
operator|.
name|TRACE_INT
case|:
name|LOG
operator|.
name|trace
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPurge ()
specifier|public
name|void
name|testPurge
parameter_list|()
throws|throws
name|Exception
block|{
name|setupAppender
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|setupAppender
argument_list|(
literal|2
argument_list|,
literal|1000
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 1"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 3"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorCounts ()
specifier|public
name|void
name|testErrorCounts
parameter_list|()
throws|throws
name|Exception
block|{
name|cutoff
operator|.
name|clear
argument_list|()
expr_stmt|;
name|setupAppender
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 2"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
name|Time
operator|.
name|now
argument_list|()
operator|/
literal|1000
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 3"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWarningCounts ()
specifier|public
name|void
name|testWarningCounts
parameter_list|()
throws|throws
name|Exception
block|{
name|cutoff
operator|.
name|clear
argument_list|()
expr_stmt|;
name|setupAppender
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|WARN
argument_list|,
literal|"test message 1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|WARN
argument_list|,
literal|"test message 2"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
name|Time
operator|.
name|now
argument_list|()
operator|/
literal|1000
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|WARN
argument_list|,
literal|"test message 3"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWarningMessages ()
specifier|public
name|void
name|testWarningMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|cutoff
operator|.
name|clear
argument_list|()
expr_stmt|;
name|setupAppender
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|WARN
argument_list|,
literal|"test message 1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|WARN
argument_list|,
literal|"test message 2"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
argument_list|>
name|errorsMap
init|=
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
argument_list|>
name|warningsMap
init|=
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|errorsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|warningsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|warningsMap
operator|.
name|containsKey
argument_list|(
literal|"test message 1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|warningsMap
operator|.
name|containsKey
argument_list|(
literal|"test message 2"
argument_list|)
argument_list|)
expr_stmt|;
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
name|msg1Info
init|=
name|warningsMap
operator|.
name|get
argument_list|(
literal|"test message 1"
argument_list|)
decl_stmt|;
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
name|msg2Info
init|=
name|warningsMap
operator|.
name|get
argument_list|(
literal|"test message 2"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|msg1Info
operator|.
name|count
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|msg2Info
operator|.
name|count
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
name|Time
operator|.
name|now
argument_list|()
operator|/
literal|1000
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|WARN
argument_list|,
literal|"test message 3"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|errorsMap
operator|=
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|warningsMap
operator|=
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|errorsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|warningsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|warningsMap
operator|.
name|containsKey
argument_list|(
literal|"test message 3"
argument_list|)
argument_list|)
expr_stmt|;
name|errorsMap
operator|=
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|warningsMap
operator|=
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|errorsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|warningsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|warningsMap
operator|.
name|containsKey
argument_list|(
literal|"test message 3"
argument_list|)
argument_list|)
expr_stmt|;
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
name|msg3Info
init|=
name|warningsMap
operator|.
name|get
argument_list|(
literal|"test message 3"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|msg3Info
operator|.
name|count
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorMessages ()
specifier|public
name|void
name|testErrorMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|cutoff
operator|.
name|clear
argument_list|()
expr_stmt|;
name|setupAppender
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 2"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
argument_list|>
name|errorsMap
init|=
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
argument_list|>
name|warningsMap
init|=
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|errorsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|warningsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|errorsMap
operator|.
name|containsKey
argument_list|(
literal|"test message 1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|errorsMap
operator|.
name|containsKey
argument_list|(
literal|"test message 2"
argument_list|)
argument_list|)
expr_stmt|;
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
name|msg1Info
init|=
name|errorsMap
operator|.
name|get
argument_list|(
literal|"test message 1"
argument_list|)
decl_stmt|;
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
name|msg2Info
init|=
name|errorsMap
operator|.
name|get
argument_list|(
literal|"test message 2"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|msg1Info
operator|.
name|count
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|msg2Info
operator|.
name|count
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
name|Time
operator|.
name|now
argument_list|()
operator|/
literal|1000
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"test message 3"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|errorsMap
operator|=
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|warningsMap
operator|=
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|errorsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|warningsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|errorsMap
operator|.
name|containsKey
argument_list|(
literal|"test message 3"
argument_list|)
argument_list|)
expr_stmt|;
name|errorsMap
operator|=
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|warningsMap
operator|=
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|errorsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|warningsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|errorsMap
operator|.
name|containsKey
argument_list|(
literal|"test message 3"
argument_list|)
argument_list|)
expr_stmt|;
name|Log4jWarningErrorMetricsAppender
operator|.
name|Element
name|msg3Info
init|=
name|errorsMap
operator|.
name|get
argument_list|(
literal|"test message 3"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|msg3Info
operator|.
name|count
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInfoDebugTrace ()
specifier|public
name|void
name|testInfoDebugTrace
parameter_list|()
block|{
name|cutoff
operator|.
name|clear
argument_list|()
expr_stmt|;
name|setupAppender
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|cutoff
operator|.
name|add
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
literal|"test message 1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|,
literal|"test message 2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|logMessages
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|,
literal|"test message 3"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getErrorCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getWarningCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getErrorMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appender
operator|.
name|getWarningMessagesAndCounts
argument_list|(
name|cutoff
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

