begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cli
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
name|cli
operator|.
name|util
operator|.
name|CLICommandTypes
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
name|cli
operator|.
name|util
operator|.
name|CLITestCmd
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
name|cli
operator|.
name|util
operator|.
name|CommandExecutor
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
DECL|class|CLITestCmdMR
specifier|public
class|class
name|CLITestCmdMR
extends|extends
name|CLITestCmd
block|{
DECL|method|CLITestCmdMR (String str, CLICommandTypes type)
specifier|public
name|CLITestCmdMR
parameter_list|(
name|String
name|str
parameter_list|,
name|CLICommandTypes
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|str
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is not implemented because HadoopArchive constructor requires JobConf    * to create an archive object. Because TestMRCLI uses setup method from    * TestHDFSCLI the initialization of executor objects happens before a config    * is created and updated. Thus, actual calls to executors happen in the body    * of the test method.    */
annotation|@
name|Override
DECL|method|getExecutor (String tag, Configuration conf)
specifier|public
name|CommandExecutor
name|getExecutor
parameter_list|(
name|String
name|tag
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Method isn't supported"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

