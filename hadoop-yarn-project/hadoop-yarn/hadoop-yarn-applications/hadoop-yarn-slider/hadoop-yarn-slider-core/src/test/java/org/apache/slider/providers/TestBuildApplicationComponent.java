begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
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
name|slider
operator|.
name|client
operator|.
name|SliderClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|params
operator|.
name|SliderActions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|SliderFileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
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
name|slider
operator|.
name|core
operator|.
name|main
operator|.
name|ServiceLauncher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|util
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
name|slider
operator|.
name|utils
operator|.
name|YarnZKMiniClusterTestBase
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
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|params
operator|.
name|Arguments
operator|.
name|ARG_APPDEF
import|;
end_import

begin_comment
comment|/**  * Test for building / resolving components of type APPLICATION.  */
end_comment

begin_class
DECL|class|TestBuildApplicationComponent
specifier|public
class|class
name|TestBuildApplicationComponent
extends|extends
name|YarnZKMiniClusterTestBase
block|{
DECL|method|checkComponentNames (List<Component> components, Set<String> names)
specifier|private
specifier|static
name|void
name|checkComponentNames
parameter_list|(
name|List
argument_list|<
name|Component
argument_list|>
name|components
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|names
operator|.
name|size
argument_list|()
argument_list|,
name|components
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Component
name|comp
range|:
name|components
control|)
block|{
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|buildAndCheckComponents (String appName, String appDef, SliderFileSystem sfs, Set<String> names)
specifier|public
name|void
name|buildAndCheckComponents
parameter_list|(
name|String
name|appName
parameter_list|,
name|String
name|appDef
parameter_list|,
name|SliderFileSystem
name|sfs
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|Throwable
block|{
name|ServiceLauncher
argument_list|<
name|SliderClient
argument_list|>
name|launcher
init|=
name|createOrBuildCluster
argument_list|(
name|SliderActions
operator|.
name|ACTION_BUILD
argument_list|,
name|appName
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|ARG_APPDEF
argument_list|,
name|ExampleAppJson
operator|.
name|resourceName
argument_list|(
name|appDef
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SliderClient
name|sliderClient
init|=
name|launcher
operator|.
name|getService
argument_list|()
decl_stmt|;
name|addToTeardown
argument_list|(
name|sliderClient
argument_list|)
expr_stmt|;
comment|// verify the cluster exists
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sliderClient
operator|.
name|actionExists
argument_list|(
name|appName
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify generated conf
name|List
argument_list|<
name|Component
argument_list|>
name|components
init|=
name|ServiceApiUtil
operator|.
name|getApplicationComponents
argument_list|(
name|sfs
argument_list|,
name|appName
argument_list|)
decl_stmt|;
name|checkComponentNames
argument_list|(
name|components
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExternalComponentBuild ()
specifier|public
name|void
name|testExternalComponentBuild
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|clustername
init|=
name|createMiniCluster
argument_list|(
literal|""
argument_list|,
name|getConfiguration
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|describe
argument_list|(
literal|"verify external components"
argument_list|)
expr_stmt|;
name|SliderFileSystem
name|sfs
init|=
name|createSliderFileSystem
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nameSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|nameSet
operator|.
name|add
argument_list|(
literal|"simple"
argument_list|)
expr_stmt|;
name|nameSet
operator|.
name|add
argument_list|(
literal|"master"
argument_list|)
expr_stmt|;
name|nameSet
operator|.
name|add
argument_list|(
literal|"worker"
argument_list|)
expr_stmt|;
name|buildAndCheckComponents
argument_list|(
literal|"app-1"
argument_list|,
name|ExampleAppJson
operator|.
name|APP_JSON
argument_list|,
name|sfs
argument_list|,
name|nameSet
argument_list|)
expr_stmt|;
name|buildAndCheckComponents
argument_list|(
literal|"external-0"
argument_list|,
name|ExampleAppJson
operator|.
name|EXTERNAL_JSON_0
argument_list|,
name|sfs
argument_list|,
name|nameSet
argument_list|)
expr_stmt|;
name|nameSet
operator|.
name|add
argument_list|(
literal|"other"
argument_list|)
expr_stmt|;
name|buildAndCheckComponents
argument_list|(
literal|"external-1"
argument_list|,
name|ExampleAppJson
operator|.
name|EXTERNAL_JSON_1
argument_list|,
name|sfs
argument_list|,
name|nameSet
argument_list|)
expr_stmt|;
name|nameSet
operator|.
name|add
argument_list|(
literal|"another"
argument_list|)
expr_stmt|;
name|buildAndCheckComponents
argument_list|(
literal|"external-2"
argument_list|,
name|ExampleAppJson
operator|.
name|EXTERNAL_JSON_2
argument_list|,
name|sfs
argument_list|,
name|nameSet
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

