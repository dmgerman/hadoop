begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_class
DECL|class|FSCmdExecutor
specifier|public
class|class
name|FSCmdExecutor
extends|extends
name|CommandExecutor
block|{
DECL|field|namenode
specifier|protected
name|String
name|namenode
init|=
literal|null
decl_stmt|;
DECL|field|shell
specifier|protected
name|FsShell
name|shell
init|=
literal|null
decl_stmt|;
DECL|method|FSCmdExecutor (String namenode, FsShell shell)
specifier|public
name|FSCmdExecutor
parameter_list|(
name|String
name|namenode
parameter_list|,
name|FsShell
name|shell
parameter_list|)
block|{
name|this
operator|.
name|namenode
operator|=
name|namenode
expr_stmt|;
name|this
operator|.
name|shell
operator|=
name|shell
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute (final String cmd)
specifier|protected
name|void
name|execute
parameter_list|(
specifier|final
name|String
name|cmd
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
name|getCommandAsArgs
argument_list|(
name|cmd
argument_list|,
literal|"NAMENODE"
argument_list|,
name|this
operator|.
name|namenode
argument_list|)
decl_stmt|;
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

