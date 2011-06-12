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
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|mapred
operator|.
name|StatisticsCollector
operator|.
name|TimeWindow
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
name|mapred
operator|.
name|StatisticsCollector
operator|.
name|Stat
import|;
end_import

begin_class
DECL|class|TestStatisticsCollector
specifier|public
class|class
name|TestStatisticsCollector
extends|extends
name|TestCase
block|{
DECL|method|testMovingWindow ()
specifier|public
name|void
name|testMovingWindow
parameter_list|()
throws|throws
name|Exception
block|{
name|StatisticsCollector
name|collector
init|=
operator|new
name|StatisticsCollector
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|TimeWindow
name|window
init|=
operator|new
name|TimeWindow
argument_list|(
literal|"test"
argument_list|,
literal|6
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|TimeWindow
name|sincStart
init|=
name|StatisticsCollector
operator|.
name|SINCE_START
decl_stmt|;
name|TimeWindow
index|[]
name|windows
init|=
block|{
name|sincStart
block|,
name|window
block|}
decl_stmt|;
name|Stat
name|stat
init|=
name|collector
operator|.
name|createStat
argument_list|(
literal|"m1"
argument_list|,
name|windows
argument_list|)
decl_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|window
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|sincStart
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|3
operator|+
literal|3
operator|)
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|window
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|sincStart
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|3
operator|+
literal|3
operator|)
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|window
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|sincStart
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|3
operator|+
literal|3
operator|+
literal|10
operator|+
literal|10
operator|)
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|window
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|26
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|sincStart
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|3
operator|+
literal|3
operator|+
literal|10
operator|+
literal|10
operator|+
literal|10
operator|+
literal|10
operator|)
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|window
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|46
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|sincStart
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|3
operator|+
literal|3
operator|+
literal|10
operator|+
literal|10
operator|+
literal|10
operator|+
literal|10
operator|)
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|window
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|56
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|sincStart
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|12
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|10
operator|+
literal|10
operator|+
literal|10
operator|+
literal|10
operator|+
literal|10
operator|+
literal|12
operator|)
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|window
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|68
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|sincStart
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|13
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|10
operator|+
literal|10
operator|+
literal|10
operator|+
literal|10
operator|+
literal|10
operator|+
literal|12
operator|)
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|window
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|81
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|sincStart
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|inc
argument_list|(
literal|14
argument_list|)
expr_stmt|;
name|collector
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|10
operator|+
literal|10
operator|+
literal|10
operator|+
literal|12
operator|+
literal|13
operator|+
literal|14
operator|)
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|window
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|95
argument_list|,
name|stat
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|sincStart
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

