begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.wsrs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|wsrs
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
DECL|class|TestIntegerParam
specifier|public
class|class
name|TestIntegerParam
block|{
annotation|@
name|Test
DECL|method|param ()
specifier|public
name|void
name|param
parameter_list|()
throws|throws
name|Exception
block|{
name|IntegerParam
name|param
init|=
operator|new
name|IntegerParam
argument_list|(
literal|"p"
argument_list|,
literal|"1"
argument_list|)
block|{     }
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|param
operator|.
name|getDomain
argument_list|()
argument_list|,
literal|"an integer"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|param
operator|.
name|value
argument_list|()
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|param
operator|.
name|toString
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|param
operator|=
operator|new
name|IntegerParam
argument_list|(
literal|"p"
argument_list|,
literal|null
argument_list|)
block|{     }
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|param
operator|.
name|value
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|param
operator|=
operator|new
name|IntegerParam
argument_list|(
literal|"p"
argument_list|,
literal|""
argument_list|)
block|{     }
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|param
operator|.
name|value
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|invalid1 ()
specifier|public
name|void
name|invalid1
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|IntegerParam
argument_list|(
literal|"p"
argument_list|,
literal|"x"
argument_list|)
block|{     }
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|invalid2 ()
specifier|public
name|void
name|invalid2
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|IntegerParam
argument_list|(
literal|"p"
argument_list|,
literal|""
operator|+
name|Long
operator|.
name|MAX_VALUE
argument_list|)
block|{     }
expr_stmt|;
block|}
block|}
end_class

end_unit

