begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestNameNodeConfiguration
specifier|public
class|class
name|TestNameNodeConfiguration
block|{
comment|/**    * Detect duplicate keys in {@link NameNode#NAMENODE_SPECIFIC_KEYS}.    */
annotation|@
name|Test
DECL|method|testNameNodeSpecificKeys ()
specifier|public
name|void
name|testNameNodeSpecificKeys
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|keySet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|NameNode
operator|.
name|NAMENODE_SPECIFIC_KEYS
control|)
block|{
name|assertTrue
argument_list|(
literal|"Duplicate key: "
operator|+
name|key
operator|+
literal|" in NameNode.NAMENODE_SPECIFIC_KEYS."
argument_list|,
name|keySet
operator|.
name|add
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

