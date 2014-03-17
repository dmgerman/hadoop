begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.applications.unmanagedamlauncher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|applications
operator|.
name|unmanagedamlauncher
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|net
operator|.
name|URL
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
name|net
operator|.
name|NetUtils
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
name|api
operator|.
name|ApplicationMasterProtocol
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
name|api
operator|.
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|FinishApplicationMasterResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
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
name|api
operator|.
name|records
operator|.
name|FinalApplicationStatus
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
name|client
operator|.
name|ClientRMProxy
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
name|conf
operator|.
name|YarnConfiguration
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
name|server
operator|.
name|MiniYARNCluster
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
name|BeforeClass
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

begin_class
DECL|class|TestUnmanagedAMLauncher
specifier|public
class|class
name|TestUnmanagedAMLauncher
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestUnmanagedAMLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|yarnCluster
specifier|protected
specifier|static
name|MiniYARNCluster
name|yarnCluster
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|protected
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting up YARN cluster"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|,
literal|128
argument_list|)
expr_stmt|;
if|if
condition|(
name|yarnCluster
operator|==
literal|null
condition|)
block|{
name|yarnCluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestUnmanagedAMLauncher
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//get the address
name|Configuration
name|yarnClusterConfig
init|=
name|yarnCluster
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniYARN ResourceManager published address: "
operator|+
name|yarnClusterConfig
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniYARN ResourceManager published web address: "
operator|+
name|yarnClusterConfig
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|webapp
init|=
name|yarnClusterConfig
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Web app address still unbound to a host at "
operator|+
name|webapp
argument_list|,
operator|!
name|webapp
operator|.
name|startsWith
argument_list|(
literal|"0.0.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Yarn webapp is at "
operator|+
name|webapp
argument_list|)
expr_stmt|;
name|URL
name|url
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"yarn-site.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not find 'yarn-site.xml' dummy file in classpath"
argument_list|)
throw|;
block|}
comment|//write the document to a buffer (not directly to the file, as that
comment|//can cause the file being written to get read -which will then fail.
name|ByteArrayOutputStream
name|bytesOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|yarnClusterConfig
operator|.
name|writeXml
argument_list|(
name|bytesOut
argument_list|)
expr_stmt|;
name|bytesOut
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//write the bytes to the file in the classpath
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|bytesOut
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"setup thread sleep interrupted. message="
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|yarnCluster
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|yarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|yarnCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|method|getTestRuntimeClasspath ()
specifier|private
specifier|static
name|String
name|getTestRuntimeClasspath
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to generate classpath for app master from current thread's classpath"
argument_list|)
expr_stmt|;
name|String
name|envClassPath
init|=
literal|""
decl_stmt|;
name|String
name|cp
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cp
operator|!=
literal|null
condition|)
block|{
name|envClassPath
operator|+=
name|cp
operator|.
name|trim
argument_list|()
operator|+
name|File
operator|.
name|pathSeparator
expr_stmt|;
block|}
comment|// yarn-site.xml at this location contains proper config for mini cluster
name|ClassLoader
name|thisClassLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
name|thisClassLoader
operator|.
name|getResource
argument_list|(
literal|"yarn-site.xml"
argument_list|)
decl_stmt|;
name|envClassPath
operator|+=
operator|new
name|File
argument_list|(
name|url
operator|.
name|getFile
argument_list|()
argument_list|)
operator|.
name|getParent
argument_list|()
expr_stmt|;
return|return
name|envClassPath
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testDSShell ()
specifier|public
name|void
name|testDSShell
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|classpath
init|=
name|getTestRuntimeClasspath
argument_list|()
decl_stmt|;
name|String
name|javaHome
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"JAVA_HOME"
argument_list|)
decl_stmt|;
if|if
condition|(
name|javaHome
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"JAVA_HOME not defined. Test not running."
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
index|[]
name|args
init|=
block|{
literal|"--classpath"
block|,
name|classpath
block|,
literal|"--queue"
block|,
literal|"default"
block|,
literal|"--cmd"
block|,
name|javaHome
operator|+
literal|"/bin/java -Xmx512m "
operator|+
name|TestUnmanagedAMLauncher
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" success"
block|}
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing Launcher"
argument_list|)
expr_stmt|;
name|UnmanagedAMLauncher
name|launcher
init|=
operator|new
name|UnmanagedAMLauncher
argument_list|(
operator|new
name|Configuration
argument_list|(
name|yarnCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|initSuccess
init|=
name|launcher
operator|.
name|init
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|initSuccess
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running Launcher"
argument_list|)
expr_stmt|;
name|boolean
name|result
init|=
name|launcher
operator|.
name|run
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Launcher run completed. Result="
operator|+
name|result
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testDSShellError ()
specifier|public
name|void
name|testDSShellError
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|classpath
init|=
name|getTestRuntimeClasspath
argument_list|()
decl_stmt|;
name|String
name|javaHome
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"JAVA_HOME"
argument_list|)
decl_stmt|;
if|if
condition|(
name|javaHome
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"JAVA_HOME not defined. Test not running."
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
index|[]
name|args
init|=
block|{
literal|"--classpath"
block|,
name|classpath
block|,
literal|"--queue"
block|,
literal|"default"
block|,
literal|"--cmd"
block|,
name|javaHome
operator|+
literal|"/bin/java -Xmx512m "
operator|+
name|TestUnmanagedAMLauncher
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" failure"
block|}
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing Launcher"
argument_list|)
expr_stmt|;
name|UnmanagedAMLauncher
name|launcher
init|=
operator|new
name|UnmanagedAMLauncher
argument_list|(
operator|new
name|Configuration
argument_list|(
name|yarnCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|initSuccess
init|=
name|launcher
operator|.
name|init
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|initSuccess
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running Launcher"
argument_list|)
expr_stmt|;
try|try
block|{
name|launcher
operator|.
name|run
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception to occur as launch should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
comment|// provide main method so this class can act as AM
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"success"
argument_list|)
condition|)
block|{
name|ApplicationMasterProtocol
name|client
init|=
name|ClientRMProxy
operator|.
name|createRMProxy
argument_list|(
name|conf
argument_list|,
name|ApplicationMasterProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
name|client
operator|.
name|registerApplicationMaster
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
name|NetUtils
operator|.
name|getHostname
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|FinishApplicationMasterResponse
name|resp
init|=
name|client
operator|.
name|finishApplicationMaster
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|,
literal|"success"
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resp
operator|.
name|getIsUnregistered
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

