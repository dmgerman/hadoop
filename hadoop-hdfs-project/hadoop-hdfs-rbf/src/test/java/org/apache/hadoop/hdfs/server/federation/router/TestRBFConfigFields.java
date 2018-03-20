begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
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
name|conf
operator|.
name|TestConfigurationFieldsBase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_comment
comment|/**  * Unit test class to compare the following RBF configuration class:  *<p></p>  * {@link RBFConfigKeys}  *<p></p>  * against hdfs-rbf-default.xml for missing properties.  *<p></p>  * Refer to {@link org.apache.hadoop.conf.TestConfigurationFieldsBase}  * for how this class works.  */
end_comment

begin_class
DECL|class|TestRBFConfigFields
specifier|public
class|class
name|TestRBFConfigFields
extends|extends
name|TestConfigurationFieldsBase
block|{
annotation|@
name|Override
DECL|method|initializeMemberVariables ()
specifier|public
name|void
name|initializeMemberVariables
parameter_list|()
block|{
name|xmlFilename
operator|=
literal|"hdfs-rbf-default.xml"
expr_stmt|;
name|configurationClasses
operator|=
operator|new
name|Class
index|[]
block|{
name|RBFConfigKeys
operator|.
name|class
block|}
expr_stmt|;
comment|// Set error modes
name|errorIfMissingConfigProps
operator|=
literal|true
expr_stmt|;
name|errorIfMissingXmlProps
operator|=
literal|true
expr_stmt|;
comment|// Initialize used variables
name|configurationPropsToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
comment|// Allocate
name|xmlPropsToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|xmlPrefixToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

