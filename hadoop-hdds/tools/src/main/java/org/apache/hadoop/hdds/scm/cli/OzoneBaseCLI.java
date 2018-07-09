begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
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
name|commons
operator|.
name|cli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|ParseException
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
name|Configured
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
name|Tool
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_comment
comment|/**  * This class is the base CLI for scm, om and scmadm.  */
end_comment

begin_class
DECL|class|OzoneBaseCLI
specifier|public
specifier|abstract
class|class
name|OzoneBaseCLI
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|method|dispatch (CommandLine cmd, Options opts)
specifier|protected
specifier|abstract
name|int
name|dispatch
parameter_list|(
name|CommandLine
name|cmd
parameter_list|,
name|Options
name|opts
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
function_decl|;
DECL|method|parseArgs (String[] argv, Options opts)
specifier|protected
specifier|abstract
name|CommandLine
name|parseArgs
parameter_list|(
name|String
index|[]
name|argv
parameter_list|,
name|Options
name|opts
parameter_list|)
throws|throws
name|ParseException
function_decl|;
DECL|method|getOptions ()
specifier|protected
specifier|abstract
name|Options
name|getOptions
parameter_list|()
function_decl|;
DECL|method|displayHelp ()
specifier|protected
specifier|abstract
name|void
name|displayHelp
parameter_list|()
function_decl|;
block|}
end_class

end_unit

