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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestScriptBasedMapping
specifier|public
class|class
name|TestScriptBasedMapping
extends|extends
name|TestCase
block|{
DECL|field|mapping
specifier|private
name|ScriptBasedMapping
name|mapping
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|names
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|names
decl_stmt|;
DECL|method|TestScriptBasedMapping ()
specifier|public
name|TestScriptBasedMapping
parameter_list|()
block|{
name|mapping
operator|=
operator|new
name|ScriptBasedMapping
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
name|mapping
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoArgsMeansNoResult ()
specifier|public
name|void
name|testNoArgsMeansNoResult
parameter_list|()
block|{
name|names
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
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
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

