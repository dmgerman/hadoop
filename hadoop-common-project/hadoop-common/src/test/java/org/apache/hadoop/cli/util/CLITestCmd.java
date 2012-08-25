begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cli.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cli
operator|.
name|util
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
name|fs
operator|.
name|FsShell
import|;
end_import

begin_comment
comment|/**  * Class to define Test Command along with its type  */
end_comment

begin_class
DECL|class|CLITestCmd
specifier|public
class|class
name|CLITestCmd
implements|implements
name|CLICommand
block|{
DECL|field|type
specifier|private
specifier|final
name|CLICommandTypes
name|type
decl_stmt|;
DECL|field|cmd
specifier|private
specifier|final
name|String
name|cmd
decl_stmt|;
DECL|method|CLITestCmd (String str, CLICommandTypes type)
specifier|public
name|CLITestCmd
parameter_list|(
name|String
name|str
parameter_list|,
name|CLICommandTypes
name|type
parameter_list|)
block|{
name|cmd
operator|=
name|str
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
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
name|CLICommandFS
condition|)
return|return
operator|new
name|FSCmdExecutor
argument_list|(
name|tag
argument_list|,
operator|new
name|FsShell
argument_list|()
argument_list|)
return|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown type of test command: "
operator|+
name|getType
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|CLICommandTypes
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|getCmd ()
specifier|public
name|String
name|getCmd
parameter_list|()
block|{
return|return
name|cmd
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|cmd
return|;
block|}
block|}
end_class

end_unit

