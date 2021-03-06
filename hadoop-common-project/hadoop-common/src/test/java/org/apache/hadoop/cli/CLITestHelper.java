begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cli
operator|.
name|util
operator|.
name|*
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
name|cli
operator|.
name|util
operator|.
name|CommandExecutor
operator|.
name|Result
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
name|CommonConfigurationKeys
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
name|Shell
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
name|StringUtils
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Tests for the Command Line Interface (CLI)  */
end_comment

begin_class
DECL|class|CLITestHelper
specifier|public
class|class
name|CLITestHelper
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
name|CLITestHelper
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// In this mode, it runs the command and compares the actual output
comment|// with the expected output
DECL|field|TESTMODE_TEST
specifier|public
specifier|static
specifier|final
name|String
name|TESTMODE_TEST
init|=
literal|"test"
decl_stmt|;
comment|// Run the tests
comment|// If it is set to nocompare, run the command and do not compare.
comment|// This can be useful populate the testConfig.xml file the first time
comment|// a new command is added
DECL|field|TESTMODE_NOCOMPARE
specifier|public
specifier|static
specifier|final
name|String
name|TESTMODE_NOCOMPARE
init|=
literal|"nocompare"
decl_stmt|;
DECL|field|TEST_CACHE_DATA_DIR
specifier|public
specifier|static
specifier|final
name|String
name|TEST_CACHE_DATA_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.cache.data"
argument_list|,
literal|"build/test/cache"
argument_list|)
decl_stmt|;
comment|//By default, run the tests. The other mode is to run the commands and not
comment|// compare the output
DECL|field|testMode
specifier|protected
name|String
name|testMode
init|=
name|TESTMODE_TEST
decl_stmt|;
comment|// Storage for tests read in from the config file
DECL|field|testsFromConfigFile
specifier|protected
name|ArrayList
argument_list|<
name|CLITestData
argument_list|>
name|testsFromConfigFile
init|=
literal|null
decl_stmt|;
DECL|field|testComparators
specifier|protected
name|ArrayList
argument_list|<
name|ComparatorData
argument_list|>
name|testComparators
init|=
literal|null
decl_stmt|;
DECL|field|comparatorData
specifier|protected
name|ComparatorData
name|comparatorData
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|protected
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|clitestDataDir
specifier|protected
name|String
name|clitestDataDir
init|=
literal|null
decl_stmt|;
DECL|field|username
specifier|protected
name|String
name|username
init|=
literal|null
decl_stmt|;
comment|/**    * Read the test config file - testConf.xml    */
DECL|method|readTestConfigFile ()
specifier|protected
name|void
name|readTestConfigFile
parameter_list|()
block|{
name|String
name|testConfigFile
init|=
name|getTestFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|testsFromConfigFile
operator|==
literal|null
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|testConfigFile
operator|=
name|TEST_CACHE_DATA_DIR
operator|+
name|File
operator|.
name|separator
operator|+
name|testConfigFile
expr_stmt|;
try|try
block|{
name|SAXParser
name|p
init|=
operator|(
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
operator|)
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|p
operator|.
name|parse
argument_list|(
name|testConfigFile
argument_list|,
name|getConfigParser
argument_list|()
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception while reading test config file {}:"
argument_list|,
name|testConfigFile
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|success
operator|=
literal|false
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Error reading test config file"
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Method decides what is a proper configuration file parser for this type    * of CLI tests.    * Ancestors need to override the implementation if a parser with additional    * features is needed. Also, such ancestor has to provide its own    * TestConfigParser implementation    * @return an instance of TestConfigFileParser class    */
DECL|method|getConfigParser ()
specifier|protected
name|TestConfigFileParser
name|getConfigParser
parameter_list|()
block|{
return|return
operator|new
name|TestConfigFileParser
argument_list|()
return|;
block|}
DECL|method|getTestFile ()
specifier|protected
name|String
name|getTestFile
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/*    * Setup    */
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Read the testConfig.xml file
name|readTestConfigFile
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHORIZATION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|clitestDataDir
operator|=
operator|new
name|File
argument_list|(
name|TEST_CACHE_DATA_DIR
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'+'
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tear down    */
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|displayResults
argument_list|()
expr_stmt|;
block|}
comment|/**    * Expand the commands from the test config xml file    * @param cmd    * @return String expanded command    */
DECL|method|expandCommand (final String cmd)
specifier|protected
name|String
name|expandCommand
parameter_list|(
specifier|final
name|String
name|cmd
parameter_list|)
block|{
name|String
name|expCmd
init|=
name|cmd
decl_stmt|;
name|expCmd
operator|=
name|expCmd
operator|.
name|replaceAll
argument_list|(
literal|"CLITEST_DATA"
argument_list|,
name|clitestDataDir
argument_list|)
expr_stmt|;
name|expCmd
operator|=
name|expCmd
operator|.
name|replaceAll
argument_list|(
literal|"USERNAME"
argument_list|,
name|username
argument_list|)
expr_stmt|;
return|return
name|expCmd
return|;
block|}
comment|/**    * Display the summarized results    */
DECL|method|displayResults ()
specifier|private
name|void
name|displayResults
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Detailed results:"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"----------------------------------\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testsFromConfigFile
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CLITestData
name|td
init|=
name|testsFromConfigFile
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|boolean
name|testResult
init|=
name|td
operator|.
name|getTestResult
argument_list|()
decl_stmt|;
comment|// Display the details only if there is a failure
if|if
condition|(
operator|!
name|testResult
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"-------------------------------------------"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"                    Test ID: ["
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"           Test Description: ["
operator|+
name|td
operator|.
name|getTestDesc
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|testCommands
init|=
name|td
operator|.
name|getTestCommands
argument_list|()
decl_stmt|;
for|for
control|(
name|CLICommand
name|cmd
range|:
name|testCommands
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"              Test Commands: ["
operator|+
name|expandCommand
argument_list|(
name|cmd
operator|.
name|getCmd
argument_list|()
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|cleanupCommands
init|=
name|td
operator|.
name|getCleanupCommands
argument_list|()
decl_stmt|;
for|for
control|(
name|CLICommand
name|cmd
range|:
name|cleanupCommands
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"           Cleanup Commands: ["
operator|+
name|expandCommand
argument_list|(
name|cmd
operator|.
name|getCmd
argument_list|()
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|ComparatorData
argument_list|>
name|compdata
init|=
name|td
operator|.
name|getComparatorData
argument_list|()
decl_stmt|;
for|for
control|(
name|ComparatorData
name|cd
range|:
name|compdata
control|)
block|{
name|boolean
name|resultBoolean
init|=
name|cd
operator|.
name|getTestResult
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"                 Comparator: ["
operator|+
name|cd
operator|.
name|getComparatorType
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"         Comparision result:   ["
operator|+
operator|(
name|resultBoolean
condition|?
literal|"pass"
else|:
literal|"fail"
operator|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"            Expected output:   ["
operator|+
name|expandCommand
argument_list|(
name|cd
operator|.
name|getExpectedOutput
argument_list|()
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"              Actual output:   ["
operator|+
name|cd
operator|.
name|getActualOutput
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Summary results:"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"----------------------------------\n"
argument_list|)
expr_stmt|;
name|boolean
name|overallResults
init|=
literal|true
decl_stmt|;
name|int
name|totalPass
init|=
literal|0
decl_stmt|;
name|int
name|totalFail
init|=
literal|0
decl_stmt|;
name|int
name|totalComparators
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testsFromConfigFile
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CLITestData
name|td
init|=
name|testsFromConfigFile
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|totalComparators
operator|+=
name|testsFromConfigFile
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getComparatorData
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|boolean
name|resultBoolean
init|=
name|td
operator|.
name|getTestResult
argument_list|()
decl_stmt|;
if|if
condition|(
name|resultBoolean
condition|)
block|{
name|totalPass
operator|++
expr_stmt|;
block|}
else|else
block|{
name|totalFail
operator|++
expr_stmt|;
block|}
name|overallResults
operator|&=
name|resultBoolean
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"               Testing mode: "
operator|+
name|testMode
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"             Overall result: "
operator|+
operator|(
name|overallResults
condition|?
literal|"+++ PASS +++"
else|:
literal|"--- FAIL ---"
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|totalPass
operator|+
name|totalFail
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"               # Tests pass: "
operator|+
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"               # Tests fail: "
operator|+
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"               # Tests pass: "
operator|+
name|totalPass
operator|+
literal|" ("
operator|+
operator|(
literal|100
operator|*
name|totalPass
operator|/
operator|(
name|totalPass
operator|+
name|totalFail
operator|)
operator|)
operator|+
literal|"%)"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"               # Tests fail: "
operator|+
name|totalFail
operator|+
literal|" ("
operator|+
operator|(
literal|100
operator|*
name|totalFail
operator|/
operator|(
name|totalPass
operator|+
name|totalFail
operator|)
operator|)
operator|+
literal|"%)"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"         # Validations done: "
operator|+
name|totalComparators
operator|+
literal|" (each test may do multiple validations)"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Failing tests:"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"--------------"
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|boolean
name|foundTests
init|=
literal|false
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|testsFromConfigFile
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|resultBoolean
init|=
name|testsFromConfigFile
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getTestResult
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|resultBoolean
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|": "
operator|+
name|testsFromConfigFile
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getTestDesc
argument_list|()
argument_list|)
expr_stmt|;
name|foundTests
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|foundTests
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"NONE"
argument_list|)
expr_stmt|;
block|}
name|foundTests
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Passing tests:"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"--------------"
argument_list|)
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|testsFromConfigFile
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|resultBoolean
init|=
name|testsFromConfigFile
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getTestResult
argument_list|()
decl_stmt|;
if|if
condition|(
name|resultBoolean
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|": "
operator|+
name|testsFromConfigFile
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getTestDesc
argument_list|()
argument_list|)
expr_stmt|;
name|foundTests
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|foundTests
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"NONE"
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"One of the tests failed. "
operator|+
literal|"See the Detailed results to identify "
operator|+
literal|"the command that failed"
argument_list|,
name|overallResults
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compare the actual output with the expected output    * @param compdata    * @return    */
DECL|method|compareTestOutput (ComparatorData compdata, Result cmdResult)
specifier|private
name|boolean
name|compareTestOutput
parameter_list|(
name|ComparatorData
name|compdata
parameter_list|,
name|Result
name|cmdResult
parameter_list|)
block|{
comment|// Compare the output based on the comparator
name|String
name|comparatorType
init|=
name|compdata
operator|.
name|getComparatorType
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|comparatorClass
init|=
literal|null
decl_stmt|;
comment|// If testMode is "test", then run the command and compare the output
comment|// If testMode is "nocompare", then run the command and dump the output.
comment|// Do not compare
name|boolean
name|compareOutput
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|testMode
operator|.
name|equals
argument_list|(
name|TESTMODE_TEST
argument_list|)
condition|)
block|{
try|try
block|{
comment|// Initialize the comparator class and run its compare method
name|comparatorClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.hadoop.cli.util."
operator|+
name|comparatorType
argument_list|)
expr_stmt|;
name|ComparatorBase
name|comp
init|=
operator|(
name|ComparatorBase
operator|)
name|comparatorClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|compareOutput
operator|=
name|comp
operator|.
name|compare
argument_list|(
name|cmdResult
operator|.
name|getCommandOutput
argument_list|()
argument_list|,
name|expandCommand
argument_list|(
name|compdata
operator|.
name|getExpectedOutput
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error in instantiating the comparator"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|compareOutput
return|;
block|}
DECL|method|compareTextExitCode (ComparatorData compdata, Result cmdResult)
specifier|private
name|boolean
name|compareTextExitCode
parameter_list|(
name|ComparatorData
name|compdata
parameter_list|,
name|Result
name|cmdResult
parameter_list|)
block|{
return|return
name|compdata
operator|.
name|getExitCode
argument_list|()
operator|==
name|cmdResult
operator|.
name|getExitCode
argument_list|()
return|;
block|}
comment|/***********************************    ************* TESTS RUNNER    *********************************/
DECL|method|testAll ()
specifier|public
name|void
name|testAll
parameter_list|()
block|{
name|assertTrue
argument_list|(
literal|"Number of tests has to be greater then zero"
argument_list|,
name|testsFromConfigFile
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"TestAll"
argument_list|)
expr_stmt|;
comment|// Run the tests defined in the testConf.xml config file.
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|testsFromConfigFile
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|CLITestData
name|testdata
init|=
name|testsFromConfigFile
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
comment|// Execute the test commands
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|testCommands
init|=
name|testdata
operator|.
name|getTestCommands
argument_list|()
decl_stmt|;
name|Result
name|cmdResult
init|=
literal|null
decl_stmt|;
for|for
control|(
name|CLICommand
name|cmd
range|:
name|testCommands
control|)
block|{
try|try
block|{
name|cmdResult
operator|=
name|execute
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|overallTCResult
init|=
literal|true
decl_stmt|;
comment|// Run comparators
name|ArrayList
argument_list|<
name|ComparatorData
argument_list|>
name|compdata
init|=
name|testdata
operator|.
name|getComparatorData
argument_list|()
decl_stmt|;
for|for
control|(
name|ComparatorData
name|cd
range|:
name|compdata
control|)
block|{
specifier|final
name|String
name|comptype
init|=
name|cd
operator|.
name|getComparatorType
argument_list|()
decl_stmt|;
name|boolean
name|compareOutput
init|=
literal|false
decl_stmt|;
name|boolean
name|compareExitCode
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|comptype
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"none"
argument_list|)
condition|)
block|{
name|compareOutput
operator|=
name|compareTestOutput
argument_list|(
name|cd
argument_list|,
name|cmdResult
argument_list|)
expr_stmt|;
if|if
condition|(
name|cd
operator|.
name|getExitCode
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
comment|// No need to check exit code if not specified
name|compareExitCode
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|compareExitCode
operator|=
name|compareTextExitCode
argument_list|(
name|cd
argument_list|,
name|cmdResult
argument_list|)
expr_stmt|;
block|}
name|overallTCResult
operator|&=
operator|(
name|compareOutput
operator|&
name|compareExitCode
operator|)
expr_stmt|;
block|}
name|cd
operator|.
name|setExitCode
argument_list|(
name|cmdResult
operator|.
name|getExitCode
argument_list|()
argument_list|)
expr_stmt|;
name|cd
operator|.
name|setActualOutput
argument_list|(
name|cmdResult
operator|.
name|getCommandOutput
argument_list|()
argument_list|)
expr_stmt|;
name|cd
operator|.
name|setTestResult
argument_list|(
name|compareOutput
argument_list|)
expr_stmt|;
block|}
name|testdata
operator|.
name|setTestResult
argument_list|(
name|overallTCResult
argument_list|)
expr_stmt|;
comment|// Execute the cleanup commands
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|cleanupCommands
init|=
name|testdata
operator|.
name|getCleanupCommands
argument_list|()
decl_stmt|;
for|for
control|(
name|CLICommand
name|cmd
range|:
name|cleanupCommands
control|)
block|{
try|try
block|{
name|execute
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * this method has to be overridden by an ancestor    */
DECL|method|execute (CLICommand cmd)
specifier|protected
name|CommandExecutor
operator|.
name|Result
name|execute
parameter_list|(
name|CLICommand
name|cmd
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Unknown type of test command:"
operator|+
name|cmd
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
comment|/*    * Parser class for the test config xml file    */
DECL|class|TestConfigFileParser
class|class
name|TestConfigFileParser
extends|extends
name|DefaultHandler
block|{
DECL|field|charString
name|String
name|charString
init|=
literal|null
decl_stmt|;
DECL|field|td
name|CLITestData
name|td
init|=
literal|null
decl_stmt|;
DECL|field|testCommands
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|testCommands
init|=
literal|null
decl_stmt|;
DECL|field|cleanupCommands
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|cleanupCommands
init|=
literal|null
decl_stmt|;
DECL|field|runOnWindows
name|boolean
name|runOnWindows
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
DECL|method|startDocument ()
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|testsFromConfigFile
operator|=
operator|new
name|ArrayList
argument_list|<
name|CLITestData
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startElement (String uri, String localName, String qName, Attributes attributes)
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"test"
argument_list|)
condition|)
block|{
name|td
operator|=
operator|new
name|CLITestData
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"test-commands"
argument_list|)
condition|)
block|{
name|testCommands
operator|=
operator|new
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"cleanup-commands"
argument_list|)
condition|)
block|{
name|cleanupCommands
operator|=
operator|new
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"comparators"
argument_list|)
condition|)
block|{
name|testComparators
operator|=
operator|new
name|ArrayList
argument_list|<
name|ComparatorData
argument_list|>
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"comparator"
argument_list|)
condition|)
block|{
name|comparatorData
operator|=
operator|new
name|ComparatorData
argument_list|()
expr_stmt|;
name|comparatorData
operator|.
name|setExitCode
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|charString
operator|=
literal|""
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|endElement (String uri, String localName,String qName)
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"description"
argument_list|)
condition|)
block|{
name|td
operator|.
name|setTestDesc
argument_list|(
name|charString
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"windows"
argument_list|)
condition|)
block|{
name|runOnWindows
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|charString
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"test-commands"
argument_list|)
condition|)
block|{
name|td
operator|.
name|setTestCommands
argument_list|(
name|testCommands
argument_list|)
expr_stmt|;
name|testCommands
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"cleanup-commands"
argument_list|)
condition|)
block|{
name|td
operator|.
name|setCleanupCommands
argument_list|(
name|cleanupCommands
argument_list|)
expr_stmt|;
name|cleanupCommands
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"command"
argument_list|)
condition|)
block|{
if|if
condition|(
name|testCommands
operator|!=
literal|null
condition|)
block|{
name|testCommands
operator|.
name|add
argument_list|(
operator|new
name|CLITestCmd
argument_list|(
name|charString
argument_list|,
operator|new
name|CLICommandFS
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cleanupCommands
operator|!=
literal|null
condition|)
block|{
name|cleanupCommands
operator|.
name|add
argument_list|(
operator|new
name|CLITestCmd
argument_list|(
name|charString
argument_list|,
operator|new
name|CLICommandFS
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"comparators"
argument_list|)
condition|)
block|{
name|td
operator|.
name|setComparatorData
argument_list|(
name|testComparators
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"comparator"
argument_list|)
condition|)
block|{
name|testComparators
operator|.
name|add
argument_list|(
name|comparatorData
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"type"
argument_list|)
condition|)
block|{
name|comparatorData
operator|.
name|setComparatorType
argument_list|(
name|charString
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"expected-output"
argument_list|)
condition|)
block|{
name|comparatorData
operator|.
name|setExpectedOutput
argument_list|(
name|charString
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"expected-exit-code"
argument_list|)
condition|)
block|{
name|comparatorData
operator|.
name|setExitCode
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|charString
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"test"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Shell
operator|.
name|WINDOWS
operator|||
name|runOnWindows
condition|)
block|{
name|testsFromConfigFile
operator|.
name|add
argument_list|(
name|td
argument_list|)
expr_stmt|;
block|}
name|td
operator|=
literal|null
expr_stmt|;
name|runOnWindows
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"mode"
argument_list|)
condition|)
block|{
name|testMode
operator|=
name|charString
expr_stmt|;
if|if
condition|(
operator|!
name|testMode
operator|.
name|equals
argument_list|(
name|TESTMODE_NOCOMPARE
argument_list|)
operator|&&
operator|!
name|testMode
operator|.
name|equals
argument_list|(
name|TESTMODE_TEST
argument_list|)
condition|)
block|{
name|testMode
operator|=
name|TESTMODE_TEST
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|characters (char[] ch, int start, int length)
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|charString
operator|+=
name|s
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

