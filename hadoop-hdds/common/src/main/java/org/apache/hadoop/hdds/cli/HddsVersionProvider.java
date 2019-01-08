begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
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
name|utils
operator|.
name|HddsVersionInfo
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|IVersionProvider
import|;
end_import

begin_comment
comment|/**  * Version provider for the CLI interface.  */
end_comment

begin_class
DECL|class|HddsVersionProvider
specifier|public
class|class
name|HddsVersionProvider
implements|implements
name|IVersionProvider
block|{
annotation|@
name|Override
DECL|method|getVersion ()
specifier|public
name|String
index|[]
name|getVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[]
block|{
name|HddsVersionInfo
operator|.
name|HDDS_VERSION_INFO
operator|.
name|getBuildVersion
argument_list|()
block|}
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

