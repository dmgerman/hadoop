begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.freon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|freon
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
operator|.
name|Command
import|;
end_import

begin_comment
comment|/**  * Ozone data generator and performance test tool.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"ozone freon"
argument_list|,
name|description
operator|=
literal|"Load generator and tester tool for ozone"
argument_list|,
name|subcommands
operator|=
name|RandomKeyGenerator
operator|.
name|class
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
DECL|class|Freon
specifier|public
class|class
name|Freon
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
block|{
operator|new
name|Freon
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

