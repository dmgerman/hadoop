begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_class
DECL|class|TestScriptBasedMapping
specifier|public
class|class
name|TestScriptBasedMapping
block|{
DECL|method|TestScriptBasedMapping ()
specifier|public
name|TestScriptBasedMapping
parameter_list|()
block|{    }
annotation|@
name|Test
DECL|method|testNoArgsMeansNoResult ()
specifier|public
name|void
name|testNoArgsMeansNoResult
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
name|setInt
argument_list|(
name|ScriptBasedMapping
operator|.
name|SCRIPT_ARG_COUNT_KEY
argument_list|,
name|ScriptBasedMapping
operator|.
name|MIN_ALLOWABLE_ARGS
operator|-
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScriptBasedMapping
operator|.
name|SCRIPT_FILENAME_KEY
argument_list|,
literal|"any-filename"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScriptBasedMapping
operator|.
name|SCRIPT_FILENAME_KEY
argument_list|,
literal|"any-filename"
argument_list|)
expr_stmt|;
name|ScriptBasedMapping
name|mapping
init|=
name|createMapping
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|names
operator|.
name|add
argument_list|(
literal|"some.machine.name"
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
literal|"other.machine.name"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
name|mapping
operator|.
name|resolve
argument_list|(
name|names
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Expected an empty list"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoFilenameMeansSingleSwitch ()
specifier|public
name|void
name|testNoFilenameMeansSingleSwitch
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ScriptBasedMapping
name|mapping
init|=
name|createMapping
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected to be single switch"
argument_list|,
name|mapping
operator|.
name|isSingleSwitch
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected to be single switch"
argument_list|,
name|AbstractDNSToSwitchMapping
operator|.
name|isMappingSingleSwitch
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFilenameMeansMultiSwitch ()
specifier|public
name|void
name|testFilenameMeansMultiSwitch
parameter_list|()
throws|throws
name|Throwable
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
name|ScriptBasedMapping
operator|.
name|SCRIPT_FILENAME_KEY
argument_list|,
literal|"any-filename"
argument_list|)
expr_stmt|;
name|ScriptBasedMapping
name|mapping
init|=
name|createMapping
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Expected to be multi switch"
argument_list|,
name|mapping
operator|.
name|isSingleSwitch
argument_list|()
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected to be single switch"
argument_list|,
name|mapping
operator|.
name|isSingleSwitch
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNullConfig ()
specifier|public
name|void
name|testNullConfig
parameter_list|()
throws|throws
name|Throwable
block|{
name|ScriptBasedMapping
name|mapping
init|=
name|createMapping
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected to be single switch"
argument_list|,
name|mapping
operator|.
name|isSingleSwitch
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createMapping (Configuration conf)
specifier|private
name|ScriptBasedMapping
name|createMapping
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ScriptBasedMapping
name|mapping
init|=
operator|new
name|ScriptBasedMapping
argument_list|()
decl_stmt|;
name|mapping
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|mapping
return|;
block|}
block|}
end_class

end_unit

