begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|base
operator|.
name|Supplier
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|HdfsConfiguration
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
name|test
operator|.
name|GenericTestUtils
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
name|Appender
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
name|AsyncAppender
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|Collections
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
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_comment
comment|/**  * Test periodic logging of NameNode metrics.  */
end_comment

begin_class
DECL|class|TestNameNodeMetricsLogger
specifier|public
class|class
name|TestNameNodeMetricsLogger
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestNameNodeMetricsLogger
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testMetricsLoggerOnByDefault ()
specifier|public
name|void
name|testMetricsLoggerOnByDefault
parameter_list|()
throws|throws
name|IOException
block|{
name|NameNode
name|nn
init|=
name|makeNameNode
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|nn
operator|.
name|metricsLoggerTimer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDisableMetricsLogger ()
specifier|public
name|void
name|testDisableMetricsLogger
parameter_list|()
throws|throws
name|IOException
block|{
name|NameNode
name|nn
init|=
name|makeNameNode
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|nn
operator|.
name|metricsLoggerTimer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMetricsLoggerIsAsync ()
specifier|public
name|void
name|testMetricsLoggerIsAsync
parameter_list|()
throws|throws
name|IOException
block|{
name|makeNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|logger
init|=
operator|(
operator|(
name|Log4JLogger
operator|)
name|NameNode
operator|.
name|MetricsLog
operator|)
operator|.
name|getLogger
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Appender
argument_list|>
name|appenders
init|=
name|Collections
operator|.
name|list
argument_list|(
name|logger
operator|.
name|getAllAppenders
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|appenders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|AsyncAppender
argument_list|)
expr_stmt|;
block|}
comment|/**    * Publish a fake metric under the "Hadoop:" domain and ensure it is    * logged by the metrics logger.    */
annotation|@
name|Test
DECL|method|testMetricsLogOutput ()
specifier|public
name|void
name|testMetricsLogOutput
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|TestFakeMetric
name|metricsProvider
init|=
operator|new
name|TestFakeMetric
argument_list|()
decl_stmt|;
name|MBeans
operator|.
name|register
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|"DummyMetrics"
argument_list|,
name|metricsProvider
argument_list|)
expr_stmt|;
name|makeNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Log metrics early and often.
specifier|final
name|PatternMatchingAppender
name|appender
init|=
operator|new
name|PatternMatchingAppender
argument_list|(
literal|"^.*FakeMetric42.*$"
argument_list|)
decl_stmt|;
name|addAppender
argument_list|(
name|NameNode
operator|.
name|MetricsLog
argument_list|,
name|appender
argument_list|)
expr_stmt|;
comment|// Ensure that the supplied pattern was matched.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|appender
operator|.
name|isMatched
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a NameNode object that listens on a randomly chosen port    * number.    *    * @param enableMetricsLogging true if periodic metrics logging is to be    *                             enabled, false otherwise.    */
DECL|method|makeNameNode (boolean enableMetricsLogging)
specifier|private
name|NameNode
name|makeNameNode
parameter_list|(
name|boolean
name|enableMetricsLogging
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FS_DEFAULT_NAME_KEY
argument_list|,
literal|"hdfs://localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_NAMENODE_METRICS_LOGGER_PERIOD_SECONDS_KEY
argument_list|,
name|enableMetricsLogging
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
comment|// If enabled, log early and log often
return|return
operator|new
name|TestNameNode
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|addAppender (Log log, Appender appender)
specifier|private
name|void
name|addAppender
parameter_list|(
name|Log
name|log
parameter_list|,
name|Appender
name|appender
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|logger
init|=
operator|(
operator|(
name|Log4JLogger
operator|)
name|log
operator|)
operator|.
name|getLogger
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Appender
argument_list|>
name|appenders
init|=
name|Collections
operator|.
name|list
argument_list|(
name|logger
operator|.
name|getAllAppenders
argument_list|()
argument_list|)
decl_stmt|;
operator|(
operator|(
name|AsyncAppender
operator|)
name|appenders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
comment|/**    * A NameNode that stubs out the NameSystem for testing.    */
DECL|class|TestNameNode
specifier|private
specifier|static
class|class
name|TestNameNode
extends|extends
name|NameNode
block|{
annotation|@
name|Override
DECL|method|loadNamesystem (Configuration conf)
specifier|protected
name|void
name|loadNamesystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|namesystem
operator|=
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|TestNameNode (Configuration conf)
specifier|public
name|TestNameNode
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|TestFakeMetricMXBean
specifier|public
interface|interface
name|TestFakeMetricMXBean
block|{
DECL|method|getFakeMetric42 ()
name|int
name|getFakeMetric42
parameter_list|()
function_decl|;
block|}
comment|/**    * MBean for testing    */
DECL|class|TestFakeMetric
specifier|public
specifier|static
class|class
name|TestFakeMetric
implements|implements
name|TestFakeMetricMXBean
block|{
annotation|@
name|Override
DECL|method|getFakeMetric42 ()
specifier|public
name|int
name|getFakeMetric42
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/**    * An appender that matches logged messages against the given    * regular expression.    */
DECL|class|PatternMatchingAppender
specifier|public
specifier|static
class|class
name|PatternMatchingAppender
extends|extends
name|AppenderSkeleton
block|{
DECL|field|pattern
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
DECL|field|matched
specifier|private
specifier|volatile
name|boolean
name|matched
decl_stmt|;
DECL|method|PatternMatchingAppender (String pattern)
specifier|public
name|PatternMatchingAppender
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|this
operator|.
name|matched
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|isMatched ()
specifier|public
name|boolean
name|isMatched
parameter_list|()
block|{
return|return
name|matched
return|;
block|}
annotation|@
name|Override
DECL|method|append (LoggingEvent event)
specifier|protected
name|void
name|append
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|pattern
operator|.
name|matcher
argument_list|(
name|event
operator|.
name|getMessage
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|matched
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|requiresLayout ()
specifier|public
name|boolean
name|requiresLayout
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

