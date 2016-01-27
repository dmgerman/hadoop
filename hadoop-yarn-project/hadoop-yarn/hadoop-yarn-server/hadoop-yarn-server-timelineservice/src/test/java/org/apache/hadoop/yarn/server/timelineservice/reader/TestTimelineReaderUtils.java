begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|reader
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
name|assertArrayEquals
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
name|assertNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
DECL|class|TestTimelineReaderUtils
specifier|public
class|class
name|TestTimelineReaderUtils
block|{
annotation|@
name|Test
DECL|method|testSplitUsingEscapeAndDelimChar ()
specifier|public
name|void
name|testSplitUsingEscapeAndDelimChar
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|TimelineReaderUtils
operator|.
name|split
argument_list|(
literal|"*!cluster!*!b**o***!xer!oozie**"
argument_list|,
literal|'!'
argument_list|,
literal|'*'
argument_list|)
decl_stmt|;
name|String
index|[]
name|arr
init|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|arr
operator|=
name|list
operator|.
name|toArray
argument_list|(
name|arr
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"!cluster"
block|,
literal|"!b*o*!xer"
block|,
literal|"oozie*"
block|}
argument_list|,
name|arr
argument_list|)
expr_stmt|;
name|list
operator|=
name|TimelineReaderUtils
operator|.
name|split
argument_list|(
literal|"*!cluster!*!b**o***!xer!!"
argument_list|,
literal|'!'
argument_list|,
literal|'*'
argument_list|)
expr_stmt|;
name|arr
operator|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|arr
operator|=
name|list
operator|.
name|toArray
argument_list|(
name|arr
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"!cluster"
block|,
literal|"!b*o*!xer"
block|,
literal|""
block|,
literal|""
block|}
argument_list|,
name|arr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJoinAndEscapeStrings ()
specifier|public
name|void
name|testJoinAndEscapeStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"*!cluster!*!b**o***!xer!oozie**"
argument_list|,
name|TimelineReaderUtils
operator|.
name|joinAndEscapeStrings
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"!cluster"
block|,
literal|"!b*o*!xer"
block|,
literal|"oozie*"
block|}
argument_list|,
literal|'!'
argument_list|,
literal|'*'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"*!cluster!*!b**o***!xer!!"
argument_list|,
name|TimelineReaderUtils
operator|.
name|joinAndEscapeStrings
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"!cluster"
block|,
literal|"!b*o*!xer"
block|,
literal|""
block|,
literal|""
block|}
argument_list|,
literal|'!'
argument_list|,
literal|'*'
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|TimelineReaderUtils
operator|.
name|joinAndEscapeStrings
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"!cluster"
block|,
literal|"!b*o*!xer"
block|,
literal|null
block|,
literal|""
block|}
argument_list|,
literal|'!'
argument_list|,
literal|'*'
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

