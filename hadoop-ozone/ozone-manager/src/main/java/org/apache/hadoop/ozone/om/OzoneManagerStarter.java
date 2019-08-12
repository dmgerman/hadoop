begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
name|hdds
operator|.
name|cli
operator|.
name|GenericCli
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
name|hdds
operator|.
name|cli
operator|.
name|HddsVersionProvider
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|discovery
operator|.
name|DiscoveryUtil
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
name|hdds
operator|.
name|tracing
operator|.
name|TracingUtil
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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|AuthenticationException
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

begin_import
import|import
name|picocli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Command
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

begin_comment
comment|/**  * This class provides a command line interface to start the OM  * using Picocli.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"ozone om"
argument_list|,
name|hidden
operator|=
literal|true
argument_list|,
name|description
operator|=
literal|"Start or initialize the Ozone Manager."
argument_list|,
name|versionProvider
operator|=
name|HddsVersionProvider
operator|.
name|class
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|)
DECL|class|OzoneManagerStarter
specifier|public
class|class
name|OzoneManagerStarter
extends|extends
name|GenericCli
block|{
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|receiver
specifier|private
name|OMStarterInterface
name|receiver
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
name|OzoneManagerStarter
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|TracingUtil
operator|.
name|initTracing
argument_list|(
literal|"OzoneManager"
argument_list|)
expr_stmt|;
operator|new
name|OzoneManagerStarter
argument_list|(
operator|new
name|OzoneManagerStarter
operator|.
name|OMStarterHelper
argument_list|()
argument_list|)
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|OzoneManagerStarter (OMStarterInterface receiverObj)
specifier|public
name|OzoneManagerStarter
parameter_list|(
name|OMStarterInterface
name|receiverObj
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|receiver
operator|=
name|receiverObj
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
comment|/**      * This method is invoked only when a sub-command is not called. Therefore      * if someone runs "ozone om" with no parameters, this is the method      * which runs and starts the OM.      */
name|commonInit
argument_list|()
expr_stmt|;
name|startOm
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/**    * This function is used by the command line to start the OM.    */
DECL|method|startOm ()
specifier|private
name|void
name|startOm
parameter_list|()
throws|throws
name|Exception
block|{
name|receiver
operator|.
name|start
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * This function implements a sub-command to allow the OM to be    * initialized from the command line.    */
annotation|@
name|CommandLine
operator|.
name|Command
argument_list|(
name|name
operator|=
literal|"--init"
argument_list|,
name|customSynopsis
operator|=
literal|"ozone om [global options] --init"
argument_list|,
name|hidden
operator|=
literal|false
argument_list|,
name|description
operator|=
literal|"Initialize the Ozone Manager if not already initialized"
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|,
name|versionProvider
operator|=
name|HddsVersionProvider
operator|.
name|class
argument_list|)
DECL|method|initOm ()
specifier|public
name|void
name|initOm
parameter_list|()
throws|throws
name|Exception
block|{
name|commonInit
argument_list|()
expr_stmt|;
name|boolean
name|result
init|=
name|receiver
operator|.
name|init
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|result
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"OM Init failed."
argument_list|)
throw|;
block|}
block|}
comment|/**    * This function should be called by each command to ensure the configuration    * is set and print the startup banner message.    */
DECL|method|commonInit ()
specifier|private
name|void
name|commonInit
parameter_list|()
block|{
name|conf
operator|=
name|createOzoneConfiguration
argument_list|()
expr_stmt|;
if|if
condition|(
name|DiscoveryUtil
operator|.
name|loadGlobalConfig
argument_list|(
name|conf
argument_list|)
condition|)
block|{
comment|//reload the configuration with the downloaded new configs.
name|conf
operator|=
name|createOzoneConfiguration
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|originalArgs
init|=
name|getCmd
argument_list|()
operator|.
name|getParseResult
argument_list|()
operator|.
name|originalArgs
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|OzoneManager
operator|.
name|class
argument_list|,
name|originalArgs
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
block|}
comment|/**    * This static class wraps the external dependencies needed for this command    * to execute its tasks. This allows the dependency to be injected for unit    * testing.    */
DECL|class|OMStarterHelper
specifier|static
class|class
name|OMStarterHelper
implements|implements
name|OMStarterInterface
block|{
DECL|method|start (OzoneConfiguration conf)
specifier|public
name|void
name|start
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|OzoneManager
name|om
init|=
name|OzoneManager
operator|.
name|createOm
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|om
operator|.
name|start
argument_list|()
expr_stmt|;
name|om
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|init (OzoneConfiguration conf)
specifier|public
name|boolean
name|init
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
return|return
name|OzoneManager
operator|.
name|omInit
argument_list|(
name|conf
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

