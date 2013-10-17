begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.oncrpc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|oncrpc
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
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
DECL|class|TestXDR
specifier|public
class|class
name|TestXDR
block|{
DECL|method|serializeInt (int times)
specifier|private
name|void
name|serializeInt
parameter_list|(
name|int
name|times
parameter_list|)
block|{
name|XDR
name|w
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|times
condition|;
operator|++
name|i
control|)
name|w
operator|.
name|writeInt
argument_list|(
literal|23
argument_list|)
expr_stmt|;
name|XDR
name|r
init|=
name|w
operator|.
name|asReadOnlyWrap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|times
condition|;
operator|++
name|i
control|)
name|Assert
operator|.
name|assertEquals
argument_list|(
name|r
operator|.
name|readInt
argument_list|()
argument_list|,
literal|23
argument_list|)
expr_stmt|;
block|}
DECL|method|serializeLong (int times)
specifier|private
name|void
name|serializeLong
parameter_list|(
name|int
name|times
parameter_list|)
block|{
name|XDR
name|w
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|times
condition|;
operator|++
name|i
control|)
name|w
operator|.
name|writeLongAsHyper
argument_list|(
literal|23
argument_list|)
expr_stmt|;
name|XDR
name|r
init|=
name|w
operator|.
name|asReadOnlyWrap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|times
condition|;
operator|++
name|i
control|)
name|Assert
operator|.
name|assertEquals
argument_list|(
name|r
operator|.
name|readHyper
argument_list|()
argument_list|,
literal|23
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPerformance ()
specifier|public
name|void
name|testPerformance
parameter_list|()
block|{
specifier|final
name|int
name|TEST_TIMES
init|=
literal|8
operator|<<
literal|20
decl_stmt|;
name|serializeInt
argument_list|(
name|TEST_TIMES
argument_list|)
expr_stmt|;
name|serializeLong
argument_list|(
name|TEST_TIMES
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

