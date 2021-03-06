begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.top.window
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
operator|.
name|top
operator|.
name|window
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
DECL|class|TestRollingWindow
specifier|public
class|class
name|TestRollingWindow
block|{
DECL|field|WINDOW_LEN
specifier|final
name|int
name|WINDOW_LEN
init|=
literal|60000
decl_stmt|;
DECL|field|BUCKET_CNT
specifier|final
name|int
name|BUCKET_CNT
init|=
literal|10
decl_stmt|;
DECL|field|BUCKET_LEN
specifier|final
name|int
name|BUCKET_LEN
init|=
name|WINDOW_LEN
operator|/
name|BUCKET_CNT
decl_stmt|;
annotation|@
name|Test
DECL|method|testBasics ()
specifier|public
name|void
name|testBasics
parameter_list|()
block|{
name|RollingWindow
name|window
init|=
operator|new
name|RollingWindow
argument_list|(
name|WINDOW_LEN
argument_list|,
name|BUCKET_CNT
argument_list|)
decl_stmt|;
name|long
name|time
init|=
literal|1
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The initial sum of rolling window must be 0"
argument_list|,
literal|0
argument_list|,
name|window
operator|.
name|getSum
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
name|time
operator|=
name|WINDOW_LEN
operator|+
name|BUCKET_LEN
operator|*
literal|3
operator|/
literal|2
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The initial sum of rolling window must be 0"
argument_list|,
literal|0
argument_list|,
name|window
operator|.
name|getSum
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
name|window
operator|.
name|incAt
argument_list|(
name|time
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The sum of rolling window does not reflect the recent update"
argument_list|,
literal|5
argument_list|,
name|window
operator|.
name|getSum
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
name|time
operator|+=
name|BUCKET_LEN
expr_stmt|;
name|window
operator|.
name|incAt
argument_list|(
name|time
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The sum of rolling window does not reflect the recent update"
argument_list|,
literal|11
argument_list|,
name|window
operator|.
name|getSum
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
name|time
operator|+=
name|WINDOW_LEN
operator|-
name|BUCKET_LEN
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The sum of rolling window does not reflect rolling effect"
argument_list|,
literal|6
argument_list|,
name|window
operator|.
name|getSum
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
name|time
operator|+=
name|BUCKET_LEN
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The sum of rolling window does not reflect rolling effect"
argument_list|,
literal|0
argument_list|,
name|window
operator|.
name|getSum
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReorderedAccess ()
specifier|public
name|void
name|testReorderedAccess
parameter_list|()
block|{
name|RollingWindow
name|window
init|=
operator|new
name|RollingWindow
argument_list|(
name|WINDOW_LEN
argument_list|,
name|BUCKET_CNT
argument_list|)
decl_stmt|;
name|long
name|time
init|=
literal|2
operator|*
name|WINDOW_LEN
operator|+
name|BUCKET_LEN
operator|*
literal|3
operator|/
literal|2
decl_stmt|;
name|window
operator|.
name|incAt
argument_list|(
name|time
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|time
operator|++
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The sum of rolling window does not reflect the recent update"
argument_list|,
literal|5
argument_list|,
name|window
operator|.
name|getSum
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|reorderedTime
init|=
name|time
operator|-
literal|2
operator|*
name|BUCKET_LEN
decl_stmt|;
name|window
operator|.
name|incAt
argument_list|(
name|reorderedTime
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The sum of rolling window does not reflect the reordered update"
argument_list|,
literal|11
argument_list|,
name|window
operator|.
name|getSum
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
name|time
operator|=
name|reorderedTime
operator|+
name|WINDOW_LEN
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The sum of rolling window does not reflect rolling effect"
argument_list|,
literal|5
argument_list|,
name|window
operator|.
name|getSum
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

