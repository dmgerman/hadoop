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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|Path
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

begin_comment
comment|/**  * test SkipBadRecords  *   *   */
end_comment

begin_class
DECL|class|TestSkipBadRecords
specifier|public
class|class
name|TestSkipBadRecords
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testSkipBadRecords ()
specifier|public
name|void
name|testSkipBadRecords
parameter_list|()
block|{
comment|// test default values
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|SkipBadRecords
operator|.
name|getAttemptsToStartSkipping
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SkipBadRecords
operator|.
name|getAutoIncrMapperProcCount
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SkipBadRecords
operator|.
name|getAutoIncrReducerProcCount
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|SkipBadRecords
operator|.
name|getMapperMaxSkipRecords
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|SkipBadRecords
operator|.
name|getReducerMaxSkipGroups
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|SkipBadRecords
operator|.
name|getSkipOutputPath
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// test setters
name|SkipBadRecords
operator|.
name|setAttemptsToStartSkipping
argument_list|(
name|conf
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|SkipBadRecords
operator|.
name|setAutoIncrMapperProcCount
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SkipBadRecords
operator|.
name|setAutoIncrReducerProcCount
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SkipBadRecords
operator|.
name|setMapperMaxSkipRecords
argument_list|(
name|conf
argument_list|,
literal|6L
argument_list|)
expr_stmt|;
name|SkipBadRecords
operator|.
name|setReducerMaxSkipGroups
argument_list|(
name|conf
argument_list|,
literal|7L
argument_list|)
expr_stmt|;
name|JobConf
name|jc
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|SkipBadRecords
operator|.
name|setSkipOutputPath
argument_list|(
name|jc
argument_list|,
operator|new
name|Path
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test getters
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|SkipBadRecords
operator|.
name|getAttemptsToStartSkipping
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SkipBadRecords
operator|.
name|getAutoIncrMapperProcCount
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SkipBadRecords
operator|.
name|getAutoIncrReducerProcCount
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6L
argument_list|,
name|SkipBadRecords
operator|.
name|getMapperMaxSkipRecords
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7L
argument_list|,
name|SkipBadRecords
operator|.
name|getReducerMaxSkipGroups
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|SkipBadRecords
operator|.
name|getSkipOutputPath
argument_list|(
name|jc
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

