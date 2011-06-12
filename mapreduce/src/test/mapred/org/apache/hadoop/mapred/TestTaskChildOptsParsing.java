begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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

begin_class
DECL|class|TestTaskChildOptsParsing
specifier|public
class|class
name|TestTaskChildOptsParsing
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|field|TASK_ID
specifier|private
specifier|static
specifier|final
name|TaskAttemptID
name|TASK_ID
init|=
operator|new
name|TaskAttemptID
argument_list|()
decl_stmt|;
DECL|field|EXPECTED_RESULTS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|EXPECTED_RESULTS
init|=
operator|new
name|String
index|[]
block|{
literal|"-Dfoo=bar"
block|,
literal|"-Dbaz=biz"
block|}
decl_stmt|;
DECL|method|performTest (String input)
specifier|private
name|void
name|performTest
parameter_list|(
name|String
name|input
parameter_list|)
block|{
name|String
index|[]
name|result
init|=
name|TaskRunner
operator|.
name|parseChildJavaOpts
argument_list|(
name|input
argument_list|,
name|TASK_ID
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|EXPECTED_RESULTS
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseChildJavaOptsLeadingSpace ()
specifier|public
name|void
name|testParseChildJavaOptsLeadingSpace
parameter_list|()
block|{
name|performTest
argument_list|(
literal|" -Dfoo=bar -Dbaz=biz"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseChildJavaOptsTrailingSpace ()
specifier|public
name|void
name|testParseChildJavaOptsTrailingSpace
parameter_list|()
block|{
name|performTest
argument_list|(
literal|"-Dfoo=bar -Dbaz=biz "
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseChildJavaOptsOneSpace ()
specifier|public
name|void
name|testParseChildJavaOptsOneSpace
parameter_list|()
block|{
name|performTest
argument_list|(
literal|"-Dfoo=bar -Dbaz=biz"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseChildJavaOptsMulitpleSpaces ()
specifier|public
name|void
name|testParseChildJavaOptsMulitpleSpaces
parameter_list|()
block|{
name|performTest
argument_list|(
literal|"-Dfoo=bar  -Dbaz=biz"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseChildJavaOptsOneTab ()
specifier|public
name|void
name|testParseChildJavaOptsOneTab
parameter_list|()
block|{
name|performTest
argument_list|(
literal|"-Dfoo=bar\t-Dbaz=biz"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseChildJavaOptsMultipleTabs ()
specifier|public
name|void
name|testParseChildJavaOptsMultipleTabs
parameter_list|()
block|{
name|performTest
argument_list|(
literal|"-Dfoo=bar\t\t-Dbaz=biz"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

