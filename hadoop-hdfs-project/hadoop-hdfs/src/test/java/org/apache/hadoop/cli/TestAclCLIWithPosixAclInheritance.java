begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_POSIX_ACL_INHERITANCE_ENABLED_KEY
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test ACL CLI with POSIX ACL inheritance enabled.  */
end_comment

begin_class
DECL|class|TestAclCLIWithPosixAclInheritance
specifier|public
class|class
name|TestAclCLIWithPosixAclInheritance
extends|extends
name|TestAclCLI
block|{
annotation|@
name|Override
DECL|method|initConf ()
specifier|protected
name|void
name|initConf
parameter_list|()
block|{
name|super
operator|.
name|initConf
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_NAMENODE_POSIX_ACL_INHERITANCE_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTestFile ()
specifier|protected
name|String
name|getTestFile
parameter_list|()
block|{
return|return
literal|"testAclCLIWithPosixAclInheritance.xml"
return|;
block|}
annotation|@
name|Test
annotation|@
name|Override
DECL|method|testAll ()
specifier|public
name|void
name|testAll
parameter_list|()
block|{
name|super
operator|.
name|testAll
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

