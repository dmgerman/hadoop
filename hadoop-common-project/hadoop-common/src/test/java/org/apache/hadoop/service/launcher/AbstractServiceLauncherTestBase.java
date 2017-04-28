begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.service.launcher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|launcher
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|service
operator|.
name|Service
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
name|service
operator|.
name|ServiceOperations
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
name|test
operator|.
name|GenericTestUtils
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
name|util
operator|.
name|ExitCodeProvider
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
name|ExitUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|rules
operator|.
name|Timeout
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
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
DECL|class|AbstractServiceLauncherTestBase
specifier|public
class|class
name|AbstractServiceLauncherTestBase
extends|extends
name|Assert
implements|implements
name|LauncherExitCodes
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
name|AbstractServiceLauncherTestBase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONF_FILE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|CONF_FILE_DIR
init|=
literal|"target/launcher/conf"
decl_stmt|;
comment|/**    * A service which will be automatically stopped on teardown.    */
DECL|field|serviceToTeardown
specifier|private
name|Service
name|serviceToTeardown
decl_stmt|;
comment|/**    * All tests have a short life.    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|15000
argument_list|)
decl_stmt|;
comment|/**    * Rule to provide the method name.    */
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
comment|/**    * Turn off the exit util JVM exits, downgrading them to exception throws.    */
annotation|@
name|BeforeClass
DECL|method|disableJVMExits ()
specifier|public
specifier|static
name|void
name|disableJVMExits
parameter_list|()
block|{
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
name|ExitUtil
operator|.
name|disableSystemHalt
argument_list|()
expr_stmt|;
block|}
comment|/**    * rule to name the thread JUnit.    */
annotation|@
name|Before
DECL|method|nameThread ()
specifier|public
name|void
name|nameThread
parameter_list|()
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"JUnit"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|stopService ()
specifier|public
name|void
name|stopService
parameter_list|()
block|{
name|ServiceOperations
operator|.
name|stopQuietly
argument_list|(
name|serviceToTeardown
argument_list|)
expr_stmt|;
block|}
DECL|method|setServiceToTeardown (Service serviceToTeardown)
specifier|public
name|void
name|setServiceToTeardown
parameter_list|(
name|Service
name|serviceToTeardown
parameter_list|)
block|{
name|this
operator|.
name|serviceToTeardown
operator|=
name|serviceToTeardown
expr_stmt|;
block|}
comment|/**    * Assert that a service is in a state.    * @param service service    * @param expected expected state    */
DECL|method|assertInState (Service service, Service.STATE expected)
specifier|protected
name|void
name|assertInState
parameter_list|(
name|Service
name|service
parameter_list|,
name|Service
operator|.
name|STATE
name|expected
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|Service
operator|.
name|STATE
name|actual
init|=
name|service
operator|.
name|getServiceState
argument_list|()
decl_stmt|;
name|failif
argument_list|(
name|actual
operator|!=
name|expected
argument_list|,
literal|"Service %s in state %s expected state: %s"
argument_list|,
name|service
operator|.
name|getName
argument_list|()
argument_list|,
name|actual
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert a service has stopped.    * @param service service    */
DECL|method|assertStopped (Service service)
specifier|protected
name|void
name|assertStopped
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|assertInState
argument_list|(
name|service
argument_list|,
name|Service
operator|.
name|STATE
operator|.
name|STOPPED
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that an exception code matches the value expected.    * @param expected expected value    * @param text text in exception -can be null    * @param e exception providing the actual value    */
DECL|method|assertExceptionDetails (int expected, String text, ExitCodeProvider e)
specifier|protected
name|void
name|assertExceptionDetails
parameter_list|(
name|int
name|expected
parameter_list|,
name|String
name|text
parameter_list|,
name|ExitCodeProvider
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|String
name|toString
init|=
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|exitCode
init|=
name|e
operator|.
name|getExitCode
argument_list|()
decl_stmt|;
name|boolean
name|failed
init|=
name|expected
operator|!=
name|exitCode
decl_stmt|;
name|failed
operator||=
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|text
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|contains
argument_list|(
name|toString
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|failif
argument_list|(
name|failed
argument_list|,
literal|"Expected exception with exit code %d and text \"%s\""
operator|+
literal|" but got the exit code %d"
operator|+
literal|" in \"%s\""
argument_list|,
name|expected
argument_list|,
name|text
argument_list|,
name|exitCode
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert the launch come was a service creation failure.    * @param classname argument    */
DECL|method|assertServiceCreationFails (String classname)
specifier|protected
name|void
name|assertServiceCreationFails
parameter_list|(
name|String
name|classname
parameter_list|)
block|{
name|assertLaunchOutcome
argument_list|(
name|EXIT_SERVICE_CREATION_FAILURE
argument_list|,
literal|""
argument_list|,
name|classname
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert a launch outcome.    * @param expected expected value    * @param text text in exception -can be null    * @param args CLI args    */
DECL|method|assertLaunchOutcome (int expected, String text, String... args)
specifier|protected
name|void
name|assertLaunchOutcome
parameter_list|(
name|int
name|expected
parameter_list|,
name|String
name|text
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Launching service with expected outcome {}"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
name|ServiceLauncher
operator|.
name|serviceMain
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceLaunchException
name|e
parameter_list|)
block|{
name|assertExceptionDetails
argument_list|(
name|expected
argument_list|,
name|text
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assert a launch runs.    * @param args CLI args    */
DECL|method|assertRuns (String... args)
specifier|protected
name|void
name|assertRuns
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
name|assertLaunchOutcome
argument_list|(
literal|0
argument_list|,
literal|""
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Init and start a service.    * @param service the service    * @return the service    */
DECL|method|run (S service)
specifier|protected
parameter_list|<
name|S
extends|extends
name|Service
parameter_list|>
name|S
name|run
parameter_list|(
name|S
name|service
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|service
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|service
return|;
block|}
comment|/**    * Save a configuration to a config file in the target dir.    * @param conf config    * @return absolute path    * @throws IOException problems    */
DECL|method|configFile (Configuration conf)
specifier|protected
name|String
name|configFile
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
name|CONF_FILE_DIR
argument_list|)
decl_stmt|;
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|file
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"conf"
argument_list|,
literal|".xml"
argument_list|,
name|directory
argument_list|)
decl_stmt|;
try|try
init|(
name|OutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
name|conf
operator|.
name|writeXml
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
return|return
name|file
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
comment|/**    * Create a new config from key-val pairs.    * @param kvp a list of key, value, ...    * @return a new configuration    */
DECL|method|newConf (String... kvp)
specifier|protected
name|Configuration
name|newConf
parameter_list|(
name|String
modifier|...
name|kvp
parameter_list|)
block|{
name|int
name|len
init|=
name|kvp
operator|.
name|length
decl_stmt|;
name|assertEquals
argument_list|(
literal|"unbalanced keypair len of "
operator|+
name|len
argument_list|,
literal|0
argument_list|,
name|len
operator|%
literal|2
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
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
name|len
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|kvp
index|[
name|i
index|]
argument_list|,
name|kvp
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|conf
return|;
block|}
comment|/** varargs to list conversion. */
DECL|method|asList (String... args)
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|asList
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Launch a service with the given list of arguments. Returns    * the service launcher, from which the created service can be extracted    * via {@link ServiceLauncher#getService()}.    * The service is has its execute() method called, but     * @param serviceClass service class to create    * @param conf configuration    * @param args list of arguments    * @param execute execute/wait for the service to stop    * @param<S> service type    * @return the service launcher    * @throws ExitUtil.ExitException if the launch's exit code != 0    */
DECL|method|launchService ( Class serviceClass, Configuration conf, List<String> args, boolean execute)
specifier|protected
parameter_list|<
name|S
extends|extends
name|Service
parameter_list|>
name|ServiceLauncher
argument_list|<
name|S
argument_list|>
name|launchService
parameter_list|(
name|Class
name|serviceClass
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|,
name|boolean
name|execute
parameter_list|)
throws|throws
name|ExitUtil
operator|.
name|ExitException
block|{
name|ServiceLauncher
argument_list|<
name|S
argument_list|>
name|serviceLauncher
init|=
operator|new
name|ServiceLauncher
argument_list|<>
argument_list|(
name|serviceClass
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|ExitUtil
operator|.
name|ExitException
name|exitException
init|=
name|serviceLauncher
operator|.
name|launchService
argument_list|(
name|conf
argument_list|,
name|args
argument_list|,
literal|false
argument_list|,
name|execute
argument_list|)
decl_stmt|;
if|if
condition|(
name|exitException
operator|.
name|getExitCode
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// success
return|return
name|serviceLauncher
return|;
block|}
else|else
block|{
comment|// launch failure
throw|throw
name|exitException
throw|;
block|}
block|}
comment|/**    * Launch a service with the given list of arguments. Returns    * the service launcher, from which the created service can be extracted.    * via {@link ServiceLauncher#getService()}.    *    * This call DOES NOT call {@link LaunchableService#execute()} or wait for    * a simple service to finish. It returns the service that has been created,    * initialized and started.    * @param serviceClass service class to create    * @param conf configuration    * @param args varargs launch arguments    * @param<S> service type    * @return the service launcher    * @throws ExitUtil.ExitException  if the launch's exit code != 0    */
DECL|method|launchService ( Class serviceClass, Configuration conf, String... args)
specifier|protected
parameter_list|<
name|S
extends|extends
name|Service
parameter_list|>
name|ServiceLauncher
argument_list|<
name|S
argument_list|>
name|launchService
parameter_list|(
name|Class
name|serviceClass
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|ExitUtil
operator|.
name|ExitException
block|{
return|return
name|launchService
argument_list|(
name|serviceClass
argument_list|,
name|conf
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Launch expecting an exception.    * @param serviceClass service class to create    * @param conf configuration    * @param expectedText expected text; may be "" or null    * @param errorCode error code     * @param args varargs launch arguments    * @return the exception returned if there was a match    * @throws AssertionError on a mismatch of expectation and actual    */
DECL|method|launchExpectingException (Class serviceClass, Configuration conf, String expectedText, int errorCode, String... args)
specifier|protected
name|ExitUtil
operator|.
name|ExitException
name|launchExpectingException
parameter_list|(
name|Class
name|serviceClass
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|expectedText
parameter_list|,
name|int
name|errorCode
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
try|try
block|{
name|ServiceLauncher
argument_list|<
name|Service
argument_list|>
name|launch
init|=
name|launchService
argument_list|(
name|serviceClass
argument_list|,
name|conf
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|failf
argument_list|(
literal|"Expected an exception with error code %d and text \"%s\" "
operator|+
literal|" -but the service completed with :%s"
argument_list|,
name|errorCode
argument_list|,
name|expectedText
argument_list|,
name|launch
operator|.
name|getServiceException
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|ExitUtil
operator|.
name|ExitException
name|e
parameter_list|)
block|{
name|int
name|actualCode
init|=
name|e
operator|.
name|getExitCode
argument_list|()
decl_stmt|;
name|boolean
name|condition
init|=
name|errorCode
operator|!=
name|actualCode
operator|||
operator|!
name|StringUtils
operator|.
name|contains
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|expectedText
argument_list|)
decl_stmt|;
name|failif
argument_list|(
name|condition
argument_list|,
literal|"Expected an exception with error code %d and text \"%s\" "
operator|+
literal|" -but the service threw an exception with exit code %d: %s"
argument_list|,
name|errorCode
argument_list|,
name|expectedText
argument_list|,
name|actualCode
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
block|}
end_class

end_unit

