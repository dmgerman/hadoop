begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.nodelabels
package|package
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
name|nodemanager
operator|.
name|nodelabels
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
name|PrintWriter
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
name|FileContext
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
name|service
operator|.
name|ServiceStateException
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
name|nodelabels
operator|.
name|NodeLabelTestBase
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
name|Test
import|;
end_import

begin_class
DECL|class|TestScriptBasedNodeLabelsProvider
specifier|public
class|class
name|TestScriptBasedNodeLabelsProvider
extends|extends
name|NodeLabelTestBase
block|{
DECL|field|testRootDir
specifier|protected
specifier|static
name|File
name|testRootDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestScriptBasedNodeLabelsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-localDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|nodeLabelsScriptFile
specifier|private
specifier|final
name|File
name|nodeLabelsScriptFile
init|=
operator|new
name|File
argument_list|(
name|testRootDir
argument_list|,
name|Shell
operator|.
name|appendScriptExtension
argument_list|(
literal|"failingscript"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|nodeLabelsProvider
specifier|private
name|ScriptBasedNodeLabelsProvider
name|nodeLabelsProvider
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|testRootDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|nodeLabelsProvider
operator|=
operator|new
name|ScriptBasedNodeLabelsProvider
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|testRootDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|testRootDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodeLabelsProvider
operator|!=
literal|null
condition|)
block|{
name|nodeLabelsProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getConfForNodeLabelScript ()
specifier|private
name|Configuration
name|getConfForNodeLabelScript
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_SCRIPT_BASED_NODE_LABELS_PROVIDER_PATH
argument_list|,
name|nodeLabelsScriptFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// set bigger interval so that test cases can be run
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS
argument_list|,
literal|1
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000l
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NODE_LABELS_PROVIDER_FETCH_TIMEOUT_MS
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|writeNodeLabelsScriptFile (String scriptStr, boolean setExecutable)
specifier|private
name|void
name|writeNodeLabelsScriptFile
parameter_list|(
name|String
name|scriptStr
parameter_list|,
name|boolean
name|setExecutable
parameter_list|)
throws|throws
name|IOException
block|{
name|PrintWriter
name|pw
init|=
literal|null
decl_stmt|;
try|try
block|{
name|FileUtil
operator|.
name|setWritable
argument_list|(
name|nodeLabelsScriptFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|setReadable
argument_list|(
name|nodeLabelsScriptFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|nodeLabelsScriptFile
argument_list|)
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
name|scriptStr
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|pw
condition|)
block|{
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|FileUtil
operator|.
name|setExecutable
argument_list|(
name|nodeLabelsScriptFile
argument_list|,
name|setExecutable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeLabelsScriptRunnerCreation ()
specifier|public
name|void
name|testNodeLabelsScriptRunnerCreation
parameter_list|()
throws|throws
name|IOException
block|{
comment|// If no script configured then initialization of service should fail
name|ScriptBasedNodeLabelsProvider
name|nodeLabelsProvider
init|=
operator|new
name|ScriptBasedNodeLabelsProvider
argument_list|()
decl_stmt|;
name|initilizeServiceFailTest
argument_list|(
literal|"Expected to fail fast when no script is configured and "
operator|+
literal|"ScriptBasedNodeLabelsProvider service is inited"
argument_list|,
name|nodeLabelsProvider
argument_list|)
expr_stmt|;
comment|// If script configured is blank then initialization of service should fail
name|nodeLabelsProvider
operator|=
operator|new
name|ScriptBasedNodeLabelsProvider
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_SCRIPT_BASED_NODE_LABELS_PROVIDER_PATH
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|initilizeServiceFailTest
argument_list|(
literal|"Expected to fail fast when script path configuration is blank"
operator|+
literal|"and ScriptBasedNodeLabelsProvider service is inited."
argument_list|,
name|nodeLabelsProvider
argument_list|)
expr_stmt|;
comment|// If script configured is not executable then no timertask /
comment|// NodeLabelsScriptRunner initialized
name|nodeLabelsProvider
operator|=
operator|new
name|ScriptBasedNodeLabelsProvider
argument_list|()
expr_stmt|;
name|writeNodeLabelsScriptFile
argument_list|(
literal|""
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|initilizeServiceFailTest
argument_list|(
literal|"Expected to fail fast when script is not executable"
operator|+
literal|"and ScriptBasedNodeLabelsProvider service is inited."
argument_list|,
name|nodeLabelsProvider
argument_list|)
expr_stmt|;
comment|// If configured script is executable then timertask /
comment|// NodeLabelsScriptRunner should be initialized
name|nodeLabelsProvider
operator|=
operator|new
name|ScriptBasedNodeLabelsProvider
argument_list|()
expr_stmt|;
name|writeNodeLabelsScriptFile
argument_list|(
literal|""
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|nodeLabelsProvider
operator|.
name|init
argument_list|(
name|getConfForNodeLabelScript
argument_list|()
argument_list|)
expr_stmt|;
name|nodeLabelsProvider
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Node Label Script runner should be started when script"
operator|+
literal|" is executable"
argument_list|,
name|nodeLabelsProvider
operator|.
name|getTimerTask
argument_list|()
argument_list|)
expr_stmt|;
name|nodeLabelsProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|initilizeServiceFailTest (String message, ScriptBasedNodeLabelsProvider nodeLabelsProvider)
specifier|private
name|void
name|initilizeServiceFailTest
parameter_list|(
name|String
name|message
parameter_list|,
name|ScriptBasedNodeLabelsProvider
name|nodeLabelsProvider
parameter_list|)
block|{
try|try
block|{
name|nodeLabelsProvider
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceStateException
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"IOException was expected"
argument_list|,
name|IOException
operator|.
name|class
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testConfigForNoTimer ()
specifier|public
name|void
name|testConfigForNoTimer
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConfForNodeLabelScript
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS
argument_list|,
name|AbstractNodeDescriptorsProvider
operator|.
name|DISABLE_NODE_DESCRIPTORS_PROVIDER_FETCH_TIMER
argument_list|)
expr_stmt|;
name|String
name|normalScript
init|=
literal|"echo NODE_PARTITION:X86"
decl_stmt|;
name|writeNodeLabelsScriptFile
argument_list|(
name|normalScript
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|nodeLabelsProvider
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nodeLabelsProvider
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
literal|"Timer is not expected to be created when interval is configured as -1"
argument_list|,
name|nodeLabelsProvider
operator|.
name|getScheduler
argument_list|()
argument_list|)
expr_stmt|;
comment|// Ensure that even though timer is not run script is run at least once so
comment|// that NM registers/updates Labels with RM
name|assertNLCollectionEquals
argument_list|(
name|toNodeLabelSet
argument_list|(
literal|"X86"
argument_list|)
argument_list|,
name|nodeLabelsProvider
operator|.
name|getDescriptors
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeLabelsScript ()
specifier|public
name|void
name|testNodeLabelsScript
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|scriptWithoutLabels
init|=
literal|""
decl_stmt|;
name|String
name|normalScript
init|=
literal|"echo NODE_PARTITION:Windows"
decl_stmt|;
name|String
name|scrptWithMultipleLinesHavingNodeLabels
init|=
literal|"echo NODE_PARTITION:RAM\n echo NODE_PARTITION:JDK1_6"
decl_stmt|;
name|String
name|timeOutScript
init|=
name|Shell
operator|.
name|WINDOWS
condition|?
literal|"@echo off\nping -n 4 127.0.0.1>nul\n"
operator|+
literal|"echo NODE_PARTITION:ALL"
else|:
literal|"sleep 4\necho NODE_PARTITION:ALL"
decl_stmt|;
name|writeNodeLabelsScriptFile
argument_list|(
name|scriptWithoutLabels
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|nodeLabelsProvider
operator|.
name|init
argument_list|(
name|getConfForNodeLabelScript
argument_list|()
argument_list|)
expr_stmt|;
name|nodeLabelsProvider
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500l
argument_list|)
expr_stmt|;
name|TimerTask
name|timerTask
init|=
name|nodeLabelsProvider
operator|.
name|getTimerTask
argument_list|()
decl_stmt|;
name|timerTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
literal|"Node Label Script runner should return null when script doesnt "
operator|+
literal|"give any Labels output"
argument_list|,
name|nodeLabelsProvider
operator|.
name|getDescriptors
argument_list|()
argument_list|)
expr_stmt|;
name|writeNodeLabelsScriptFile
argument_list|(
name|normalScript
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|timerTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertNLCollectionEquals
argument_list|(
name|toNodeLabelSet
argument_list|(
literal|"Windows"
argument_list|)
argument_list|,
name|nodeLabelsProvider
operator|.
name|getDescriptors
argument_list|()
argument_list|)
expr_stmt|;
comment|// multiple lines with partition tag then the last line's partition info
comment|// needs to be taken.
name|writeNodeLabelsScriptFile
argument_list|(
name|scrptWithMultipleLinesHavingNodeLabels
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|timerTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertNLCollectionEquals
argument_list|(
name|toNodeLabelSet
argument_list|(
literal|"JDK1_6"
argument_list|)
argument_list|,
name|nodeLabelsProvider
operator|.
name|getDescriptors
argument_list|()
argument_list|)
expr_stmt|;
comment|// timeout script.
name|writeNodeLabelsScriptFile
argument_list|(
name|timeOutScript
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|timerTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"Node Labels should not be set after timeout "
argument_list|,
name|toNodeLabelSet
argument_list|(
literal|"ALL"
argument_list|)
argument_list|,
name|nodeLabelsProvider
operator|.
name|getDescriptors
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

