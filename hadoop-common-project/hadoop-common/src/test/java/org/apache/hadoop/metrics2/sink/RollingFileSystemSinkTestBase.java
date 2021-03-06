begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.sink
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|sink
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|configuration2
operator|.
name|SubsetConfiguration
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileStatus
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
name|FileSystem
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
name|FileUtil
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
name|LocatedFileStatus
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
name|fs
operator|.
name|RemoteIterator
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
name|MetricsException
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
name|MetricsRecord
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
name|MetricsSystem
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
name|annotation
operator|.
name|Metric
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
name|annotation
operator|.
name|Metrics
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
name|annotation
operator|.
name|Metric
operator|.
name|Type
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
name|impl
operator|.
name|ConfigBuilder
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
name|impl
operator|.
name|MetricsSystemImpl
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
name|impl
operator|.
name|TestMetricsConfig
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
name|lib
operator|.
name|MutableGaugeInt
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
name|lib
operator|.
name|MutableGaugeLong
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
name|junit
operator|.
name|AfterClass
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
name|rules
operator|.
name|TestName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * This class is a base class for testing the {@link RollingFileSystemSink}  * class in various contexts. It provides the a number of useful utility  * methods for classes that extend it.  */
end_comment

begin_class
DECL|class|RollingFileSystemSinkTestBase
specifier|public
class|class
name|RollingFileSystemSinkTestBase
block|{
DECL|field|SINK_PRINCIPAL_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|SINK_PRINCIPAL_KEY
init|=
literal|"rfssink.principal"
decl_stmt|;
DECL|field|SINK_KEYTAB_FILE_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|SINK_KEYTAB_FILE_KEY
init|=
literal|"rfssink.keytab"
decl_stmt|;
DECL|field|ROOT_TEST_DIR
specifier|protected
specifier|static
specifier|final
name|File
name|ROOT_TEST_DIR
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
literal|"RollingFileSystemSinkTest"
argument_list|)
decl_stmt|;
DECL|field|DATE_FORMAT
specifier|protected
specifier|static
specifier|final
name|SimpleDateFormat
name|DATE_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMMddHH"
argument_list|)
decl_stmt|;
DECL|field|methodDir
specifier|protected
specifier|static
name|File
name|methodDir
decl_stmt|;
comment|/**    * The name of the current test method.    */
annotation|@
name|Rule
DECL|field|methodName
specifier|public
name|TestName
name|methodName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
comment|/**    * A sample metric class    */
annotation|@
name|Metrics
argument_list|(
name|name
operator|=
literal|"testRecord1"
argument_list|,
name|context
operator|=
literal|"test1"
argument_list|)
DECL|class|MyMetrics1
specifier|protected
class|class
name|MyMetrics1
block|{
annotation|@
name|Metric
argument_list|(
name|value
operator|=
block|{
literal|"testTag1"
block|,
literal|""
block|}
argument_list|,
name|type
operator|=
name|Type
operator|.
name|TAG
argument_list|)
DECL|method|testTag1 ()
name|String
name|testTag1
parameter_list|()
block|{
return|return
literal|"testTagValue1"
return|;
block|}
annotation|@
name|Metric
argument_list|(
name|value
operator|=
block|{
literal|"testTag2"
block|,
literal|""
block|}
argument_list|,
name|type
operator|=
name|Type
operator|.
name|TAG
argument_list|)
DECL|method|gettestTag2 ()
name|String
name|gettestTag2
parameter_list|()
block|{
return|return
literal|"testTagValue2"
return|;
block|}
annotation|@
name|Metric
argument_list|(
name|value
operator|=
block|{
literal|"testMetric1"
block|,
literal|"An integer gauge"
block|}
argument_list|,
name|always
operator|=
literal|true
argument_list|)
DECL|field|testMetric1
name|MutableGaugeInt
name|testMetric1
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|value
operator|=
block|{
literal|"testMetric2"
block|,
literal|"A long gauge"
block|}
argument_list|,
name|always
operator|=
literal|true
argument_list|)
DECL|field|testMetric2
name|MutableGaugeLong
name|testMetric2
decl_stmt|;
DECL|method|registerWith (MetricsSystem ms)
specifier|public
name|MyMetrics1
name|registerWith
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|)
block|{
return|return
name|ms
operator|.
name|register
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"-m1"
argument_list|,
literal|null
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
comment|/**    * Another sample metrics class    */
annotation|@
name|Metrics
argument_list|(
name|name
operator|=
literal|"testRecord2"
argument_list|,
name|context
operator|=
literal|"test1"
argument_list|)
DECL|class|MyMetrics2
specifier|protected
class|class
name|MyMetrics2
block|{
annotation|@
name|Metric
argument_list|(
name|value
operator|=
block|{
literal|"testTag22"
block|,
literal|""
block|}
argument_list|,
name|type
operator|=
name|Type
operator|.
name|TAG
argument_list|)
DECL|method|testTag1 ()
name|String
name|testTag1
parameter_list|()
block|{
return|return
literal|"testTagValue22"
return|;
block|}
DECL|method|registerWith (MetricsSystem ms)
specifier|public
name|MyMetrics2
name|registerWith
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|)
block|{
return|return
name|ms
operator|.
name|register
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"-m2"
argument_list|,
literal|null
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
comment|/**    * Set the date format's timezone to GMT.    */
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|DATE_FORMAT
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|ROOT_TEST_DIR
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete the test directory for this test.    * @throws IOException thrown if the delete fails    */
annotation|@
name|AfterClass
DECL|method|deleteBaseDir ()
specifier|public
specifier|static
name|void
name|deleteBaseDir
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|ROOT_TEST_DIR
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create the test directory for this test.    * @throws IOException thrown if the create fails    */
annotation|@
name|Before
DECL|method|createMethodDir ()
specifier|public
name|void
name|createMethodDir
parameter_list|()
throws|throws
name|IOException
block|{
name|methodDir
operator|=
operator|new
name|File
argument_list|(
name|ROOT_TEST_DIR
argument_list|,
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Test directory already exists: "
operator|+
name|methodDir
argument_list|,
name|methodDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set up the metrics system, start it, and return it. The principal and    * keytab properties will not be set.    *    * @param path the base path for the sink    * @param ignoreErrors whether the sink should ignore errors    * @param allowAppend whether the sink is allowed to append to existing files    * @return the metrics system    */
DECL|method|initMetricsSystem (String path, boolean ignoreErrors, boolean allowAppend)
specifier|protected
name|MetricsSystem
name|initMetricsSystem
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|ignoreErrors
parameter_list|,
name|boolean
name|allowAppend
parameter_list|)
block|{
return|return
name|initMetricsSystem
argument_list|(
name|path
argument_list|,
name|ignoreErrors
argument_list|,
name|allowAppend
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Set up the metrics system, start it, and return it.    * @param path the base path for the sink    * @param ignoreErrors whether the sink should ignore errors    * @param allowAppend whether the sink is allowed to append to existing files    * @param useSecureParams whether to set the principal and keytab properties    * @return the org.apache.hadoop.metrics2.MetricsSystem    */
DECL|method|initMetricsSystem (String path, boolean ignoreErrors, boolean allowAppend, boolean useSecureParams)
specifier|protected
name|MetricsSystem
name|initMetricsSystem
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|ignoreErrors
parameter_list|,
name|boolean
name|allowAppend
parameter_list|,
name|boolean
name|useSecureParams
parameter_list|)
block|{
comment|// If the prefix is not lower case, the metrics system won't be able to
comment|// read any of the properties.
name|String
name|prefix
init|=
name|methodName
operator|.
name|getMethodName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|ConfigBuilder
name|builder
init|=
operator|new
name|ConfigBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"*.period"
argument_list|,
literal|10000
argument_list|)
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.class"
argument_list|,
name|MockSink
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.basepath"
argument_list|,
name|path
argument_list|)
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.source"
argument_list|,
literal|"testsrc"
argument_list|)
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.context"
argument_list|,
literal|"test1"
argument_list|)
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.ignore-error"
argument_list|,
name|ignoreErrors
argument_list|)
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.allow-append"
argument_list|,
name|allowAppend
argument_list|)
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.roll-offset-interval-millis"
argument_list|,
literal|0
argument_list|)
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.roll-interval"
argument_list|,
literal|"1h"
argument_list|)
operator|.
name|add
argument_list|(
literal|"*.queue.capacity"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|useSecureParams
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.keytab-key"
argument_list|,
name|SINK_KEYTAB_FILE_KEY
argument_list|)
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|".sink.mysink0.principal-key"
argument_list|,
name|SINK_PRINCIPAL_KEY
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|save
argument_list|(
name|TestMetricsConfig
operator|.
name|getTestFilename
argument_list|(
literal|"hadoop-metrics2-"
operator|+
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsSystemImpl
name|ms
init|=
operator|new
name|MetricsSystemImpl
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
name|ms
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|ms
return|;
block|}
comment|/**    * Helper method that writes metrics files to a target path, reads those    * files, and returns the contents of all files as a single string. This    * method will assert that the correct number of files is found.    *    * @param ms an initialized MetricsSystem to use    * @param path the target path from which to read the logs    * @param count the number of log files to expect    * @return the contents of the log files    * @throws IOException when the log file can't be read    * @throws URISyntaxException when the target path is an invalid URL    */
DECL|method|doWriteTest (MetricsSystem ms, String path, int count)
specifier|protected
name|String
name|doWriteTest
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|,
name|String
name|path
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
specifier|final
name|String
name|then
init|=
name|DATE_FORMAT
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|+
literal|"00"
decl_stmt|;
name|MyMetrics1
name|mm1
init|=
operator|new
name|MyMetrics1
argument_list|()
operator|.
name|registerWith
argument_list|(
name|ms
argument_list|)
decl_stmt|;
operator|new
name|MyMetrics2
argument_list|()
operator|.
name|registerWith
argument_list|(
name|ms
argument_list|)
expr_stmt|;
name|mm1
operator|.
name|testMetric1
operator|.
name|incr
argument_list|()
expr_stmt|;
name|mm1
operator|.
name|testMetric2
operator|.
name|incr
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
comment|// publish the metrics
try|try
block|{
name|ms
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|ms
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
return|return
name|readLogFile
argument_list|(
name|path
argument_list|,
name|then
argument_list|,
name|count
argument_list|)
return|;
block|}
comment|/**    * Read the log files at the target path and return the contents as a single    * string. This method will assert that the correct number of files is found.    *    * @param path the target path    * @param then when the test method began. Used to find the log directory in    * the case that the test run crosses the top of the hour.    * @param count the number of log files to expect    * @return    * @throws IOException    * @throws URISyntaxException    */
DECL|method|readLogFile (String path, String then, int count)
specifier|protected
name|String
name|readLogFile
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|then
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
specifier|final
name|String
name|now
init|=
name|DATE_FORMAT
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|+
literal|"00"
decl_stmt|;
specifier|final
name|String
name|logFile
init|=
name|getLogFilename
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
name|path
argument_list|)
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|metrics
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|FileStatus
name|status
range|:
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
control|)
block|{
name|Path
name|logDir
init|=
name|status
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// There are only two possible valid log directory names: the time when
comment|// the test started and the current time.  Anything else can be ignored.
if|if
condition|(
name|now
operator|.
name|equals
argument_list|(
name|logDir
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|then
operator|.
name|equals
argument_list|(
name|logDir
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|readLogData
argument_list|(
name|fs
argument_list|,
name|findMostRecentLogFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|logDir
argument_list|,
name|logFile
argument_list|)
argument_list|)
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
name|assertFileCount
argument_list|(
name|fs
argument_list|,
name|logDir
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"No valid log directories found"
argument_list|,
name|found
argument_list|)
expr_stmt|;
return|return
name|metrics
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Read the target log file and append its contents to the StringBuilder.    * @param fs the target FileSystem    * @param logFile the target file path    * @param metrics where to append the file contents    * @throws IOException thrown if the file cannot be read    */
DECL|method|readLogData (FileSystem fs, Path logFile, StringBuilder metrics)
specifier|protected
name|void
name|readLogData
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|logFile
parameter_list|,
name|StringBuilder
name|metrics
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataInputStream
name|fsin
init|=
name|fs
operator|.
name|open
argument_list|(
name|logFile
argument_list|)
decl_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fsin
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|metrics
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Return the path to the log file to use, based on the initial path. The    * initial path must be a valid log file path. This method will find the    * most recent version of the file.    *    * @param fs the target FileSystem    * @param initial the path from which to start    * @return the path to use    * @throws IOException thrown if testing for file existence fails.    */
DECL|method|findMostRecentLogFile (FileSystem fs, Path initial)
specifier|protected
name|Path
name|findMostRecentLogFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|initial
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|logFile
init|=
literal|null
decl_stmt|;
name|Path
name|nextLogFile
init|=
name|initial
decl_stmt|;
name|int
name|id
init|=
literal|1
decl_stmt|;
do|do
block|{
name|logFile
operator|=
name|nextLogFile
expr_stmt|;
name|nextLogFile
operator|=
operator|new
name|Path
argument_list|(
name|initial
operator|.
name|toString
argument_list|()
operator|+
literal|"."
operator|+
name|id
argument_list|)
expr_stmt|;
name|id
operator|+=
literal|1
expr_stmt|;
block|}
do|while
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|nextLogFile
argument_list|)
condition|)
do|;
return|return
name|logFile
return|;
block|}
comment|/**    * Return the name of the log file for this host.    *    * @return the name of the log file for this host    */
DECL|method|getLogFilename ()
specifier|protected
specifier|static
name|String
name|getLogFilename
parameter_list|()
throws|throws
name|UnknownHostException
block|{
return|return
literal|"testsrc-"
operator|+
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|+
literal|".log"
return|;
block|}
comment|/**    * Assert that the given contents match what is expected from the test    * metrics.    *    * @param contents the file contents to test    */
DECL|method|assertMetricsContents (String contents)
specifier|protected
name|void
name|assertMetricsContents
parameter_list|(
name|String
name|contents
parameter_list|)
block|{
comment|// Note that in the below expression we allow tags and metrics to go in
comment|// arbitrary order, but the records must be in order.
specifier|final
name|Pattern
name|expectedContentPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\d+\\s+test1.testRecord1:\\s+Context=test1,\\s+"
operator|+
literal|"(testTag1=testTagValue1,\\s+testTag2=testTagValue2|"
operator|+
literal|"testTag2=testTagValue2,\\s+testTag1=testTagValue1),"
operator|+
literal|"\\s+Hostname=.*,\\s+"
operator|+
literal|"(testMetric1=1,\\s+testMetric2=2|testMetric2=2,\\s+testMetric1=1)"
operator|+
literal|"[\\n\\r]*^\\d+\\s+test1.testRecord2:\\s+Context=test1,"
operator|+
literal|"\\s+testTag22=testTagValue22,\\s+Hostname=.*$[\\n\\r]*"
argument_list|,
name|Pattern
operator|.
name|MULTILINE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Sink did not produce the expected output. Actual output was: "
operator|+
name|contents
argument_list|,
name|expectedContentPattern
operator|.
name|matcher
argument_list|(
name|contents
argument_list|)
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that the given contents match what is expected from the test    * metrics when there is pre-existing data.    *    * @param contents the file contents to test    */
DECL|method|assertExtraContents (String contents)
specifier|protected
name|void
name|assertExtraContents
parameter_list|(
name|String
name|contents
parameter_list|)
block|{
comment|// Note that in the below expression we allow tags and metrics to go in
comment|// arbitrary order, but the records must be in order.
specifier|final
name|Pattern
name|expectedContentPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"Extra stuff[\\n\\r]*"
operator|+
literal|"^\\d+\\s+test1.testRecord1:\\s+Context=test1,\\s+"
operator|+
literal|"(testTag1=testTagValue1,\\s+testTag2=testTagValue2|"
operator|+
literal|"testTag2=testTagValue2,\\s+testTag1=testTagValue1),"
operator|+
literal|"\\s+Hostname=.*,\\s+"
operator|+
literal|"(testMetric1=1,\\s+testMetric2=2|testMetric2=2,\\s+testMetric1=1)"
operator|+
literal|"[\\n\\r]*^\\d+\\s+test1.testRecord2:\\s+Context=test1,"
operator|+
literal|"\\s+testTag22=testTagValue22,\\s+Hostname=.*$[\\n\\r]*"
argument_list|,
name|Pattern
operator|.
name|MULTILINE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Sink did not produce the expected output. Actual output was: "
operator|+
name|contents
argument_list|,
name|expectedContentPattern
operator|.
name|matcher
argument_list|(
name|contents
argument_list|)
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Call {@link #doWriteTest} after pre-creating the log file and filling it    * with junk data.    *    * @param path the base path for the test    * @param ignoreErrors whether to ignore errors    * @param allowAppend whether to allow appends    * @param count the number of files to expect    * @return the contents of the final log file    * @throws IOException if a file system operation fails    * @throws InterruptedException if interrupted while calling    * {@link #getNowNotTopOfHour()}    * @throws URISyntaxException if the path is not a valid URI    */
DECL|method|doAppendTest (String path, boolean ignoreErrors, boolean allowAppend, int count)
specifier|protected
name|String
name|doAppendTest
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|ignoreErrors
parameter_list|,
name|boolean
name|allowAppend
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|URISyntaxException
block|{
name|preCreateLogFile
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|doWriteTest
argument_list|(
name|initMetricsSystem
argument_list|(
name|path
argument_list|,
name|ignoreErrors
argument_list|,
name|allowAppend
argument_list|)
argument_list|,
name|path
argument_list|,
name|count
argument_list|)
return|;
block|}
comment|/**    * Create a file at the target path with some known data in it:    *&quot;Extra stuff&quot;.    *    * If the test run is happening within 20 seconds of the top of the hour,    * this method will sleep until the top of the hour.    *    * @param path the target path under which to create the directory for the    * current hour that will contain the log file.    *    * @throws IOException thrown if the file creation fails    * @throws InterruptedException thrown if interrupted while waiting for the    * top of the hour.    * @throws URISyntaxException thrown if the path isn't a valid URI    */
DECL|method|preCreateLogFile (String path)
specifier|protected
name|void
name|preCreateLogFile
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|URISyntaxException
block|{
name|preCreateLogFile
argument_list|(
name|path
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create files at the target path with some known data in them.  Each file    * will have the same content:&quot;Extra stuff&quot;.    *    * If the test run is happening within 20 seconds of the top of the hour,    * this method will sleep until the top of the hour.    *    * @param path the target path under which to create the directory for the    * current hour that will contain the log files.    * @param numFiles the number of log files to create    * @throws IOException thrown if the file creation fails    * @throws InterruptedException thrown if interrupted while waiting for the    * top of the hour.    * @throws URISyntaxException thrown if the path isn't a valid URI    */
DECL|method|preCreateLogFile (String path, int numFiles)
specifier|protected
name|void
name|preCreateLogFile
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|numFiles
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|URISyntaxException
block|{
name|Calendar
name|now
init|=
name|getNowNotTopOfHour
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
name|path
argument_list|)
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
name|DATE_FORMAT
operator|.
name|format
argument_list|(
name|now
operator|.
name|getTime
argument_list|()
argument_list|)
operator|+
literal|"00"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|getLogFilename
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create the log file to force the sink to append
try|try
init|(
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|)
init|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"Extra stuff\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|numFiles
operator|>
literal|1
condition|)
block|{
name|int
name|count
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|numFiles
condition|)
block|{
name|file
operator|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|getLogFilename
argument_list|()
operator|+
literal|"."
operator|+
name|count
argument_list|)
expr_stmt|;
comment|// Create the log file to force the sink to append
try|try
init|(
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|)
init|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"Extra stuff\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|count
operator|+=
literal|1
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Return a calendar based on the current time.  If the current time is very    * near the top of the hour (less than 20 seconds), sleep until the new hour    * before returning a new Calendar instance.    *    * @return a new Calendar instance that isn't near the top of the hour    * @throws InterruptedException if interrupted while sleeping    */
DECL|method|getNowNotTopOfHour ()
specifier|public
name|Calendar
name|getNowNotTopOfHour
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Calendar
name|now
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
decl_stmt|;
comment|// If we're at the very top of the hour, sleep until the next hour
comment|// so that we don't get confused by the directory rolling
if|if
condition|(
operator|(
name|now
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
operator|==
literal|59
operator|)
operator|&&
operator|(
name|now
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
operator|>
literal|40
operator|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
operator|(
literal|61
operator|-
name|now
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
operator|)
operator|*
literal|1000L
argument_list|)
expr_stmt|;
name|now
operator|.
name|setTime
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|now
return|;
block|}
comment|/**    * Assert that the number of log files in the target directory is as expected.    * @param fs the target FileSystem    * @param dir the target directory path    * @param expected the expected number of files    * @throws IOException thrown if listing files fails    */
DECL|method|assertFileCount (FileSystem fs, Path dir, int expected)
specifier|public
name|void
name|assertFileCount
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|dir
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|i
init|=
name|fs
operator|.
name|listFiles
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"The sink created additional unexpected log files. "
operator|+
name|count
operator|+
literal|" files were created"
argument_list|,
name|expected
operator|>=
name|count
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The sink created too few log files. "
operator|+
name|count
operator|+
literal|" files were "
operator|+
literal|"created"
argument_list|,
name|expected
operator|<=
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * This class is a {@link RollingFileSystemSink} wrapper that tracks whether    * an exception has been thrown during operations.    */
DECL|class|MockSink
specifier|public
specifier|static
class|class
name|MockSink
extends|extends
name|RollingFileSystemSink
block|{
DECL|field|errored
specifier|public
specifier|static
specifier|volatile
name|boolean
name|errored
init|=
literal|false
decl_stmt|;
DECL|field|initialized
specifier|public
specifier|static
specifier|volatile
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|init (SubsetConfiguration conf)
specifier|public
name|void
name|init
parameter_list|(
name|SubsetConfiguration
name|conf
parameter_list|)
block|{
try|try
block|{
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MetricsException
name|ex
parameter_list|)
block|{
name|errored
operator|=
literal|true
expr_stmt|;
throw|throw
operator|new
name|MetricsException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|putMetrics (MetricsRecord record)
specifier|public
name|void
name|putMetrics
parameter_list|(
name|MetricsRecord
name|record
parameter_list|)
block|{
try|try
block|{
name|super
operator|.
name|putMetrics
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MetricsException
name|ex
parameter_list|)
block|{
name|errored
operator|=
literal|true
expr_stmt|;
throw|throw
operator|new
name|MetricsException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MetricsException
name|ex
parameter_list|)
block|{
name|errored
operator|=
literal|true
expr_stmt|;
throw|throw
operator|new
name|MetricsException
argument_list|(
name|ex
argument_list|)
throw|;
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
try|try
block|{
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MetricsException
name|ex
parameter_list|)
block|{
name|errored
operator|=
literal|true
expr_stmt|;
throw|throw
operator|new
name|MetricsException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

