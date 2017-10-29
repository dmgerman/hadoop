begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|client
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
name|io
operator|.
name|FileUtils
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
name|util
operator|.
name|ToolRunner
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
name|cli
operator|.
name|ApplicationCLI
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
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
name|service
operator|.
name|api
operator|.
name|records
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
name|yarn
operator|.
name|service
operator|.
name|conf
operator|.
name|ExampleAppJson
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
name|service
operator|.
name|utils
operator|.
name|ServiceApiUtil
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
name|service
operator|.
name|utils
operator|.
name|SliderFileSystem
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
name|IOException
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|conf
operator|.
name|YarnServiceConf
operator|.
name|YARN_SERVICE_BASE_PATH
import|;
end_import

begin_class
DECL|class|TestServiceCLI
specifier|public
class|class
name|TestServiceCLI
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
name|TestServiceCLI
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|basedir
specifier|private
name|File
name|basedir
decl_stmt|;
DECL|field|fs
specifier|private
name|SliderFileSystem
name|fs
decl_stmt|;
DECL|field|basedirProp
specifier|private
name|String
name|basedirProp
decl_stmt|;
DECL|method|runCLI (String[] args)
specifier|private
name|void
name|runCLI
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"running CLI: yarn {}"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|ApplicationCLI
name|cli
init|=
operator|new
name|ApplicationCLI
argument_list|()
decl_stmt|;
name|cli
operator|.
name|setSysOutPrintStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysErrPrintStream
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|cli
argument_list|,
name|ApplicationCLI
operator|.
name|preProcessArgs
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|cli
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|buildApp (String serviceName, String appDef)
specifier|private
name|void
name|buildApp
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|String
name|appDef
parameter_list|)
throws|throws
name|Throwable
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"app"
block|,
literal|"-D"
block|,
name|basedirProp
block|,
literal|"-save"
block|,
name|serviceName
block|,
name|ExampleAppJson
operator|.
name|resourceName
argument_list|(
name|appDef
argument_list|)
block|}
decl_stmt|;
name|runCLI
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|buildApp (String serviceName, String appDef, String lifetime, String queue)
specifier|private
name|void
name|buildApp
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|String
name|appDef
parameter_list|,
name|String
name|lifetime
parameter_list|,
name|String
name|queue
parameter_list|)
throws|throws
name|Throwable
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"app"
block|,
literal|"-D"
block|,
name|basedirProp
block|,
literal|"-save"
block|,
name|serviceName
block|,
name|ExampleAppJson
operator|.
name|resourceName
argument_list|(
name|appDef
argument_list|)
block|,
literal|"-updateLifetime"
block|,
name|lifetime
block|,
literal|"-changeQueue"
block|,
name|queue
block|}
decl_stmt|;
name|runCLI
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Throwable
block|{
name|basedir
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"apps"
argument_list|)
expr_stmt|;
name|basedirProp
operator|=
name|YARN_SERVICE_BASE_PATH
operator|+
literal|"="
operator|+
name|basedir
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YARN_SERVICE_BASE_PATH
argument_list|,
name|basedir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|=
operator|new
name|SliderFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|basedir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|basedir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|basedir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|basedir
operator|!=
literal|null
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|basedir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFlexComponents ()
specifier|public
name|void
name|testFlexComponents
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// currently can only test building apps, since that is the only
comment|// operation that doesn't require an RM
comment|// TODO: expand CLI test to try other commands
name|String
name|serviceName
init|=
literal|"app-1"
decl_stmt|;
name|buildApp
argument_list|(
name|serviceName
argument_list|,
name|ExampleAppJson
operator|.
name|APP_JSON
argument_list|)
expr_stmt|;
name|checkApp
argument_list|(
name|serviceName
argument_list|,
literal|"master"
argument_list|,
literal|1L
argument_list|,
literal|3600L
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|serviceName
operator|=
literal|"app-2"
expr_stmt|;
name|buildApp
argument_list|(
name|serviceName
argument_list|,
name|ExampleAppJson
operator|.
name|APP_JSON
argument_list|,
literal|"1000"
argument_list|,
literal|"qname"
argument_list|)
expr_stmt|;
name|checkApp
argument_list|(
name|serviceName
argument_list|,
literal|"master"
argument_list|,
literal|1L
argument_list|,
literal|1000L
argument_list|,
literal|"qname"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkApp (String serviceName, String compName, long count, Long lifetime, String queue)
specifier|private
name|void
name|checkApp
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|String
name|compName
parameter_list|,
name|long
name|count
parameter_list|,
name|Long
name|lifetime
parameter_list|,
name|String
name|queue
parameter_list|)
throws|throws
name|IOException
block|{
name|Service
name|service
init|=
name|ServiceApiUtil
operator|.
name|loadService
argument_list|(
name|fs
argument_list|,
name|serviceName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|serviceName
argument_list|,
name|service
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|lifetime
argument_list|,
name|service
operator|.
name|getLifetime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|queue
argument_list|,
name|service
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Component
argument_list|>
name|components
init|=
name|service
operator|.
name|getComponents
argument_list|()
decl_stmt|;
for|for
control|(
name|Component
name|component
range|:
name|components
control|)
block|{
if|if
condition|(
name|component
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|compName
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|count
argument_list|,
name|component
operator|.
name|getNumberOfContainers
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

