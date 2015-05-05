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
name|CLICommandErasureCodingCli
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
name|cli
operator|.
name|util
operator|.
name|ErasureCodingCliCmdExecutor
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
name|tools
operator|.
name|erasurecode
operator|.
name|ECCli
import|;
end_import

begin_class
DECL|class|CLITestCmdErasureCoding
specifier|public
class|class
name|CLITestCmdErasureCoding
extends|extends
name|CLITestCmd
block|{
DECL|method|CLITestCmdErasureCoding (String str, CLICommandTypes type)
specifier|public
name|CLITestCmdErasureCoding
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
annotation|@
name|Override
DECL|method|getExecutor (String tag)
specifier|public
name|CommandExecutor
name|getExecutor
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|getType
argument_list|()
operator|instanceof
name|CLICommandErasureCodingCli
condition|)
return|return
operator|new
name|ErasureCodingCliCmdExecutor
argument_list|(
name|tag
argument_list|,
operator|new
name|ECCli
argument_list|()
argument_list|)
return|;
return|return
name|super
operator|.
name|getExecutor
argument_list|(
name|tag
argument_list|)
return|;
block|}
block|}
end_class

end_unit

