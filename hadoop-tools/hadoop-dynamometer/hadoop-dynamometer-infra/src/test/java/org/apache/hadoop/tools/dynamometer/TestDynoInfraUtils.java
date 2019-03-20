begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
package|;
end_package

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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|assertEquals
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

begin_comment
comment|/** Tests for {@link DynoInfraUtils}. */
end_comment

begin_class
DECL|class|TestDynoInfraUtils
specifier|public
class|class
name|TestDynoInfraUtils
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDynoInfraUtils
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testParseStaleDatanodeListSingleDatanode ()
specifier|public
name|void
name|testParseStaleDatanodeListSingleDatanode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Confirm all types of values can be properly parsed
name|String
name|json
init|=
literal|"{"
operator|+
literal|"\"1.2.3.4:5\": {"
operator|+
literal|"  \"numBlocks\": 5,"
operator|+
literal|"  \"fooString\":\"stringValue\","
operator|+
literal|"  \"fooInteger\": 1,"
operator|+
literal|"  \"fooFloat\": 1.0,"
operator|+
literal|"  \"fooArray\": []"
operator|+
literal|"}"
operator|+
literal|"}"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|out
init|=
name|DynoInfraUtils
operator|.
name|parseStaleDataNodeList
argument_list|(
name|json
argument_list|,
literal|10
argument_list|,
name|LOG
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"1.2.3.4:5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseStaleDatanodeListMultipleDatanodes ()
specifier|public
name|void
name|testParseStaleDatanodeListMultipleDatanodes
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|json
init|=
literal|"{"
operator|+
literal|"\"1.2.3.4:1\": {\"numBlocks\": 0}, "
operator|+
literal|"\"1.2.3.4:2\": {\"numBlocks\": 15}, "
operator|+
literal|"\"1.2.3.4:3\": {\"numBlocks\": 5}, "
operator|+
literal|"\"1.2.3.4:4\": {\"numBlocks\": 10} "
operator|+
literal|"}"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|out
init|=
name|DynoInfraUtils
operator|.
name|parseStaleDataNodeList
argument_list|(
name|json
argument_list|,
literal|10
argument_list|,
name|LOG
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"1.2.3.4:1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"1.2.3.4:3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

