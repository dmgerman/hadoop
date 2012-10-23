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
name|InputStream
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
name|junit
operator|.
name|framework
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
name|getName
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
name|yarnCluster
operator|.
name|getConfig
argument_list|()
operator|.
name|writeXml
argument_list|(
name|os
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
name|yarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
name|yarnCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getTestRuntimeClasspath ()
specifier|private
specifier|static
name|String
name|getTestRuntimeClasspath
parameter_list|()
block|{
name|InputStream
name|classpathFileStream
init|=
literal|null
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
name|String
name|envClassPath
init|=
literal|""
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to generate classpath for app master from current thread's classpath"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Create classpath from generated classpath
comment|// Check maven pom.xml for generated classpath info
comment|// Works if compile time env is same as runtime. Mainly tests.
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
name|String
name|generatedClasspathFile
init|=
literal|"yarn-apps-am-generated-classpath"
decl_stmt|;
name|classpathFileStream
operator|=
name|thisClassLoader
operator|.
name|getResourceAsStream
argument_list|(
name|generatedClasspathFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|classpathFileStream
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not classpath resource from class loader"
argument_list|)
expr_stmt|;
return|return
name|envClassPath
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Readable bytes from stream="
operator|+
name|classpathFileStream
operator|.
name|available
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|classpathFileStream
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|cp
init|=
name|reader
operator|.
name|readLine
argument_list|()
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not find the necessary resource to generate class path for tests. Error="
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|classpathFileStream
operator|!=
literal|null
condition|)
block|{
name|classpathFileStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to close class path file stream or reader. Error="
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|envClassPath
return|;
block|}
annotation|@
name|Test
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
comment|// start dist-shell with 0 containers because container launch will fail if
comment|// there are no dist cache resources.
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
literal|"org.apache.hadoop.yarn.applications.distributedshell.ApplicationMaster "
operator|+
literal|"--container_memory 128 --num_containers 0 --priority 0 --shell_command ls"
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
block|}
end_class

end_unit

