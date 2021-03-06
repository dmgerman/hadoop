begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell.find
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|find
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
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
name|util
operator|.
name|Deque
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
name|fs
operator|.
name|shell
operator|.
name|PathData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
DECL|class|TestFilterExpression
specifier|public
class|class
name|TestFilterExpression
block|{
DECL|field|expr
specifier|private
name|Expression
name|expr
decl_stmt|;
DECL|field|test
specifier|private
name|FilterExpression
name|test
decl_stmt|;
annotation|@
name|Rule
DECL|field|globalTimeout
specifier|public
name|Timeout
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|expr
operator|=
name|mock
argument_list|(
name|Expression
operator|.
name|class
argument_list|)
expr_stmt|;
name|test
operator|=
operator|new
name|FilterExpression
argument_list|(
name|expr
argument_list|)
block|{     }
expr_stmt|;
block|}
comment|// test that the child expression is correctly set
annotation|@
name|Test
DECL|method|expression ()
specifier|public
name|void
name|expression
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|expr
argument_list|,
name|test
operator|.
name|expression
argument_list|)
expr_stmt|;
block|}
comment|// test that setOptions method is called
annotation|@
name|Test
DECL|method|setOptions ()
specifier|public
name|void
name|setOptions
parameter_list|()
throws|throws
name|IOException
block|{
name|FindOptions
name|options
init|=
name|mock
argument_list|(
name|FindOptions
operator|.
name|class
argument_list|)
decl_stmt|;
name|test
operator|.
name|setOptions
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|)
operator|.
name|setOptions
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|// test the apply method is called and the result returned
annotation|@
name|Test
DECL|method|apply ()
specifier|public
name|void
name|apply
parameter_list|()
throws|throws
name|IOException
block|{
name|PathData
name|item
init|=
name|mock
argument_list|(
name|PathData
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|expr
operator|.
name|apply
argument_list|(
name|item
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Result
operator|.
name|PASS
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Result
operator|.
name|FAIL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Result
operator|.
name|PASS
argument_list|,
name|test
operator|.
name|apply
argument_list|(
name|item
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Result
operator|.
name|FAIL
argument_list|,
name|test
operator|.
name|apply
argument_list|(
name|item
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|apply
argument_list|(
name|item
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|// test that the finish method is called
annotation|@
name|Test
DECL|method|finish ()
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|test
operator|.
name|finish
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|)
operator|.
name|finish
argument_list|()
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|// test that the getUsage method is called
annotation|@
name|Test
DECL|method|getUsage ()
specifier|public
name|void
name|getUsage
parameter_list|()
block|{
name|String
index|[]
name|usage
init|=
operator|new
name|String
index|[]
block|{
literal|"Usage 1"
block|,
literal|"Usage 2"
block|,
literal|"Usage 3"
block|}
decl_stmt|;
name|when
argument_list|(
name|expr
operator|.
name|getUsage
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|usage
argument_list|,
name|test
operator|.
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|)
operator|.
name|getUsage
argument_list|()
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|// test that the getHelp method is called
annotation|@
name|Test
DECL|method|getHelp ()
specifier|public
name|void
name|getHelp
parameter_list|()
block|{
name|String
index|[]
name|help
init|=
operator|new
name|String
index|[]
block|{
literal|"Help 1"
block|,
literal|"Help 2"
block|,
literal|"Help 3"
block|}
decl_stmt|;
name|when
argument_list|(
name|expr
operator|.
name|getHelp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|help
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|help
argument_list|,
name|test
operator|.
name|getHelp
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|)
operator|.
name|getHelp
argument_list|()
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|// test that the isAction method is called
annotation|@
name|Test
DECL|method|isAction ()
specifier|public
name|void
name|isAction
parameter_list|()
block|{
name|when
argument_list|(
name|expr
operator|.
name|isAction
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|isAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|test
operator|.
name|isAction
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|isAction
argument_list|()
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|// test that the isOperator method is called
annotation|@
name|Test
DECL|method|isOperator ()
specifier|public
name|void
name|isOperator
parameter_list|()
block|{
name|when
argument_list|(
name|expr
operator|.
name|isAction
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|isAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|test
operator|.
name|isAction
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|isAction
argument_list|()
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|// test that the getPrecedence method is called
annotation|@
name|Test
DECL|method|getPrecedence ()
specifier|public
name|void
name|getPrecedence
parameter_list|()
block|{
name|int
name|precedence
init|=
literal|12345
decl_stmt|;
name|when
argument_list|(
name|expr
operator|.
name|getPrecedence
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|precedence
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|precedence
argument_list|,
name|test
operator|.
name|getPrecedence
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|)
operator|.
name|getPrecedence
argument_list|()
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|// test that the addChildren method is called
annotation|@
name|Test
DECL|method|addChildren ()
specifier|public
name|void
name|addChildren
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Deque
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
name|mock
argument_list|(
name|Deque
operator|.
name|class
argument_list|)
decl_stmt|;
name|test
operator|.
name|addChildren
argument_list|(
name|expressions
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|)
operator|.
name|addChildren
argument_list|(
name|expressions
argument_list|)
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|// test that the addArguments method is called
annotation|@
name|Test
DECL|method|addArguments ()
specifier|public
name|void
name|addArguments
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Deque
argument_list|<
name|String
argument_list|>
name|args
init|=
name|mock
argument_list|(
name|Deque
operator|.
name|class
argument_list|)
decl_stmt|;
name|test
operator|.
name|addArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|expr
argument_list|)
operator|.
name|addArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

