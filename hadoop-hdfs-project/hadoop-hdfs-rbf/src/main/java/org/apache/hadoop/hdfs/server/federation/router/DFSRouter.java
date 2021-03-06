begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ExitUtil
operator|.
name|terminate
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
name|DFSUtil
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
name|service
operator|.
name|CompositeService
operator|.
name|CompositeServiceShutdownHook
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
name|ShutdownHookManager
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

begin_comment
comment|/**  * Tool to start the {@link Router} for Router-based federation.  */
end_comment

begin_class
DECL|class|DFSRouter
specifier|public
specifier|final
class|class
name|DFSRouter
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
name|DFSRouter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Usage string for help message. */
DECL|field|USAGE
specifier|private
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"Usage: hdfs dfsrouter"
decl_stmt|;
comment|/** Priority of the Router shutdown hook. */
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|public
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|30
decl_stmt|;
DECL|method|DFSRouter ()
specifier|private
name|DFSRouter
parameter_list|()
block|{
comment|// This is just a class to trigger the Router
block|}
comment|/**    * Main run loop for the router.    *    * @param argv parameters.    */
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
block|{
if|if
condition|(
name|DFSUtil
operator|.
name|parseHelpArgument
argument_list|(
name|argv
argument_list|,
name|USAGE
argument_list|,
name|System
operator|.
name|out
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|Router
operator|.
name|class
argument_list|,
name|argv
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|Router
name|router
init|=
operator|new
name|Router
argument_list|()
decl_stmt|;
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|CompositeServiceShutdownHook
argument_list|(
name|router
argument_list|)
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|router
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|router
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to start router"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

