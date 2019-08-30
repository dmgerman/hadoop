begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.insight
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|insight
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
name|picocli
operator|.
name|CommandLine
import|;
end_import

begin_comment
comment|/**  * Command line utility to check logs/metrics of internal ozone components.  */
end_comment

begin_class
annotation|@
name|CommandLine
operator|.
name|Command
argument_list|(
name|name
operator|=
literal|"ozone insight"
argument_list|,
name|hidden
operator|=
literal|true
argument_list|,
name|description
operator|=
literal|"Show debug information about a selected "
operator|+
literal|"Ozone component"
argument_list|,
name|versionProvider
operator|=
name|HddsVersionProvider
operator|.
name|class
argument_list|,
name|subcommands
operator|=
block|{
name|ListSubCommand
operator|.
name|class
block|,
name|LogSubcommand
operator|.
name|class
block|,
name|MetricsSubCommand
operator|.
name|class
block|,
name|ConfigurationSubCommand
operator|.
name|class
block|}
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|)
DECL|class|Insight
specifier|public
class|class
name|Insight
extends|extends
name|GenericCli
block|{
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
operator|new
name|Insight
argument_list|()
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

